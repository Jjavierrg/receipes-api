package mappers;

import com.google.inject.Inject;
import controllers.dto.*;
import models.entities.*;
import models.repositories.RepositoryFactory;

import java.util.HashMap;
import java.util.stream.Collectors;

public class RecipeMapper implements IMapper<Recipe, RecipeDto> {
    private final RepositoryFactory repositoryFactory;

    @Inject
    public RecipeMapper(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public RecipeDto toDto(Recipe entity) {
        if (entity == null)
            return null;

        var dto = new RecipeDto();
        dto.id = entity.id;
        dto.name = entity.title;
        dto.description = entity.description;

        if (entity.categories != null) {
            dto.categories = entity.categories.stream().map(x -> x.name).collect(Collectors.toList());
        }

        if (entity.ingredients != null) {
            dto.ingredients = entity.ingredients.stream().map(x -> {
                var child = new IngredientDto();
                child.measure = x.measure.description;
                child.food = x.food.name;
                child.quantity = x.quantity;
                return child;
            }).collect(Collectors.toList());
        }

        if (entity.photo != null) {
            dto.photo = new RecipePhotoDto();
            dto.photo.title = entity.photo.title;
            dto.photo.url = entity.photo.url;
            dto.photo.width = entity.photo.width;
            dto.photo.height = entity.photo.height;
        }

        return dto;
    }

    @Override
    public Recipe toEntity(RecipeDto dto) {
        if (dto == null)
            return null;

        var entity = new Recipe();
        entity.id = dto.id;
        entity.title = dto.name;
        entity.description = dto.description;

        if (dto.categories != null) {
            entity.categories = dto.categories.stream().map(this::getCategoryByName).collect(Collectors.toList());
        }

        if (dto.ingredients != null) {
            entity.ingredients = dto.ingredients.stream().map(this::getIngredientByIngredientDto).collect(Collectors.toList());
        }

        if (dto.photo != null) {
            entity.photo = this.getRecipePhoto(dto.id);
            entity.photo.title = dto.photo.title;
            entity.photo.url = dto.photo.url;
            entity.photo.width = dto.photo.width;
            entity.photo.height = dto.photo.height;
        }

        return entity;
    }

    private Category getCategoryByName(String name) {
        var repo = this.repositoryFactory.getRepository(Category.class);
        var category = repo.finder.query().where().ieq("name", name).setMaxRows(1).findOneOrEmpty();

        if (category.isPresent())
            return category.get();

        var cat = new Category();
        cat.name = name;
        return repo.insert(cat);
    }

    private Ingredient getIngredientByIngredientDto(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.quantity = dto.quantity;
        ingredient.food = this.getFoodByName(dto.food);
        ingredient.measure = this.getMeasureByDescription(dto.measure);

        return ingredient;
    }

    private Food getFoodByName(String foodName) {
        var foodRepo = this.repositoryFactory.getRepository(Food.class);
        var food = foodRepo.finder.query().where().ieq("name", foodName).findOneOrEmpty();

        if (food.isPresent())
            return food.get();

        var newFood = new Food();
        newFood.name = foodName;
        return foodRepo.insert(newFood);

    }

    private Measure getMeasureByDescription(String measureDescription) {
        var measureRepo = this.repositoryFactory.getRepository(Measure.class);
        var measure = measureRepo.finder.query().where().ieq("description", measureDescription).findOneOrEmpty();

        if (measure.isPresent())
            return measure.get();

        var meas = new Measure();
        meas.description = measureDescription;
        return measureRepo.insert(meas);
    }

    private RecipePhoto getRecipePhoto(Long recipeId) {
        if (recipeId == null || recipeId < 0)
            return new RecipePhoto();

        var photoRepo = this.repositoryFactory.getRepository(RecipePhoto.class);
        var photo = photoRepo.finder.query().fetch("recipe").where().eq("recipe.id", recipeId).findOneOrEmpty();

        return photo.orElse(new RecipePhoto());
    }

    @Override
    public HashMap<String, String> getFieldRelations() {
        var fieldMapping = new HashMap<String, String>();
        fieldMapping.put("name", "title");
        fieldMapping.put("categories", "categories.name");
        fieldMapping.put("ingredients.measure", "ingredients.measure.description");
        fieldMapping.put("ingredients.food", "ingredients.food.name");

        return fieldMapping;
    }
}
