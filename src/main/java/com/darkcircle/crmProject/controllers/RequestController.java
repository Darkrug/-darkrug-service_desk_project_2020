package com.darkcircle.crmProject.controllers;

import com.darkcircle.crmProject.models.Request;
import com.darkcircle.crmProject.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Controller
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @GetMapping("/request")
    public String requestAdd(Model model) {
        return "request";
    }

    @PostMapping("/request")
    public String requestAdd(@RequestParam String name,
                              @RequestParam String surname,
                              @RequestParam String middleName,
                              @RequestParam String email,
                              @RequestParam String phoneNumber,
                              @RequestParam String problem,
                              Model model){

        Date requestDate = new Date();
        Request request = new Request(name, surname, middleName, email, phoneNumber, problem, requestDate);
        requestRepository.save(request);
        return "redirect:/request/administration";
    }

    @GetMapping("/request/administration")
    public String requestAdministration(Model model) {
        Iterable<Request> requests = requestRepository.findAll();
        model.addAttribute("requests", requests);
        return "request_administration";
    }

    @GetMapping("/request/{id}")
    public String requestDetails(@PathVariable(value = "id") long id, Model model) {
        if(!requestRepository.existsById(id)){return "redirect:/administration";}

        Optional<Request> request = requestRepository.findById(id);
        ArrayList<Request> res = new ArrayList<>();
        request.ifPresent(res :: add);
        model.addAttribute("request", res);
        return "request_details";
    }

    @GetMapping("/request/{id}/edit")
    public String requestEdit(@PathVariable(value = "id") long id, Model model) {
        if(!requestRepository.existsById(id)){return "redirect:/request/administration";}

        Optional<Request> request = requestRepository.findById(id);
        ArrayList<Request> res = new ArrayList<>();
        request.ifPresent(res :: add);
        model.addAttribute("request", res);
        return "request_edit";
    }

    @PostMapping("/request/{id}/edit")
    public String requestUpdate(@PathVariable(value = "id") long id,
                                @RequestParam String name,
                                @RequestParam String surname,
                                @RequestParam String middleName,
                                @RequestParam String email,
                                @RequestParam String phoneNumber,
                                @RequestParam String problem,
                                Model model){
        Request request = requestRepository.findById(id).orElseThrow();
        request.setName(name);
        request.setSurname(surname);
        request.setMiddleName(middleName);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);
        request.setProblem(problem);
        requestRepository.save(request);
        return "redirect:/request/administration";
    }

}
