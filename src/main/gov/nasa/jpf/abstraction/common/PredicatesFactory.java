package gov.nasa.jpf.abstraction.common;

import gov.nasa.jpf.abstraction.common.access.AccessExpression;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesLexer;
import gov.nasa.jpf.abstraction.predicate.parser.PredicatesParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class PredicatesFactory {
    private static PredicatesParser createFromString(String definition) {
        ANTLRInputStream chars = new ANTLRInputStream(definition);
        PredicatesLexer lexer = new PredicatesLexer(chars);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PredicatesParser parser = new PredicatesParser(tokens);

        return parser;
    }

    public static Predicate createPredicateFromString(String definition) {
        return createFromString(definition).predicate().val[0];
    }

    public static AccessExpression createAccessExpressionFromString(String definition) {
        return createFromString(definition).standalonepath().val[0];
    }

    public static Expression createExpressionFromString(String definition) {
        return createFromString(definition).standaloneexpression().val[0];
    }
}
