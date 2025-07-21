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
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftService {

    private static final int BATCH_SIZE = 10_000;
    private static final String INSERT_GIFTS_QUERY = "INSERT INTO gift (name, price, child_id, version) VALUES %s";
    private static final String SINGLE_GIFT_PARAMS = "(\"%s\", \"%s\", \"%s\", 0),";

    private final JdbcTemplate jdbcTemplate;


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
        if (child.getGifts().size() >= 3) {
            throw new GiftApiException("Child already has 3 gifts");
        }
        Gift gift = GiftMapper.toEntity(command);
        gift.setChild(child);
        return GiftMapper.toDto(giftRepository.save(gift));
    }

    public GiftDto updateGift(UpdateGiftCommand command, long id, long childId) {
        Gift existingGift = giftRepository.findByIdAndChildId(id, childId)
                .orElseThrow(() -> new GiftApiException(MessageFormat
                        .format("Could not find gift with id={0} for child id={1}", id, childId)));
        GiftMapper.update(command, existingGift);
        return GiftMapper.toDto(giftRepository.save(existingGift));
    }

    public void deleteById(long id, long childId) {
        if (!giftRepository.existsByIdAndChildId(id, childId)) {
            throw new GiftApiException(MessageFormat
                    .format("Could not find gift with id={0} for child id={1}", id, childId));
        }
        giftRepository.deleteById(id);
    }

    public void importGiftsDB(MultipartFile file) {
        StringBuilder params = new StringBuilder();
        int counter = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                params.append(String.format(SINGLE_GIFT_PARAMS, parts[0], Double.parseDouble(parts[1]), Integer.parseInt(parts[2])));
                if (++counter % BATCH_SIZE == 0) {
                    int paramsLength = params.length();
                    params.delete(paramsLength - 1, paramsLength);
                    jdbcTemplate.batchUpdate(String.format(INSERT_GIFTS_QUERY, params));
                    log.info("Generated {} gifts", counter);
                    counter = 0;
                    params.setLength(0);
                }
            }
        } catch (Exception e) {
            throw new GiftApiException(MessageFormat.format("Error while parsing CSV file: {0}", e.getMessage()), e);
        }
    }
}
