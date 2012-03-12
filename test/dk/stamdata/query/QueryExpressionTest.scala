package dk.stamdata.query

import org.junit.Test
import org.junit.Before
import org.junit.Assert._
import dk.stamdata.AbstractPersonTest

class QueryTest extends AbstractPersonTest {
  
  @Test
  def testQueryWithNoWhereExp() {
    val result = entityManager.query("entity Person")
    assertEquals(1, result.analyzedExpressions.size)
  }
  
  @Test
  def testQueryWithAndExp() {
    val result = entityManager.query("""entity Person where person.surName == "Hansen" and person.firstName == "Karina" """)
    assertEquals(1, result.results.size)
  }
  
  @Test
  def testQueryWithOrExp() {
    val result = entityManager.query("""entity Person where person.surName == "Hansen" or person.firstName == "Karina" """)
    assertEquals(5, result.results.size)
  }
  
  @Test
  def testQueryExpBinding() {
    val result = entityManager.query("""entity Person where person.surName == "Hansen" and person.firstName == "Karina" or person.surName == "Jensen" """)
     assertEquals(5, result.results.size)
  }
  
  @Test
  def testQueryWithNestedExpressions() {
    val result = entityManager.query("""entity Person where person.surName == "Hansen" and (person.firstName == "Karina" or person.firstName == "Inger")""")
     assertEquals(2, result.results.size)
  }
}
