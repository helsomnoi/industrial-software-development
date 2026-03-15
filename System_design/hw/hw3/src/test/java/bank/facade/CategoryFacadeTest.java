package bank.facade;

import bank.domain.Category;
import bank.domain.OperationType;
import bank.repository.CategoryRepository;
import bank.factory.Impl.CategoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryFacadeTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryFactory categoryFactory;

    @InjectMocks
    private CategoryFacade categoryFacade;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category("Тест", OperationType.INCOME, "описание");
        testCategory.setId("cat-1");
    }

    @Test
    void create_Success() {
        when(categoryRepository.existsByNameAndType("Зарплата", OperationType.INCOME)).thenReturn(false);
        when(categoryFactory.create("Зарплата", OperationType.INCOME, "Доход")).thenReturn(testCategory);
        when(categoryRepository.save(any())).thenReturn(testCategory);

        Category result = categoryFacade.create("Зарплата", OperationType.INCOME, "Доход");

        assertNotNull(result);
        assertEquals(testCategory, result);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void create_Duplicate_ThrowsException() {
        when(categoryRepository.existsByNameAndType("Зарплата", OperationType.INCOME)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> categoryFacade.create("Зарплата", OperationType.INCOME, ""));
        assertTrue(exception.getMessage().contains("уже существует"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any())).thenReturn(testCategory);

        Category updated = categoryFacade.updateCategory("cat-1", "Новое имя", "Новое описание");

        assertEquals("Новое имя", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void getCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById("non-existent")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> categoryFacade.getCategory("non-existent"));
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        List<Category> categories = categoryFacade.getAllCategories();
        assertEquals(1, categories.size());
    }

    @Test
    void getCategoriesByType_ShouldReturnFiltered() {
        when(categoryRepository.findByType(OperationType.INCOME)).thenReturn(List.of(testCategory));
        List<Category> result = categoryFacade.getCategoriesByType(OperationType.INCOME);
        assertEquals(1, result.size());
    }

    @Test
    void deleteCategory_Success() {
        when(categoryRepository.findById("cat-1")).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).deleteById("cat-1");

        categoryFacade.deleteCategory("cat-1");
        verify(categoryRepository).deleteById("cat-1");
    }
}