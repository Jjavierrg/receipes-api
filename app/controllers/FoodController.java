package controllers;

import controllers.dto.BaseDto;
import models.entities.Food;

public class FoodController extends BaseController<Food, BaseDto> {
    protected FoodController() {
        super(Food.class, BaseDto.class);
    }

    @Override
    protected BaseDto toDto(Food entity) {
        if (entity == null)
            return null;

        var dto = new BaseDto();
        dto.id = entity.id;
        dto.name = entity.name;

        return dto;
    }

    @Override
    protected Food toEntity(BaseDto dto) {
        if (dto == null)
            return null;

        var entity = new Food();
        entity.id = dto.id;
        entity.name = dto.name;

        return entity;
    }
}