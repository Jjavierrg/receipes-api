package interpreter;

import io.ebean.Expression;

public interface IExpression {
    Expression evaluate();
}
