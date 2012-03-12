package dk.stamdata.query.parser

import scala.util.parsing.combinator._

class QueryParser extends JavaTokenParsers{

  def query: Parser[QueryAST] = entity~join~where ^^
    {case entity~join~where => new QueryAST(entity, join, where)} 
  
  def entity: Parser[String] = "entity "~>string
  
  def string: Parser[String] = """\w+""".r
  
  def join: Parser[List[Attribute]] =  opt("join "~>rep1sep(att, ",")) ^^
    {
      case Some(joins) => joins
      case None => List()
    }
  
  def att: Parser[Attribute] = string~"."~string~rep("."~string) ^^
    {case objString~dot~attString~attStrings => Attribute(((objString + dot + attString) /: attStrings) (_ + "." + _._2))}
    
  def where: Parser[ExpAST] = opt("where "~>exp) ^^
    {
      case Some(exp) => exp
      case None => EmptyExpAST()
    }
                                 
  def exp: Parser[ExpAST] = andOrExp
  
  def andOrExp: Parser[AndOrExp] = binExp~rep(("and" | "or")~binExp) ^^
    {case exp~list => AndOrExp(exp, list.map(operatorValue2Tuple => (operatorValue2Tuple._1, operatorValue2Tuple._2)))}
  
  def binExp: Parser[ExpAST] = 
    att~("==" | "!=" | "<=" | "<" | ">=" | ">")~value ^^ {case att~op~value => BinaryOpExp(att, op, value) } |
    "("~>andOrExp<~")"
  
  def value: Parser[Value] = 
    //floatingPointNumber ^^ {FloatingPointNumber(_)} | 
    att |
    stringLiteral ^^ {case string: String => StringLitteral(string.substring(1,string.length - 1))} |
    string ^^ {Value(_)}
    //"true" ^^ {Boolean(_)} | 
    //"false" ^^ {Boolean(_)}
}
