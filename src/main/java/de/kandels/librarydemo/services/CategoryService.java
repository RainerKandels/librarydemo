package de.kandels.librarydemo.services;

import de.kandels.librarydemo.dtos.ReadCategoryDto;
import de.kandels.librarydemo.dtos.UpdateCategoryDto;
import de.kandels.librarydemo.dtos.WriteCategoryDto;
import de.kandels.librarydemo.entities.Book;
import de.kandels.librarydemo.entities.Category;
import de.kandels.librarydemo.exceptions.RecordNotFoundException;
import de.kandels.librarydemo.repositories.BookRepository;
import de.kandels.librarydemo.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BookRepository bookRepository;


    public List<ReadCategoryDto> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<ReadCategoryDto> categoryDtos = new ArrayList<>();
        for (Category category: categories){
            categoryDtos.add(mapFromEntityToDto(category));
        }
        return categoryDtos;
    }

    public ReadCategoryDto getCategoryById(Long categoryId) throws RecordNotFoundException {
        Category category = getCategoryEntityById(categoryId);
        return mapFromEntityToDto(category);
    }

    public ReadCategoryDto createCategory(WriteCategoryDto writeCategoryDto){
        Category writeCategory = new Category(
                null,
                writeCategoryDto.getName(),
                writeCategoryDto.getDescription()
        );
        Category readCategory = categoryRepository.save(writeCategory);
        return mapFromEntityToDto(readCategory);
    }

    public ReadCategoryDto updateCategory(UpdateCategoryDto updateCategoryDto) throws RecordNotFoundException{
        Category original = getCategoryEntityById(updateCategoryDto.getId());
        Category writeCategory = new Category(
                updateCategoryDto.getId(),
                updateCategoryDto.getName(),
                updateCategoryDto.getDescription()
        );
        Category readCategory = categoryRepository.save(writeCategory);
        return mapFromEntityToDto(readCategory);
    }

    public void deleteCategory(Long categoryId){
        Category readCategory = getCategoryEntityById(categoryId);

        // When category is deleted all book references are set to null
        bookRepository.updateCategoryToNull(readCategory);

        categoryRepository.deleteById(categoryId);
    }

    /**
     * provides information about how many books are in a category
     * @param category The category that we want the book-count information for
     * @return Number of books that are referenced to be in this category
     */
    public Long countBooksInCategory(Category category){
        Book probe = new Book();
        probe.setCategory(category);
        return bookRepository.count(Example.of(probe));
    }

    private ReadCategoryDto mapFromEntityToDto(Category category){
        return new ReadCategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                countBooksInCategory(category)
                );
    }

    public Category getCategoryEntityById(Long categoryId) throws RecordNotFoundException {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()){
            throw new RecordNotFoundException("CategoryId not found.");
        }
        return category.get();
    }
}
