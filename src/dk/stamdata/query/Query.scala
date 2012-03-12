package dk.stamdata.query

import dk.stamdata.EntityManager
import parser._
import org.apache.log4j.Logger;
import dk.stamdata.EntityData

class Query() {
  
  val logger = Logger.getLogger(getClass.getName)

  val parser = new QueryParser()
  
  def query(query: String, em: EntityManager) : QueryResult = {
    logger.debug("parsing query")
    val startTime = System.currentTimeMillis
    val parseResult = parser.parseAll(parser.query, query)
    if (!parseResult.successful) {  
      throw new Exception("Parse error: " + parseResult)
    }
    logger.debug("done parsing in " + (System.currentTimeMillis - startTime) + "ms")
    
    val expresseionTime = System.currentTimeMillis
    logger.debug("creating code for query")
    val entityData = em.entities(parseResult.get.entity)
    val analyzedExpressions = analyzeRootExp(parseResult.get.whereExp, entityData)
    logger.debug("done creating code in " + (System.currentTimeMillis - expresseionTime) + "ms")
    
    val timeForRunningCode = System.currentTimeMillis
    logger.debug("Running query code")
    val results = QueryRunner.performQuery(analyzedExpressions, entityData.data)
    val result = new QueryResult(results, parseResult.get, analyzedExpressions)
    logger.debug("done running code in " + (System.currentTimeMillis - timeForRunningCode) + "ms")
    result
  }
  
  private def analyzeRootExp(exp: ExpAST, entity: EntityData) : List[(AnalyzedExpression, String)] = {
    exp match {
      case andOrExp: AndOrExp => analyzeAndOrExp(andOrExp, entity)
      case EmptyExpAST() => List((new AnalyzedLeafExpression(exp, (data: Set[AnyRef]) => data, null), null))
    }
  }
  
  private def analyzeAndOrExp(exp: AndOrExp, entity: EntityData) : List[(AnalyzedExpression, String)] = {
    var exp1 = exp.exp
    var list = exp.list
    var result: List[(AnalyzedExpression, String)] = List()
    while(!list.isEmpty) {
      val analyzedExp1 = analyzeExp(exp1, entity)
      val op = list.head._1
      result = (analyzedExp1, op) :: result
      exp1 = list.head._2
      list = list.tail
    }
    result = (analyzeExp(exp1, entity), null) :: result
    result.reverse
  }
  
  private def analyzeExp(exp: ExpAST, entity: EntityData) = {
    exp match {
      case andOrExp: AndOrExp => new AnalyzedTreeExpression(analyzeAndOrExp(andOrExp, entity))
      case binaryOp: BinaryOpExp => analyzeBinaryOpExp(binaryOp, entity)
    }
  }
  
/*  private def analyzeListOfAndOrExps(exp: ExpAST, op: String, listOfExp: (String, ExpAST), entity: EntityData) = {
    analyzeExp(exp, entity) :: 
  }*/

  private def analyzeBinaryOpExp(exp: BinaryOpExp, entity: EntityData): AnalyzedLeafExpression = {
    val firstProp = exp.attribute.str.split("\\.")(1)
    val attributeField = entity.clas.getDeclaredField(firstProp)
    val rightSide = TypesManager.getValue(exp.value2.value, attributeField.getType)
    val operator: Operator = Operator.getOperator(exp.op)
    val indexMethod = entity.indexes.get(attributeField.getName) match {
      case Some(index: AnyRef) => operator.indexCode(index, rightSide)
      case None => null
    }
    new AnalyzedLeafExpression(exp, operator.scanCode(attributeField, rightSide), indexMethod)
  }
}


abstract case class AnalyzedExpression()
  
  case class AnalyzedLeafExpression(val exp: ExpAST, val scanCode: Set[AnyRef] => Set[AnyRef], val indexCode: () => Set[AnyRef]) extends AnalyzedExpression{
    def canUseIndex = indexCode != null
  }
  case class AnalyzedTreeExpression(val expressions : List[(AnalyzedExpression, String)]) extends AnalyzedExpression
