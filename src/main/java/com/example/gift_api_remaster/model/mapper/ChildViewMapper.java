package com.example.gift_api_remaster.model.mapper;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.ChildView;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChildViewMapper {

    public static ChildView toDto(Child child) {
        if (child == null) return null;
        return ChildView.builder()
                .id(child.getId())
                .name(child.getName())
                .surname(child.getSurname())
                .birthday(child.getBirthday())
                .build();
    }


}
