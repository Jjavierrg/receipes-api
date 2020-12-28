package controllers.dto;

import java.util.List;

public class RecipeDto extends BaseDto {
    public String description;
    public List<String> categories;
    public List<IngredientDto> ingredients;
    public RecipePhotoDto photo;
}
