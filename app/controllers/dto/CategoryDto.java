package controllers.dto;

import java.util.List;

public class CategoryDto extends BaseDto {
    public CategoryDto parent;
    public List<CategoryDto> children;
}
