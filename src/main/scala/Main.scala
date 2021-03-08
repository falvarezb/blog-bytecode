package fjab

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

object Main extends LazyLogging {

  def main(args: Array[String]): Unit = {
    example1()
    //example2()
  }

  def example1(): Unit = {

    def recordTime(requestTimestamp: Long): PartialFunction[Try[_], Unit] = {
      case Success(_) =>
        logger.info("running 'andThen' function in the future")
        logger.info(s"io latency: ${System.currentTimeMillis() - requestTimestamp}")
    }

    def asyncJob(): Future[Int] = {
      logger.info("executing some code in main thread")
      Thread.sleep(2000)

      //async call
      Future {
        logger.info("io call running in new thread")
        Thread.sleep(5000)
        3
      }
    }

    val f = asyncJob().
      map{
        logger.info("running 'map' body in the present ...")
        i: Int => {
          logger.info("running 'map' function in the future")
          i + 1
        }
      }
      .andThen{
        logger.info("running 'andThen' body in the present ...")
        recordTime(System.currentTimeMillis())
      }

    logger.info(Await.result(f, 10 seconds).toString)
  }

  def example2(): Unit = {
    def asyncJob(): Future[Int] = {
      evilFunction()

      //async call
      Future {
        logger.info("io call running in new thread")
        Thread.sleep(5000)
        3
      }
    }

    def evilFunction() = throw new Exception

    val f = asyncJob()
    f.onComplete {
      case Success(value) => logger.info(value.toString)
      case Failure(exception) => logger.info("error")
    }
    Await.result(f, 1 seconds)
  }
}
