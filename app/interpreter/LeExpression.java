package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class LeExpression extends FinalExpression {
    public LeExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() { return Expr.le(propertyName, value); }
}