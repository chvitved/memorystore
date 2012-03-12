package dk.stamdata.query;

import org.junit.Test
import org.junit.Before
import org.junit.Assert._;
import dk.stamdata.AbstractPersonTest

class QueryOperatorsTest extends AbstractPersonTest {

	@Test
	def testEqualsWithIndex() {
		val result = entityManager.query("""entity Person where person.firstName == "Karina" """)
		assertResult(result, 2, true)
	}

	@Test
	def testEqualsWithoutIndex() {
		val result = entityManager.query("""entity Person where person.buildingNumber == 1 """)
		assertResult(result, 2, false)
	}
 
    @Test
    def testLessThanWithIndex() {
      val result = entityManager.query("""entity Person where person.muncipalityCode < 925 """)
	  assertResult(result, 26, true)
    }
    
    @Test
    def testLessThanWithoutIndex() {
      val result = entityManager.query("""entity Person where person.status < 5 """)
	  assertResult(result, 25, false)
    }

    def assertResult(result: QueryResult, numerOfResults: Int, useIndex: Boolean) {
      result.analyzedExpressions.head._1 match {
		case exp: AnalyzedLeafExpression => {
			assertEquals(useIndex, exp.canUseIndex)
            if (useIndex) {
			  assertEquals(numerOfResults,exp.indexCode().size)              
            }
			assertEquals(numerOfResults,exp.scanCode(personSet).size)
		}
		case exp: AnalyzedTreeExpression => fail
      }
      assertEquals(numerOfResults,result.results.size)
    }
}