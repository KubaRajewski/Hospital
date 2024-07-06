package com.example.zajecia7doktorki.domain;

import com.example.zajecia7doktorki.model.ActionPerformed;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "admin_id")
    private Customer admin;

    @CreatedDate
    private LocalDate createdDate;

    @Enumerated(EnumType.STRING)
    private ActionPerformed actionPerformed;

    private String oldValue;

    private String newValue;

    private String changedField;

}
