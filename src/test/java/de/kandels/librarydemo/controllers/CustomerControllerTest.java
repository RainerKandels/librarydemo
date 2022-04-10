package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.*;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.services.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    AuthController authController;
    @Autowired
    CustomerService customerService;
    @Autowired
    CustomerController customerController;

    //Root User Credentials
    String email = "test2@mail.de";
    String password = "root";
    ReadCustomerDto testCustomer;
    String requestToken;

    @BeforeEach
    void setUp() {
        testCustomer = customerService.createCustomer(new WriteCustomerDto("TestName", email, password, null));
        RequestTokenDto requestTokenDto = (RequestTokenDto) authController.login(new LoginCustomerDto(email, password)).getBody();
        requestToken = requestTokenDto.getRequestToken();
    }

    @AfterEach
    void tearDown() {
        try {
            customerService.deleteCustomer(testCustomer.getId());
        } catch (RecordNotFoundException ignored){

        }
    }

    @Test
    void getAllCustomers() {
        ResponseEntity<?> response1 = customerController.getAllCustomers(new RequestTokenDto(requestToken));
        List<ReadCustomerDto> allCustomers = (List<ReadCustomerDto>) response1.getBody();
        assertNotNull(allCustomers);
        assertTrue(allCustomers.size()> 0);
    }

    @Test
    void getCustomerById() {
        ResponseEntity<?> response1 = customerController.getCustomerById(new IdAndTokenDto(testCustomer.getId(),requestToken));
        ReadCustomerDto readCustomerDto = (ReadCustomerDto) response1.getBody();
        assertNotNull(readCustomerDto);
        assertEquals(testCustomer.getId(),readCustomerDto.getId());
        assertEquals(testCustomer.getEmail(), readCustomerDto.getEmail());
        assertEquals(testCustomer.getName(), readCustomerDto.getName());
    }

    @Test
    void createCustomer() {
        // already tested with the getCustomerById Test together with the set up and tear down
    }

    @Test
    void updateCustomer() {
        ResponseEntity<?> response1 = customerController.updateCustomer(
                new UpdateCustomerDto(
                        testCustomer.getId(),
                        "TestName2",
                        "email@email.de",
                        "root2",
                        requestToken));
        ReadCustomerDto readCustomerDto = (ReadCustomerDto) response1.getBody();

        assertNotNull(readCustomerDto);
        assertEquals(testCustomer.getId(), readCustomerDto.getId());
        assertEquals("TestName2", readCustomerDto.getName());
        assertEquals("email@email.de", readCustomerDto.getEmail());


    }

    @Test
    void deleteCategory() {
        ResponseEntity<?> response1 = customerController.getAllCustomers(new RequestTokenDto(requestToken));
        List<ReadCustomerDto> allCustomersBefore = (List<ReadCustomerDto>) response1.getBody();

        customerController.deleteCustomer(new IdAndTokenDto(testCustomer.getId(), requestToken));

        ResponseEntity<?> response2 = customerController.getAllCustomers(new RequestTokenDto(requestToken));
        List<ReadCustomerDto> allCustomersAfter = (List<ReadCustomerDto>) response2.getBody();

        assertNotNull(allCustomersAfter);
        assertNotNull(allCustomersBefore);
        assertEquals(allCustomersAfter.size(), allCustomersBefore.size()-1);
    }
}