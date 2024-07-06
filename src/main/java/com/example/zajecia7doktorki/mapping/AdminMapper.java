package com.example.zajecia7doktorki.mapping;

import com.example.zajecia7doktorki.command.AdminCommand;
import com.example.zajecia7doktorki.domain.Admin;
import com.example.zajecia7doktorki.dto.AdminDTO;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDTO adminEntityToDTO(Admin admin);

    Admin adminCommandToAdminEntity(AdminCommand adminCommand);

}
