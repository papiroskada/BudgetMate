package com.example.PersonalAccounting.util.dto_entity_converter;


import com.example.PersonalAccounting.dto.TransactionDTO;
import com.example.PersonalAccounting.entity.Transaction;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionDTOConverter {

    private final ModelMapper modelMapper;

    public TransactionDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Transaction convertToTransaction(TransactionDTO transactionDTO) {
        return modelMapper.map(transactionDTO, Transaction.class);}

    public TransactionDTO convertToTransactionDTO(Transaction transaction) {
        return modelMapper.map(transaction, TransactionDTO.class);}
}
