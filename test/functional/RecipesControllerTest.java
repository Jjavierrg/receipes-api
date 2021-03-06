package functional;

import controllers.ControllerTest;
import controllers.dto.*;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

public class RecipesControllerTest extends ControllerTest<RecipeDto> {
    public RecipesControllerTest() {
        super("/recipe", "/recipes");
    }

    @Override
    public RecipeDto getValidDto() {
        var recipe = new RecipeDto();
        recipe.name = "Test Recipe";
        recipe.description = "123456789012345678901234567890123456789012345678901";

        recipe.photo = new RecipePhotoDto();
        recipe.photo.width = 500;
        recipe.photo.height = 500;
        recipe.photo.url="http://www.testurl.com/test/image.png";
        recipe.photo.title="Test recipe photo";

        recipe.categories = Arrays.asList("Categoria 1", "Desayuno", "Categoria 2");
        recipe.ingredients = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            var ingredient = new IngredientDto();
            ingredient.quantity = i + 10;
            ingredient.food = "Food " + i;
            ingredient.measure = "gr";

            recipe.ingredients.add(ingredient);
        }

        return recipe;
    }

    @Override
    public RecipeDto getInvalidDto() {
        var recipe = this.getValidDto();
        recipe.description = "short desc";

        return recipe;
    }

    @Test
    public void shouldValidatePhoto() {
        var dto = this.getValidDto();
        dto.photo.url = null;
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));

        dto.photo.url = "invalid url";
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));
    }

    @Test
    public void shouldValidateIngredients() {
        var dto = this.getValidDto();
        dto.ingredients.get(0).measure = null;
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));

        dto.ingredients.get(0).measure = "2";
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));

        dto.ingredients.get(0).measure = "measure";
        dto.ingredients.get(0).quantity = 0;
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));

        dto.ingredients.get(0).quantity = 1;
        dto.ingredients.get(0).food = null;
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));

        dto.ingredients.get(0).food = "ab";
        assertEquals(BAD_REQUEST, launchPostRequestAndGetResponseCode(dto));
    }

    private int launchPostRequestAndGetResponseCode(RecipeDto dto) {
        var jsonDto = Json.toJson(dto);

        Http.RequestBuilder request = new Http.RequestBuilder()
                .uri(this.singleRoute)
                .method(POST)
                .bodyJson(jsonDto);

        Result result = route(app, request);
        return result.status();
    }
}

