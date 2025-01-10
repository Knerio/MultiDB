package de.derioo.multidb.repository;

import de.derioo.multidb.method.sort.Sort;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.List;

public interface Repository<E, ID> {

    /**
     * This method counts all documents of the collection in the mongodb.
     *
     * @return The amount of total entities in this repository.
     */
    long countMany();

    /**
     * This method deletes the given entity, by filtering with the entity's "@Id" field/unique identifier.
     *
     * @param entity The entity, which should be deleted.
     * @return true, if the entity was deleted successfully.
     */
    boolean delete(E entity);

    /**
     * This method deletes all entities of the repository.
     * Difference between drop is, that the indices stay the same.
     *
     * @return true, if entities were successfully deleted.
     */
    boolean deleteMany();

    /**
     * This method deletes the entity with the given identifier, filtering like the "#delete(E entity)" method.
     *
     * @param identifier The unique identifier of the entity, which should be deleted.
     * @return true, if the entity was deleted successfully.
     */
    boolean deleteById(ID identifier);

    /**
     * This method deletes all entities of the given list, filtering like the "#delete(E entity)" method.
     *
     * @param entityList The List with the entities, which should be deleted.
     * @return true, if all entities were deleted successfully.
     */
    boolean deleteMany(Collection<E> entityList);

    /**
     * This method deletes all entities with the id within the given list.
     *
     * @param idList The List with the ids of the entities, which should be deleted.
     * @return true, if all entities were deleted successfully.
     */
    boolean deleteManyById(Collection<ID> idList);


    /**
     * Checks if an entity with the given unique identifier exists in the repository, like the "#exists(E entity)" method.
     *
     * @param identifier The identifier of the entity, which should be checked.
     * @return true, if an entity with the given identifier exists in the collection.
     */
    boolean existsById(ID identifier);

    /**
     * Finds all entities of the collection
     *
     * @return A List with all entities of the repository.
     */
    List<E> findMany();

    /**
     * Find the first entity with the given unique identifier.
     * If the entity is not found, "null" is returned.
     *
     * @param identifier The unique identifier of the entity, which is used to filter.
     * @return The found entity, if it exists, or "null" if it not exists.
     */

    E findFirstById(ID identifier);


    /**
     * Saves the given entity to the database.
     * If the entity exists, the existing document is updated.
     * If the entity doesn't exist, a new document is created.
     *
     * @param entity The entity, which should be saved.
     * @return true, if the entity was successfully saved.
     */
    boolean save(E... entity);

    /**
     * This method uses the Bson object as a filter
     * <pre><code>
     * // Example usage
     *
     * repository.findFirstByFilter(Filters.eq("list.id", "someId")) // finds the entity based on a item in the "list"
     * </code></pre>
     * @param filter the filter
     * @return the first matching entity
     */
    E filterFirst(Bson filter);

    /**
     * Finds all entities, that match the filter
     * @param filter the filter
     * @return all matching entities by the filter
     * @see Repository#filterFirst(Bson)
     */
    List<E> filterMany(Bson filter);


}
