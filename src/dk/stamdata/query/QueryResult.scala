package dk.stamdata.query

import dk.stamdata.query.parser.QueryAST

class QueryResult(val results: Set[_], val queryParseTree: QueryAST, val analyzedExpressions: List[(AnalyzedExpression, String)]) {

}
