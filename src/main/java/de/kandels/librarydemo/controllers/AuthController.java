package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.LoginCustomerDto;
import de.kandels.librarydemo.exceptions.InvalidLoginDataException;
import de.kandels.librarydemo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class AuthController {

    @Autowired
    AuthService authService;

    /**
     * Customers can log in with email and password to get a request token that
     * is valid for one Hour
     * @param loginCustomerDto contains email and password fields
     * @return RequestTokenDto
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCustomerDto loginCustomerDto){
        try {
            return ResponseEntity.ok(authService.login(loginCustomerDto));
        } catch (InvalidLoginDataException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

}
