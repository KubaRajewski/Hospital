package com.example.zajecia7doktorki.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantsUtil {

    public static final String APPLICATION_JSON_MEDIA_TYPE = "application/json";
    public static final String ACCESS_DENIED = "Access denied";
    public static final String EXPIRED = "expired";
    public static final String BEARER = "Bearer ";
    public static final String ROLE = "role";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String DOCTOR = "DOCTOR";
    public static final String DOCTOR_WITH_THIS_LOGIN_DOES_NOT_EXIST = "Doctor with this login does not exist";
    public static final String LOGIN_DOES_NOT_EXIST = "Login does not exist";
    public static final String PATIENT = "PATIENT";
    public static final String PATIENT_WITH_THIS_LOGIN_DOES_NOT_EXIST = "Patient with this login does not exist";
    public static final String ADMIN_WITH_THIS_LOGIN_DOES_NOT_EXIST = "Admin with this login does not exist";

}
