package dk.stamdata.index

import dk.stamdata.parsers.ParserConfig
import dk.stamdata.parsers.fixedlengthparser.FixedLengthParser
import dk.stamdata.{Person, AbstractPersonTest}

import org.junit.Test
import org.junit.Before
import org.junit.Assert._

class IndexOperatorTest extends AbstractPersonTest{

  var firstNameIndex: Index[Person, String] = _
  var birthDateIntegerIndex: Index[Person, Int] = _
  
  @Before
  def setup1() {
    
    val personsList = personsTyped.toList
    
    firstNameIndex = Index.create(personsTyped,(person: Person) => person.firstName)
    birthDateIntegerIndex = Index.create(personsTyped, (person: Person) => Integer.parseInt(person.birthday))
  }
  
  @Test
  def testEquals() {
    assertEquals(0, firstNameIndex.===("NotExisting").size)
    assertEquals(1, firstNameIndex.===("Anita").size)
    assertEquals(2, firstNameIndex.===("Anette").size)
    val ps = birthDateIntegerIndex.===(19800101) 
    assertEquals(1,ps.size)
    assertEquals("Tina", ps.elements.next.firstName)   
  }
  
  @Test
  def testSmallerThan() {
    assertEquals(0, birthDateIntegerIndex.<(1900).size)
    assertEquals(2, birthDateIntegerIndex.<(19110000).size)
    assertEquals(27, birthDateIntegerIndex.<(20000000).size)
  }
  
  @Test
  def testSmallerThanEquals() {
    assertEquals(0, birthDateIntegerIndex.<=(1900).size)
    assertEquals(2, birthDateIntegerIndex.<=(19090101).size)
    assertEquals(3, birthDateIntegerIndex.<=(19111111).size)
  }
  
  @Test
  def testLargerThan() {
    assertEquals(0, birthDateIntegerIndex.>(20000000).size)
    assertEquals(2, birthDateIntegerIndex.>(19990000).size)
    assertEquals(3, birthDateIntegerIndex.>(19800000).size)
  }
  
  @Test
  def testLargerThanEquals() {
    assertEquals(0, birthDateIntegerIndex.>=(20000000).size)
    assertEquals(2, birthDateIntegerIndex.>=(19990101).size)
    assertEquals(3, birthDateIntegerIndex.>=(19800101).size)
  }
}
