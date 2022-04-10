package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.IdAndTokenDto;
import de.kandels.librarydemo.dtos.UpdateCategoryDto;
import de.kandels.librarydemo.dtos.WriteCategoryDto;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.services.AuthService;
import de.kandels.librarydemo.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final String unauthorizedMessage = "Please Provide A Valid Request Token To Edit Categories.";

    @Autowired
    CategoryService categoryService;
    @Autowired
    AuthService authService;

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/get/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId){
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @PostMapping("/new")
    public ResponseEntity<?> createCategory (@RequestBody WriteCategoryDto writeCategoryDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(writeCategoryDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        return ResponseEntity.ok(categoryService.createCategory(writeCategoryDto));

    }
    @PutMapping("/update")
    public ResponseEntity<?> updateCategory (@RequestBody UpdateCategoryDto updateCategoryDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(updateCategoryDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            return ResponseEntity.ok(categoryService.updateCategory(updateCategoryDto));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCategory(@RequestBody IdAndTokenDto deleteIdDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(deleteIdDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            categoryService.deleteCategory(deleteIdDto.getId());
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


}
