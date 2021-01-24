package mappers;

import controllers.dto.FoodDto;
import models.entities.Food;

import java.util.HashMap;

public class FoodMapper implements IMapper<Food, FoodDto> {
    @Override
    public FoodDto toDto(Food entity) {
        if (entity == null)
            return null;

        var dto = new FoodDto();
        dto.id = entity.id;
        dto.name = entity.name;

        return dto;
    }

    @Override
    public Food toEntity(FoodDto dto) {
        if (dto == null)
            return null;

        var entity = new Food();
        entity.id = dto.id;
        entity.name = dto.name;

        return entity;
    }

    @Override
    public HashMap<String, String> getFieldRelations() {
        return new HashMap<>();
    }
}
