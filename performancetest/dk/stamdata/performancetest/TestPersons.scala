package dk.stamdata.performancetest

import dk.stamdata.EntityManager
import dk.stamdata.parsers.ParserConfig
import dk.stamdata.parsers.fixedlengthparser.FixedLengthParser

import dk.stamdata.wrappers.PersistentHashSetWrapper
object TestPersons {

  def main(args: Array[String]) {
	var start = System.currentTimeMillis
    val parserConfig = new ParserConfig(new FixedLengthParser(), "Cp850")
    parserConfig + ("performancetest/dk/stamdata/performancetest/cpr-big.txt", classOf[Person])
    val result = parserConfig.parse()  
    println("Time " + (System.currentTimeMillis - start) + "ms")
    
    val persons: Set[AnyRef] = PersistentHashSetWrapper[AnyRef]() ++ result(classOf[Person])
    println("number of persons: " + persons.size)
    
    val t1 = System.currentTimeMillis
    persons.foreach(_.toString)
    println("done in " + (System.currentTimeMillis - t1))
    
    var personSet = new scala.collection.immutable.HashSet[Person]()
    //var personSet = dk.stamdata.wrappers.ClojurePersistentSet.empty[AnyRef]
    persons.foreach((o: AnyRef) => personSet = personSet + o.asInstanceOf[Person])
    //persons.foreach((o: AnyRef) => personSet.add(o.asInstanceOf[Person]))
    val t2 = System.currentTimeMillis
    val ps = personSet.filter(_.status == 90)
    //val ps = dk.stamdata.query.Operator.getOperator("==").scanCode(classOf[Person].getDeclaredField("status"), new Integer(90))(personSet)
    println("done in " + (System.currentTimeMillis - t2))
    
    
    //dk.stamdata.query.EqualsOperator.scanCode(classOf[Person].getDeclaredField("personIdentifer"), "1111111118")(persons)	

    System.gc
    java.lang.Thread.sleep(1000)
    println("memory: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000D) + "m")
    
    val entityManager = new EntityManager()
    entityManager + (classOf[Person], persons)
    
    //warm up
    entityManager.query(""" entity Person where person.personIdentifer == "1111111119" """)
    
    //query with index
    entityManager.query(""" entity Person where person.personIdentifer == "1111111118" """)
    
    entityManager.query(""" entity Person where person.personIdentifer == "1111111119" """)
    
    entityManager.query(""" entity Person where person.firstName == "Anita" """)

    //query without index
    entityManager.query("""entity Person where person.status == 90 """)
    
    //query without index
    entityManager.query("""entity Person where person.status == 90 or person.status == 5""")
    
    //query without index
    entityManager.query("""entity Person where person.status == 01 and person.status == 5""")
    
    Thread.sleep(2000)
    println("done")
  }  
}
