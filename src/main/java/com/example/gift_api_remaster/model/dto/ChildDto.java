package com.example.gift_api_remaster.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ChildDto {

    private long id;
    private String name;
    private String surname;
    private LocalDate birthday;
    private long giftsCount;

}
