package com.example.zajecia7doktorki.domain;

import com.example.zajecia7doktorki.model.HealthCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("PATIENT")
public class Patient extends Customer {

    @Enumerated(EnumType.STRING)
    private HealthCondition healthCondition;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}
