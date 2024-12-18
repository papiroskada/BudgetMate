package com.example.PersonalAccounting.util.dto_entity_converter;

import com.example.PersonalAccounting.dto.AccumulationDTO;
import com.example.PersonalAccounting.entity.Accumulation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccumulationDtoConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public AccumulationDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Accumulation convertToAccumulation(AccumulationDTO accumulationDTO) {
        return modelMapper.map(accumulationDTO, Accumulation.class);
    }

    public AccumulationDTO convertToAccumulationDTO(Accumulation accumulation) {
        return modelMapper.map(accumulation, AccumulationDTO.class);
    }
}
