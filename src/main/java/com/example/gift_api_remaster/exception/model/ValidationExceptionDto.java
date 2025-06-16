package com.example.gift_api_remaster.exception.model;

import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationExceptionDto extends ExceptionDto {

    private final List<ViolationInfo> violations = new ArrayList<>();

    public ValidationExceptionDto() {
        super("Constraint Validation failed");
    }

    public void addViolation(String field, String info) {
        violations.add(new ViolationInfo(field, info));
    }

    @Value
    public static class ViolationInfo {
        String field;
        String info;
    }
}
