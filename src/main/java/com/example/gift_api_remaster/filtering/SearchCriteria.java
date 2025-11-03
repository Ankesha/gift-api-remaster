package com.example.gift_api_remaster.filtering;


import lombok.Data;

@Data
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;


}
