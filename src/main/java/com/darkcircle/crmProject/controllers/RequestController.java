package com.darkcircle.crmProject.controllers;

import com.darkcircle.crmProject.enums.Roles;
import com.darkcircle.crmProject.enums.WorkList;
import com.darkcircle.crmProject.mail.MailNotification;
import com.darkcircle.crmProject.models.Request;
import com.darkcircle.crmProject.models.User;
import com.darkcircle.crmProject.repositories.RequestRepository;
import com.darkcircle.crmProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    ////////////////////////USER////////////////////////

    ////////////////////////USER_REQUEST////////////////////////

    @GetMapping("user/{id}/request/success")
    public String clientRequestSuccess(@PathVariable(value = "id") Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);

        return "client_request_success_page";
    }

    @GetMapping("user/{id}/request")
    public String clientRequestAdd(@PathVariable(value = "id") Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "wrong_user";
        }
        model.addAttribute("usr", user);

        return "client_request_page";
    }

    @PostMapping("user/{id}/request")
    public String clientRequestAdd(@PathVariable(value = "id") Long id,
                                   @RequestParam String workType,
                                   @RequestParam String workList,
                                   @RequestParam String addInfo,
                                   Model model) {

        User user = userRepository.findById(id).orElseThrow();
        String name = user.getName();
        String company = user.getCompany();

        String requestStatus = "Новая";
        String responsible = setRequestResponsible(workList);
        String workDuration = null;
        Date requestDate = new Date();

        Request request = new Request(name, company, workType, addInfo, requestStatus, responsible, workList, workDuration, requestDate);
        requestRepository.save(request);

        return "redirect:/user/{id}/request/success";
    }

    ////////////////////////USER_ALL_REQUESTS////////////////////////

    @GetMapping("user/{id}/request/all")
    public String clientAllRequests(@PathVariable(value = "id") Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);


        Pageable page = PageRequest.of(0, 20 , Sort.by("requestDate").descending() );
        String name = user.getName();
        Iterator requestIterator = requestRepository.findByName(name, page).iterator();
        ArrayList<Request> newRequests = new ArrayList<Request>();
        ArrayList<Request> inProgressRequests = new ArrayList<Request>();
        ArrayList<Request> finishedRequests = new ArrayList<Request>();
        while (requestIterator.hasNext()) {
            Request request = (Request) requestIterator.next();
            if (request.getRequestStatus().equals("Новая")) {
                newRequests.add(request);
            }
            if (request.getRequestStatus().equals("В работе")) {
                inProgressRequests.add(request);
            }
            if (request.getRequestStatus().equals("Заявка выполнена")) {
                finishedRequests.add(request);
            }
        }
        newRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        inProgressRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        finishedRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        model.addAttribute("newRequests", newRequests);
        model.addAttribute("inProgressRequests", inProgressRequests);
        model.addAttribute("finishedRequests", finishedRequests);
        
        return "client_all_requests_page";
    }

    ////////////////////////USER_REQUEST_DETAILS////////////////////////

    @GetMapping("user/{id}/request/{request_id}")
    public String clientRequestDetails(@PathVariable(value = "id") Long id, @PathVariable(value = "request_id") long request_id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);

        if (!requestRepository.existsById(request_id)) {
            return "redirect:/user/{id}/request/all";
        }

        Optional<Request> request = requestRepository.findById(request_id);
        ArrayList<Request> res = new ArrayList<>();
        request.ifPresent(res::add);
        model.addAttribute("request", res);
        return "client_request_details_page";
    }

    ////////////////////////SPECIALIST////////////////////////

    @GetMapping("specialist/{id}/request")
    public String specialistRequestAdd(@PathVariable(value = "id") Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "wrong_user";
        }
        model.addAttribute("usr", user);

        return "specialist_request_page";
    }

    @PostMapping("specialist/{id}/request")
    public String specialistRequestAdd(@PathVariable(value = "id") Long id,
                                       @RequestParam String name,
                                       @RequestParam String company,
                                       @RequestParam String workType,
                                       @RequestParam String workList,
                                       @RequestParam String addInfo,
                                       Model model) {

        User user = userRepository.findById(id).orElseThrow();

        String requestStatus = "Новая";
        String responsible = setRequestResponsible(workList);
        String workDuration = null;
        Date requestDate = new Date();

        Request request = new Request(name, company, workType, addInfo, requestStatus, responsible, workList, workDuration, requestDate);
        requestRepository.save(request);


        return "redirect:/specialist/{id}/request/all";
    }

    ////////////////////////SPECIALIST_ALL_REQUESTS////////////////////////

    @GetMapping("specialist/{id}/request/all")
    public String specialistAllRequests(@PathVariable(value = "id") Long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);


        Iterator<Request> requestIterator = requestRepository.findAll().iterator();
        ArrayList<Request> newRequests = new ArrayList<Request>();
        ArrayList<Request> inProgressRequests = new ArrayList<Request>();
        ArrayList<Request> finishedRequests = new ArrayList<Request>();
        while (requestIterator.hasNext()) {
            Request request = (Request) requestIterator.next();
            if (request.getRequestStatus().equals("Новая")) {
                newRequests.add(request);
            }
            if (request.getRequestStatus().equals("В работе")) {
                inProgressRequests.add(request);
            }
            if (request.getRequestStatus().equals("Заявка выполнена")) {
                finishedRequests.add(request);
            }
        }
        newRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        inProgressRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        finishedRequests.sort((o1, o2) -> o2.getRequestDate().compareTo(o1.getRequestDate()));
        model.addAttribute("newRequests", newRequests);
        model.addAttribute("inProgressRequests", inProgressRequests);
        model.addAttribute("finishedRequests", finishedRequests);

        return "specialist_all_requests_page";
    }

    ////////////////////////SPECIALIST_REQUEST_DETAILS////////////////////////

    @GetMapping("specialist/{id}/request/{request_id}")
    public String specialistRequestDetails(@PathVariable(value = "id") Long id, @PathVariable(value = "request_id") long request_id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);

        if (!requestRepository.existsById(request_id)) {
            return "redirect:/specialist/{id}/request/all";
        }

        Optional<Request> request = requestRepository.findById(request_id);
        ArrayList<Request> res = new ArrayList<>();
        request.ifPresent(res::add);
        model.addAttribute("request", res);

        return "specialist_request_details_page";
    }

    ////////////////////////SPECIALIST_REQUEST_EDIT////////////////////////

    @GetMapping("specialist/{id}/request/{request_id}/edit")
    public String specialistRequestEdit(@PathVariable(value = "request_id") long request_id, @PathVariable(value = "id") long id, Model model) {

        User user = userRepository.findById(id).orElseThrow();
        if (!userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().equals(user)) {
            return "error";
        }
        model.addAttribute("usr", user);

        HashSet<String> specialists = new HashSet<>();
        for (User specialist : userRepository.findAll()) {
            if(specialist.getRoles().contains(Roles.ADMIN) || specialist.getRoles().contains(Roles.SPECIALIST))
            {specialists.add(specialist.getName());}
        }
        model.addAttribute("specialists", specialists);

        if (!requestRepository.existsById(request_id)) {
            return "redirect:/specialist/{id}/request/all";
        }

        Optional<Request> request = requestRepository.findById(request_id);
        ArrayList<Request> res = new ArrayList<>();
        request.ifPresent(res::add);
        model.addAttribute("request", res);

        return "specialist_request_edit_page";
    }

    @PostMapping("specialist/{id}/request/{request_id}/edit")
    public String specialistRequestUpdate(@PathVariable(value = "request_id") long request_id, @PathVariable(value = "id") long id,
                                          @RequestParam String requestStatus,
                                          @RequestParam String workDuration,
                                          @RequestParam String responsible,
                                          Model model) {

        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("usr", user);

        Request request = requestRepository.findById(request_id).orElseThrow();

        request.setRequestStatus(requestStatus);
        request.setWorkDuration(workDuration);
        request.setResponsible(responsible);

        requestRepository.save(request);

        return "specialist_request_success_page";
    }


    ////////////////////////Общие страницы////////////////////////

    @GetMapping("/request")
    public String createRequest (Model model){
        model.addAttribute("usr", userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow());

        return "request_page";
    }

    @GetMapping("/request/all/{page_id}")
    public String showAllRequests (@PathVariable(value = "page_id") int page_id, Model model) {

        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        int nextPageInt = page_id + 1;
        String nextPage = String.valueOf(nextPageInt);
        model.addAttribute("nextPage", nextPage);

        Pageable page = PageRequest.of(page_id, 4 , Sort.by("requestDate").descending() );

        if (user.getRoles().contains(Roles.ADMIN)) {
            Page<Request> requests = requestRepository.findAll(page);
            model.addAttribute("requests", requests);
        } else  if (user.getRoles().contains(Roles.SPECIALIST)) {
            List<Request> requests = requestRepository.findByResponsible(user.getName(), page);
            model.addAttribute("requests", requests);
        } else {
            List<Request> requests = requestRepository.findByName(user.getName(), page);
            model.addAttribute("requests", requests);
        }

        return "request_all_page";
    }


    public void sendNotification(Request request, String name, String company){
        MailNotification mailNotification = new MailNotification();
        mailNotification.sendSimpleMessage("kna12vh@gmail.com",
                "Новая заявка №" + request.getId(),
                "" + request.getRequestDate().toString() + " была создана новая заявка № " + request.getId() + " от " + name + " " + company);
    }

    public String setRequestResponsible(String workList){

        if (workList.equals(WorkList.SUPPORT_1C.getDisplayValue())) {
            return userRepository.findByAssignment(workList).orElseThrow().getName();
        } else return userRepository.findByAssignment("Default").orElseThrow().getName();

    }

}
