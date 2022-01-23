package application.management.task.web;

import application.management.task.model.Application;
import application.management.task.model.State;
import application.management.task.repository.ApplicationRepository;
import application.management.task.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        Application firstApplication = Application.builder().id(BigInteger.valueOf(12345)).state(State.CREATED).name("name").content("content").build();
        Application secondApplication = Application.builder().id(BigInteger.valueOf(67890)).state(State.CREATED).name("name").content("content").build();
        Application thirdApplication = Application.builder().id(BigInteger.valueOf(11111)).state(State.CREATED).name("nameThird").content("content").build();
        Application fourthApplication = Application.builder().id(BigInteger.valueOf(22222)).state(State.CREATED).name("name").content("contentFourth").build();
        applicationService.addApplication(firstApplication);
        applicationService.addApplication(secondApplication);
        applicationService.addApplication(thirdApplication);
        applicationService.addApplication(fourthApplication);
    }

    @Test
    public void contextLoads() {
        assertThat(applicationService).isNotNull();
        assertThat(applicationRepository).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void when_valid_input_then_returns_status_ok() throws Exception {
        Application application = Application.builder().name("name").content("content").build();

        mockMvc.perform(post("/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isCreated());
    }

    @Test
    void when_get_application_then_returns_proper_values() throws Exception {

        mockMvc.perform(get("/{id}", BigInteger.valueOf(12345)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("12345"))
                .andExpect(jsonPath("$.state").value("CREATED"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void when_verify_application_then_has_verified_status() throws Exception {

        mockMvc.perform(patch("/verify/{id}", BigInteger.valueOf(12345)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("12345"))
                .andExpect(jsonPath("$.state").value("VERIFIED"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void when_accept_application_then_has_accepted_status() throws Exception {

        mockMvc.perform(patch("/accept/{id}", BigInteger.valueOf(12345)));
        mockMvc.perform(patch("/verify/{id}", BigInteger.valueOf(12345)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("12345"))
                .andExpect(jsonPath("$.state").value("VERIFIED"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void when_publish_application_then_has_published_status() throws Exception {

        mockMvc.perform(patch("/verify/{id}", BigInteger.valueOf(12345)));
        mockMvc.perform(patch("/accept/{id}", BigInteger.valueOf(12345)));
        mockMvc.perform(patch("/publish/{id}", BigInteger.valueOf(12345)))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("12345"))
                .andExpect(jsonPath("$.state").value("PUBLISHED"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.content").value("content"));
    }

    @Test
    void when_not_valid_input_then_throws_error() throws Exception {
        Application application = Application.builder().content("content").build();

        mockMvc.perform(post("/add")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(application)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }


}