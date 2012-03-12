package dk.stamdata.query.parser

abstract case class ExpAST()

case class EmptyExpAST() extends ExpAST 

case class AndOrExp(exp: ExpAST, list: List[(String, ExpAST)]) extends ExpAST

case class BinaryOpExp(attribute: Attribute, op: String, value2: Value) extends ExpAST


case class ExpWithBracketsAST(exp: ExpAST) extends ExpAST {
  
  override def toString() = {
    "(" + exp + ")"
  }
  
} 

case class Value(value: String) extends ExpAST 
  //case class FloatingPointNumber(number: String) extends Value
  case class Attribute(str: String) extends Value(str)
  case class StringLitteral(string: String) extends Value(string)
  //case class Boolean(boolString: String) extends Value
