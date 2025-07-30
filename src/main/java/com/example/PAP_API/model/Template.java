package com.example.PAP_API.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OrderBy("orderIndex ASC") // Ensures JPA returns ordered list
    private List<Question> questions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_manager_id", nullable = false)
    private HRManager hrManager;

    // Optional helper method for managing both sides of the relationship
    public void addQuestion(Question question) {
        questions.add(question);
        question.setTemplate(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTemplate(null);
    }
}
