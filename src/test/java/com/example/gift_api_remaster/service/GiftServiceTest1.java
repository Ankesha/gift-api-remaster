package com.example.gift_api_remaster.service;

import com.example.gift_api_remaster.exception.GiftApiException;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.Gift;
import com.example.gift_api_remaster.model.command.CreateGiftCommand;
import com.example.gift_api_remaster.model.command.UpdateGiftCommand;
import com.example.gift_api_remaster.model.dto.GiftDto;
import com.example.gift_api_remaster.repository.ChildRepository;
import com.example.gift_api_remaster.repository.GiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest1 {

    @Spy
    private GiftRepository giftRepository;

    @Mock
    private ChildRepository childRepository;

    @InjectMocks
    private GiftService giftService;

    @Test
    void findById_happyPath_resultsInGiftBeingReturned() {
        Gift gift = new Gift();
        int giftId = 1;
        int childId = 1;
        Child child = new Child();
        child.setId(childId);
        gift.setChild(child);


        when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.of(gift));
        GiftDto result = giftService.findById(child.getId(), giftId);

        assertNotNull(result);
        verify(giftRepository).findByIdAndChildId(giftId, childId);
        //assertEquals(giftId, result.getId());
    }

    @Test
    void findAll_happyPath_resultInListBeingReturned() {
        //given
        long childId = 1;
        Child child = Child.builder()
                .id(childId)
                .name("Mati")
                .surname("Kowal")
                .build();
        Gift gift = Gift.builder()
                .name("telefon")
                .price(33.3)
                .child(child)
                .build();
        Gift gift1 = Gift.builder()
                .name("Pilka")
                .price(55.5)
                .child(child)
                .build();

        Set<Gift> gifts = Set.of(gift, gift1);
        child.setGiftList(gifts);
        List<Gift> giftList = new ArrayList<>(gifts);

        // when(childRepository.findById(childId)).thenReturn(Optional.of(child));
        when(giftRepository.findAllByChildId(childId)).thenReturn(giftList);
        //when
        List<GiftDto> result = giftService.findAllByChildId(child.getId());

        assertNotNull(result);
        assertEquals(giftList.size(), result.size());
        verify(giftRepository).findAllByChildId(childId);
    }

    @Test
    void deleteById_existing_deletesSuccessfully() {
        // Given
        long giftId = 1L;
        long childId = 1L;
        when(giftRepository.existsByIdAndChildId(giftId, childId)).thenReturn(true);
        doNothing().when(giftRepository).deleteById(giftId);

        // When / Then
        assertDoesNotThrow(() -> giftService.deleteById(giftId, childId));

        // Then
        verify(giftRepository).existsByIdAndChildId(giftId, childId);
        verify(giftRepository).deleteById(giftId);
    }

    @Test
    void saveGiftToChild_happyPath_returnsDto() {
        // Given
        long childId = 1L;
        CreateGiftCommand command = CreateGiftCommand.builder()
                .name("Bike")
                .price(99.99)
                .build();

        Child child = Child.builder()
                .id(childId)
                .giftList(Set.of())
                .build();

        Gift toSave = new Gift();
        toSave.setName("Bike");
        toSave.setPrice(99.99);
        toSave.setChild(child);

        Gift saved = Gift.builder()
                .id(5L)
                .name("Bike")
                .price(99.99)
                .child(child)
                .build();

        when(childRepository.findById(childId)).thenReturn(Optional.of(child));
        when(giftRepository.save(any(Gift.class))).thenReturn(saved);

        // When
        GiftDto result = giftService.saveGiftToChild(command, childId);

        // Then
        verify(childRepository).findById(childId);
        verify(giftRepository).save(any(Gift.class));
        assertEquals(5L, result.getId());
        assertEquals("Bike", result.getName());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    void SaveGiftToChild_childNotFound_throwsException() {
        // Given
        long childId = 1L;
        CreateGiftCommand command = CreateGiftCommand.builder()
                .name("Bike")
                .price(99.99)
                .build();

        when(childRepository.findById(childId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException ex = assertThrows(GiftApiException.class, () ->
                giftService.saveGiftToChild(command, childId)
        );
        assertTrue(ex.getMessage().contains("Could not find child with id=" + childId));
        verify(childRepository).findById(childId);
    }

    @Test
    void saveGiftToChild_childAlreadyHasThreeGifts_throwsException() {
        // Given
        long childId = 1L;
        CreateGiftCommand command = CreateGiftCommand.builder()
                .name("Bike")
                .price(99.99)
                .build();

        Child child = Child.builder()
                .id(childId)
                .giftList(Set.of(
                        new Gift(), new Gift(), new Gift() // already has 3 gifts
                ))
                .build();

        when(childRepository.findById(childId)).thenReturn(Optional.of(child));

        // When / Then
        GiftApiException ex = assertThrows(GiftApiException.class, () ->
                giftService.saveGiftToChild(command, childId)
        );
        assertTrue(ex.getMessage().contains("Child already has 3 gifts"));
        verify(childRepository).findById(childId);
    }

    @Test
    void updateGift_happyPath_returnsUpdatedDto() {
        // Given
        long giftId = 1L;
        long childId = 1L;
        UpdateGiftCommand command = UpdateGiftCommand.builder()
                .name("Updated Gift")
                .price(50.0)
                .build();

        Gift existingGift = Gift.builder()
                .id(giftId)
                .name("Old Gift")
                .price(30.0)
                .child(Child.builder().id(childId).build())
                .build();

        when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.of(existingGift));
        when(giftRepository.save(any(Gift.class))).thenReturn(existingGift);

        // When
        GiftDto result = giftService.updateGift(command, giftId, childId);

        // Then
        verify(giftRepository).findByIdAndChildId(giftId, childId);
        verify(giftRepository).save(any(Gift.class));
        assertEquals("Updated Gift", result.getName());
        assertEquals(50.0, result.getPrice());
    }

    @Test
    void deleteById_nonExisting_throwsException() {
        //given
        long giftId = 1L;
        long childId = 2L;

        when(giftRepository.existsByIdAndChildId(giftId, childId)).thenReturn(false);

        GiftApiException ex = assertThrows(GiftApiException.class,
                () -> giftService.deleteById(giftId, childId));
        assertTrue(ex.getMessage().contains("Could not find gift with id=" + giftId + " for child id=" + childId));

        verify(giftRepository).existsByIdAndChildId(giftId, childId);
        verify(giftRepository, never()).deleteById(giftId);
    }
}