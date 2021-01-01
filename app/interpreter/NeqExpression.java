package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Not equal operation for filtering. In case of string, insensitive comparison is applied
 */
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