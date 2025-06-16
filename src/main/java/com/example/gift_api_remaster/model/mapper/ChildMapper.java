package com.example.gift_api_remaster.model.mapper;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.command.UpdateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import lombok.experimental.UtilityClass;


@UtilityClass
public class ChildMapper {


    public static ChildDto toDto(Child child) {
        if (child == null) return null;
        return ChildDto.builder()
                .id(child.getId())
                .name(child.getName())
                .surname(child.getSurname())
                .birthday(child.getBirthday())
                .build();
    }


    public static Child toEntity(CreateChildCommand command) {
        if (command == null) return null;
        return Child.builder()
                .name(command.getName())
                .surname(command.getSurname())
                .birthday(command.getBirthday())
                .build();
    }


    public static void update(UpdateChildCommand command, Child targetChild) {
        if (command == null) {
            return;
        }
        targetChild.setName(command.getName());
        targetChild.setSurname(command.getSurname());
        targetChild.setBirthday(command.getBirthday());
        targetChild.setVersion(command.getVersion());
    }
}
