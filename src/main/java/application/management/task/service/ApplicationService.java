package application.management.task.service;

import application.management.task.model.Application;
import application.management.task.model.History;
import application.management.task.model.State;
import application.management.task.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private HistoryService historyService;

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationById(BigInteger id) {
        return applicationRepository.findById(id).get();
    }

    public Application addApplication(Application application) {
        application.setState(State.CREATED);
        return applicationRepository.save(application);
    }

    public void verifyApplication(BigInteger id) {
        changeApplicationState(id, State.CREATED, State.VERIFIED);
    }

    public void acceptApplication(BigInteger id) {
        changeApplicationState(id, State.VERIFIED, State.ACCEPTED);
    }

    public void publishApplication(BigInteger id) {
        changeApplicationState(id, State.ACCEPTED, State.PUBLISHED);
    }

    private void changeApplicationState(BigInteger id, State currentState, State expectedState) {
        Application application = applicationRepository.findById(id).get();
        if (application.getState().equals(currentState)) {
            History history = History.builder().date(LocalDate.now()).oldState(currentState).applicationId(application.getId()).build();
            historyService.addHistory(history);
            application.setState(expectedState);
            applicationRepository.save(application);
        }
    }

    public void deleteApplication(BigInteger id, History historyReason) {
        Application application = applicationRepository.findById(id).get();
        if (application.getState().equals(State.CREATED)) {
            History history = History.builder().date(LocalDate.now()).oldState(State.DELETED).applicationId(application.getId()).resignReason(historyReason.getResignReason()).build();
            historyService.addHistory(history);
            applicationRepository.delete(application);
        }
    }

    public void rejectApplication(BigInteger id, History historyReason) {
        Application application = applicationRepository.findById(id).get();
        if (application.getState().equals(State.VERIFIED) || application.getState().equals(State.ACCEPTED)) {
            History history = History.builder().date(LocalDate.now()).oldState(State.REJECTED).applicationId(application.getId()).resignReason(historyReason.getResignReason()).build();
            historyService.addHistory(history);
            applicationRepository.delete(application);
        }
    }

    public void updateApplication(Application application){
        Application applicationToUpdate = applicationRepository.findById(application.getId()).get();
        if (applicationToUpdate.getState().equals(State.CREATED) || applicationToUpdate.getState().equals(State.VERIFIED)){
            applicationToUpdate.setContent(application.getContent());
            applicationToUpdate.setName(application.getName());
            applicationRepository.save(applicationToUpdate);
        }
    }
}
