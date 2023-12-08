package com.goltsov.test_task.test_task.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.goltsov.test_task.test_task.util.Priority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Column(name = "header")
    private String header;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_status_id", referencedColumnName = "id")
    @NotNull(message = "Task status cannot be blank or null")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User executor;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private Set<Commentary> commentaries = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    public Task(String header, String description, TaskStatus taskStatus,
                User author, User executor, Set<Commentary> commentaries,
                Priority priority) {
        this.header = header;
        this.description = description;
        this.taskStatus = taskStatus;
        this.author = author;
        this.executor = executor;
        this.commentaries = commentaries;
        this.priority = priority;
    }

    public void addCommentary(Commentary commentary) {
        if (commentary != null) {
            commentaries.add(commentary);
            commentary.setTask(this);
        }
    }

    public void removeCommentary(Commentary commentary) {
        commentaries.remove(commentary);
        commentary.setTask(null);
    }


}
