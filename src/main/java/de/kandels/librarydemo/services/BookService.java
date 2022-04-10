package de.kandels.librarydemo.services;

import de.kandels.librarydemo.dtos.ReadBookDto;
import de.kandels.librarydemo.dtos.UpdateBookDto;
import de.kandels.librarydemo.dtos.WriteBookDto;
import de.kandels.librarydemo.entities.Book;
import de.kandels.librarydemo.entities.Category;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class BookService {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    CategoryService categoryService;


    public List<ReadBookDto> getAllBooks(){
        List<Book> books =  bookRepository.findAll();
        List<ReadBookDto> bookDtos = new ArrayList<>();
        for (Book book: books){
            bookDtos.add(mapFromEntityToDto(book));
        }
        return bookDtos;
    }

    public ReadBookDto getBookById(Long bookId) throws RecordNotFoundException {
        Book book = getBookEntityById(bookId);

        return mapFromEntityToDto(book);
    }

    public ReadBookDto createBook(WriteBookDto writeBookDto) throws RecordNotFoundException {
        Category category = categoryService.getCategoryEntityById(writeBookDto.getCategoryId());
        Book writeBook = new Book(
                null,
                writeBookDto.getTitle(),
                writeBookDto.getAuthor(),
                writeBookDto.getPublisher(),
                writeBookDto.getPublishingYear(),
                category
        );
        Book readBook = bookRepository.save(writeBook);
        return mapFromEntityToDto(readBook);
    }

    public ReadBookDto updateBook(UpdateBookDto updateBookDto) throws RecordNotFoundException {
        Category category = categoryService.getCategoryEntityById(updateBookDto.getCategoryId());

        Book originalBook = getBookEntityById(updateBookDto.getId());

        Book updateBook = new Book(
                updateBookDto.getId(),
                updateBookDto.getTitle(),
                updateBookDto.getAuthor(),
                updateBookDto.getPublisher(),
                updateBookDto.getPublishingYear(),
                category
        );
        Book readBook =  bookRepository.save(updateBook);
        return mapFromEntityToDto(readBook);
    }

    public void deleteBook(Long bookId){
        bookRepository.deleteById(bookId);
    }


    private ReadBookDto mapFromEntityToDto(Book book) {
        return new ReadBookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishingYear(),
                book.getCategory().getId(),
                book.getCategory().getName()
        );
    }

    private Book getBookEntityById(Long bookId){
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()){
            throw new RecordNotFoundException("BookId not found.");
        }
        return book.get();
    }
}
