package bank.repository;

import bank.domain.Category;
import bank.domain.OperationType;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    List<Category> findAll();
    List<Category> findByType(OperationType type);
    void deleteById(String id);
    boolean existsByNameAndType(String name, OperationType type);
}