package converters.xml;

import controllers.dto.BaseDto;
import controllers.dto.RecipeDto;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.status;

public abstract class XmlConverter<TDto extends BaseDto> implements IXmlConverter<TDto> {
    @Override
    public TDto toEntity(Http.RequestBody body) {
        var document = body.asXml();
        if (document == null || !document.hasChildNodes())
            return null;

        return this.parseEntityFromNode(document.getFirstChild());
    }

    protected abstract TDto parseEntityFromNode(Node node);
    protected abstract List<TDto> parseListFromNode(Node node);

    @Override
    public Result toResult(int statusCode, List<TDto> list) {
        return status(statusCode);
    }

    @Override
    public Result toResult(int statusCode, TDto entity) {
        return status(statusCode);
    }

    /**
     * Check if {@link Node} has a tag with desired name
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected boolean tagValuePresent(Node node, String tagName) {
        var list = this.getNodeTagListIfPresent(node, tagName);
        return (list != null && list.size() > 0);
    }

    /**
     * Get all node content if is present in node parameter a tag with desired name
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected Node getFirstNodeTagValueIfPresent(Node node, String tagName) {
        var list = this.getNodeTagListIfPresent(node, tagName);
        var first = list.stream().findFirst();

        return first.orElse(null);
    }

    /**
     * Get children node list with tag
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected List<Node> getNodeTagListIfPresent(Node node, String tagName) {
        var element = (Element) node;

        var nodeList = new ArrayList<Node>();
            for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
                if (child.getNodeType() == Node.ELEMENT_NODE && tagName.equalsIgnoreCase(child.getNodeName())) {
                    nodeList.add(child);
                }
            }

            return nodeList;
    }

    /**
     * Get all node value as text if is present in node parameter a tag with desired name
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected String getTextTagValueIfPresent(Node node, String tagName) {
        var tagNode = getFirstNodeTagValueIfPresent(node, tagName);
        if (tagNode != null)
            return tagNode.getChildNodes().item(0).getNodeValue();

        return null;
    }

    /**
     * Get all node value as Long if is present in node parameter a tag with desired name
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected Long getLongTagValueIfPresent(Node node, String tagName) {
        var textValue = this.getTextTagValueIfPresent(node, tagName);
        if (textValue == null || textValue.isEmpty())
            return null;

        return Long.parseLong(textValue.trim());
    }

    /**
     * Get all node value as Long if is present in node parameter a tag with desired name
     * @param node {@link Node} node to check
     * @param tagName tag name to find
     */
    protected Integer getIntTagValueIfPresent(Node node, String tagName) {
        var textValue = this.getTextTagValueIfPresent(node, tagName);
        if (textValue == null || textValue.isEmpty())
            return null;

        return Integer.parseInt(textValue.trim());
    }
}
