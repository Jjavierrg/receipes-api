package converters.xml;

import controllers.dto.CategoryDto;
import org.w3c.dom.Node;
import play.mvc.Result;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.status;

public class CategoryConverter extends XmlConverter<CategoryDto> {
    @Override
    public Result toResult(int statusCode, List<CategoryDto> list) {
        return status(statusCode, views.xml.categories.render(list));
    }

    @Override
    public Result toResult(int statusCode, CategoryDto entity) {
        return status(statusCode, views.xml.category.render(entity));
    }

    @Override
    protected CategoryDto parseEntityFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        if (!node.getNodeName().equalsIgnoreCase("category"))
            throw new MalformedParametersException();

        var result = new CategoryDto();

        result.id = getLongTagValueIfPresent(node, "id");
        result.name = getTextTagValueIfPresent(node, "name");
        result.parent = this.parseEntityFromNode(this.getFirstNodeTagValueIfPresent(node, "parent"));
        result.children = this.parseListFromNode(this.getFirstNodeTagValueIfPresent(node, "children"));

        return result;
    }

    @Override
    protected List<CategoryDto> parseListFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        var result = new ArrayList<CategoryDto>();
        var children = this.getNodeTagListIfPresent(node, "category");

        children.forEach((x)-> {
            var category = this.parseEntityFromNode(x);
            if (category != null)
                result.add(category);
        });

        return result;
    }
}
