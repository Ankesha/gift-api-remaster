package com.example.gift_api_remaster.service.creation;


import com.example.gift_api_remaster.model.Boy;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component("BOY")
public class BoyCreationStrategy implements ChildCreationStrategy {

    @Override
    public Boy create(CreateChildCommand command) {
        Map<String, String> params = command.getParams();
        return Boy.builder()
                .name(params.get("name"))
                .surname(params.get("surname"))
                .birthday(LocalDate.parse(params.get("dateOfBirth")))
                .pipeLength(Double.parseDouble(params.get("pipeLength")))
                .build();
    }
}
