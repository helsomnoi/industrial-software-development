package bank.facade;

import bank.domain.Category;
import bank.domain.OperationType;
import bank.factory.Impl.CategoryFactory;
import bank.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CategoryFacade {
    private final CategoryRepository categoryRepository;
    private final CategoryFactory categoryFactory;

    public Category create(String name, OperationType type, String description) {
        validateCategory(name, type);
        Category category = categoryFactory.create(name, type, description);
        return categoryRepository.save(category);
    }

    public Category updateCategory(String categoryId, String newName, String newDescription) {
        Category category = getCategory(categoryId);
        category.setName(newName);
        category.setDescription(newDescription);
        return categoryRepository.save(category);
    }

    public void deleteCategory(String categoryId) {
        Category category = getCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    public Category getCategory(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Категория "
                        + categoryId + " не найдена"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByType(OperationType type) {
        return categoryRepository.findByType(type);
    }

    private void validateCategory(String name, OperationType type) {
        if (categoryRepository.existsByNameAndType(name, type)) {
            throw new IllegalArgumentException("Категория с именем "
            + name + " и типом " + type + "уже существует");
        }
    }
}
