package de.kandels.librarydemo.services;

import de.kandels.librarydemo.dtos.PasswordHashSaltDto;
import de.kandels.librarydemo.dtos.ReadCustomerDto;
import de.kandels.librarydemo.dtos.UpdateCustomerDto;
import de.kandels.librarydemo.dtos.WriteCustomerDto;
import de.kandels.librarydemo.entities.Customer;
import de.kandels.librarydemo.exceptions.InvalidEmailException;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    AuthService authService;

    public List<ReadCustomerDto> getAllCustomers(){
        List<Customer> customers =  customerRepository.findAll();
        List<ReadCustomerDto> customerDtos = new ArrayList<>();
        for (Customer customer: customers){
            customerDtos.add(mapFormEntityToDto(customer));
        }
        return customerDtos;
    }

    public ReadCustomerDto getCustomerById(Long customerId) throws RecordNotFoundException, InvalidEmailException{
        Customer customer = getCustomerEntityById(customerId);
        return mapFormEntityToDto(customer);
    }

    public ReadCustomerDto createCustomer (WriteCustomerDto writeCustomerDto) throws InvalidEmailException {
        validateEmail(writeCustomerDto.getEmail());

        Customer writeCustomer = new Customer();
        writeCustomer.setName(writeCustomerDto.getName());
        writeCustomer.setEmail(writeCustomerDto.getEmail().toLowerCase());

        PasswordHashSaltDto passwordHashSaltDto = authService.createPasswordHashAndSalt(writeCustomerDto.getPassword());
        writeCustomer.setPasswordSalt(passwordHashSaltDto.getPasswordSalt());
        writeCustomer.setPasswordHash(passwordHashSaltDto.getPasswordHash());

        Customer readCustomer = customerRepository.save(writeCustomer);
        return mapFormEntityToDto(readCustomer);
    }

    public ReadCustomerDto updateCustomer (UpdateCustomerDto updateCustomerDto) throws InvalidEmailException, RecordNotFoundException {
        validateEmail(updateCustomerDto.getEmail());

        Customer originalCustomer = getCustomerEntityById(updateCustomerDto.getId()); //Check if Id can be found in DB

        PasswordHashSaltDto passwordHashAndSalt = authService.createPasswordHashAndSalt(updateCustomerDto.getPassword());
        Customer writeCustomer = new Customer(
                updateCustomerDto.getId(),
                updateCustomerDto.getName(),
                updateCustomerDto.getEmail().toLowerCase(),
                passwordHashAndSalt.getPasswordHash(),
                passwordHashAndSalt.getPasswordSalt()
        );
        Customer readCustomer = customerRepository.save(writeCustomer);
        return mapFormEntityToDto(readCustomer);
    }

    public void deleteCustomer(Long customerId){
        Customer originalCustomer = getCustomerEntityById(customerId); //Check if Id can be found in DB

        //When customer is deleted, also delete his request token
        authService.deleteRequestTokenByCustomerId(customerId);

        customerRepository.deleteById(customerId);
    }

    private ReadCustomerDto mapFormEntityToDto(Customer customer){
        return new ReadCustomerDto(customer.getId(), customer.getName(), customer.getEmail());
    }

    /**
     * validates an email adress by two factors:
     *    1. Checks if the format of the email is correct. (Done with the RFC 5322 Pattern)
     *    2. Checks if email already exists in DB
     * @param email String of the email adresse to be validated
     * @return Boolean - True if none of the two check throws the custom exception
     * @throws InvalidEmailException thrown when email is not correct
     */
    public Boolean validateEmail(String email) throws InvalidEmailException{

        email = email.toLowerCase();

        //Check Format with RFC 5322 Pattern
        String regexPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        boolean correctFormat =  Pattern.compile(regexPattern).matcher(email).matches();

        if (!correctFormat){
            throw new InvalidEmailException("Invalid Email Format.");
        }

        //Check if email already exists in DB
        Customer probe = new Customer();
        probe.setEmail(email);
        Optional<Customer> customer = customerRepository.findOne(Example.of(probe));
        boolean uniqueInDb = customer.isEmpty();

        if (!uniqueInDb){
            throw new InvalidEmailException("Email Already Exists In Customer Database");
        }

        return true;
    }

    private Customer getCustomerEntityById(Long customerId){
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()){
            throw new RecordNotFoundException("CustomerId Not Found.");
        }
        return customer.get();
    }

}
