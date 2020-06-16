package com.darkcircle.crmProject.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name, company, workType, addInfo, requestStatus, responsible, workList, workDuration;
    private Date requestDate;

    public Request(String name, String company, String workType, String addInfo, String requestStatus, String responsible, String workList, String workDuration, Date requestDate) {
        this.name = name;
        this.company = company;
        this.workType = workType;
        this.addInfo = addInfo;
        this.requestStatus = requestStatus;
        this.workList = workList;
        this.workDuration = workDuration;
        this.requestDate = requestDate;
        this.responsible = responsible;
    }

    public Request() {
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getWorkList() {
        return workList;
    }

    public void setWorkList(String workList) {
        this.workList = workList;
    }

    public String getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(String workDuration) {
        this.workDuration = workDuration;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
