package com.example.gift_api_remaster.service.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GiftRegistrationJob {

    private final GiftRegistrationService giftRegistrationService;

    //@Scheduled(cron = "*/10 */10 * * * *")
    public void registerGifts() {
        giftRegistrationService.process();
    }
}
