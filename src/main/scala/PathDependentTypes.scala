package path_dependent_type

/*
  Types within the path can be referred to by two operators, # (hash) and . (dot).
  The former is known as type projection, and T#m refers to the type member m of the type T

  Here, we defined a type, Lock, with a nested type, Key. The key can be referenced using its path, Lock.Key,
  or by using a projection, Lock#Key. The former denotes a type tied to a specific instance, and the latter denotes
  a type that is not

  Path-dependent types are way of stating that type of one object, depends on another object
 */

//Representation 1: inner class
case class Lock1() {
  final case class Key1()
  def openWith(key: Key1): Lock1 = this
  def openWithMaster(key: Lock1#Key1): Lock1 = this
  def makeKey: Key1 = new Key1
  def makeMasterKey: Lock1#Key1 = new Key1
}

//Representation 2: type member
sealed trait Key2
class YellowKey2 extends Key2
class BlackKey2 extends Key2

trait Lock2 {
  type Key <: Key2
  def openWith(key: Key): Lock2 = this
  def openWithMaster(key: Key2): Lock2 = this
  def makeKey: Key
  def makeMasterKey: Key2 = new Key2 {}
}
class YellowLock2() extends Lock2 {
  override type Key = YellowKey2
  override def makeKey: Key = new Key
}
class BlackLock2() extends Lock2 {
  override type Key = BlackKey2
  override def makeKey: Key = new Key
}

object PathDependentTypes extends App {

  //Representation 1: inner class
  val blueLock: Lock1 = Lock1()
  val redLock: Lock1 = Lock1()
  val blueKey: blueLock.Key1 = blueLock.makeKey
  val anotherBlueKey: blueLock.Key1 = blueLock.makeKey
  val redKey: redLock.Key1 = redLock.makeKey

  blueLock.openWith(blueKey)
  blueLock.openWith(anotherBlueKey)
  //blueLock.openWith(redKey) // compile error
  //redLock.openWith(blueKey) // compile error

  val masterKey: Lock1#Key1 = redLock.makeMasterKey

  blueLock.openWithMaster(masterKey)
  redLock.openWithMaster(masterKey)


  /////////////

  //Representation 2: type member
  val yellowLock = new YellowLock2()
  val yellowKey = yellowLock.makeKey
  yellowLock.openWith(yellowKey)

  val blackLock = new BlackLock2()
  //blackLock.openWith(yellowKey) //compile error

  val myMasterKey = yellowLock.makeMasterKey
  yellowLock.openWithMaster(myMasterKey)
  blackLock.openWithMaster(myMasterKey)

}




