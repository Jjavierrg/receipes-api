package mappers;

import controllers.dto.BaseDto;
import models.entities.BaseModel;
import models.entities.Recipe;

import java.util.HashMap;

/**
 * Create a relation between an {@link T} entity and {@link TDto} representation
 *
 * @param <T>    Database entity
 * @param <TDto> Front representation
 */
public interface IMapper<T extends BaseModel, TDto extends BaseDto> {
    /**
     * Transform {@link T} entity to {@link TDto} representation
     *
     * @param entity Database entity
     */
    TDto toDto(T entity);

    /**
     * Transform {@link TDto} representation to {@link T} entity
     *
     * @param dto Front representation object
     */
    T toEntity(TDto dto);

    /**
     * Get field correlation between {@link T} fields and {@link TDto} fields
     */
    HashMap<String, String> getFieldRelations();
}
