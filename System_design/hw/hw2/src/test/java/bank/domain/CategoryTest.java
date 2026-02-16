package bank.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void constructor_ShouldCreateCategoryWithCorrectFields() {
        Category category = new Category("Зарплата", OperationType.INCOME, "Основной доход");

        assertNotNull(category.getId());
        assertEquals("Зарплата", category.getName());
        assertEquals(OperationType.INCOME, category.getType());
        assertEquals("Основной доход", category.getDescription());
        assertNotNull(category.getCreatedAt());
        assertTrue(category.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void setters_ShouldUpdateFields() {
        Category category = new Category("Старое имя", OperationType.EXPENSE, "Старое описание");
        category.setName("Новое имя");
        category.setDescription("Новое описание");

        assertEquals("Новое имя", category.getName());
        assertEquals("Новое описание", category.getDescription());
    }

}