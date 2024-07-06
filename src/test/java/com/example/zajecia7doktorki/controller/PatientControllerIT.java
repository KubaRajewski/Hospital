package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AppointmentCommand;
import com.example.zajecia7doktorki.command.PatientCommand;
import com.example.zajecia7doktorki.command.PatientUpdateCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.model.Role;
import com.example.zajecia7doktorki.model.Status;
import com.example.zajecia7doktorki.repository.AppointmentRepository;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("application-test")
//@AutoConfigureMockMvc
@AutoConfigureTestDatabase
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PatientControllerIT {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    private Doctor doctor;
    private Patient patient;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .build();
        doctor = setUpDoctor();
        patient = setUpPatient();
        appointment = createTestAppointment();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        appointmentRepository.deleteAll();
    }


    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetPatient() throws Exception {

        this.mockMvc.perform(get("/api/v1/patients/get")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("brajan"))
                .andExpect(jsonPath("$.surname").value("szymanski"));
    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetAllPatients() throws Exception {
        this.mockMvc.perform(get("/api/v1/patients/getAll")
                        .param("size", "1")
                        .param("page", "0")
                        .param("sort", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldUpdatePatient() throws Exception {
        PatientUpdateCommand patientUpdateCommand = new PatientUpdateCommand("Rychu", "Zbychu", 15);


        this.mockMvc.perform(put("/api/v1/patients")
                        .content(objectMapper.writeValueAsString(patientUpdateCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rychu"))
                .andExpect(jsonPath("$.surname").value("Zbychu"));
    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDeletePatient() throws Exception {
        this.mockMvc.perform(delete("/api/v1/patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(customerRepository.findById(patient.getId()).isEmpty());
    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldCreateAppointment() throws Exception {
        AppointmentCommand appointmentCommand = new AppointmentCommand();
        appointmentCommand.setDate(LocalDate.of(2023, 12, 16));

        this.mockMvc.perform(put("/api/v1/patients/makeAppointment/{doctorId}", doctor.getId())
                        .content(objectMapper.writeValueAsString(appointmentCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldCancelAppointment() throws Exception {
        this.mockMvc.perform(put("/api/v1/patients/appointments/cancel/{appointmentId}", appointment.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(appointmentRepository.existsById(appointment.getId()));
        assertThat(appointmentRepository.findById(appointment.getId()).get()
                .getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    @WithUserDetails(value = "newUser", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetPatientAppointments() throws Exception {

        mockMvc.perform(get("/api/v1/patients/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldThrowValidationMessageWhenSavePatientWithInvalidName() throws Exception {
        PatientCommand patientCommand = new PatientCommand("a", "szymanski", 19, "11111111111", "amelia", "amelia");

        this.mockMvc.perform(post("/api/v1/register/patient")
                        .content(objectMapper.writeValueAsString(patientCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("Your name should contain between 3 to 20 letters",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.name"))));
    }

    @Test
    void shouldThrowValidationMessageWhenSavePatientWithInvalidSurname() throws Exception {
        PatientCommand patientCommand = new PatientCommand("amelia", "s", 19, "11111111111", "amelia", "amelia");


        this.mockMvc.perform(post("/api/v1/register/patient")
                        .content(objectMapper.writeValueAsString(patientCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("Your surname should contain between 3 to 20 letters",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.surname"))));
    }


    @Test
    void shouldThrowValidationMessageWhenSavePatientWithInvalidAge() throws Exception {
        PatientCommand patientCommand = new PatientCommand("amelia", "szymanski", 0, "11111111111", "amelia", "amelia");

        this.mockMvc.perform(post("/api/v1/register/patient")
                        .content(objectMapper.writeValueAsString(patientCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("Age should not be zero",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.age"))));
    }

    @Test
    void shouldThrowValidationMessageWhenSavePatientWithInvalidPesel() throws Exception {
        PatientCommand patientCommand = new PatientCommand("amelia", "szymanski", 19, "1111111111", "amelia", "amelia");

        this.mockMvc.perform(post("/api/v1/register/patient")
                        .content(objectMapper.writeValueAsString(patientCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("PESEL must be exactly 11 letters long",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.pesel"))));
    }

    @Test
    void shouldThrowValidationMessageWhenMakeAppointmentWithInvalidDate() throws Exception {
        AppointmentCommand appointmentCommand = new AppointmentCommand(LocalDate.of(2021, 12, 12));

        this.mockMvc.perform(put("/api/v1/patients/makeAppointment/{doctorId}", doctor.getId())
                        .content(objectMapper.writeValueAsString(appointmentCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("Appointment date cannot be from the past",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.date"))));
    }

    private Doctor createTestDoctor(String name, String surname, int age, String pesel, String specialization, String login, String password) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSurname(surname);
        doctor.setAge(age);
        doctor.setPesel(pesel);
        doctor.setSpecialization(specialization);
        doctor.setLogin(login);
        doctor.setPassword(password);
        doctor.setRole(Role.USER);
        return customerRepository.save(doctor);
    }

    private Patient createTestPatient(String name, String surname, int age, String pesel, String login, String password) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setSurname(surname);
        patient.setAge(age);
        patient.setPesel(pesel);
        patient.setLogin(login);
        patient.setPassword(password);
        patient.setRole(Role.USER);
        return customerRepository.save(patient);
    }

    private Doctor setUpDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("brajan");
        doctor.setSurname("szymanski");
        doctor.setAge(35);
        doctor.setPesel("55555555555");
        doctor.setSpecialization("dentysta");
        doctor.setLogin("newDoctor");
        doctor.setPassword(encoder.encode("password"));
        doctor.setRole(Role.USER);
        return customerRepository.save(doctor);
    }

    private Patient setUpPatient() {
        Patient patient = new Patient();
        patient.setName("brajan");
        patient.setSurname("szymanski");
        patient.setAge(35);
        patient.setLogin("newUser");
        patient.setPassword("password");
        patient.setPesel("12345678901");
        patient.setRole(Role.USER);
        return customerRepository.save(patient);
    }

    private Appointment createTestAppointment() {
        LocalDate date = LocalDate.of(2023, 12, 12);
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(date);
        return appointmentRepository.save(appointment);
    }
}
