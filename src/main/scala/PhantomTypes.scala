package phantom_types

/*
The phantom type in Scala is a type that is never instantiated at runtime.
Because of this, it is only useful at compile time to express domain constraints similar to (generalized) type constraints
 */

object PhantomTypes extends App {

  val lock = new Lock[Open]
  //lock.open //won't compile
  lock.close.open.break
}

sealed trait LockState
sealed trait Open extends LockState
sealed trait Closed extends LockState
sealed trait Broken extends LockState

case class Lock[State <: LockState]() {

  def break: Lock[Broken] = Lock()
  def open(implicit ev: State =:= Closed): Lock[Open] = Lock()
  def close(implicit ev: State =:= Open): Lock[Closed] = Lock()
}

