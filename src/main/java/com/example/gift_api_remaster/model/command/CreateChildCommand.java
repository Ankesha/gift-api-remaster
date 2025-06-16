package com.example.gift_api_remaster.model.command;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateChildCommand {


    //TODO: Zrobic walidacje
    @NotBlank(message = "name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\s]*", message = "name can only contain alphabets and space")
    private String name;
    @NotBlank(message = "name cannot be null")
    @Pattern(regexp = "[a-zA-Z\\s]*", message = "name can only contain alphabets and space")
    private String surname;
    @NotNull(message = "date has to be in correct order")
    @Past
    private LocalDate birthday;

}
