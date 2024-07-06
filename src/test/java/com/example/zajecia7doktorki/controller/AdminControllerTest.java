package com.example.zajecia7doktorki.controller;

import com.example.zajecia7doktorki.command.AdminUpdateCommand;
import com.example.zajecia7doktorki.domain.*;
import com.example.zajecia7doktorki.model.ActionPerformed;
import com.example.zajecia7doktorki.model.Role;
import com.example.zajecia7doktorki.repository.ActionRepository;
import com.example.zajecia7doktorki.repository.AppointmentRepository;
import com.example.zajecia7doktorki.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AdminControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;
    private Admin admin;
    private Appointment appointment;


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .build();
        admin = createTestAdmin();
        appointment = createTestAppointment();
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        appointmentRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admins/get")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(admin.getName()));

    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldGetAllAdmins() throws Exception {
        mockMvc.perform(get("/api/v1/admins/getAll")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Admin"));
    }


    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldUpdateAdmin() throws Exception {
        AdminUpdateCommand updateCommand = new AdminUpdateCommand();
        updateCommand.setName("UpdatedName");

        mockMvc.perform(put("/api/v1/admins")
                        .content(objectMapper.writeValueAsString(updateCommand))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDeleteCustomer() throws Exception {
        Customer customer = createTestCustomer();

        mockMvc.perform(delete("/api/v1/admins/customer/{id}", customer.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        assertFalse(customerRepository.existsById(customer.getId()));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDeleteAppointment() throws Exception {
        mockMvc.perform(delete("/api/v1/admins/appointment/{id}", appointment.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        assertFalse(appointmentRepository.existsById(appointment.getId()));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldLockCustomer() throws Exception {
        Customer customer = createTestCustomer();

        mockMvc.perform(put("/api/v1/admins/lock/" + customer.getId()))
                .andDo(print())
                .andExpect(status().isOk());


        assertTrue(customerRepository.findById(customer.getId()).get().getLocked());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldUnlockCustomer() throws Exception {
        Customer customer = createTestCustomer();

        mockMvc.perform(put("/api/v1/admins/unlock/" + customer.getId()))
                .andExpect(status().isOk());

        assertFalse(customerRepository.findById(customer.getId()).get().getLocked());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDisableCustomer() throws Exception {
        Customer customer = createTestCustomer();

        mockMvc.perform(put("/api/v1/admins/disable/" + customer.getId()))
                .andExpect(status().isOk());

        assertFalse(customerRepository.findById(customer.getId()).get().getEnabled());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldEnableCustomer() throws Exception {
        Customer customer = createTestCustomer();

        mockMvc.perform(put("/api/v1/admins/enable/" + customer.getId()))
                .andExpect(status().isOk());

        assertTrue(customerRepository.findById(customer.getId()).get().getEnabled());
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsService")
    void shouldDisplayActions() throws Exception {
        Action action = new Action();
        action.setActionPerformed(ActionPerformed.DELETING_APPOINTMENT);
        actionRepository.save(action);

        mockMvc.perform(get("/api/v1/admins/actions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
        //sprawdzic pierwsze pole
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setName("Janek");
        customer.setSurname("Kowalski");
        customer.setAge(30);
        customer.setPesel("22222222222");
        customer.setLogin("Janek");
        customer.setPassword("Janek");
        customer.setLocked(false);
        customer.setEnabled(true);
        customer.setRole(Role.USER);
        return customerRepository.save(customer);
    }

    private Admin createTestAdmin() {
        Admin admin = new Admin();
        admin.setName("Admin");
        admin.setSurname("Adminowski");
        admin.setAge(20);
        admin.setPesel("11111111111");
        admin.setLogin("admin");
        admin.setPassword(encoder.encode("password"));
        admin.setRole(Role.ADMIN);
        return customerRepository.save(admin);
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setName("Customer");
        patient.setSurname("Klientowski");
        patient.setAge(19);
        patient.setPesel("11111121111");
        patient.setLogin("customer");
        patient.setPassword(encoder.encode("password"));
        patient.setRole(Role.USER);
        return customerRepository.save(patient);
    }

    private Patient setUpPatient() {
        Patient patient = new Patient();
        patient.setName("brajan");
        patient.setSurname("szymanski");
        patient.setAge(35);
        patient.setLogin("newUser");
        patient.setPassword(encoder.encode("password"));
        patient.setPesel("12345678901");
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

    private Appointment createTestAppointment() {
        LocalDate date = LocalDate.of(2023, 12, 12);
        Patient patient = setUpPatient();
        Doctor doctor = setUpDoctor();
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(date);
        return appointmentRepository.save(appointment);
    }
}
