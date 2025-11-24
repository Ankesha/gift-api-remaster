package com.example.gift_api_remaster.service.operations;


import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.ChildView;
import com.example.gift_api_remaster.model.Girl;
import com.example.gift_api_remaster.model.GirlView;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import com.example.gift_api_remaster.model.dto.GirlDto;
import com.example.gift_api_remaster.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GirlOperations implements ChildOperations {

    private final ChildRepository childRepository;

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

    @Override
    public GirlDto mapToDto(Child child) {
        Girl girl = (Girl) child;
        return GirlDto.builder()
                .id(girl.getId())
                .name(girl.getName())
                .surname(girl.getSurname())
                .birthday(girl.getBirthday())
                .dressColor(girl.getDressColor())
                .build();
    }

    @Override
    public ChildDto mapToDto(ChildView childView){
        GirlView girlView = (GirlView) childView;
        return GirlDto.builder()
                .id(girlView.getId())
                .name(girlView.getName())
                .surname(girlView.getSurname())
                .birthday(girlView.getBirthday())
                .dressColor(girlView.getDressColor())
                .build();


    }

}
