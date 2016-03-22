import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import models.{Gender, Person}
import org.joda.time.DateTime

/**
  * Created by earvinkayonga on 22/03/2016.
  *
  * Source : https://www.toptal.com/scala/concurrency-and-fault-tolerance-made-easy-an-intro-to-akka
  */

case object Done
case class ProcessPerson(string: String)
case class StartWorkingMsg()

object AsyncReader extends  App{

  import akka.util.Timeout
  import scala.concurrent.duration._
  import akka.dispatch.ExecutionContexts._
  import akka.pattern.ask

  implicit val ec = global

  override def main(args: Array[String]) {
    val system = ActorSystem("System")

    val actor = system.actorOf(Props(new Reader("data/person.json")))
    implicit val timeout = Timeout(3 seconds)
    val future = actor ? StartWorkingMsg()
    future.map { result =>
      println("Total number of words " + result)
      system.shutdown()
    }
  }




  /**
    * Actor that parse a line to A Person Object then prints it
    */
  class Printer extends Actor{
    def receive = {
      case ProcessPerson(string: String) => {
        val data = string.split(',')
        val p = Person(
            id = UUID.randomUUID(),
            gender = Gender.fromString(data(1).split(":")(1)),
            firstName = data(2).split(":")(1),
            lastName = data(3).split(":")(1),
            email = data(4).split(":")(1),
            date = DateTime.parse(data(5).split(":")(1).substring(1,11))
        )

        println("Person: "+ p)
      }
      case _ => println("Unknown Signal")
    }
  }

  /**
    * Actor that Open a file and push every line to its invoker
    */
  class Reader(filename: String) extends Actor{

    // Ref to its invoker
    private var fileSender: Option[ActorRef] = None

    private var running = false

    def receive = {

      case StartWorkingMsg() => {
        if (running) {
          println("Warning: You send a StartWorking Message to a Reader Actor who is already Working")
        } else {
          running = true
          fileSender = Some(sender) // save reference to process invoker
          import scala.io.Source._
          fromFile(filename).getLines.foreach { line =>
            context.actorOf(Props[Printer]) ! ProcessPerson(line)
          }
        }
      }
      case _ => println("Unknown Signal")
    }
  }

}
