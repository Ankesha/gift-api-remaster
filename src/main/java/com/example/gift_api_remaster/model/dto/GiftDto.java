package com.example.gift_api_remaster.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GiftDto {

    private Long id;
    private String name;
    private double price;
    private long idChild;

}