package com.example.gift_api_remaster.controller;


import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.command.CreateGiftCommand;
import com.example.gift_api_remaster.model.command.UpdateChildCommand;
import com.example.gift_api_remaster.model.command.UpdateGiftCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import com.example.gift_api_remaster.model.dto.GiftDto;
import com.example.gift_api_remaster.service.ChildService;
import com.example.gift_api_remaster.service.GiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/children")
public class ChildController {

    private final ChildService childService;
    private final GiftService giftService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChildDto findById(@PathVariable long id) {
        return childService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ChildDto> findAll() {
        return childService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@Valid - dodajemy w argumentach metody ktróre przyjmują dane z requesta, aby sprawdzić czy są poprawne
    public ChildDto save(@Valid @RequestBody CreateChildCommand command) {
        return childService.save(command);
    }

    @DeleteMapping("/{id}")//TODO: czy na pewno na takim mappoing czy wystaczy post ?
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long id) {
        childService.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChildDto update(@Valid @RequestBody UpdateChildCommand command, @PathVariable long id) {
        return childService.update(command, id);
    }

    @GetMapping("/{id}/gifts")
    @ResponseStatus(HttpStatus.OK)
    public List<GiftDto> findAllGiftsByChildId(@PathVariable long id) {
        return giftService.findAllByChildId(id);
    }

    @GetMapping("/{childId}/gifts/{giftId}")
    @ResponseStatus(HttpStatus.OK)
    public GiftDto findGiftById(@PathVariable long giftId, @PathVariable long childId) {
        return giftService.findById(giftId, childId);
    }

    @PostMapping("/{childId}/gifts")
    @ResponseStatus(HttpStatus.CREATED)
    public GiftDto saveGiftToChild(@RequestBody CreateGiftCommand command, @PathVariable long childId) {
        return giftService.saveGiftToChild(command, childId);
    }

    @PutMapping("/{childId}/gifts/{giftId}")
    @ResponseStatus(HttpStatus.OK)
    public GiftDto updateGift(@RequestBody UpdateGiftCommand command, @PathVariable long giftId, @PathVariable long childId) {
        return giftService.updateGift(command, giftId, childId);
    }

    @DeleteMapping("/{childId}/gifts/{giftId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGiftById(@PathVariable long giftId, @PathVariable long childId) {
        giftService.deleteById(giftId, childId);
    }


}
