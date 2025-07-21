package com.example.gift_api_remaster.model.command;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChildCommand {


    @NotNull(message = "name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\s]*", message = "name can only contain alphabets and space")
    private String name;
    @NotNull(message = "name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\s]*", message = "name can only contain alphabets and space")
    private String surname;
    @NotNull(message = "date has to be in correct order")
    @Past
    private LocalDate birthday;
    @Positive
    private int version;


}
