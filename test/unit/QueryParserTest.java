package unit;

import org.junit.Test;
import static org.junit.Assert.*;
import interpreter.*;

public class QueryParserTest {
    private IParser parser;

    public QueryParserTest() {
        this.parser = new ExpressionParser();
    }

    @Test
    public void ShouldParseEqFilter() {
        String filterQuery="propA eq 'test'";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof EqExpression);
    }

    @Test
    public void ShouldParseNeFilter() {
        String filterQuery="propA ne 'test'";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof NeqExpression);
    }

    @Test
    public void ShouldParseGtFilter() {
        String filterQuery="propA gt 24";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof GtExpression);
    }

    @Test
    public void ShouldParseGeFilter() {
        String filterQuery="propA ge 24";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof GeExpression);
    }

    @Test
    public void ShouldParseLtFilter() {
        String filterQuery="propA lt 24";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof LtExpression);
    }

    @Test
    public void ShouldParseLeFilter() {
        String filterQuery="propA le 24";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof LeExpression);
    }

    @Test
    public void ShouldParseLkFilter() {
        String filterQuery="propA lk '%test%'";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof LikeExpression);
    }

    @Test
    public void ShouldParseNlFilter() {
        String filterQuery="propA nl '%test%'";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof NlikeExpression);
    }

    @Test
    public void ShouldParseComplexExpression() {
        String filterQuery="propA nl '%test%' and (propA gt 24) and (propA le 24 or propB le 24 or propC le 24 or (propD le 24 and (propE le 24)))";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
        assertTrue(result instanceof AndExpression);
    }

    @Test(expected = Exception.class)
    public void ShouldNotParseInvalidExpression() {
        String filterQuery="propA nl '%test%' or (propB)";
        IExpression result = this.parser.parse(filterQuery);

        assertNotNull(result);
    }
}
