package com.example.gift_api_remaster.model.mapper;

import com.example.gift_api_remaster.model.Gift;
import com.example.gift_api_remaster.model.command.CreateGiftCommand;
import com.example.gift_api_remaster.model.command.UpdateGiftCommand;
import com.example.gift_api_remaster.model.dto.GiftDto;
import lombok.experimental.UtilityClass;


@UtilityClass
public class GiftMapper {

    public static GiftDto toDto(Gift gift) {
        if (gift == null) return null;
        return GiftDto.builder()
                .id(gift.getId())
                .name(gift.getName())
                .price(gift.getPrice())
                .idChild(gift.getChild().getId())
                .build();
    }


    public static Gift toEntity(CreateGiftCommand command) {
        if (command == null) return null;
        return Gift.builder()
                .name(command.getName())
                .price(command.getPrice())
                .build();
    }


    public static void update(UpdateGiftCommand command, Gift targetGift) {
        if (command == null) {
            return;
        }
        targetGift.setName(command.getName());
        targetGift.setPrice(command.getPrice());
    }
}
