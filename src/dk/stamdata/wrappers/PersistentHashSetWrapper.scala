package dk.stamdata.wrappers

/***
 *
 * Ran into a problem with scala.collection.immutable.HashSet
 * found the bug report:
 * https://lampsvn.epfl.ch/trac/scala/ticket/408
 * 
 * Working around it with other set implementations
 * tryed clojures PersistentHashSet.
 * But it seems quite slow
 * 
 * Trying to implement a set using scala immutable hashmap gives me the same error as encountered with scalas set
 * 
 ***/

object PersistentHashSetWrapper {
  
  def empty[A]: Set[A] = new PersistentHashSetWrapper(Set())
  
  def apply[A](elems: A*): Set[A] = empty[A] ++ elems
}

class PersistentHashSetWrapper[A] private(val set: Set[A]) extends Set[A]{
  
  override def +(elem: A): Set[A] = new PersistentHashSetWrapper(set + elem)
  
  override def -(elem: A): Set[A] = new PersistentHashSetWrapper(set - elem)
  
  override def size: Int = set.size
  
  override def contains(elem: A): Boolean = set.contains(elem)
  
  override def iterator = set.iterator
  
  
 override def elements: Iterator[A] ={ 
   val it = set.iterator
   new Iterator[A] {
	    def hasNext: Boolean = it.hasNext 
	    def next(): A = it.next.asInstanceOf[A];
	  } 
 }
}



