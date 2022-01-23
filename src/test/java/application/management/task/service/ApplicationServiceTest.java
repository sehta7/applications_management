package application.management.task.service;

import application.management.task.error.ApplicationNotFoundException;
import application.management.task.error.NoParameterException;
import application.management.task.error.NoReasonException;
import application.management.task.error.WrongStateException;
import application.management.task.model.Application;
import application.management.task.model.ErrorMessage;
import application.management.task.model.History;
import application.management.task.model.State;
import application.management.task.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;

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
    public void contextLoads(){
        assertThat(applicationService).isNotNull();
        assertThat(applicationRepository).isNotNull();
    }

    @Test
    public void when_save_application_should_returns_application() {
        Application application = Application.builder().state(State.CREATED).name("name").content("content").build();

        Application savedApplication = applicationService.addApplication(application);

        assertEquals(savedApplication.getId(), application.getId());
        assertEquals(savedApplication.getState(), application.getState());
        assertEquals(savedApplication.getName(), application.getName());
        assertEquals(savedApplication.getContent(), application.getContent());
    }

    @Test
    public void when_save_application_should_have_created_status() {
        Application application = Application.builder().name("name").content("content").build();

        Application savedApplication = applicationService.addApplication(application);

        assertEquals(savedApplication.getState(), State.CREATED);
    }

    @Test
    public void when_save_application_without_name_should_throws_exception() {
        Application application = Application.builder().content("content").build();

        Throwable exception = assertThrows(NoParameterException.class, () -> applicationService.addApplication(application));
        assertEquals(ErrorMessage.NO_PARAMETER.message, exception.getMessage());
    }

    @Test
    public void when_save_application_without_content_should_throws_exception() {
        Application application = Application.builder().name("name").build();

        Throwable exception = assertThrows(NoParameterException.class, () -> applicationService.addApplication(application));
        assertEquals(ErrorMessage.NO_PARAMETER.message, exception.getMessage());
    }

    @Test
    public void when_get_application_returns_proper_values() {
        Application foundApplication = applicationService.getApplicationById(BigInteger.valueOf(12345));

        assertEquals(State.CREATED, foundApplication.getState());
        assertEquals("name", foundApplication.getName());
        assertEquals("content", foundApplication.getContent());
    }

    @Test
    public void when_get_all_applications_returns_proper_number() {
        List<Application> applicationList = applicationService.getAllApplications();
        assertEquals(4, applicationList.size());
    }

    @Test
    public void when_verify_application_has_state_verified() {
        Application application = applicationService.verifyApplication(BigInteger.valueOf(12345));
        assertEquals(State.VERIFIED, application.getState());
    }

    @Test
    public void when_application_verified_in_wrong_state_throws_exception() {
        applicationService.verifyApplication(BigInteger.valueOf(12345));
        Throwable exception = assertThrows(WrongStateException.class, () -> applicationService.verifyApplication(BigInteger.valueOf(12345)));
        assertEquals(String.format(ErrorMessage.WRONG_STATE.message, State.CREATED), exception.getMessage());
    }

    @Test
    public void when_accept_applications_has_state_accepted() {
        applicationService.verifyApplication(BigInteger.valueOf(12345));
        Application application = applicationService.acceptApplication(BigInteger.valueOf(12345));
        assertEquals(State.ACCEPTED, application.getState());
    }

    @Test
    public void when_application_accepted_in_wrong_state_throws_exception() {
        Throwable exception = assertThrows(WrongStateException.class, () -> applicationService.acceptApplication(BigInteger.valueOf(12345)));
        assertEquals(String.format(ErrorMessage.WRONG_STATE.message, State.VERIFIED), exception.getMessage());
    }

    @Test
    public void when_publish_applications_has_state_published() {
        applicationService.verifyApplication(BigInteger.valueOf(12345));
        applicationService.acceptApplication(BigInteger.valueOf(12345));
        Application application = applicationService.publishApplication(BigInteger.valueOf(12345));
        assertEquals(State.PUBLISHED, application.getState());
    }

    @Test
    public void when_application_published_in_wrong_state_throws_exception() {
        Throwable exception = assertThrows(WrongStateException.class, () -> applicationService.publishApplication(BigInteger.valueOf(12345)));
        assertEquals(String.format(ErrorMessage.WRONG_STATE.message, State.ACCEPTED), exception.getMessage());
    }

    @Test
    public void when_get_application_filter_by_name_and_state_returns_proper(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> pages = applicationService.getAllPages(pageable, "name", State.CREATED);
        assertEquals(3, pages.getTotalElements());
    }

    @Test
    public void when_get_application_filter_by_name_returns_proper(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> pages = applicationService.getAllPages(pageable, "name", null);
        assertEquals(3, pages.getTotalElements());
    }

    @Test
    public void when_get_application_filter_by_state_returns_proper(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Application> pages = applicationService.getAllPages(pageable, null, State.CREATED);
        assertEquals(4, pages.getTotalElements());
    }

    @Test
    public void when_delete_application_throws_exception_for_its_id(){
        applicationService.deleteApplication(BigInteger.valueOf(12345), History.builder().resignReason("reason").build());
        Throwable exception = assertThrows(ApplicationNotFoundException.class, () -> applicationService.getApplicationById(BigInteger.valueOf(12345)));
        assertEquals(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, "12345"), exception.getMessage());
    }

    @Test
    public void when_delete_application_without_reason_throws_exception(){
        Throwable exception = assertThrows(NoReasonException.class, () -> applicationService.deleteApplication(BigInteger.valueOf(12345), new History()));
        assertEquals(ErrorMessage.NO_REASON.message, exception.getMessage());
    }

    @Test
    public void when_reject_application_throws_exception_for_its_id(){
        applicationService.verifyApplication(BigInteger.valueOf(12345));
        applicationService.rejectApplication(BigInteger.valueOf(12345), History.builder().resignReason("reason").build());
        Throwable exception = assertThrows(ApplicationNotFoundException.class, () -> applicationService.getApplicationById(BigInteger.valueOf(12345)));
        assertEquals(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, "12345"), exception.getMessage());
    }

    @Test
    public void when_reject_application_without_reason_throws_exception(){
        applicationService.verifyApplication(BigInteger.valueOf(12345));
        Throwable exception = assertThrows(NoReasonException.class, () -> applicationService.rejectApplication(BigInteger.valueOf(12345), new History()));
        assertEquals(ErrorMessage.NO_REASON.message, exception.getMessage());
    }
}