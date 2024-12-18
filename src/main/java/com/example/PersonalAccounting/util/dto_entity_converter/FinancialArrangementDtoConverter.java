package com.example.PersonalAccounting.util.dto_entity_converter;

import com.example.PersonalAccounting.dto.FinancialArrangementDTO;
import com.example.PersonalAccounting.entity.FinancialArrangement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FinancialArrangementDtoConverter {

    private final ModelMapper modelMapper;

    @Autowired
    public FinancialArrangementDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FinancialArrangement convertToFA(FinancialArrangementDTO financialArrangementDTO) {
        return modelMapper.map(financialArrangementDTO, FinancialArrangement.class);
    }

    public FinancialArrangementDTO convertToFADTO(FinancialArrangement financialArrangement) {
        return modelMapper.map(financialArrangement, FinancialArrangementDTO.class);
    }
}
