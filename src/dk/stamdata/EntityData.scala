package dk.stamdata

import dk.stamdata.parsers.utils.ParserAnnotationHelper
import index._
import annotations.CreateIndex

object EntityData {
  
  private def setupIndexes(clas: Class[_], data: Set[AnyRef]) : Map[String, Index[AnyRef,_]] = {
    val fieldsWithIndexAnnotation  = ParserAnnotationHelper.getFieldAnnotations(clas, classOf[CreateIndex])
    var map = Map[String, Index[AnyRef,_]]()
    fieldsWithIndexAnnotation.foreach(((fieldTuple)) => {
      val field = fieldTuple._1
      val index = IndexFactory.createIndex(data, field)      
      map = map + ((field.getName, index))
    })
    map
  }
  
  private def updateAllIndexes(indxs: Map[String, Index[AnyRef,_]], changeIndex: Index[AnyRef,_] => Index[AnyRef,_]): Map[String, Index[AnyRef,_]] = {     
    def updateMap(map: Map[String, Index[AnyRef,_]], element: (String, Index[AnyRef,_]))  = {map + ((element._1, changeIndex(element._2)))}
    (Map[String, Index[AnyRef,_]]() /: indxs.elements) (updateMap)
  }
}

class EntityData private(val clas: Class[_], val data: Set[AnyRef], indxs: Map[String, Index[AnyRef,_]]) {
  
  def this(clas: Class[_], data: Set[AnyRef]) = this(clas, data, null) 
  
  val indexes: Map[String, Index[AnyRef,_]] = 
    if (indxs == null) {
	  EntityData.setupIndexes(clas, data)    
    } else {
      indxs
    }  
  
  def + (obj: AnyRef) : EntityData = {
    if (obj.getClass != clas) {
      throw new Exception("obj has class " + obj.getClass + " expected " + clas);
    }
    new EntityData(obj.getClass, data + obj, EntityData.updateAllIndexes(indexes, _ + obj))
  }  
  
  def - (obj: AnyRef) : EntityData = {
    if (obj.getClass != clas) {
      throw new Exception("obj has class " + obj.getClass + " expected " + clas);
    }
    new EntityData(obj.getClass, data - obj, EntityData.updateAllIndexes(indexes, _ - obj))
  }  
}
