package interpreter;

import java.lang.reflect.MalformedParametersException;
import java.util.*;

/***
 * Default grammatical parser interface for filtering params
 */
public class ExpressionParser implements IParser {
    private final List<String> logical = Arrays.asList("and", "or");
    private Map<String, String> fieldCorrelations;

    public ExpressionParser() { this(new HashMap<>()); }

    public ExpressionParser(Map<String, String> fieldCorrelations) {
        this.fieldCorrelations = fieldCorrelations == null ? new HashMap<>() : fieldCorrelations;
    }

    @Override
    public IExpression parse(String expession) {
        var tokens = tokenizeString(expession);
        return getExpressionTree(tokens);
    }

    /**
     * Get a expression with a tree representation for desired tokens
     * @param tokens list of elements with which the expression is generated
     * @return Expression according to token list
     */
    private IExpression getExpressionTree(List<String> tokens) {
        var tokensToEvaluate = new ArrayList<String>();
        IExpression previousExpession = null;
        var operator = "";

        for (String token : tokens) {
            if (logical.contains(token.toLowerCase())) {
                if (!operator.isEmpty()) {
                    var expr = getExpressionTree(tokensToEvaluate);
                    previousExpession = getLogicalExpression(expr, previousExpession, operator);
                } else {
                    previousExpession = getFinalExpression(tokensToEvaluate);
                }
                operator = token;
                tokensToEvaluate.clear();
            } else
                tokensToEvaluate.add(token);
        }

        if (!operator.isEmpty()) {
            var expr = getExpressionTree(tokensToEvaluate);
            return getLogicalExpression(expr, previousExpession, operator);
        }

        return getFinalExpression(tokensToEvaluate);
    }

    /**
     * Check if a token is a expression that is needed to evaluate
     * @param token token string representation
     * @return true if token contains a expression. False if token is a final node
     */
    private boolean isUnaryExpression(String token) {
        return token.startsWith("(") && token.endsWith(")");
    }

    /**
     * Evaluate a token an get the associated expression
     * @param token token string representation
     * @return Associated token expression
     */
    private IExpression getUnaryExpression(String token) {
        if (!isUnaryExpression(token))
            throw new MalformedParametersException();

        var tokens = tokenizeString(token.substring(1, token.length() - 1));
        return getExpressionTree(tokens);
    }

    /**
     * Get the corresponding Logical expression according string operator
     * @param expr1 First side of logical expression
     * @param expr2 Second side of logical expression
     * @param operator Logical operator to be applied
     * @return Logical expression
     */
    private IExpression getLogicalExpression(IExpression expr1, IExpression expr2, String operator) {
        if (operator.equalsIgnoreCase("and"))
            return new AndExpression(expr1, expr2);
        else if (operator.equalsIgnoreCase("or"))
            return new OrExpression(expr1, expr2);
        else
            throw new MalformedParametersException();
    }

    /**
     * Get final filter expression from token list
     * @param tokens list of tokens to generate final expression
     * @return According filter expression
     */
    private IExpression getFinalExpression(List<String> tokens) {
        if (tokens.size() == 1 && isUnaryExpression(tokens.get(0)))
            return getUnaryExpression(tokens.get(0));

        String propName = tokens.get(0);
        String operator = tokens.get(1);
        String rawValue = tokens.get(2);
        Object parsedValue;

        propName = this.fieldCorrelations.getOrDefault(propName.toLowerCase(), propName);

        if (rawValue.startsWith("'") && rawValue.endsWith("'"))
            parsedValue = rawValue.substring(1, rawValue.length() - 1);
        else {
            try {
                parsedValue = Double.parseDouble(rawValue);
            } catch (Exception e) {
                parsedValue = rawValue;
            }
        }

        if (operator.equalsIgnoreCase("eq"))
            return new EqExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("ne"))
            return new NeqExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("gt"))
            return new GtExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("ge"))
            return new GeExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("lt"))
            return new LtExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("le"))
            return new LeExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("lk"))
            return new LikeExpression(propName, parsedValue);
        else if (operator.equalsIgnoreCase("nl"))
            return new NlikeExpression(propName, parsedValue);
        else
            throw new MalformedParametersException();
    }

    /**
     * Split query string into expression parts to be evaluated
     * @param query query params string
     * @return list of tokens contained in query param string
     */
    private List<String> tokenizeString(String query) {
        var result = new ArrayList<String>();
        if (query.isEmpty())
            return result;

        query = query.toLowerCase().trim();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == '(' || query.charAt(i) == '\'') {
                if (!temp.toString().trim().isEmpty())
                    result.add(temp.toString());

                var closeChar = query.charAt(i) == '(' ? ')' : '\'';
                var subquery = getBalancedString(query.substring(i), query.charAt(i), closeChar);
                i += subquery.length();

                temp = new StringBuilder();
                result.add(subquery);
            } else if (query.charAt(i) == ' ') {
                if (!temp.toString().trim().isEmpty())
                    result.add(temp.toString());

                temp = new StringBuilder();
            } else
                temp.append(query.charAt(i));
        }

        if (!temp.toString().trim().isEmpty())
            result.add(temp.toString());

        return result;
    }

    /**
     * Get substring closed between openChar and closeChar taking care about nested occurrences
     * @param string sting to be evaluated
     * @param openChar opening tag
     * @param closeChar closing tag
     * @return substring contained between opening tag and its closing tag
     */
    private String getBalancedString(String string, char openChar, char closeChar) {
        if (string.isEmpty())
            return "";

        var symbols = new Stack<Integer>();
        int startIndex = -1;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == closeChar && !symbols.isEmpty()) {
                symbols.pop();
                if (symbols.isEmpty())
                    return string.substring(startIndex, i + 1);
            }

            if (string.charAt(i) == openChar) {
                symbols.push(i);
                startIndex = startIndex == -1 ? i : startIndex;
            }
        }

        return string;
    }
}
