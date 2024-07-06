package com.example.zajecia7doktorki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Zajecia7DoktorkiApplication {

    public static void main(String[] args) {
        /*
        Stworz aplikacje z pomoca springa gdzie beda mogli Pacjenci rejestrowac sie do Lekarza. Pacjent(id, name, surname, age, pesel, wizyty), Doktor(id, name, surname, specialization, age) Appointment(patientId, DoctorId, date)
        Pacjent ma miec mozliwosc zapisania sie do specjalisty. Mamy stworzyc pelnego CRUD'a. Doktor ma miec mozliwosc odwolania wizyty.
        Msmy miec mozliwosc wypisania wszystkich pacjentow danego doktorka
        I liste wizyt kazdego pacjenta
         */

        SpringApplication.run(Zajecia7DoktorkiApplication.class, args);
    }

}
