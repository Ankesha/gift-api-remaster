package com.example.gift_api_remaster.service.operations;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;

public interface ChildOperations {

    String OPERATIONS_SUFFIX = "Operations";

    Child create(CreateChildCommand command);

    ChildDto mapToDto(Child child);

}
