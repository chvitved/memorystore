package dk.stamdata.query

import dk.stamdata.index.Index
import dk.stamdata.ReflectionUtil

import java.lang.reflect.Field

object Operator {
	def getOperator(string : String) = {
		string match {
		case "==" => EqualsOperator
		case "<" => LessThanOperator
		case ">" => BiggerThanOperator
		//TODO map each operator
		}
	}
}

abstract class Operator() {

	def scanCode(attribute: Field, parameter: AnyRef) : Set[AnyRef] => Set[AnyRef] = {
		(data: Set[AnyRef]) => {
		  println("start iterating")
		  val time = System.currentTimeMillis
		  val iterator = data.filter((value: AnyRef) => makeOrderedOperation(ReflectionUtil.getFieldValue(attribute, value), parameter, operator))
		  println("end iterating: " + (System.currentTimeMillis - time))
		  iterator
		}
	}

	protected def makeOrderedOperation(param1: AnyRef, param2: AnyRef, operation: (Comparable[AnyRef], Comparable[AnyRef]) => Boolean) : Boolean = {
		(param1, param2) match {
			case (param1: Comparable[AnyRef], param2: Comparable[AnyRef]) => operation(param1, param2)
			case _ => throw new Exception("Could not perform and operation on parameters: " + param1 + " and " + param2 + " they have not been parsed to values that implement the comparable interface");
		}
	}

	def operator(param1: Comparable[AnyRef], param2: Comparable[AnyRef]) : Boolean

	def indexCode[A](index: Index[AnyRef,A], parameter: A) : () => Set[AnyRef]

}

object EqualsOperator extends Operator{

	override def operator(param1: Comparable[AnyRef], param2: Comparable[AnyRef]) = {
		param1 == param2
	}

	override def indexCode[A](index: Index[AnyRef,A], parameter: A) : () => Set[AnyRef] = {
		() => index === (parameter)
	}
}

object LessThanOperator extends Operator {
	override def indexCode[A](index: Index[AnyRef,A], parameter: A) : () => Set[AnyRef] = {
			() => index < parameter
	}
 
	override def operator(param1: Comparable[AnyRef], param2: Comparable[AnyRef]) = {
		param1.compareTo(param2) == -1;	
	}
}

object BiggerThanOperator extends Operator {
	override def indexCode[A](index: Index[AnyRef,A], parameter: A) : () => Set[AnyRef] = {
			() => index > parameter
	}
 
	override def operator(param1: Comparable[AnyRef], param2: Comparable[AnyRef]) = {
		param1.compareTo(param2) == 1;	
	}
}
