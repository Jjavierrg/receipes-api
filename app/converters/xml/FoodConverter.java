package converters.xml;

import controllers.dto.BaseDto;
import controllers.dto.FoodDto;
import org.w3c.dom.Node;
import play.mvc.Result;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static play.mvc.Results.status;

public class FoodConverter extends XmlConverter<FoodDto> {
    @Override
    public Result toResult(int statusCode, List<FoodDto> list) {
        return status(statusCode, views.xml.baseList.render(list.stream().map(x -> (BaseDto) x).collect(Collectors.toList()), "foods", "food"));
    }

    @Override
    public Result toResult(int statusCode, FoodDto entity) {
        return status(statusCode, views.xml.baseItem.render(entity, "food"));
    }

    @Override
    protected FoodDto parseEntityFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        if (!node.getNodeName().equalsIgnoreCase("food"))
            throw new MalformedParametersException();

        var result = new FoodDto();
        result.id = getLongTagValueIfPresent(node, "id");
        result.name = getTextTagValueIfPresent(node, "name");

        return result;
    }

    @Override
    protected List<FoodDto> parseListFromNode(Node node) {
        if (node == null || !node.hasChildNodes())
            return null;

        var result = new ArrayList<FoodDto>();
        var children = this.getNodeTagListIfPresent(node, "food");

        children.forEach((x)-> {
            var food = this.parseEntityFromNode(x);
            if (food != null)
                result.add(food);
        });

        return result;
    }
}
