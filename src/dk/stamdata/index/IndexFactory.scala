package dk.stamdata.index

import java.lang.reflect.Field
import dk.stamdata.ReflectionUtil

object IndexFactory {
   
  private val indexMap: Map[Class[_], (Set[AnyRef], Field) => Index[AnyRef,_]] = Map(
    (classOf[String], (entities: Set[AnyRef], field: Field) => Index.create(entities, (obj: AnyRef) => ReflectionUtil.getFieldValue(field, obj).asInstanceOf[String])),
    (classOf[Double], (entities: Set[AnyRef], field: Field) => Index.create(entities, (obj: AnyRef) => ReflectionUtil.getFieldValue(field, obj).asInstanceOf[Double])),
    (classOf[Int], (entities: Set[AnyRef], field: Field) => Index.create(entities, (obj: AnyRef) => ReflectionUtil.getFieldValue(field, obj).asInstanceOf[Int]))
  )
  
  def createIndex(entities: Set[AnyRef], field: Field) = {
    indexMap(field.getType)(entities, field)
    
    
  }
}
