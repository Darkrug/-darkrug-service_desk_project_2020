package com.darkcircle.crmProject.controllers;

import com.darkcircle.crmProject.enums.Roles;
import com.darkcircle.crmProject.models.Request;
import com.darkcircle.crmProject.models.User;
import com.darkcircle.crmProject.report.CreateReport;
import com.darkcircle.crmProject.repositories.RequestRepository;
import com.darkcircle.crmProject.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

@Controller
public class ReportController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${info.path}")
    String folderPath;

    @GetMapping("/report")
    public String downloadReport(Model model) {
        User user = userRepository.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
        model.addAttribute("usr", user);

        HashSet<String> companies = new HashSet<>();
        if (user.getRoles().contains(Roles.ADMIN) || user.getRoles().contains(Roles.SPECIALIST)) {
            for (User client : userRepository.findAll()) {
                companies.add(client.getCompany());
            }
        } else {
            companies.add(user.getCompany());
        }

        model.addAttribute("companies", companies);

        return "report_page";
    }

    @PostMapping("/report")
    public String downloadReport(@RequestParam String date1,
                               @RequestParam String date2,
                               @RequestParam String company) {


        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate1 = null;
        Date convertedDate2 = null;
        try {
            convertedDate1 = dateFormat.parse(date1);
            convertedDate2 = dateFormat.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        CreateReport createReport = new CreateReport();

        ArrayList<Request> iteratedRequests = new ArrayList<>();
        Iterator<Request> iterator = requestRepository.findAll().iterator();
        while (iterator.hasNext()) {
            Request nextRequest = iterator.next();
            if (nextRequest.getCompany().equals(company) & nextRequest.getRequestDate().after(convertedDate1) & nextRequest.getRequestDate().before(convertedDate2) & nextRequest.getRequestStatus().equals("Заявка выполнена")) {
                iteratedRequests.add(nextRequest);
            }
        }

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        String filename = "Report_" + dateFormat1.format(new Date()) + ".pdf";
        createReport.createPDF(iteratedRequests, convertedDate1, convertedDate2, company, folderPath + filename);

        return "redirect:/file/" + filename;
    }

    @RequestMapping("/file/{fileName}")
    @ResponseBody
    public void show(@PathVariable("fileName") String filename, HttpServletResponse response) {
        if (filename.contains(".pdf")) {
            response.setContentType("application/pdf");
        }

        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        response.setHeader("Content-Transfer-Encoding", "binary");

        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(folderPath + filename);
            int length;
            byte[] buf = new byte[1024];
            while ((length = fis.read(buf)) > 0) {
                bos.write(buf, 0, length);
            }
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String decode(String text) throws UnsupportedEncodingException {
        return new String(URLDecoder.decode(text, "UTF-8").getBytes("UTF-8"), "UTF-8");
    }

}
