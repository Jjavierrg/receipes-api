package interpreter;

/**
 * Base class for final filter Expression
 */
public abstract class FinalExpression implements IExpression
{
    protected String propertyName;
    protected Object value;

    public FinalExpression(String propertyName, Object value)
    {
        this.propertyName = propertyName;
        this.value = value;
    }
}