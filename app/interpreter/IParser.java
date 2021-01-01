package interpreter;

/**
 * Grammatical parser interface for filtering params
 */
public interface IParser {
    /**
     * Converts query filter param string in a treeview Expression for ORM filter query
     * @param expession query filter param string
     * @return Expression for ORM filter query
     */
    IExpression parse(String expession);
}
