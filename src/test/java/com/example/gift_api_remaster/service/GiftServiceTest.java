package com.example.gift_api_remaster.service;

import com.example.gift_api_remaster.exception.GiftApiException;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.command.CreateChildCommand;
import com.example.gift_api_remaster.model.command.UpdateChildCommand;
import com.example.gift_api_remaster.model.dto.ChildDto;
import com.example.gift_api_remaster.repository.ChildRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChildServiceTest {

    @Mock
    private ChildRepository childRepository;

    //@Spy // Bedzie sie zachowuwac jak mock, ale pozwoli na wywolywanie rzeczy z klasy ChildMapp

    @InjectMocks
    private ChildService childService;


    @Test
    void findById_happyPath_resultsInChildBeingReturned() {
        // Given jest to prszygotowanie danych, ktore beda uzywane w teście
        long childId = 1;
        Child fromRepository = Child.builder()
                .id(childId)
                .name("Mati")
                .surname("Kowalski")
                .birthday(java.time.LocalDate.of(2020, 1, 1))
                .build();
        when(childRepository.findById(childId)).thenReturn(Optional.of(fromRepository));

        // When jest to wywołanie metody, która ma być testowana
        ChildDto result = childService.findById(childId);

        // Then jest to sprawdzenie, czy wynik jest zgodny z oczekiwaniami
        verify(childRepository).findById(childId);
        assertEquals(childId, result.getId());
        assertEquals(fromRepository.getName(), result.getName());
        assertEquals(fromRepository.getSurname(), result.getSurname());
        assertEquals(fromRepository.getBirthday(), result.getBirthday());
    }

//    @Test
//    void findAll_happyPath_resultInListBeingReturned() {
//        // Given
//        Child child1 = Child.builder()
//                .id(1L)
//                .name("Tomek")
//                .surname("kowal")
//                .birthday(java.time.LocalDate.of(2020, 1, 1))
//                .build();
//        Child child2 = Child.builder()
//                .id(2L)
//                .name("Janek")
//                .surname("Bobek")
//                .birthday(java.time.LocalDate.of(2021, 1, 1))
//                .build();
//
//        List<Child> childrenList = List.of(child1, child2);
//
//        when(childRepository.findAll()).thenReturn(childrenList);
//
//        //when
//        List<ChildDto> result = childService.findAll();
//
//        // Then
//        verify(childRepository).findAll();
//        assertEquals(result.size(), childrenList.size());
//        //generalnie przy fidnAll sprawdzic mozmey czy referencje do obiektow, sa zgodne z tymi, ktore mamy w repozytorium
//
//    }

    @Test
    void update_happyPath_updatesAndReturnsDto() {
        // Given
        long childId = 1L;
        Child existingChild = Child.builder()
                .id(childId)
                .name("OldName")
                .surname("OldSurname")
                .birthday(LocalDate.of(2015, 5, 5))
                .build();
        UpdateChildCommand command = UpdateChildCommand.builder()
                .name("NewName")
                .surname("NewSurname")
                .birthday(LocalDate.of(2016, 6, 6))
                .build();

        when(childRepository.findById(childId)).thenReturn(Optional.of(existingChild));
        when(childRepository.save(existingChild)).thenReturn(existingChild);

        // When
        ChildDto result = childService.update(command, childId);

        // Then
        verify(childRepository).findById(childId);
        verify(childRepository).save(existingChild);
        assertEquals(childId, result.getId());
        assertEquals("NewName", result.getName());
        assertEquals("NewSurname", result.getSurname());
        assertEquals(LocalDate.of(2016, 6, 6), result.getBirthday());
    }
    @Test
    void update_nonExistingChild_throwsException() {
        // Given
        long childId = 42L;
        UpdateChildCommand command = UpdateChildCommand.builder()
                .name("Name")
                .surname("Surname")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();

        when(childRepository.findById(childId)).thenReturn(Optional.empty());

        // When / Then
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                childService.update(command, childId)
        );
        assertTrue(ex.getMessage().contains("Child with id = " + childId + " not found"));
        verify(childRepository).findById(childId);
        verify(childRepository, never()).save(any());
    }
    @Test
    void save_happyPath_ReturnsDto() {
        // Given
        long childId = 1L;
        CreateChildCommand command = CreateChildCommand.builder()
                .name("Anna")
                .surname("Nowak")
                .birthday(LocalDate.of(2018, 3, 3))
                .build();
        Child savedChild = Child.builder()
                .id(childId)
                .name("Anna")
                .surname("Nowak")
                .birthday(LocalDate.of(2018, 3, 3))
                .build();
        when(childRepository.save(any(Child.class))).thenReturn(savedChild);

        // When
        ChildDto result = childService.save(command);

        // Then
        verify(childRepository).save(any(Child.class));
        assertEquals(childId, result.getId());
        assertEquals("Anna", result.getName());
        assertEquals("Nowak", result.getSurname());
        assertEquals(LocalDate.of(2018, 3, 3), result.getBirthday());
    }
    @Test
    void deleteById_existing_deletesSuccessfully() {
        // Given
        long childId = 2L;
        when(childRepository.existsById(childId)).thenReturn(true);
        doNothing().when(childRepository).deleteById(childId);

        // When
        assertDoesNotThrow(() -> childService.deleteById(childId));

        // Then
        verify(childRepository).existsById(childId);
        verify(childRepository).deleteById(childId);
    }
    @Test
    void deleteById_nonExisting_throwsException() {
        // Given
        long childId = 99L;
        when(childRepository.existsById(childId)).thenReturn(false);

        // When / Then
        GiftApiException ex = assertThrows(GiftApiException.class, () ->
                childService.deleteById(childId)
        );
        assertTrue(ex.getMessage().contains("Child with id " + childId + " not found"));
        verify(childRepository).existsById(childId);
        verify(childRepository, never()).deleteById(anyLong());
    }


}