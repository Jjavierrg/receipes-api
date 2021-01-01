package controllers.dto;

import controllers.dto.validators.*;
import play.data.validation.Constraints;

/**
 * Dto class for {@link models.entities.Ingredient} model inside recipe.
 */
public class IngredientDto {
    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @Constraints.Min(1)
    public int quantity;

    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @Constraints.MinLength(3)
    public String food;

    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @Constraints.MinLength(2)
    public String measure;
}
