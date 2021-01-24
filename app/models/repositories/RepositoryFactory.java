package models.repositories;

import models.entities.BaseModel;

public class RepositoryFactory {
    public <T extends BaseModel> BaseRepository<T> getRepository(Class<T> type) {
        return new BaseRepository<>(type);
    }
}
