package com.example.gift_api_remaster.service.registration;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.Gift;
import com.example.gift_api_remaster.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GiftRegistrationService {

    private static final int BATCH_SIZE = 100;
    private static final int TOTAL_CHILDREN = 2000000;

    private final ChildRepository childRepository;

    public void process() {
        log.info("processing gift registration");
        int page = 0;
        Page<Child> childrenPage;

        do {
            childrenPage = childRepository.findAll(PageRequest.of(page, BATCH_SIZE));
            for (Child child : childrenPage) {
                processChild(child);
            }
            page++;
        } while (childrenPage.hasNext());

        // chcemy wysłać informację (dla uproszczenia log) na temat wszystkich prezentów, które łapią się w kryteria (cena powyżej 100zł)
        // na temat każdego dziecka informacja ma zostać zarejestrowana tylko raz, na temat wszystkich jego prezentów jednocześnie.


    }

    public void processChild(Child child) {
        List<Gift> giftToProcess = child.getGifts()
                .stream()
                .filter(gift -> gift.getPrice() > 100)
                .toList();
        if (!giftToProcess.isEmpty()) {
            log.info("Gifts to process for child: {}", child.getName());
        }
    }

}
