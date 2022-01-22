package application.management.task.service;

import application.management.task.model.History;
import application.management.task.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    public void addHistory(History history){
        historyRepository.save(history);
    }

    public List<History> getAllHistory(){
        return historyRepository.findAll();
    }

    public History getHistoryById(BigInteger id){
        return historyRepository.findById(id).get();
    }

    public List<History> getHistoryOfApplication(BigInteger applicationId){
        return historyRepository.findAllByApplicationId(applicationId);
    }
}
