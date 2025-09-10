package org.shetsko.model.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    NEW ("Новый"),
    IN_PROGRESS ("В процессе"),
    COMPLETED ("Выполнен");

    private final String translate;

    TaskStatus(String translate) {
        this.translate = translate;
    }
}
