package controllers.dto;

import java.util.List;

/**
 * Dto class for {@link models.entities.Category} model
 */
public class CategoryDto extends BaseDto {
    public CategoryDto parent;
    public List<CategoryDto> children;
}
