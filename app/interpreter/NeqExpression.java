package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class NeqExpression extends FinalExpression
{
    public NeqExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() {
        if (this.value instanceof String)
            return Expr.not(Expr.ieq(propertyName, value.toString()));
        else
            return Expr.ne(propertyName, value);
    }
}