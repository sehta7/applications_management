package application.management.task.web;

import application.management.task.model.Application;
import application.management.task.model.History;
import application.management.task.model.State;
import application.management.task.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Application>> getAllApplications(){
        List<Application> allApplications = applicationService.getAllApplications();
        return ResponseEntity.ok(allApplications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplication(@PathVariable BigInteger id){
        Application application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    @PostMapping("/add")
    public ResponseEntity<Application> addApplication(@RequestBody Application application){
        Application createdApplication = applicationService.addApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
    }

    @PatchMapping("/verify/{id}")
    public ResponseEntity<Application> verifyApplication(@PathVariable BigInteger id){
        Application verifiedApplication = applicationService.verifyApplication(id);
        return ResponseEntity.ok(verifiedApplication);
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<Application> acceptApplication(@PathVariable BigInteger id){
        Application acceptedApplication = applicationService.acceptApplication(id);
        return ResponseEntity.ok(acceptedApplication);
    }

    @PatchMapping("/publish/{id}")
    public ResponseEntity<Application> publishApplication(@PathVariable BigInteger id){
        Application publishedApplication = applicationService.publishApplication(id);
        return ResponseEntity.ok(publishedApplication);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Application> deleteApplication(@PathVariable BigInteger id, @RequestBody History history){
        Application deletedApplication = applicationService.deleteApplication(id, history);
        return ResponseEntity.ok(deletedApplication);
    }

    @DeleteMapping("/reject/{id}")
    public ResponseEntity<Application> rejectApplication(@PathVariable BigInteger id, @RequestBody History history){
        Application rejectedApplication = applicationService.rejectApplication(id, history);
        return ResponseEntity.ok(rejectedApplication);
    }

    @PutMapping("/update")
    public ResponseEntity<Application> updateApplication(@RequestBody Application application){
        Application updatedApplication = applicationService.updateApplication(application);
        return ResponseEntity.ok(updatedApplication);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<Application>> getAllPages(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size,
                                                         @RequestParam(required = false) String name, @RequestParam(required = false) State state) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10);
        Page<Application> applicationPages = applicationService.getAllPages(pageable, name, state);
        return ResponseEntity.ok(applicationPages);
    }
}
