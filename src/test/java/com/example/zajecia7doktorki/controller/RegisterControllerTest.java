package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AdminCommand;
import com.example.zajecia7doktorki.command.DoctorCommand;
import com.example.zajecia7doktorki.command.PatientCommand;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("application-tests")
@AutoConfigureTestDatabase
class RegisterControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .build();
        customerRepository.deleteAll();
    }

    @Test
    void shouldCreateDoctor() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand("Jan", "Kowalski", "Cardiology", 45, "98012112345", "Jan", "Jan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Jan"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));
    }

    @Test
    void shouldCreatePatient() throws Exception {
        PatientCommand patientCommand = new PatientCommand("Rychu", "Zbychu", 15, "12345678910", "Rychu", "rychu");

        this.mockMvc.perform(post("/api/v1/register/patient")
                        .content(objectMapper.writeValueAsString(patientCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Rychu"))
                .andExpect(jsonPath("$.login").value("Rychu"));
    }

    @Test
    void shouldCreateAdmin() throws Exception {
        AdminCommand adminCommand = new AdminCommand("Rychu", "Zbychu", 15, "12345678910", "Rychu", "Rychu");

        this.mockMvc.perform(post("/api/v1/register/admin")
                        .content(objectMapper.writeValueAsString(adminCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Rychu"))
                .andExpect(jsonPath("$.login").value("Rychu"));
    }
}