package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Case insensitive Like operation for filtering.
 */
public class LikeExpression extends FinalExpression {
    public LikeExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() { return Expr.ilike(propertyName, value.toString()); }
}