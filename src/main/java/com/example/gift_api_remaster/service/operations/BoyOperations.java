package com.example.gift_api_remaster.service.operations;

import com.example.gift_api_remaster.model.Boy;
import com.example.gift_api_remaster.model.BoyView;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.ChildView;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.dto.BoyDto;
import com.example.gift_api_remaster.model.dto.ChildDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class BoyOperations implements ChildOperations {

    @Override
    public Boy create(CreateChildCommand command) {
        Map<String, String> params = command.getParams();
        return Boy.builder()
                .name(params.get("name"))
                .surname(params.get("surname"))
                .birthday(LocalDate.parse(params.get("birthday")))
                .pipeLength(Double.parseDouble(params.get("pipeLength")))
                .build();
    }

    @Override
    public BoyDto mapToDto(Child child) {
        Boy boy = (Boy) child;
        return BoyDto.builder()
                .id(boy.getId())
                .name(boy.getName())
                .surname(boy.getSurname())
                .birthday(boy.getBirthday())
                .pipeLength(boy.getPipeLength())
                .build();
    }

    @Override
    public ChildDto mapToDto(ChildView childView) {
        BoyView boyView = (BoyView) childView;
        return BoyDto.builder()
                .id(boyView.getId())
                .name(boyView.getName())
                .surname(boyView.getSurname())
                .birthday(boyView.getBirthday())
                .pipeLength(boyView.getPipeLength())
                .build();
    }
}
