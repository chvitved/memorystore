package dk.stamdata.query

object TypesManager {
  
  private val typeMap: Map[Class[_], String => Comparable[_]] = Map(
    (classOf[String], (string: String) => string), 
    (classOf[Double], (string: String) => java.lang.Double.parseDouble(string)),
    (classOf[Int], (string: String) => java.lang.Integer.parseInt(string)),
    (classOf[Long], (string: String) => java.lang.Long.parseLong(string))
  )

  def getValue(value: String, requiredType: Class[_]) = {
    typeMap.get(requiredType) match {
      case Some(method) => method(value)
      case None => throw new Exception("Could not create a value of type " + requiredType + "from " + value)
    }
  }
}
