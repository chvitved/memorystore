package dk.stamdata.query.parser

import scala.util.parsing.combinator._

class OldQueryParser extends JavaTokenParsers {

//  def query: Parser[QueryAST] = entity~join~where ^^
//    {case entity~join~where => new QueryAST(entity, join, where)} 
//  
//  def entity: Parser[String] = "entity "~>string
//  
//  def string: Parser[String] = """\w+""".r
//  
//  def join: Parser[List[String]] =  opt("join "~>rep1sep(att, ",")) ^^
//    {
//      case Some(joins) => joins
//      case None => List()
//    }
//  
//  def att: Parser[String] = string~"."~string~rep("."~string) ^^
//    {case objString~dot~attString~attStrings => ((objString + dot + attString) /: attStrings) (_ + "." + _._2)}
//    
//  def where: Parser[ExpAST] = opt("where "~>exp) ^^
//    {
//      case Some(exp) => exp
//      case None => EmptyExpAST()
//    }
//                                 
//  def exp: Parser[ExpAST] = andOrExp
//  
//  def andOrExp: Parser[BinaryOpExp] = eqExp~rep(("and" | "or")~eqExp) ^^
//    {case eqExp~list => BinaryOpExp(eqExp, list.map(operatorValue2Tuple => (operatorValue2Tuple._1, operatorValue2Tuple._2)))}
//  
//  def eqExp: Parser[BinaryOpExp] = relExp~rep(("==" | "!=")~relExp) ^^
//    {case relExp~list => BinaryOpExp(relExp, list.map(operatorValue2Tuple => (operatorValue2Tuple._1, operatorValue2Tuple._2)))}
//  
//  def relExp: Parser[BinaryOpExp] = value ~ rep(("<=" | "<" | ">=" | ">") ~ value) ^^
//    {case value1~list => BinaryOpExp(value1, list.map(operatorValue2Tuple => (operatorValue2Tuple._1, operatorValue2Tuple._2)))}
//    
//  def value: Parser[ExpAST] = 
//    "("~>exp<~")" ^^ {ExpWithBracketsAST(_)} |
//    floatingPointNumber ^^ {FloatingPointNumber(_)} | 
//    att  ^^ {Attribute(_)} |
//    stringLiteral ^^ {StringLitteral(_)} | 
//    "true" ^^ {Boolean(_)} | 
//    "false" ^^ {Boolean(_)}
}
