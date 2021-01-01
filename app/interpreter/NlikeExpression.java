package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Case insensitive Not Like operation for filtering.
 */
public class NlikeExpression extends FinalExpression {
    public NlikeExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() { return Expr.not(Expr.ilike(propertyName, value.toString())); }
}