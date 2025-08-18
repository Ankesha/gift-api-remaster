package com.example.gift_api_remaster.service;

import com.example.gift_api_remaster.controller.ChildController;
import com.example.gift_api_remaster.model.Boy;
import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.repository.ChildRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
//                .andExpect(jsonPath("$.content.[0].pipeLength").value(boy.getPipeLength()))
                .andExpect(jsonPath("$.content.[0].giftsCount").value(0))
        ;


    }

}
