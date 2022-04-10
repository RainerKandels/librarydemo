package de.kandels.librarydemo.services;

import de.kandels.librarydemo.dtos.LoginCustomerDto;
import de.kandels.librarydemo.dtos.PasswordHashSaltDto;
import de.kandels.librarydemo.dtos.RequestTokenDto;
import de.kandels.librarydemo.entities.Customer;
import de.kandels.librarydemo.entities.RequestToken;
import de.kandels.librarydemo.exceptions.InvalidLoginDataException;
import de.kandels.librarydemo.repositories.CustomerRepository;
import de.kandels.librarydemo.repositories.RequestTokenRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;


@Service
public class AuthService {
    private final Long TOKEN_VALIDITY_DURATION = 3600L; // 3600 seconds = 1 hour
    private final int TOKEN_BYTE_LENGTH = 64;
    private final int PW_SALT_LENGTH = 16;
    private final int PW_HASH_LENGTH = 128;
    private final int PW_HASH_ITERATION_COUNT = 65536;

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    @Autowired
    RequestTokenRepository requestTokenRepository;
    @Autowired
    CustomerRepository customerRepository;

    /**
     * checks if email and password are a valid pair in the customer database
     * @param loginData contains email and password
     * @return the request token that will not be changed for one hour
     * @throws InvalidLoginDataException thrown when email or password is incorrect
     */
    public RequestTokenDto login (LoginCustomerDto loginData) throws InvalidLoginDataException{

        //Check if there is a customer with the provided email
        Customer probe = new Customer();
        probe.setEmail(loginData.getEmail().toLowerCase());
        Optional<Customer> customer = customerRepository.findOne(Example.of(probe));
        if (customer.isEmpty()) {
            throw new InvalidLoginDataException("Email Or Password Incorrect.");
        }

        // Check if the password is correct
        Boolean passwordIsCorrect = comparePasswords(loginData.getPassword(), customer.get().getPasswordHash(), customer.get().getPasswordSalt());
        if (!passwordIsCorrect) {
            throw new InvalidLoginDataException("Email Or Password Incorrect.");
        }

        return new RequestTokenDto(getRequestToken(customer.get().getId()));
    }

    /**
     * The password is being hashed using the passwordSalt.
     * This compares if the newly generated hash equals the one in the database
     * @param password String of the unhashed password
     * @param passwordHash the password hash that is stored in the customer db
     * @param passwordSalt the password salt that is stored in the customer db
     * @return True if the two password hashes are the same
     */
    private Boolean comparePasswords(String password, byte[] passwordHash, byte[] passwordSalt){
        return Arrays.equals(hashPasswordFromSalt(password, passwordSalt),passwordHash);
    }

    /**
     * returns the request token for a specific customer
     * checks if there is already a token for that customer in the db
     * then checks
     * @param customerId customer that the request - token is assigned to
     * @return Request token
     */
    private String getRequestToken(Long customerId){

        //Check if there is already a token for that cuser in the db
        //and check if this token is still valid (it has an expiration date)
        Optional<RequestToken> lastToken = requestTokenRepository.findById(customerId);
        if (lastToken.isPresent() && lastToken.get().getExpirationTime().isAfter(LocalDateTime.now())){
            return lastToken.get().getRequestToken();
        }

        //if there is no valid token, create a new one with a validity of one hour
        RequestToken newRequestToken = new RequestToken(
                customerId,
                generateNewToken(),
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(TOKEN_VALIDITY_DURATION)
        );

        requestTokenRepository.save(newRequestToken); //save the new token to the db
        return newRequestToken.getRequestToken();

    }

    private String generateNewToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    /**
     * Generates a new random password Salt and uses it to convert the provided password into a password hash
     * @param password String of the unedited password
     * @return contains the password salt and hash
     */
    @SneakyThrows
    public PasswordHashSaltDto createPasswordHashAndSalt(String password){
        byte[] passwordSalt = new byte[PW_SALT_LENGTH];
        secureRandom.nextBytes(passwordSalt);

        byte[] passwordHash = hashPasswordFromSalt(password, passwordSalt);
        return new PasswordHashSaltDto(passwordHash, passwordSalt);
    }

    /**
     * Takes the salt and converts the provided password into a password hash
     * It uses the PBKDF2 Hashing Standart
     * @param password String of the unedited password
     * @param salt The password salt that has been generated for the customer
     * @return the newly generated password hash that will be stored in the customer db
     */
    @SneakyThrows
    private byte[] hashPasswordFromSalt(String password, byte[] salt){
        // PBKDF2 Hashing Standard
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, PW_HASH_ITERATION_COUNT, PW_HASH_LENGTH);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        return secretKeyFactory.generateSecret(keySpec).getEncoded();
    }

    /**
     * checks if a provided request-token is stored in the database and is not expired
     * customers can get this token when logging in
     * @param inputRequestToken the token provided by the user that wants to edit something in the library
     * @return True if the token exists and is not yet expired
     */
    public boolean checkRequestTokenValidity(String inputRequestToken) {
        Optional<RequestToken> requestTokenFromDB = requestTokenRepository.findOneByRequestToken(inputRequestToken);
        if (requestTokenFromDB.isEmpty()){
            return false;
        }
        return requestTokenFromDB.get().getExpirationTime().isBefore(LocalDateTime.now());

    }

    public void deleteRequestTokenByCustomerId(Long customerId){
        requestTokenRepository.deleteById(customerId);
    }
}
