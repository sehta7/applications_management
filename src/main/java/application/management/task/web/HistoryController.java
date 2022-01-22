package application.management.task.web;

import application.management.task.model.History;
import application.management.task.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private HistoryService historyService;

    @Autowired
    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/{id}")
    public History getHistory(@PathVariable BigInteger id){
        return historyService.getHistoryById(id);
    }

    @GetMapping("/application/{id}")
    public List<History> getHistoryOfApplication(@PathVariable BigInteger id){
        return historyService.getHistoryOfApplication(id);
    }

    @GetMapping("/all")
    public List<History> getAll(){
        return historyService.getAllHistory();
    }
}
