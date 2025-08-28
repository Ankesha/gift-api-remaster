package com.example.gift_api_remaster.model.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GirlDto extends ChildDto {

    private String dressColor;
}
