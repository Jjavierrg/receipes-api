package models.repositories;

import io.ebean.Finder;
import models.entities.BaseModel;

import java.util.List;

public class BaseRepository<T extends BaseModel> {
    private final Class<T> type;
    public final Finder<Long, T> finder;

    public BaseRepository(Class<T> type) {
        this.type = type;
        this.finder= new Finder<>(type);
    }


    public boolean existId(Long id) { return finder.query().where().eq("id", id).exists(); }
    public T getById(Long id) { return finder.byId(id); }
    public List<T> findAll() { return finder.all(); }
    public T insert(T entity) { this.finder.db().save(entity); return entity;}
    public T update(T entity) { this.finder.db().update(entity); return entity;}

    public boolean deleteById(Long id) { this.finder.deleteById(id); return true; }
    public boolean delete(T entity) {
        var result = this.finder.db().delete(entity);
        entity = null;
        return result;
    }
}
