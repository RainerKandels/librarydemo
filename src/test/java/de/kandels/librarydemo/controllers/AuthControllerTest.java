package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.LoginCustomerDto;
import de.kandels.librarydemo.dtos.ReadCustomerDto;
import de.kandels.librarydemo.dtos.RequestTokenDto;
import de.kandels.librarydemo.dtos.WriteCustomerDto;
import de.kandels.librarydemo.services.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {


    @Autowired
    AuthController authController;
    @Autowired
    CustomerService customerService;

    //Root User Credentials
    String email = "test2@mail.de";
    String password = "root";
    ReadCustomerDto testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerService.createCustomer(new WriteCustomerDto("TestName", email, password, null));
    }

    @AfterEach
    void tearDown() {
        customerService.deleteCustomer(testCustomer.getId());
    }

    @Test
    void login() {
        LoginCustomerDto loginCustomerDto = new LoginCustomerDto(email, password);
        RequestTokenDto response1 = (RequestTokenDto) authController.login(loginCustomerDto).getBody();
        assertThat(response1).isNotNull();
        assertEquals(88,response1.getRequestToken().length());
        assertEquals(response1, authController.login(loginCustomerDto).getBody());

    }
}