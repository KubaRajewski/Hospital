package com.example.zajecia7doktorki.config.security.jwt;

import com.example.zajecia7doktorki.model.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.ACCESS_DENIED;
import static com.example.zajecia7doktorki.constants.ConstantsUtil.APPLICATION_JSON_MEDIA_TYPE;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED, ACCESS_DENIED);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_MEDIA_TYPE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(error));
    }
}
