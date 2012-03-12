package dk.stamdata

import scala.collection.immutable.TreeMap
import scala.collection.mutable.HashMap

import dk.stamdata.parsers.ParserConfig
import dk.stamdata.parsers.fixedlengthparser.FixedLengthParser

object Main {

  def main(args: Array[String]) {
    var start = System.currentTimeMillis
    val parserConfig = new ParserConfig(new FixedLengthParser(), "Cp850")
    parserConfig + ("cpr-big.txt", classOf[Person])
    val result = parserConfig.parse()  
    println("Time " + (System.currentTimeMillis - start) + "ms")
    
    val persons = result(classOf[Person]).toList match { case list: List[Person] => list}
    println("number of persons: " + persons.length)
    
    System.gc
    java.lang.Thread.sleep(2000)
    println("memory: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000D) + "m")
    
    
    
  }  
}
