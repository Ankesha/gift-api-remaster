package com.example.gift_api_remaster.service.creation;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;

public interface ChildCreationStrategy {

    Child create(CreateChildCommand command);
}
