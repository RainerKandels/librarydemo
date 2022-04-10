package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.WriteCustomerDto;
import de.kandels.librarydemo.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class InitController {

    @Autowired
    CustomerService customerService;


    /**
     * This is unsafe!!! Only for initialization!
     * Used to initialize the first Customer in the customer Database after setting up the db
     * To be deleted as soon as database has customers to work with
     * @return returns the login credentials of the initial root user
     */
    @PostMapping("/init")
    public ResponseEntity<?> initializeRootUser(){
        WriteCustomerDto rootUser = new WriteCustomerDto(
                "RootUser",
                "root@email.de",
                "root",
                null);
        customerService.createCustomer(rootUser);
        return ResponseEntity.ok(rootUser);
    }
}
