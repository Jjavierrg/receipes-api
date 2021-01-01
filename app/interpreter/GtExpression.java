package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Greater Than operation for filtering.
 */
public class GtExpression extends FinalExpression {
    public GtExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() {
        return Expr.gt(propertyName, value);
    }
}