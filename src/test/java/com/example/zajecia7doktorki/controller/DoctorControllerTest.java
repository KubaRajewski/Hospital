package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.DoctorCommand;
import com.example.zajecia7doktorki.command.DoctorUpdateCommand;
import com.example.zajecia7doktorki.domain.Appointment;
import com.example.zajecia7doktorki.domain.Doctor;
import com.example.zajecia7doktorki.domain.Patient;
import com.example.zajecia7doktorki.model.HealthCondition;
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

import javax.print.Doc;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("application-tests")
@AutoConfigureTestDatabase
class DoctorControllerTest {

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
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetDoctor() throws Exception {
        this.mockMvc.perform(get("/api/v1/doctors/get")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("brajan"))
                .andExpect(jsonPath("$.specialization").value("dentysta"));
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetAllDoctors() throws Exception {
        this.mockMvc.perform(get("/api/v1/doctors/getAll")
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
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldUpdateDoctor() throws Exception {
        DoctorUpdateCommand doctorUpdateCommand = new DoctorUpdateCommand("Amelia", "Kowalska", 35, "Dentist");

        this.mockMvc.perform(put("/api/v1/doctors")
                        .content(objectMapper.writeValueAsString(doctorUpdateCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Kowalska"))
                .andExpect(jsonPath("$.age").value(35))
                .andExpect(jsonPath("$.specialization").value("Dentist"));
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDeleteDoctor() throws Exception {
        this.mockMvc.perform(delete("/api/v1/doctors")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(customerRepository.findById(doctor.getId()).isEmpty());
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldCancelAppointment() throws Exception {
        this.mockMvc.perform(put("/api/v1/doctors/appointments/cancel/{appointmentId}", appointment.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        assertTrue(appointmentRepository.existsById(appointment.getId()));
        assertThat(appointmentRepository.findById(appointment.getId()).get()
                .getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldSuccessAppointment() throws Exception {
        this.mockMvc.perform(put("/api/v1/doctors/appointments/success/{appointmentId}", appointment.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue(appointmentRepository.existsById(appointment.getId()));
        assertThat(appointmentRepository.findById(appointment.getId()).get()
                .getStatus()).isEqualTo(Status.SUCCESSFUL);
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldChangeHealthCondition() throws Exception {
        this.mockMvc.perform(put("/api/v1/doctors/patients/changeHealthCondition/{patientId}", patient.getId())
                        .content("GOOD"))
                .andDo(print())
                .andExpect(status().isOk());
        Patient changedPatient = (Patient) customerRepository.findById(patient.getId()).get();

        assertThat(changedPatient.getHealthCondition()).isEqualTo(HealthCondition.GOOD);
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetDoctorPatients() throws Exception {
        this.mockMvc.perform(get("/api/v1/doctors/patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    @WithUserDetails(value = "newDoctor", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetAppointments() throws Exception {
        this.mockMvc.perform(get("/api/v1/doctors/appointments")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldThrowValidationMessageWhenSaveDoctorWithInvalidName() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand(" ", "szymanski", "Kardiolog", 29, "11111111111", "brajan", "brajan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
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
    void shouldThrowValidationMessageWhenSaveDoctorWithInvalidSurname() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand("Jan", " ", "Kardiolog", 29, "11111111111", "brajan", "brajan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
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
    void shouldThrowValidationMessageWhenSaveDoctorWithInvalidSpecialization() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand("Jan", "szymanski", " ", 29, "11111111111", "brajan", "brajan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("Specialization should not be blank",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.specialization"))));
    }


    @Test
    void shouldThrowValidationMessageWhenSaveDoctorWithInvalidAge() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand("Jan", "szymanski", "Kardiolog", 20, "11111111111", "brajan", "brajan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("To be a doctor your age must be higher than 25",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.age"))));
    }

    @Test
    void shouldThrowValidationMessageWhenSaveDoctorWithInvalidPesel() throws Exception {
        DoctorCommand doctorCommand = new DoctorCommand("Jan", "szymanski", "Kardiolog", 29, "1111111111", "brajan", "brajan");

        this.mockMvc.perform(post("/api/v1/register/doctor")
                        .content(objectMapper.writeValueAsString(doctorCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> Assertions.assertEquals("PESEL must be exactly 11 letters long",
                        (JsonPath.read(result.getResponse()
                                .getContentAsString(), "$.errors.pesel"))));
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