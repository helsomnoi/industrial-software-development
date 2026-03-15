package bank.repository.Impl;

import bank.domain.Category;
import bank.domain.OperationType;
import bank.repository.CategoryRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    private final Map<String, Category> storage = new ConcurrentHashMap<>();

    @Override
    public Category save(Category category) {
        storage.put(category.getId(), category);
        return category;
    }

    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Category> findByType(OperationType type) {
        return storage.values().stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }

    @Override
    public boolean existsByNameAndType(String name, OperationType type) {
        return storage.values().stream()
                .anyMatch(c -> c.getName().equals(name) && c.getType() == type);
    }


}