package com.darkcircle.crmProject.enums;

public enum WorkType {
    REMOTE("Удаленное администрирование"),
    ON_SITE("Выезд");


    private final String displayValue;

    WorkType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
