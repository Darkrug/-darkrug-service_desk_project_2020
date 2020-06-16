package com.darkcircle.crmProject.enums;

public enum WorkList {
    SUPPORT_1C("Поддержка 1С"),
    EQUIPMENT("Компьютеры и оргтехника"),
    SOFTWARE("Программное обеспечение");


    private final String displayValue;

    WorkList(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
