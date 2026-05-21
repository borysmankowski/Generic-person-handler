package com.example.personmanagement.view;

import com.example.personmanagement.model.person.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OneToMany;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.PluralAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class PersonViewBuilder {

    private final EntityManager em;
    private final JdbcTemplate jdbc;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void rebuildPersonView() {
        try {
            Set<EntityType<?>> personTypes = collectPersonTypes();
            Set<String> columns = collectBasicColumns(personTypes);
            List<JoinSpec> joins = collectOneToManyJoins(personTypes);

            String ddl = buildCreateViewSql(columns, joins);
            log.info("Rebuilding person_view:\n{}", ddl);

            jdbc.execute("DROP VIEW IF EXISTS person_view");
            jdbc.execute(ddl);
        } catch (Exception e) {
            log.error("Failed to rebuild person_view", e);
            throw e;
        }
    }

    private Set<EntityType<?>> collectPersonTypes() {
        Set<EntityType<?>> result = new LinkedHashSet<>();
        for (EntityType<?> et : em.getMetamodel().getEntities()) {
            if (Person.class.isAssignableFrom(et.getJavaType())) {
                result.add(et);
            }
        }
        return result;
    }

    private Set<String> collectBasicColumns(Set<EntityType<?>> personTypes) {
        Set<String> columns = new LinkedHashSet<>();
        columns.add("id");
        columns.add("type");
        for (EntityType<?> et : personTypes) {
            for (Attribute<?, ?> a : et.getAttributes()) {
                if (a.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
                    columns.add(toSnakeCase(a.getName()));
                }
            }
        }
        return columns;
    }

    private List<JoinSpec> collectOneToManyJoins(Set<EntityType<?>> personTypes) {
        List<JoinSpec> joins = new ArrayList<>();
        for (EntityType<?> et : personTypes) {
            for (Attribute<?, ?> a : et.getAttributes()) {
                if (a.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY) {
                    PluralAttribute<?, ?, ?> pa = (PluralAttribute<?, ?, ?>) a;
                    EntityType<?> target = (EntityType<?>) pa.getElementType();

                    String mappedBy = readMappedBy(et.getJavaType(), a.getName());
                    if (mappedBy == null || mappedBy.isEmpty()) {
                        log.debug("Skipping @OneToMany {} on {} — no mappedBy", a.getName(), et.getName());
                        continue;
                    }

                    JoinSpec j = new JoinSpec();
                    j.alias = "j_" + a.getName().toLowerCase();
                    j.targetTable = toSnakeCase(target.getName());
                    j.joinColumn = toSnakeCase(mappedBy) + "_id";
                    j.countAlias = "number_of_" + toSnakeCase(a.getName());
                    joins.add(j);
                }
            }
        }
        return joins;
    }

    private String buildCreateViewSql(Set<String> columns, List<JoinSpec> joins) {
        StringBuilder sb = new StringBuilder("CREATE VIEW person_view AS\nSELECT ");

        List<String> projections = new ArrayList<>();
        for (String c : columns) {
            projections.add("p." + c);
        }
        for (JoinSpec j : joins) {
            projections.add("COUNT(" + j.alias + ".id) OVER (PARTITION BY p.id) AS " + j.countAlias);
        }

        sb.append(String.join(", ", projections));
        sb.append("\nFROM person p");

        for (JoinSpec j : joins) {
            sb.append("\nLEFT JOIN ").append(j.targetTable).append(" ").append(j.alias)
                    .append(" ON p.id = ").append(j.alias).append(".").append(j.joinColumn);
        }
        return sb.toString();
    }

    /**
     * JPA metamodel doesn't expose mappedBy directly, so we read the annotation via reflection,
     * walking up the class hierarchy in case the field is declared on a superclass.
     */
    private String readMappedBy(Class<?> entityClass, String fieldName) {
        Class<?> c = entityClass;
        while (c != null && c != Object.class) {
            try {
                Field f = c.getDeclaredField(fieldName);
                OneToMany ann = f.getAnnotation(OneToMany.class);
                return (ann != null) ? ann.mappedBy() : null;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    private static String toSnakeCase(String s) {
        return s.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    private static class JoinSpec {
        String alias;
        String targetTable;
        String joinColumn;
        String countAlias;
    }
}