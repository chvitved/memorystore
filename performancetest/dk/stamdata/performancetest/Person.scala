package dk.stamdata.performancetest

import dk.stamdata.parsers._
import dk.stamdata.parsers.impl.DefaultDataConverter
import dk.stamdata.parsers.fixedlengthparser._
import dk.stamdata.annotations.CreateIndex

import java.util.Date

class Person(pi: String) {

  def this() = {
    this(null)
  }
  
  @ParseIf
  def parseIf(flParserElement: FixedLengthParserElement) : Boolean = {
    flParserElement.line.startsWith("001")
  }
  
  @FLParserValue(begin=3,end=13, required = true)
  @CreateIndex
  val personIdentifer: String = pi 
  
  @FLParserValue(begin=13,end=21)
  val birthday: String = ""
  
  @Converter(classOf[SexConverter])
  @FLParserValue(begin=21,end=22)	
  val sex: Sex.Value = null
  
  //this should be an enumeration
  @FLParserValue(begin=22,end=24)	
  val status: Int = -1
  
  @FLParserValue(begin=24,end=36)
  @Converter(classOf[PersonDateConverter])
  val statusDato: Date = null
  
  @FLParserValue(begin=36,end=46)
  val currentPersonIdentifier = ""
  
  @FLParserValue(begin=46,end=58)
  @Converter(classOf[PersonDateConverter])
  val nameAndAddressProtection: Date = null
 
  @FLParserValue(begin=58,end=108)
  @CreateIndex
  val firstName: String = ""
  
  @FLParserValue(begin=108,end=148)
  @CreateIndex
  val surName: String = ""
  
  @FLParserValue(begin=148,end=182)
  val coName: String = ""
  
  @FLParserValue(begin=182,end=186)
  @CreateIndex
  val muncipalityCode: Int = -1
  
  @FLParserValue(begin=186,end=190)
  val roadCode: Int = -1
  
  @FLParserValue(begin=190,end=193)
  val houseNumber: Int = -1
  
  @FLParserValue(begin=193,end=194)
  val houseChar: String = ""
  
  @FLParserValue(begin=194,end=198)
  val buildingNumber: String = ""
  
  @FLParserValue(begin=198,end=200)
  val floor: Int = -1
  
  @FLParserValue(begin=200,end=204)
  val floorDoorNumber: String = ""
  
}

object Sex extends Enumeration {
  val male, female = Value
}

class SexConverter extends StringConverter {
  def convert(value: String): Sex.Value = {
    value match {
      case "K" => Sex.female
      case "M" => Sex.male
    }
  }
}

class PersonDateConverter extends StringConverter {
  def convert(value: String) = {
    value match {
      case "000000000000" => null
      case _ => DefaultDataConverter.convert(value, classOf[Date])
    }
  }
}