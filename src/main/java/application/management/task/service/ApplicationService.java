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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationById(BigInteger id) {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, id)));
    }

    public Application addApplication(Application application) {
        application.setState(State.CREATED);
        if ((application.getName() != null && !application.getName().isBlank()) && (application.getContent() != null && !application.getContent().isBlank())){
            return applicationRepository.save(application);
        } else {
            throw new NoParameterException(ErrorMessage.NO_PARAMETER.message);
        }
    }

    public Application verifyApplication(BigInteger id) {
        return changeApplicationState(id, State.CREATED, State.VERIFIED);
    }

    public Application acceptApplication(BigInteger id) {
        return changeApplicationState(id, State.VERIFIED, State.ACCEPTED);
    }

    public Application publishApplication(BigInteger id) {
        return changeApplicationState(id, State.ACCEPTED, State.PUBLISHED);
    }

    private Application changeApplicationState(BigInteger id, State currentState, State expectedState) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, id)));
        if (application.getState().equals(currentState)) {
            History history = History.builder().date(LocalDate.now()).oldState(currentState).applicationId(application.getId()).build();
            historyService.addHistory(history);
            application.setState(expectedState);
            applicationRepository.save(application);
            return application;
        } else {
            throw new WrongStateException(String.format(ErrorMessage.WRONG_STATE.message, currentState));
        }
    }

    public Application deleteApplication(BigInteger id, History historyReason) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, id)));
        if (application.getState().equals(State.CREATED)) {
            if (historyReason.getResignReason() != null && !historyReason.getResignReason().isBlank()){
                History history = History.builder().date(LocalDate.now()).oldState(State.DELETED).applicationId(application.getId()).resignReason(historyReason.getResignReason()).build();
                historyService.addHistory(history);
                applicationRepository.delete(application);
                return application;
            } else {
                throw new NoReasonException(ErrorMessage.NO_REASON.message);
            }
        } else {
            throw new WrongStateException(String.format(ErrorMessage.WRONG_STATE.message, State.CREATED));
        }
    }

    public Application rejectApplication(BigInteger id, History historyReason) {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, id)));
        if (application.getState().equals(State.VERIFIED) || application.getState().equals(State.ACCEPTED)) {
            if (historyReason.getResignReason() != null && !historyReason.getResignReason().isBlank()) {
                History history = History.builder().date(LocalDate.now()).oldState(State.REJECTED).applicationId(application.getId()).resignReason(historyReason.getResignReason()).build();
                historyService.addHistory(history);
                applicationRepository.delete(application);
                return application;
            } else {
                throw new NoReasonException(ErrorMessage.NO_REASON.message);
            }
        } else {
            throw new WrongStateException(String.format(ErrorMessage.WRONG_STATE.message, Arrays.asList(State.VERIFIED, State.ACCEPTED)));
        }
    }

    public Application updateApplication(Application application){
        Application applicationToUpdate = applicationRepository.findById(application.getId()).orElseThrow(() -> new ApplicationNotFoundException(String.format(ErrorMessage.ENTITY_NOT_EXIST.message, application.getId())));
        if (applicationToUpdate.getState().equals(State.CREATED) || applicationToUpdate.getState().equals(State.VERIFIED)){
            if (application.getContent() != null){
                applicationToUpdate.setContent(application.getContent());
            }
            if (application.getName() != null){
                applicationToUpdate.setName(application.getName());
            }
            applicationRepository.save(applicationToUpdate);
            return applicationToUpdate;
        } else {
            throw new WrongStateException(String.format(ErrorMessage.WRONG_STATE.message, Arrays.asList(State.CREATED, State.VERIFIED)));
        }
    }

    public Page<Application> getAllPages(Pageable pageable, String name, State state){
        Query query = new Query().with(pageable);
        final List<Criteria> criteria = new ArrayList<>();

        if (name != null && !name.isBlank())
            criteria.add(Criteria.where("name").is(name));
        if (state != null)
            criteria.add(Criteria.where("state").is(state));
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Application.class),
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), Application.class)
        );
    }
}
