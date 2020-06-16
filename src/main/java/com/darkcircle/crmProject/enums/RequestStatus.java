package com.darkcircle.crmProject.enums;

public enum RequestStatus {

    NEW("Новая"),
    IN_PROGRESS("В работе"),
    DONE("Заявка выполнена");


    private final String displayValue;

    RequestStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

}
