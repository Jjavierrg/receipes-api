package models.repositories;

import models.entities.*;

public class Context {
    public final BaseRepository<Category> categoryRepository = new BaseRepository(Category.class);
    public final BaseRepository<Food> foodRepository = new BaseRepository(Food.class);
    public final BaseRepository<Ingredient> ingredientRepository = new BaseRepository(Ingredient.class);
    public final BaseRepository<Measure> measureRepository = new BaseRepository(Measure.class);
    public final BaseRepository<Recipe> recipeRepository = new BaseRepository(Recipe.class);
    public final BaseRepository<RecipePhoto> recipePhotoRepository = new BaseRepository(RecipePhoto.class);
}
