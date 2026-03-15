package bank.factory;

import bank.domain.Category;
import bank.domain.OperationType;
import bank.factory.Impl.CategoryFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryFactoryTest {

    private final CategoryFactory factory = new CategoryFactory();

    @Test
    void create_ValidParams_ShouldReturnCategory() {
        Category category = factory.create("Зарплата", OperationType.INCOME, "Основной доход");
        assertNotNull(category);
        assertEquals("Зарплата", category.getName());
        assertEquals(OperationType.INCOME, category.getType());
        assertEquals("Основной доход", category.getDescription());
        assertNotNull(category.getId());
    }

    @Test
    void create_InsufficientParams_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> factory.create("Зарплата", OperationType.INCOME));
        assertEquals("Требуется имя, тип и описание", exception.getMessage());
    }
}