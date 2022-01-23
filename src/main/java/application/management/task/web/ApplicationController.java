package application.management.task.web;

import application.management.task.model.Application;
import application.management.task.model.History;
import application.management.task.model.State;
import application.management.task.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController()
public class ApplicationController {

    private ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/all")
    public List<Application> getAllApplications(){
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public Application getApplication(@PathVariable BigInteger id){
        return applicationService.getApplicationById(id);
    }

    @PostMapping("/add")
    public Application addApplication(@RequestBody Application application){
        return applicationService.addApplication(application);
    }

    @PatchMapping("/verify/{id}")
    public void verifyApplication(@PathVariable BigInteger id){
        applicationService.verifyApplication(id);
    }

    @PatchMapping("/accept/{id}")
    public void acceptApplication(@PathVariable BigInteger id){
        applicationService.acceptApplication(id);
    }

    @PatchMapping("/publish/{id}")
    public void publishApplication(@PathVariable BigInteger id){
        applicationService.publishApplication(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteApplication(@PathVariable BigInteger id, @RequestBody History history){
        applicationService.deleteApplication(id, history);
    }

    @DeleteMapping("/reject/{id}")
    public void rejectApplication(@PathVariable BigInteger id, @RequestBody History history){
        applicationService.rejectApplication(id, history);
    }

    @PutMapping("/update")
    public void updateApplication(@RequestBody Application application){
        applicationService.updateApplication(application);
    }

    @GetMapping("/get")
    public Page<Application> getAllPages(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                    @RequestParam(required = false) String name, @RequestParam(required = false) State state) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        return applicationService.getAllPages(pageable, name, state);
    }
}
