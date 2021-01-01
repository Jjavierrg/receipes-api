package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Equal operation for filtering. In case of string, insensitive comparison is applied
 */
public class EqExpression extends FinalExpression
{
    public EqExpression(String propertyName, Object value) {
        super(propertyName, value);
    }

    @Override
    public Expression evaluate() {
        if (this.value instanceof String)
            return Expr.ieq(propertyName, value.toString());
        else
            return Expr.eq(propertyName, value);
    }
}