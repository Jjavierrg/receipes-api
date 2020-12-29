package controllers.dto;

import controllers.dto.validators.*;
import org.hibernate.validator.constraints.URL;
import play.data.validation.Constraints;

public class RecipePhotoDto {
    public String title;
    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @URL
    public String url;
    public int width;
    public int height;
}
