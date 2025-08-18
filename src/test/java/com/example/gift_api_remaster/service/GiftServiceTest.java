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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftServiceTest {

    @Spy
    private GiftRepository giftRepository;

    @Mock
    private ChildRepository childRepository;

    @InjectMocks
    private GiftService giftService;

    @Test
    void findById_happyPath_resultsInGiftBeingReturned() {
        //given
        long giftId = 3;
        long childId = 1;
        Child child = Child.builder()
                .id(childId)
                .build();
        Gift gift = Gift.builder()
                .id(giftId)
                .name("telefon")
                .price(33.3)
                .child(child)
                .build();
        when(giftRepository.findByIdAndChildId(giftId, childId)).thenReturn(Optional.of(gift));
        //when
        GiftDto result = giftService.findById(giftId, child.getId());
        //then
        verify(giftRepository).findByIdAndChildId(giftId, childId);
        assertEquals(gift.getId(), result.getId());
        assertEquals(gift.getName(), result.getName());
        assertEquals(gift.getPrice(), result.getPrice());
        assertEquals(gift.getChild().getId(), result.getChildId());
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
        List<Gift> giftList = List.of(gift, gift1);
        when(giftRepository.findAllByChildId(childId)).thenReturn(giftList);

        //when
        List<GiftDto> result = giftService.findAllByChildId(child.getId());

        //then
        verify(giftRepository).findAllByChildId(childId);
        assertEquals(giftList.size(), result.size());
        assertEquals(giftList.getFirst().getId(), result.getFirst().getId());
        assertEquals(giftList.getFirst().getName(), result.getFirst().getName());
        assertEquals(giftList.getFirst().getPrice(), result.getFirst().getPrice());
        assertEquals(giftList.getFirst().getChild().getId(), result.getFirst().getChildId());
    }

    @Test
    void deleteById_existing_deletesSuccessfully() {
        // Given
        long giftId = 1L;
        long childId = 1L;
        when(giftRepository.existsByIdAndChildId(giftId, childId)).thenReturn(true);
//        doNothing().when(giftRepository).deleteById(giftId);

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
                .gifts(Set.of())
                .build();

        Gift saved = Gift.builder()
                .id(5L)
                .name("Bike")
                .price(99.99)
                .child(child)
                .build();

        when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));
        when(giftRepository.save(any(Gift.class))).thenReturn(saved);

        // When
        GiftDto result = giftService.saveGiftToChild(command, childId);

        // Then
        verify(childRepository).findWithLockingById(childId);
        verify(giftRepository).save(any(Gift.class));
        assertEquals(5L, result.getId());
        assertEquals("Bike", result.getName());
        assertEquals(99.99, result.getPrice());
        assertEquals(childId, result.getChildId());
    }


    @Test
    void SaveGiftToChild_childNotFound_throwsException() {
        // Given
        long childId = 1L;
        CreateGiftCommand command = CreateGiftCommand.builder()
                .name("Bike")
                .price(99.99)
                .build();

        when(childRepository.findWithLockingById(childId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException ex = assertThrows(GiftApiException.class, () ->
                giftService.saveGiftToChild(command, childId)
        );
        assertTrue(ex.getMessage().contains("Could not find child with id=" + childId));
        verify(childRepository).findWithLockingById(childId);
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
                .gifts(Set.of(
                        new Gift(), new Gift(), new Gift() // already has 3 gifts
                ))
                .build();

        when(childRepository.findWithLockingById(childId)).thenReturn(Optional.of(child));

        // When / Then
        GiftApiException ex = assertThrows(GiftApiException.class, () ->
                giftService.saveGiftToChild(command, childId)
        );
        assertTrue(ex.getMessage().contains("Child already has 3 gifts"));
        verify(childRepository).findWithLockingById(childId);
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

    }
}