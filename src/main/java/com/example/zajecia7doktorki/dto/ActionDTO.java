package com.example.zajecia7doktorki.dto;

import com.example.zajecia7doktorki.model.ActionPerformed;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionDTO {

    private AdminDTO admin;

    private LocalDate createdDate;

    private ActionPerformed actionPerformed;

    private String oldValue;

    private String newValue;

    private String changedField;

}
