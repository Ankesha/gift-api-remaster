package com.example.gift_api_remaster.service;


import com.example.gift_api_remaster.exception.GiftApiException;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.command.UpdateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import com.example.gift_api_remaster.model.mapper.ChildMapper;
import com.example.gift_api_remaster.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gift_api_remaster.model.mapper.ChildMapper.toEntity;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;



    public ChildDto findById(long id) {

//        Child child = childRepository.findById(id)
//                .orElseThrow(() -> new ChildNotFoundException("Child not found"));
//        return toDto(child);
        return childRepository.findById(id)
                .map(ChildMapper::toDto)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Child with id {0} not found", id)));
    }

    public List<ChildDto> findAll() {
        return childRepository.findAll().stream()
                .map(ChildMapper::toDto)
                .collect(Collectors.toList());
    }

    public ChildDto save(CreateChildCommand command) {
//        Child toSave = toEntity(command);
//        Child savedChild = childRepository.save(toSave);
//        return toDto(savedChild);
        return ChildMapper.toDto(childRepository.save(toEntity(command)));
    }

    @Transactional
    public ChildDto update(UpdateChildCommand command, long id) {
        Child existingChild = childRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child with id = " + id + " not found"));

        ChildMapper.update(command, existingChild);
//        Child updatedChild = childRepository.save(existingChild);
//        return toDto(updatedChild);
        return ChildMapper.toDto(childRepository.save(existingChild));
    }

    public void deleteById(long id) {
        if (!childRepository.existsById(id)) {
            throw new GiftApiException(MessageFormat
                    .format("Child with id {0} not found", id));
        }
        childRepository.deleteById(id);
    }
}
