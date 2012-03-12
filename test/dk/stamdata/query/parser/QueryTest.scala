package dk.stamdata.query.parser

import org.junit.Test
import org.junit.Before
import org.junit.Assert._

class QueryTest {
  
  @Test
  def testParseSimpleQueries() {
    val parser = new QueryParser();
    val q = 
      """entity test2
         join test.bar.foo
         where test.a > 3
      """
    var time = System.currentTimeMillis
    var result = parser.parseAll(parser.query, q);
    println(System.currentTimeMillis - time + "ms")
    println(result)
    assertTrue(result.successful)
    
    val q1 =
      """entity test
         join test.a.b, person.b
         where test.a > 3 and test.b == "str" or test.c <= 42
      """
      
    time = System.currentTimeMillis
    result = parser.parseAll(parser.query, q1);
    println(System.currentTimeMillis - time + "ms")
    println(result)
    assertTrue(result.successful)
    
    val q2 =
      """entity test
         join test.a.b, person.b
         where test.a > 3 and (test.b == "str" or test.c <= 42)
      """
      
    time = System.currentTimeMillis
    result = parser.parseAll(parser.query, q2);
    println(System.currentTimeMillis - time + "ms")
    println(result)
    assertTrue(result.successful)
  }
  

}
