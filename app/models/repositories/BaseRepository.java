package models.repositories;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Query;
import io.ebean.Transaction;
import models.entities.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for data access
 *
 * @param <T> Type associated to repository
 */
public class BaseRepository<T extends BaseModel> {
    private final Class<T> type;
    public final Finder<Long, T> finder;

    public BaseRepository(Class<T> type) {
        this.type = type;
        this.finder = new Finder<>(type);
    }

    /**
     * Begin transaction operation
     *
     * @return {@link io.ebean.Transaction}
     */
    public Transaction beginTransaction() {
        return this.finder.db().beginTransaction();
    }

    /**
     * Return first entity that match query criteria. If no results, return empty
     *
     * @param query DbQuery
     */
    public Optional<T> getFirstOrDefault(Query<T> query) {
        return query.setMaxRows(1).findOneOrEmpty();
    }

    /**
     * Return first entity that match query criteria. If no results, return empty
     *
     * @param expressionList DbQuery expression list
     */
    public Optional<T> getFirstOrDefault(ExpressionList<T> expressionList) {
        return this.getFirstOrDefault(expressionList.query());
    }

    /**
     * Verify if exist a entry of {@link T} with the required id
     *
     * @param id Primary id of entity
     * @return true if id exist, otherwise false.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean existId(Long id) {
        return finder.query().where().eq("id", id).exists();
    }

    /**
     * Get {@link T} object with the required id
     *
     * @param id Primary id of entity
     * @return {@link T} object
     */
    public T getById(Long id) {
        return finder.byId(id);
    }

    /**
     * Get a {@link List<T>} list with all entries of type {@link T}
     *
     * @return {@link List<T>}
     */
    public List<T> findAll() {
        return finder.all();
    }

    /**
     * Get a {@link List<T>} list with all entries of type {@link T} according to query criteria
     *
     * @param query query with the criteria to apply to get operation
     * @return {@link List<T>}
     */
    public List<T> findAll(Query<T> query) {
        return query.findList();
    }

    /**
     * Insert a {@link T} into database table
     *
     * @param entity object to persist into database
     * @return {@link T} with the updated info
     */

    public T insert(T entity) {
        this.finder.db().save(entity);
        return entity;
    }

    /**
     * Update a {@link T} into database table
     *
     * @param entity object to update into database
     * @return {@link T} with the updated info
     */
    public T update(T entity) {
        this.finder.db().update(entity);
        return entity;
    }

    /**
     * Remove the entry row from database table with the requested id
     *
     * @param id Primary id of entity
     * @return {@link T} with the updated info
     */
    public boolean deleteById(Long id) {
        this.finder.deleteById(id);
        return true;
    }

    /**
     * Remove a {@link T} from database table
     *
     * @param entity object to remove from database
     * @return true if was deleted successfully
     */
    public boolean delete(T entity) {
        var result = this.finder.db().delete(entity);
        //noinspection UnusedAssignment
        entity = null;
        return result;
    }

    /**
     * Update {@link T} not null properties into database table
     *
     * @param entity object to update into database
     * @return {@link T} with the updated info
     */
    public T updatePartial(T entity) {
        if (!this.existId(entity.id))
            return null;

        List<Field> fields = new ArrayList<>();
        Class<?> cls = entity.getClass();

        do {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        } while (cls != BaseModel.class);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()))
                continue;

            try {
                if (!field.canAccess(entity) || field.get(entity) == null)
                    entity.markPropertyUnset(field.getName());
            } catch (Exception e) {
                return null;
            }
        }

        return this.update(entity);
    }
}
