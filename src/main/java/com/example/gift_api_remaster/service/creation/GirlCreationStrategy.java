package com.example.gift_api_remaster.service.creation;


import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.Girl;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component("GIRL")
public class GirlCreationStrategy implements ChildCreationStrategy {

    @Override
    public Girl create(CreateChildCommand command) {
        Map<String, String> params = command.getParams();
        return Girl.builder()
                .name(params.get("name"))
                .surname(params.get("surname"))
                .birthday(LocalDate.parse(params.get("birthday")))
                .dressColor(params.get("dressColor"))
                .build();
    }
}
