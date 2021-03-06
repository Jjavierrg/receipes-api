package functional;

import controllers.ControllerTest;
import controllers.dto.*;

public class FoodControllerTest extends ControllerTest<FoodDto> {
    public FoodControllerTest() {
        super("/food", "/foods");
    }

    @Override
    public Long getIdForDeleteOperation() { return 5L; }

    @Override
    public FoodDto getValidDto() {
        var food = new FoodDto();
        food.name = "TEST NAME";

        return food;
    }

    @Override
    public FoodDto getInvalidDto() {
        var food = new FoodDto();
        food.name = "T";

        return food;
    }
}
