package interpreter;

import io.ebean.Expr;
import io.ebean.Expression;

/**
 * Logical Or operation for filtering
 */
public class OrExpression implements IExpression
{
    IExpression expr1;
    IExpression expr2;

    public OrExpression(IExpression expr1, IExpression expr2)
    {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Expression evaluate() {
        return Expr.or(this.expr1.evaluate(), this.expr2.evaluate());
    }
}