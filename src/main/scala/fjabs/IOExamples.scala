package fjabs
import cats.Applicative
import cats.effect.{ContextShift, IO}
import cats.implicits._
import cats.effect.implicits._
import cats.syntax.all._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object IOExamples extends App {

  import java.util.concurrent.Executors
  import cats.effect.{ContextShift, IO}
  import scala.concurrent.ExecutionContext

  val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  val es = Executors.newCachedThreadPool()
  val blockingEC = ExecutionContext.fromExecutor(es)

  def blockingOp: IO[Unit] = IO{Thread.sleep(3000);println(s"hello blocking: ${Thread.currentThread().getName}")}
  def doSth(): IO[Unit] = IO(println(s"do something: ${Thread.currentThread().getName}"))

  val prog =
    for {
      _ <- contextShift.evalOn(blockingEC)(blockingOp) // executes on blockingEC
      _ <- doSth()                                     // executes on contextShift
    } yield ()

  prog.unsafeRunSync()

  doSth().unsafeRunSync()
  List(contextShift.evalOn(blockingEC)(blockingOp).unsafeRunAsync(_ => ()), doSth().unsafeRunSync)

  es.shutdown()
}

