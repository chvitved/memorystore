package dk.stamdata.index

import scala.collection.immutable.{SortedMap,TreeMap, HashMap}
import dk.stamdata.wrappers.PersistentHashSetWrapper

object Index{
  
  private def buildMap[Entity,IndexType<%Ordered[IndexType]](data: Set[Entity], indexMethod: (Entity) => IndexType) : SortedMap[IndexType, Set[Entity]] = {
    val mutableMap = new scala.collection.mutable.HashMap[IndexType, scala.collection.mutable.ListBuffer[Entity]]() {
      override def default(key: IndexType) = {
          val list = new scala.collection.mutable.ListBuffer[Entity]()
          put(key, list)
          list
      }
    }
    data.foreach(entity => mutableMap(indexMethod(entity)) + entity)
    val mapWithSet = mutableMap.elements.map((tuple) => (tuple._1, PersistentHashSetWrapper(tuple._2.toSeq:_*)))
    TreeMap[IndexType, Set[Entity]](mapWithSet.toList.toSeq:_*)
    //wow these lines got ugly --improve
  }
  
  def create[Entity,IndexType<%Ordered[IndexType]](data: Set[Entity], indexMethod: (Entity) => IndexType ) = {
    new Index(buildMap(data, indexMethod), indexMethod)
  }
}

class Index[Entity,IndexType<%Ordered[IndexType]] private (val map: SortedMap[IndexType, Set[Entity]], val indexMethod: (Entity) => IndexType) {
  
  private def toSet(values: Iterable[Set[Entity]]): Set[Entity] = {
    (PersistentHashSetWrapper[Entity]() /: values) ((set1: Set[Entity], set2: Set[Entity]) => (set1 ++ set2))
  }
  
  def === (key: IndexType) : Set[Entity] = {
    map.getOrElse(key, PersistentHashSetWrapper())
  }
  
  def <(until: IndexType) : Set[Entity] = {
    toSet(map.until(until).values)
  }
  
  def <= (value: IndexType) : Set[Entity] = {
	  ===(value) ++ <(value)
  }
  
  def >(from: IndexType) : Set[Entity] = {
    >=(from) -- ===(from)
  }
  
  def >= (from: IndexType) : Set[Entity] = {
    toSet(map.from(from).values)
  }
  
  def + (obj: Entity): Index[Entity, IndexType] = {
    val key = indexMethod(obj)
    val set = map.getOrElse(key, PersistentHashSetWrapper()) + obj
    val newMap = map + ((key, set))
    new Index(newMap, indexMethod)
  }
  
  def - (obj: Entity): Index[Entity, IndexType] = {
    val key = indexMethod(obj)
    val set = map(key)
    val newMap = map + ((key, set - obj))
    new Index(newMap, indexMethod)
  }
  
}
