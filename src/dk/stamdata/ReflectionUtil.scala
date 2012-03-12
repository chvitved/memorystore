package dk.stamdata

import java.lang.reflect.Field


object ReflectionUtil {

  def getFieldValue(field: Field, obj: AnyRef) = {
    field.setAccessible(true)
    val result = field.get(obj)
    result
  }
  
  def getFieldType(clas: Class[_], fieldName: String) = {
    clas.getDeclaredField(fieldName).getType
  }
}
