package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.*;
import de.kandels.librarydemo.exceptions.InvalidEmailException;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.services.AuthService;
import de.kandels.librarydemo.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final String unauthorizedMessage = "Please Provide A Valid Request Token To Edit Customers.";

    @Autowired
    CustomerService customerService;
    @Autowired
    AuthService authService;

    @GetMapping("")
    public ResponseEntity<?> getAllCustomers(@RequestBody RequestTokenDto requestTokenDto) {
        //check for valid request-token
        if (authService.checkRequestTokenValidity(requestTokenDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/get")
    public ResponseEntity<?> getCustomerById(@RequestBody IdAndTokenDto idAndTokenDto) {
        //check for valid request-token
        if (authService.checkRequestTokenValidity(idAndTokenDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try{
            return ResponseEntity.ok(customerService.getCustomerById(idAndTokenDto.getId()));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createCustomer(@RequestBody WriteCustomerDto writeCustomerDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(writeCustomerDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            return ResponseEntity.ok(customerService.createCustomer(writeCustomerDto));
        } catch (InvalidEmailException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCustomer(@RequestBody UpdateCustomerDto updateCustomerDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(updateCustomerDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try{
            return ResponseEntity.ok(customerService.updateCustomer(updateCustomerDto));
        } catch (InvalidEmailException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCustomer(@RequestBody IdAndTokenDto deleteIdDto) {
        //check for valid request-token
        if (authService.checkRequestTokenValidity(deleteIdDto.getRequestToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            customerService.deleteCustomer(deleteIdDto.getId());
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }

    }
}