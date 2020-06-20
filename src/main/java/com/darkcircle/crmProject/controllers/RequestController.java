package com.darkcircle.crmProject.controllers;

import com.darkcircle.crmProject.enums.RequestStatus;
import com.darkcircle.crmProject.enums.Roles;
import com.darkcircle.crmProject.enums.WorkList;
import com.darkcircle.crmProject.mail.MailNotification;
import com.darkcircle.crmProject.models.Request;
import com.darkcircle.crmProject.models.User;
import com.darkcircle.crmProject.repositories.RequestRepository;
import com.darkcircle.crmProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${info.email}")
    String email;

    @Value("${info.password}")
    String password;

    @Value("${info.mailHost}")
    String mailHost;

    @Value("${info.mailPort}")
    String mailPort;

    @GetMapping("/request")
    public String createRequest(Model model) {
        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        HashSet<User> clients = new HashSet<>();
        for (User client : userRepository.findAll()) {
            if (client.getRoles().contains(Roles.CLIENT)) {
                clients.add(client);
            }
        }
        model.addAttribute("clients", clients);

        return "request_page";
    }

    @PostMapping("/request")
    public String createRequest(@RequestParam(required = false) String name,
                                @RequestParam String workType,
                                @RequestParam String workList,
                                @RequestParam String addInfo,
                                Model model) {
        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        if (user.getRoles().contains(Roles.CLIENT)) {
            Request request = new Request(user.getName(), user.getCompany(), workType, addInfo, RequestStatus.NEW.getDisplayValue(), setRequestResponsible(workList), workList, null, new Date());
            requestRepository.save(request);
            sendNotification(request,user.getName(),user.getCompany(),user.getEmail());
        } else {
            User client = userRepository.findByName(name).orElseThrow();
            Request request = new Request(name, client.getCompany(), workType, addInfo, RequestStatus.NEW.getDisplayValue(), setRequestResponsible(workList), workList, null, new Date());
            requestRepository.save(request);
        }

        return "request_success_page";
    }

    @GetMapping("request/success")
    public String requestSuccess(Model model) {

        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        return "request_success_page";
    }

    @GetMapping("request/{request_id}")
    public String requestDetails(@PathVariable(value = "request_id") long request_id, Model model) {
        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        if (!requestRepository.existsById(request_id)) {
            return "/request/all/0";
        }

        Optional<Request> request = requestRepository.findById(request_id);
        if (user.getRoles().contains(Roles.CLIENT) & !request.orElseThrow().getName().equals(user.getName())) {
            return "wrong_user";
        } else if (user.getRoles().contains(Roles.SPECIALIST) & !request.orElseThrow().getResponsible().equals(user.getName())) {
            return "wrong_user";
        } else {
            ArrayList<Request> res = new ArrayList<>();
            request.ifPresent(res::add);
            model.addAttribute("request", res);

            return "request_details_page";
        }
    }

    @GetMapping("/request/all/{page_id}")
    public String showAllRequests(@PathVariable(value = "page_id") int page_id, Model model) {

        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        int nextPageInt = page_id + 1;
        String nextPage = String.valueOf(nextPageInt);
        model.addAttribute("nextPage", nextPage);

        Pageable page = PageRequest.of(page_id, 4, Sort.by("requestDate").descending());

        if (user.getRoles().contains(Roles.ADMIN)) {
            Page<Request> requests = requestRepository.findAll(page);
            model.addAttribute("requests", requests);
        } else if (user.getRoles().contains(Roles.SPECIALIST)) {
            List<Request> requests = requestRepository.findByResponsible(user.getName(), page);
            model.addAttribute("requests", requests);
        } else {
            List<Request> requests = requestRepository.findByName(user.getName(), page);
            model.addAttribute("requests", requests);
        }

        return "request_all_page";
    }

    @GetMapping("/request/{request_id}/edit")
    public String requestEdit(@PathVariable(value = "request_id") long request_id, Model model) {

        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        if (!requestRepository.existsById(request_id)) {
            return "/request/all/0";
        }

        Optional<Request> request = requestRepository.findById(request_id);
        if (user.getRoles().contains(Roles.CLIENT)) {
            return "wrong_user";
        } else if (user.getRoles().contains(Roles.SPECIALIST) & !request.orElseThrow().getResponsible().equals(user.getName())) {
            return "wrong_user";
        } else {
            ArrayList<Request> res = new ArrayList<>();
            request.ifPresent(res::add);
            model.addAttribute("request", res);

            return "request_edit_page";
        }
    }

    @PostMapping("/request/{request_id}/edit")
    public String requestUpdate(@PathVariable(value = "request_id") long request_id,
                                @RequestParam String requestStatus,
                                @RequestParam String workDuration,
                                @RequestParam(required = false) String responsible,
                                Model model) {

        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        Request request = requestRepository.findById(request_id).orElseThrow();

        request.setRequestStatus(requestStatus);
        request.setWorkDuration(workDuration);
        if (responsible != null) {
            request.setResponsible(responsible);
        }
        requestRepository.save(request);

        return "request_success_page";
    }


    public void sendNotification(Request request, String name, String company, String to) {
        MailNotification mailNotification = new MailNotification(email, password, mailHost, mailPort);
        mailNotification.sendSimpleMessage(to,
                "Новая заявка №" + request.getId(),
                "" + request.getRequestDate().toString() + " была создана новая заявка № " + request.getId() + " от " + name + " " + company);
    }

    public String setRequestResponsible(String workList) {

        if (workList.equals(WorkList.SUPPORT_1C.getDisplayValue())) {
            return userRepository.findByAssignment(workList).orElseThrow().getName();
        } else return userRepository.findByAssignment("Default").orElseThrow().getName();

    }

}
