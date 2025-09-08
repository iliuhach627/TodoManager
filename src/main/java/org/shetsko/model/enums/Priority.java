package org.shetsko.model.enums;

public enum Priority {
    HIGH ("Высокий"),
    MEDIUM ("Средний"),
    LOW("Низкий");

    private String translate;

    Priority(String translate) {
        this.translate = translate;
    }
    public String getTranslate() {
        return translate;
    }
}
