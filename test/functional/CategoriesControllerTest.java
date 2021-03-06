package functional;

import controllers.ControllerTest;
import controllers.dto.BaseDto;
import controllers.dto.CategoryDto;
import controllers.dto.FoodDto;

public class CategoriesControllerTest extends ControllerTest<CategoryDto> {
    public CategoriesControllerTest() {
        super("/category", "/categories");
    }

    @Override
    public Long getIdForDeleteOperation() { return 3L; }

    @Override
    public CategoryDto getValidDto() {
        var category = new CategoryDto();
        category.name = "TEST NAME";

        return category;
    }

    @Override
    public CategoryDto getInvalidDto() {
        var category = new CategoryDto();
        category.name = "T";

        return category;
    }
}
