package com.example.personmanagement.search;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchCriteriaList {
    private List<SearchCriteria> searchCriteria = new ArrayList<>();

}