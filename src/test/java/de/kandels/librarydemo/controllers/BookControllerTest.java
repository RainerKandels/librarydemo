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
class BookControllerTest {

    @Autowired
    AuthController authController;
    @Autowired
    CustomerService customerService;
    @Autowired
    BookController bookController;
    @Autowired
    CategoryController categoryController;

    //Root User Credentials
    String email = "test2@mail.de";
    String password = "root";
    ReadCustomerDto testCustomer;
    String requestToken;

    ReadCategoryDto testCategory;
    ReadBookDto testBook;

    @BeforeEach
    void setUp() {
        testCustomer = customerService.createCustomer(new WriteCustomerDto("TestName", email, password, null));
        RequestTokenDto requestTokenDto = (RequestTokenDto) authController.login(new LoginCustomerDto(email, password)).getBody();
        requestToken = requestTokenDto.getRequestToken();

        testCategory = createTestCategory();

        testBook = (ReadBookDto) bookController.createBook(new WriteBookDto(
                "TestBook1",
                "TestAuthor1",
                "TestPublisher1",
                1999,
                testCategory.getId(),
                requestToken)).getBody();
    }

    @AfterEach
    void tearDown() {
        try {
            bookController.deleteBook(new IdAndTokenDto(testBook.getId(), requestToken));
        } catch (RecordNotFoundException ignore){

        }

        deleteTestCategory(testCategory);

        customerService.deleteCustomer(testCustomer.getId());

    }

    @Test
    void getAllBooks() {
        ResponseEntity<?> response1 = bookController.getAllBooks();
        List<ReadBookDto> allBooks = (List<ReadBookDto>) response1.getBody();
        assertNotNull(allBooks);
        assertTrue(allBooks.size()> 0);
    }

    @Test
    void getBookById() {
        ResponseEntity<?> response1 = bookController.getBookById(testBook.getId());
        ReadBookDto readBookDto = (ReadBookDto) response1.getBody();
        assertNotNull(readBookDto);
        assertEquals(testBook.getId(), readBookDto.getId());
        assertEquals(testBook.getAuthor(), readBookDto.getAuthor());
        assertEquals(testBook.getTitle(), readBookDto.getTitle());
        assertEquals(testBook.getPublisher(), readBookDto.getPublisher());
        assertEquals(testBook.getCategoryId(), readBookDto.getCategoryId());
        assertEquals(testBook.getPublishingYear(), readBookDto.getPublishingYear());
        assertEquals(testBook.getCategoryName(), readBookDto.getCategoryName());
    }

    @Test
    void createBook() {
        //already tested by the getBookById() Test combined with the set up and teardown
    }

    @Test
    void updateBook() {
        ResponseEntity<?> response1 = bookController.updateBook(new UpdateBookDto(
                testBook.getId(),
                "TestBook2",
                "TestAuthor2",
                "TestPublisher2",
                2000,
                testCategory.getId(),
                requestToken));
        ReadBookDto readBookDto = (ReadBookDto) response1.getBody();

        assertEquals(testBook.getId(), readBookDto.getId());
        assertEquals("TestAuthor2", readBookDto.getAuthor());
        assertEquals("TestBook2", readBookDto.getTitle());
        assertEquals("TestPublisher2", readBookDto.getPublisher());
        assertEquals(testBook.getCategoryId(), readBookDto.getCategoryId());
        assertEquals(2000, readBookDto.getPublishingYear());
        assertEquals(testBook.getCategoryName(), readBookDto.getCategoryName());
    }

    @Test
    void deleteBook() {
        ResponseEntity<?> response1 = bookController.getAllBooks();
        List<ReadBookDto> allBooksBefore = (List<ReadBookDto>) response1.getBody();

        bookController.deleteBook(new IdAndTokenDto(testBook.getId(), requestToken));

        ResponseEntity<?> response2 = bookController.getAllBooks();
        List<ReadBookDto> allBooksAfter = (List<ReadBookDto>) response2.getBody();

        assertNotNull(allBooksAfter);
        assertNotNull(allBooksBefore);
        assertEquals(allBooksAfter.size(), allBooksBefore.size()-1);
    }



    private ReadCategoryDto createTestCategory(){
        WriteCategoryDto writeCategoryDto = new WriteCategoryDto("TestKategorie", "TestBeschreibung", requestToken);
        ResponseEntity<?> response1 = categoryController.createCategory(writeCategoryDto);
        return (ReadCategoryDto) response1.getBody();
    }

    private void deleteTestCategory(ReadCategoryDto category){
        categoryController.deleteCategory(new IdAndTokenDto(category.getId(), requestToken));
    }
}