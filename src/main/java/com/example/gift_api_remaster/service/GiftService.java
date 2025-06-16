package com.example.gift_api_remaster.service;


import com.example.gift_api_remaster.exception.GiftApiException;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.Gift;
import com.example.gift_api_remaster.model.command.CreateGiftCommand;
import com.example.gift_api_remaster.model.command.UpdateGiftCommand;
import com.example.gift_api_remaster.model.dto.GiftDto;
import com.example.gift_api_remaster.model.mapper.GiftMapper;
import com.example.gift_api_remaster.repository.ChildRepository;
import com.example.gift_api_remaster.repository.GiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftService {

    private final GiftRepository giftRepository;
    private final ChildRepository childRepository;

    public List<GiftDto> findAllByChildId(long childId) {
        return giftRepository.findAllByChildId(childId).stream()
                .map(GiftMapper::toDto)
                .toList();
    }

    public GiftDto findById(long id, long childId) {
        return giftRepository.findByIdAndChildId(id, childId)
                .map(GiftMapper::toDto)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Gift with id {0} not found", id)));
    }

    @Transactional
    public GiftDto saveGiftToChild(CreateGiftCommand command, long childId) {
        Child child = childRepository.findWithLockingById(childId)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Could not find child with id={0}", childId)));
        if (child.getGiftList().size() >= 3) {
            throw new GiftApiException("Child already has 3 gifts");
        }
        Gift gift = GiftMapper.toEntity(command);
        gift.setChild(child);
        //Gift savedGift = giftRepository.save(gift); ten sam przypadek, rowaowazne z return
        //return GiftMapper.toDto(savedGift);
        return GiftMapper.toDto(giftRepository.save(gift));
    }

    public GiftDto updateGift(UpdateGiftCommand command, long id, long childId) {
        Gift existingGift = giftRepository.findByIdAndChildId(id, childId)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Could not find gift with id={0} for child id={1}", id, childId)));
        GiftMapper.update(command, existingGift);
        //Gift updatedGift = giftRepository.save(existingGift); rownowazne tak samo jak jest w return
        //return GiftMapper.toDto(updatedGift);
        return GiftMapper.toDto(giftRepository.save(existingGift));
    }

    public void deleteById(long id, long childId) {
        if (!giftRepository.existsByIdAndChildId(id, childId)) {
            throw new GiftApiException(MessageFormat
                    .format("Could not find gift with id={0} for child id={1}", id, childId));
        }
        giftRepository.deleteById(id);
    }
}
