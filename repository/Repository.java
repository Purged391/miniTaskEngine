package repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import interfaces.Identifiable;

/** Defines CRUD-style operations for identifiable entities. */
public interface Repository<ID, T extends Identifiable<ID>> {
    /** Stores an entity, rejecting an already used identifier. */
    void save(T entity);

    /** Finds an entity by identifier, if it exists. */
    Optional<T> findById(ID id);

    /** Returns a snapshot containing every stored entity. */
    List<T> findAll();

    /** Deletes an entity and reports whether one was removed. */
    boolean deleteById(ID id);

    /** Returns entities that satisfy the supplied condition. */
    List<T> findWhere(Predicate<T> condition);
}
