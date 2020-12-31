package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class CtExpression extends FinalExpression {
    public CtExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() { return Expr.contains(propertyName, value.toString()); }
}