package interpreter;

import io.ebean.Expression;

/**
 * Generic Expression filter
 */
public interface IExpression {
    /**
     * Evaluate expression and get the corresponding {@link io.ebean.Expression}
     * @return
     */
    Expression evaluate();
}
