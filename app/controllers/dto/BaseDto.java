package controllers.dto;

import controllers.dto.validators.*;
import play.data.validation.Constraints;

public class BaseDto {
    @Constraints.Required(groups = {IPutValidator.class, IPatchValidator.class})
    public Long id;

    @Constraints.Required(groups = {IPutValidator.class, IPostValidator.class})
    @Constraints.MinLength(3)
    public String name;
}
