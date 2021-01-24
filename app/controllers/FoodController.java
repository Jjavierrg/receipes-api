package controllers;

import com.google.inject.Inject;
import controllers.dto.FoodDto;
import mappers.IMapper;
import models.entities.Food;
import models.entities.Ingredient;

/**
 * Controller for {@link models.entities.Food} model
 */
public class FoodController extends BaseController<Food, FoodDto> {
    @Inject
    protected FoodController(IMapper<Food, FoodDto> mapper) {
        super(Food.class, FoodDto.class, mapper);
    }

    @Override
    public boolean canDelete(long id) {
        // check for related data
        var repo = this.repositoryFactory.getRepository(Ingredient.class);
        var existData = repo.finder.query().fetch("food").where().eq("food.id", id).exists();

        return !existData;
    }
}