package dk.stamdata

import query.Query
import org.apache.log4j.Logger;
import dk.stamdata.wrappers.PersistentHashSetWrapper

class EntityManager {
 
  val logger = Logger.getLogger(getClass.getName)
  
  var entities = Map[String, EntityData]()
 
  def + (clas: Class[_], data: Set[AnyRef]) {
    entities += ((clas.getSimpleName, new EntityData(clas, data)))
  }
  
  def + (obj: AnyRef) {
    val className = obj.getClass.getSimpleName
    val newEntityData: EntityData = entities.get(className) match {
      case Some(entityData) => entityData + obj
      case None => new EntityData(obj.getClass, PersistentHashSetWrapper(obj))
    }
    entities += ((className, newEntityData))
  }
  
  def - (obj: AnyRef) {
    val className = obj.getClass.getSimpleName
    entities += ((className, entities(className) - obj))
  }
 
  
  def query(queryString: String) = {
    logger.debug("started query <---------------------")
    val startTime = System.currentTimeMillis
    val q = new Query()
    val result = q.query(queryString, this)
    logger.debug("query finsihed in " + (System.currentTimeMillis - startTime) + "ms ----------------------")
    result
  } 
}