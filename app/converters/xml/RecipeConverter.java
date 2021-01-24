package converters.xml;

import controllers.dto.*;
import org.w3c.dom.Node;
import play.mvc.Result;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.status;

public class RecipeConverter extends XmlConverter<RecipeDto> {
    @Override
    public Result toResult(int statusCode, List<RecipeDto> list) {
        return status(statusCode, views.xml.recipes.render(list));
    }

    @Override
    public Result toResult(int statusCode, RecipeDto entity) {
        return status(statusCode, views.xml.recipe.render(entity));
    }

    @Override
    protected RecipeDto parseEntityFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        if (!node.getNodeName().equalsIgnoreCase("Recipe"))
            throw new MalformedParametersException();

        var result = new RecipeDto();

        result.id = getLongTagValueIfPresent(node, "id");
        result.name = getTextTagValueIfPresent(node, "name");
        result.description = getTextTagValueIfPresent(node, "description");
        result.photo = this.getPhoto(this.getFirstNodeTagValueIfPresent(node, "photo"));
        result.ingredients = this.getIngredients(this.getFirstNodeTagValueIfPresent(node, "ingredients"));
        result.categories = this.getCategories(this.getFirstNodeTagValueIfPresent(node, "categories"));

        return result;
    }

    @Override
    protected List<RecipeDto> parseListFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        var result = new ArrayList<RecipeDto>();
        var children = this.getNodeTagListIfPresent(node, "Recipe");

        children.forEach((x)-> {
            var recipe = this.parseEntityFromNode(x);
            if (recipe != null)
                result.add(recipe);
        });

        return result;
    }

    private RecipePhotoDto getPhoto(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        if (!node.getNodeName().equalsIgnoreCase("photo"))
            throw new MalformedParametersException();

        var result = new RecipePhotoDto();
        result.title = getTextTagValueIfPresent(node, "title");
        result.url = getTextTagValueIfPresent(node, "url");
        result.height = getIntTagValueIfPresent(node, "height");
        result.width = getIntTagValueIfPresent(node, "width");

        return result;
    }

    private IngredientDto getIngredient(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        if (!node.getNodeName().equalsIgnoreCase("ingredient"))
            throw new MalformedParametersException();

        var result = new IngredientDto();
        result.food = getTextTagValueIfPresent(node, "food");
        result.measure = getTextTagValueIfPresent(node, "measure");
        result.quantity = getIntTagValueIfPresent(node, "quantity");

        return result;
    }

    private List<IngredientDto> getIngredients(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        var result = new ArrayList<IngredientDto>();
        var children = this.getNodeTagListIfPresent(node, "ingredient");

        children.forEach((x)-> {
            var ingredient = this.getIngredient(x);
            if (ingredient != null)
                result.add(ingredient);
        });

        return result;
    }

    private List<String> getCategories(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        var result = new ArrayList<String>();
        var children = this.getNodeTagListIfPresent(node, "category");

        children.forEach((x)-> {
            var category = x.getTextContent();
            if (!category.isEmpty())
                result.add(category.trim());
        });

        return result;
    }
}
