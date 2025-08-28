package com.example.gift_api_remaster.service;

import com.example.gift_api_remaster.model.Boy;
import com.example.gift_api_remaster.repository.ChildRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChildControllerTest {

    @Autowired
    private MockMvc mockMvc;
    // mockMvc to progrmistyczny postman, za pomoca mockMvc mozemy wyslac(mockowac) requesty https na dowlny adres,
    // pozwala zweryfikowac strukture odpowiedzi

    @Autowired
    private ChildRepository childRepository;



    @Test
    void shouldReturnChildren() throws Exception {
        // Given
        Boy boy = childRepository.save(Boy.builder()
                .name("Mati")
                .surname("Kowal")
                .birthday(LocalDate.now().minusYears(5))
                .pipeLength(25.0)
                .build());

        // When
        mockMvc.perform(get("/api/v1/children"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content.[0].id").value(boy.getId()))
                .andExpect(jsonPath("$.content.[0].name").value(boy.getName()))
                .andExpect(jsonPath("$.content.[0].surname").value(boy.getSurname()))
                .andExpect(jsonPath("$.content.[0].birthday").value(boy.getBirthday().toString()))
                .andExpect(jsonPath("$.content.[0].pipeLength").value(boy.getPipeLength()))
                .andExpect(jsonPath("$.content.[0].giftsCount").value(0));
    }

    @Test
    void shouldReturnEmptyPageWhenNoChildren() throws Exception {
        // Given
        childRepository.deleteAll();

        // When
        mockMvc.perform(get("/api/v1/children"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

    }

    @Test
    void shouldRespectPaginationParameters() throws Exception {
        // Given
        childRepository.save(Boy.builder()
                .name("Mati")
                .surname("Kowal")
                .birthday(LocalDate.now().minusYears(7))
                .pipeLength(20.0)
                .build());
        childRepository.save(Boy.builder()
                .name("Kuba")
                .surname("Nowak")
                .birthday(LocalDate.now().minusYears(6))
                .pipeLength(22.5)
                .build());
        childRepository.save(Boy.builder()
                .name("Olek")
                .surname("Zieliński")
                .birthday(LocalDate.now().minusYears(5))
                .pipeLength(18.0)
                .build());

        // When & Then: strona 0, rozmiar 2
        mockMvc.perform(get("/api/v1/children?page=0&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        // When & Then: strona 1, rozmiar 2 -> powinien zostać 1 rekord
        mockMvc.perform(get("/api/v1/children?page=1&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }
    @Test
    void shouldNotExposeSubclassSpecificFieldsInList() throws Exception {
        // Given
        childRepository.save(Boy.builder()
                .name("Antek")
                .surname("Wiśniewski")
                .birthday(LocalDate.now().minusYears(8))
                .pipeLength(17.3)
                .build());

        // When & Then: upewniamy się, że pole charakterystyczne dla Boy nie "wycieka" w kontrakcie listy
        mockMvc.perform(get("/api/v1/children"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content.[0].pipeLength").doesNotExist());
    }



}
