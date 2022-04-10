package de.kandels.librarydemo.repositories;

import de.kandels.librarydemo.entities.Book;
import de.kandels.librarydemo.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    /**
     * When a category is deleted, all books that reference this category
     * are modified to put null in as a category
     * @param originalCategory the category that will get deleted
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Book b set b.category = null where b.category =:originalCategory")
    void updateCategoryToNull(@Param("originalCategory") Category originalCategory);
}