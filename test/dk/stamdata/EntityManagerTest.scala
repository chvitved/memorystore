package dk.stamdata

import org.junit.Test
import org.junit.Before
import org.junit.Assert._

import index._

class EntityManagerTest extends AbstractPersonTest{

  @Test
  def testIndexAnnotations() {
    assertEquals(1, entityManager.entities.size)
    assertEquals(classOf[Person].getSimpleName, entityManager.entities.keys.first)
    assertEquals(4, entityManager.entities(classOf[Person].getSimpleName).indexes.size)
  }
  
  @Test
  def addObject() {
    val q = """entity Person where person.personIdentifer == "new" """
    assertEquals(0, entityManager.query(q).results.size)
	val newP = new Person("new")
 
    entityManager + newP
 
	val result = entityManager.query(q)
	assertEquals(1,result.results.size)
	
	val index = entityManager.entities(classOf[Person].getSimpleName).indexes("personIdentifer").asInstanceOf[Index[Person, String]]
	assertEquals(1, index.===("new").size)
 
  }
  @Test
  def removeObject() {
    val q = """entity Person where person.personIdentifer == "1111111118" """
    val results = entityManager.query(q).results
    assertEquals(1, results.size)
    	
	val index = entityManager.entities(classOf[Person].getSimpleName).indexes("personIdentifer").asInstanceOf[Index[Person, String]]
	assertEquals(1, index.===("1111111118").size)
    
    val p = results.toList.head.asInstanceOf[Person]
    
    entityManager - p
	val result = entityManager.query(q)
	assertEquals(0,result.results.size)
 
	val idx = entityManager.entities(classOf[Person].getSimpleName).indexes("personIdentifer").asInstanceOf[Index[Person, String]]
	assertEquals(0, idx.===("1111111118").size)
 
 
  }

}
