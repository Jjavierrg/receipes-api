package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class LtExpression extends FinalExpression {
    public LtExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() {
        return Expr.lt(propertyName, value);
    }
}