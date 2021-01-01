package controllers.dto;

import controllers.dto.validators.*;
import play.data.validation.Constraints;

import javax.validation.Valid;
import java.util.List;

/**
 * Dto class for {@link models.entities.Recipe} model
 */
public class RecipeDto extends BaseDto {
    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @Constraints.MinLength(50)
    public String description;

    @Valid()
    public List<String> categories;
    @Valid()
    public List<IngredientDto> ingredients;
    @Valid()
    public RecipePhotoDto photo;
}
