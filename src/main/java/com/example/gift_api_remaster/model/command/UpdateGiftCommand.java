package com.example.gift_api_remaster.model.command;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGiftCommand {

    @NotNull(message = "name cannot be null")
    @Pattern(regexp = "[A-Za-z\\s]{1,50}", message = "name can only contain alphabets and space, up to 50 characters")
    private String name;
    @Min(value = 0, message = "price must be greater than or equal to 0")
    private double price;


//    //TODO: update danych powinno byz robione na poziome mappera
//    public void update(Gift gift, Child child) {
//        if (name != null) {
//            gift.setName(name);
//        }
//        if (price >= 0) {
//            gift.setPrice(price);
//        }
//        if (child != null) {
//            gift.setChild(child);
//        }
//    }


}
