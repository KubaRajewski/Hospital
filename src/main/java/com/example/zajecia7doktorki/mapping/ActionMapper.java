package com.example.zajecia7doktorki.mapping;

import com.example.zajecia7doktorki.domain.Action;
import com.example.zajecia7doktorki.dto.ActionDTO;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring", uses = {AdminMapper.class})
public interface ActionMapper {

    ActionDTO actionEntityToDTO(Action action);
}
