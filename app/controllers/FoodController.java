package controllers;

import controllers.dto.BaseDto;
import models.entities.Food;
import models.entities.Ingredient;
import models.repositories.BaseRepository;
import play.twirl.api.Content;

import java.util.List;

/**
 * Controller for {@link models.entities.Food} model
 */
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

    @Override
    protected Content getXMLListContent(List<BaseDto> list) {
        return views.xml.baseList.render(list, "foods", "food");
    }

    @Override
    protected Content getXMLEntityContent(BaseDto entity) {
        return views.xml.baseItem.render(entity, "food");
    }

    @Override
    public boolean canDelete(long id) {
        // check for related data
        var repo = new BaseRepository<>(Ingredient.class);
        var existData = repo.finder.query().fetch("food").where().eq("food.id", id).exists();

        return  !existData;
    }
}