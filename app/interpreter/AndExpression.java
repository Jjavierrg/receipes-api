package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

public class AndExpression implements IExpression
{
    IExpression expr1;
    IExpression expr2;

    public AndExpression(IExpression expr1, IExpression expr2)
    {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Expression evaluate() { return Expr.and(this.expr1.evaluate(), this.expr2.evaluate()); }
}