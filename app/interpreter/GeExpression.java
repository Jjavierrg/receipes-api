package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class GeExpression extends FinalExpression {
    public GeExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() {
        return Expr.ge(propertyName, value);
    }
}