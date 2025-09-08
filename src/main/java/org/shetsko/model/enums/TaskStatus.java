package org.shetsko.model.enums;

public enum TaskStatus {
    NEW ("Новый"),
    IN_PROGRESS ("В процессе"),
    COMPLETED ("Выполнен");

    private String translate;

    TaskStatus(String translate) {
        this.translate = translate;
    }
    public String getTranslate() {
        return translate;
    }
}
