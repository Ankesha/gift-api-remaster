package com.example.gift_api_remaster.model.command;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateGiftCommand {

    @NotNull(message = "name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\s]*", message = "name can only contain alphabets and space")
    private String name;
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private double price;


}
