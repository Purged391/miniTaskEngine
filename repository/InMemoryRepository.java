package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import exception.DuplicateEntityException;
import interfaces.Identifiable;

/** Hash-map-backed repository implementation intended for in-memory use. */
public class InMemoryRepository<ID, T extends Identifiable<ID>> implements Repository<ID,T> {

    private final Map<ID,T> storage = new HashMap<>();

    /** Stores an entity using its identifier as the key. */
    @Override
    public void save(T entity) {
        if(storage.containsKey(entity.id())){
            throw new DuplicateEntityException("Task already exist");
        }
        storage.put(entity.id(), entity);
    }

    /** Looks up an entity without exposing the internal map. */
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /** Returns an immutable snapshot of the stored values. */
    @Override
    public List<T> findAll() {
        return List.copyOf(storage.values());
    }

    /** Removes the entity associated with the identifier, if present. */
    @Override
    public boolean deleteById(ID id) {
        return storage.remove(id) != null;
    }

    /** Filters stored entities using the supplied predicate. */
    @Override
    public List<T> findWhere(Predicate<T> condition) {
        return storage.values().stream().filter(condition).toList();
    }
}
