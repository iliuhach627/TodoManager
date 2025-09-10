package org.shetsko.model.enums;

import lombok.Getter;

@Getter
public enum Priority {
    HIGH ("Высокий"),
    MEDIUM ("Средний"),
    LOW("Низкий");

    private final String translate;

    Priority(String translate) {
        this.translate = translate;
    }
}
