package com.example.personmanagement.search;

import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.position.JobPosition;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SalarySpecificationProvider implements SpecificationProvider {

    @Override
    public boolean supports(SearchCriteria criteria) {
        return "salary".equals(criteria.getKey());
    }

    @Override
    public Specification<Person> getSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            Double minSalary = Double.parseDouble(criteria.getValue().toString());
            Double maxSalary = Double.parseDouble(criteria.getSecondValue().toString());

            if (minSalary > maxSalary) {
                throw new IllegalArgumentException("Min salary cannot be greater than max salary");
                // TODO: 23/07/2024 Zmienic exception na jakis normalny
            }

            Subquery<JobPosition> subquery = query.subquery(JobPosition.class);
            Root<Employee> employeeRoot = subquery.from(Employee.class);
            Join<Employee, JobPosition> jobPositionJoin = employeeRoot.join("jobPositions");

            Predicate salaryPredicate = criteriaBuilder.between(jobPositionJoin.get("salary"), minSalary, maxSalary);

            subquery.select(jobPositionJoin)
                    .where(criteriaBuilder.equal(jobPositionJoin.get("employee"), root), salaryPredicate);

            return criteriaBuilder.exists(subquery);
        };
    }
}
