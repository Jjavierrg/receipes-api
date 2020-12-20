package models.repositories;

import io.ebean.Finder;
import models.entities.BaseModel;

import java.util.List;

public class BaseRepository<T extends BaseModel> {
    private final Class<T> type;
    private final Finder<Long, T> finder;

    public BaseRepository(Class<T> type) {
        this.type = type;
        this.finder= new Finder<>(type);
    }

    public T getById(Long id) { return finder.byId(id); }
    public List<T> findAll() { return finder.query().findList(); }
}
