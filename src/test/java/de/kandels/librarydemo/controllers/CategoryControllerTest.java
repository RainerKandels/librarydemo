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
class CategoryControllerTest {

    @Autowired
    CategoryController categoryController;
    @Autowired
    AuthController authController;
    @Autowired
    CustomerService customerService;

    //Root User Credentials
    private final String email = "test2@mail.de";
    private final String password = "root";
    private ReadCustomerDto testCustomer;
    private String requestToken;

    private ReadCategoryDto testCategory;

    @BeforeEach
    void setUp() {
        testCustomer = customerService.createCustomer(new WriteCustomerDto("TestName", email, password, null));
        RequestTokenDto requestTokenDto = (RequestTokenDto) authController.login(new LoginCustomerDto(email, password)).getBody();
        requestToken = requestTokenDto.getRequestToken();
        testCategory = createTestCategory();

    }

    @AfterEach
    void tearDown() {
        deleteTestCategory(testCategory);
        customerService.deleteCustomer(testCustomer.getId());
    }


    @Test
    void getAllCategories() {
        ResponseEntity<?> response1 = categoryController.getAllCategories();
        List<ReadCategoryDto> allCategories = (List<ReadCategoryDto>) response1.getBody();
        assertNotNull(allCategories);
        assertTrue(allCategories.size() > 0);
    }

    @Test
    void getCategoryById() {

        ResponseEntity<?> response1 = categoryController.getCategoryById(testCategory.getId());
        ReadCategoryDto readCategoryDto = (ReadCategoryDto) response1.getBody();
        assertNotNull(readCategoryDto);
        assertEquals(readCategoryDto.getId(), testCategory.getId());
        assertEquals(readCategoryDto.getName(), testCategory.getName());
        assertEquals(readCategoryDto.getDescription(), testCategory.getDescription());
        assertEquals(0, readCategoryDto.getNumberOfBooksInCategory());


    }

    @Test
    void createCategory() {
        WriteCategoryDto writeCategoryDto = new WriteCategoryDto("TestKategorie3", "TestBeschreibung3", requestToken);
        ResponseEntity<?> response1 = categoryController.createCategory(writeCategoryDto);
        ReadCategoryDto createdCategory = (ReadCategoryDto) response1.getBody();
        assertNotNull(createdCategory);
        assertEquals(writeCategoryDto.getName(), createdCategory.getName());
        assertEquals(writeCategoryDto.getDescription(), createdCategory.getDescription());
        assertEquals(0,createdCategory.getNumberOfBooksInCategory());

        deleteTestCategory(createdCategory);
    }

    @Test
    void updateCategory() {
        UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto(
                testCategory.getId(),
                "Updated Category",
                "Updated Description",
                null);

        ReadCategoryDto readCategoryDto = (ReadCategoryDto) categoryController.updateCategory(updateCategoryDto).getBody();
        assertNotNull(readCategoryDto);
        assertEquals(testCategory.getId(), readCategoryDto.getId());
        assertEquals(testCategory.getNumberOfBooksInCategory(), readCategoryDto.getNumberOfBooksInCategory());
        assertEquals("Updated Category", readCategoryDto.getName());
        assertEquals("Updated Description", readCategoryDto.getDescription());
    }

    @Test
    void deleteCategory() {
        ResponseEntity<?> response1 = categoryController.getAllCategories();
        List<ReadCategoryDto> allCategoriesBefore = (List<ReadCategoryDto>) response1.getBody();

        categoryController.deleteCategory(new IdAndTokenDto(testCategory.getId(), requestToken));

        ResponseEntity<?> response2 = categoryController.getAllCategories();
        List<ReadCategoryDto> allCategoriesAfter = (List<ReadCategoryDto>) response2.getBody();

        assertNotNull(allCategoriesAfter);
        assertNotNull(allCategoriesBefore);
        assertEquals(allCategoriesAfter.size(), allCategoriesBefore.size() - 1);
    }

//    Helpers

    private ReadCategoryDto createTestCategory(){
        WriteCategoryDto writeCategoryDto = new WriteCategoryDto("TestKategorie", "TestBeschreibung", requestToken);
        ResponseEntity<?> response1 = categoryController.createCategory(writeCategoryDto);
        return (ReadCategoryDto) response1.getBody();
    }

    private void deleteTestCategory(ReadCategoryDto category){
        try {
            categoryController.deleteCategory(new IdAndTokenDto(category.getId(), requestToken));
        } catch (RecordNotFoundException ignore){

        }
    }
}