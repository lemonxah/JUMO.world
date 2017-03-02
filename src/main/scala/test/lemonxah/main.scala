package test.lemonxah

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

import language.higherKinds
import language.postfixOps
import scala.concurrent.Await
import scala.io._
import scala.util.{Failure, Success}
/**
  * Project: test
  * Package: test.lemonxah
  * Created on 2/3/2017.
  * lemonxah aka lemonxah -
  * https://github.com/lemonxah
  * http://stackoverflow.com/users/2919672/lemon-xah
  */

import FunctionalSyntax._
import TaskImplicits._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class LoanRecord(msisdn: String, network: String, date: String, product: String, amount: Float)
case class AggregateRecord(network: String, product: String, month: String, amount: Float, count: Int)

object main {

  def main(args: Array[String]): Unit = {
    if (args.length == 0) println("usage: test <filename> [output filename]")
    else {
      // getting the outputFilename if it was specified in the arguments
      val outputFilename = if (args.length == 2) args(1) else "Output.csv"

      // creating the main task which would consist of reading aggregating and writing the output
      // note that we are not executing any of the code yet we are only expressing what we would
      // like to happen when we execute the task later on.
      val task = for {
        input ← readFile(args(0))
        output ← aggregate(input)
        written ← writeOutput(outputFilename)(output)
      } yield println(s"writing file $outputFilename ${if (written) "succeeded" else "failed"}.")

      // Testing the Task trampolining
      // Building up a Task that consists of 1 million operations that need to occur to test trampolining.
      // 1 million is obviously far above the stack overflow limit and to prove this we will try and print
      // the task before we run it.
      val t: Task[Int] = Stream.from(1).take(1000000).foldLeft(Task.now(0)){ case (b, a) ⇒ b.bind(i ⇒ Task.eval(i + 1)) }
      // try to print this task before running it
      try {
        println(t)
      } catch {
        // Specifically catching StackOverflowError as this is not an exception but a fatal error
        case ex: StackOverflowError ⇒
          println(s"error occurred: $ex")
      }
      // running the large data structure that will result in 1 million +1 operations
      val f = t.runAsync
      f onComplete {
        case Success(i) ⇒ println(s"trampolining sum is $i")
        case Failure(ex) ⇒ println(s"stack overflow? $ex")
      }
      // Just adding the await here so that the application does not close while other threads are busy as runAsync is non blocking and async
      Await.result(f, 2 seconds)

      // we have delayed the execution of our main task and we are going to execute the business logic now.
      val future = task.runAsync
      // Just adding the await here so that the application does not close while other threads are busy as runAsync is non blocking and async
      Await.result(future, 15 seconds)
    }
  }

  def writeOutput(filename: String)(list: List[AggregateRecord]): Task[Boolean] = Task {
    println(s"writing to file: $filename")
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)))
    try {
      writer.write("Network,Month,Product,Amount,Count")
      writer.newLine()
      list.foreach { r ⇒
        writer.write(s"${r.network},${r.month},${r.product},${r.amount},${r.count}")
        writer.newLine()
      }
      true
    } catch {
      case ex: Exception ⇒
        println(s"error: $ex")
        false
    } finally {
      writer.close()
    }
  }

  def aggregate(list: List[LoanRecord]): Task[List[AggregateRecord]] = Task {
    list.groupBy(r ⇒ (r.network, r.product, r.date.split('-')(1))).map {
      case ((network, product, month), loans) ⇒
        AggregateRecord(network, product, s"'$month'", loans.map(_.amount).sum, loans.size)
    }.toList.sortBy(r ⇒ (r.network, r.month, r.product))
  }

  def readFile(filename: String): Task[List[LoanRecord]] = Task {
    println(s"reading file: $filename")
    Source.fromFile(filename).getLines().drop(1).map { line ⇒
      val msisdn :: network :: date :: product :: amount :: Nil = line.split(',').toList
      LoanRecord(msisdn, network, date, product, amount.toFloat)
    }.toList
  }

}
