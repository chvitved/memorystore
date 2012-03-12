package dk.stamdata.query

import org.apache.log4j.Logger;
import dk.stamdata.wrappers.PersistentHashSetWrapper

object QueryRunner {

	val logger = Logger.getLogger(getClass.getName)

	val limitForUsingIndex = 500;

	def performQuery(analyzedExpressions: List[(AnalyzedExpression, String)], data: Set[AnyRef]): Set[_] = {
			doQuery(analyzedExpressions, data)
	}

	def doQuery(analyzedExpressions: List[(AnalyzedExpression, String)], data: Set[AnyRef]): Set[AnyRef] = {
			val groupedExpressions = groupExpressions(analyzedExpressions)
			runCode(groupedExpressions, data)
	}

	private def runCode(groupedExpressions: List[List[AnalyzedExpression]], data: Set[AnyRef]): Set[AnyRef] = {
			groupedExpressions match {
				case Nil => PersistentHashSetWrapper()
				case innerList::tail => {
					var indexedExps: List[AnalyzedLeafExpression] = List()
					var notIndexedExps: List[AnalyzedExpression] = List()
					for(exp <- innerList) {
						exp match {
						case treeExp: AnalyzedTreeExpression => notIndexedExps = treeExp :: notIndexedExps
						case leafExp: AnalyzedLeafExpression => {
							if (leafExp.canUseIndex) {
								indexedExps = leafExp :: indexedExps
							} else {
								notIndexedExps = leafExp :: notIndexedExps
							}
						}
						}
					}
					val firstResult = runGroup(indexedExps, notIndexedExps, data)
					val secondResult = runCode(tail, data)
					debugUnionLog(firstResult, secondResult)	
					firstResult ++ secondResult
				}
			}
	}

	private def debugUnionLog(firstResult: Set[AnyRef], secondResult: Set[AnyRef]) {
		if (logger.isDebugEnabled) {
			logger.debug("Union of " + firstResult.size + " and " + secondResult.size)
		}
	}

	private def runGroup(indexedExps: List[AnalyzedLeafExpression], notIndexedExps: List[AnalyzedExpression], data: Set[AnyRef]): Set[AnyRef] = {
			def run(indexedExps: List[AnalyzedLeafExpression], notIndexedExps: List[AnalyzedExpression], resultsSoFar: Set[AnyRef], firstCall: Boolean): Set[AnyRef] = {
					if (indexedExps.isEmpty && notIndexedExps.isEmpty) {
						resultsSoFar
					} else {
						var newIndexedExps: List[AnalyzedLeafExpression] = Nil
						var newNotIndexedExps: List[AnalyzedExpression] = Nil
						var runCodeForExp :AnalyzedExpression = null

						indexedExps match {
						case Nil => {
							notIndexedExps match {
								case exp::tail => {
									runCodeForExp = exp
									newIndexedExps = Nil
									newNotIndexedExps = tail
								}
							}
						}
						case exp::tail => {
							runCodeForExp = exp
							newIndexedExps = tail
							newNotIndexedExps = notIndexedExps
						}
					}
					var usingIndex = false
					val newResults: Set[AnyRef] =
						runCodeForExp match {
						case leafExp: AnalyzedLeafExpression => {
							if (leafExp.canUseIndex && limitForUsingIndex < resultsSoFar.size) {
								usingIndex = true
								if(firstCall) {
									println("1")
									leafExp.indexCode()
								} else {
									println("2")
									leafExp.indexCode().intersect(resultsSoFar)							  
								}
							} else {
							  println("3")
								leafExp.scanCode(resultsSoFar)
							}
						}
						case treeExp: AnalyzedTreeExpression => {
							println("4")
							doQuery(treeExp.expressions, resultsSoFar)
						}
					}

					debugExpLog(runCodeForExp, resultsSoFar, newResults, usingIndex)
					run(newIndexedExps, newNotIndexedExps, newResults, false)        
					}            
			}
			run(indexedExps,notIndexedExps, data, true);
	}

	private def debugExpLog(exp: AnalyzedExpression, resultsBefore: Set[AnyRef], resultsAfter: Set[AnyRef], usingIndex: Boolean) {
		if (logger.isDebugEnabled) {
			logger.debug("Expression: " + exp + 
//					"\n Results before: " + resultsBefore.size + 
//					"\n Results after: " + resultsAfter.size +
					"\n Using index: " + usingIndex
			)
		}
	}

	private def groupExpressions(analyzedExpressions: List[(AnalyzedExpression, String)]) : List[List[AnalyzedExpression]] = {
			var result: List[List[AnalyzedExpression]] = List()
			var innerList: List[AnalyzedExpression] = List()
			for(tuple <- analyzedExpressions) {
				tuple match {
					case (exp, "and") => innerList = exp :: innerList
					case (exp, _) => { 
						innerList = exp :: innerList
						result = innerList :: result
						innerList = List()
					}
				}
			}
			result.reverse
	}
}
