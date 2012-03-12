package dk.stamdata

import org.junit.Test
import org.junit.Before
import org.junit.Assert._

import dk.stamdata.parsers.ParserConfig
import dk.stamdata.parsers.fixedlengthparser.FixedLengthParser
import dk.stamdata.wrappers.PersistentHashSetWrapper

class AbstractPersonTest {

  var entityManager: EntityManager = _
  var personsTyped: Set[Person] = _
  var personSet: Set[AnyRef] = _
  
	  
  @Before
  def setup() {
    val parserConfig = new ParserConfig(new FixedLengthParser(), "Cp850")
    parserConfig + ("test/dk/stamdata/cpr.txt", classOf[Person])
    val result = parserConfig.parse()
    val personsList = result(classOf[Person]).toList match { case list: List[Person] => list}
    personsTyped = PersistentHashSetWrapper(personsList.toSeq:_*)
    personSet = PersistentHashSetWrapper(personsList.toSeq:_*)
    entityManager = new EntityManager()
    entityManager + (classOf[Person],  personSet)
  }
}
