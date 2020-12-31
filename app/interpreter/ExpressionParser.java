package interpreter;

import java.lang.reflect.MalformedParametersException;
import java.util.*;

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

    private boolean isUnaryExpression(String token) {
        return token.startsWith("(") && token.endsWith(")");
    }

    private IExpression getUnaryExpression(String token) {
        if (!isUnaryExpression(token))
            throw new MalformedParametersException();

        var tokens = tokenizeString(token.substring(1, token.length() - 1));
        return getExpressionTree(tokens);
    }

    private IExpression getLogicalExpression(IExpression expr1, IExpression expr2, String operator) {
        if (operator.equalsIgnoreCase("and"))
            return new AndExpression(expr1, expr2);
        else if (operator.equalsIgnoreCase("or"))
            return new OrExpression(expr1, expr2);
        else
            throw new MalformedParametersException();
    }

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
