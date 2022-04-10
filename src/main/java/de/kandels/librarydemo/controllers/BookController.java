package de.kandels.librarydemo.controllers;

import de.kandels.librarydemo.dtos.IdAndTokenDto;
import de.kandels.librarydemo.dtos.ReadBookDto;
import de.kandels.librarydemo.dtos.UpdateBookDto;
import de.kandels.librarydemo.dtos.WriteBookDto;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.services.AuthService;
import de.kandels.librarydemo.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final String unauthorizedMessage = "Please Provide A Valid Request Token To Edit Books.";

    @Autowired
    BookService bookService;
    @Autowired
    AuthService authService;


    @GetMapping("")
    public ResponseEntity<List<ReadBookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/get/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Long bookId){
        try {
            return ResponseEntity.ok(bookService.getBookById(bookId));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createBook(@RequestBody WriteBookDto writeBookDto){

        //check for valid request-token
        if (authService.checkRequestTokenValidity(writeBookDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            return ResponseEntity.ok(bookService.createBook(writeBookDto));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateBook(@RequestBody UpdateBookDto updateBookDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(updateBookDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            return ResponseEntity.ok(bookService.updateBook(updateBookDto));
        } catch (RecordNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBook(@RequestBody IdAndTokenDto deleteBookDto){
        //check for valid request-token
        if (authService.checkRequestTokenValidity(deleteBookDto.getRequestToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorizedMessage);
        }

        try {
            bookService.deleteBook(deleteBookDto.getId());
            return ResponseEntity.ok().build();
        } catch(EmptyResultDataAccessException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
