package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class NlikeExpression extends FinalExpression {
    public NlikeExpression(String propertyName, Object value) { super(propertyName, value); }

    @Override
    public Expression evaluate() { return Expr.not(Expr.ilike(propertyName, value.toString())); }
}