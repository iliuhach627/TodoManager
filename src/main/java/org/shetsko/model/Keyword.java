package org.shetsko.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "keywords", uniqueConstraints = {
        @UniqueConstraint(columnNames = "word", name = "uk_keyword_word")
})
@Getter
@Setter
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "word", nullable = false, unique = true, columnDefinition = "TEXT")
    private String word;

    // Конструкторы
    public Keyword() {}

    public Keyword(String word) {
        this.word = word.toLowerCase().trim();
    }

    // Бизнес-метод для проверки валидности
    public boolean isValid() {
        return word != null && !word.trim().isEmpty() && word.length() <= 255;
    }

    @Override
    public String toString() {
        return "Keyword{id=" + id + ", word='" + word + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyword keyword = (Keyword) o;
        return word != null && word.equalsIgnoreCase(keyword.word);
    }

    @Override
    public int hashCode() {
        return word != null ? word.toLowerCase().hashCode() : 0;
    }
}