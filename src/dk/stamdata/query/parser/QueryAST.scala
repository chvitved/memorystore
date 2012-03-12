package dk.stamdata.query.parser

class QueryAST(val entity: String, val joins: List[Attribute], val whereExp: ExpAST) {
  
  override def toString() = {
    "entity " + entity + "\n" +
    "join " + joins + "\n" + 
    "where " + whereExp.toString()
  }
  
}
