package com.nosto.spartann

import com.holdenkarau.spark.testing.RDDComparisons
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import scala.reflect.ClassTag

/**
  * Building off Holden's Spark RDD assertions to allow for even better
  * assertions.
  *
  * I'm a little lost as to whether the Spark base really does assert that
  * two RDDs not only have the same values but also that they have the same
  * number of partitions and that each partition has the same number of
  * records.
  *
  * If that library does do those assertions, this can simply be stripped out.
  *
  * https://github.com/holdenk/spark-testing-base/wiki/RDDComparisons
  *
  * @author mridang
  */
trait BetterRDDComparisons extends AnyFunSuite with RDDComparisons {

  protected def assertRDDEquals[T: ClassTag, K: ClassTag](
      expectedRDD: RDD[_ <: T],
      resultRDD: RDD[_ <: T],
      fn: T => K): Unit = {

    assertRDDEquals(expectedRDD.map { row: T =>
      fn.apply(row)
    }, resultRDD.map { row: T =>
      fn.apply(row)
    })

    // Also count the number of partitions just be sure. These underlying RDDs
    // return one partition per merchant and therefore we should also just do
    // thorough count to ensure that both RDDs have the same partition count.
    assert(expectedRDD.getNumPartitions == resultRDD.getNumPartitions)

    // Also glom each partition counts and assert the counts
    // There should be the same number of partitions in both RDDs and both RDDs
    // should have the same number of elements in each partition
    val actualPartitions: Array[Int] = resultRDD.glom.map(_.length).collect
    val expectedPartitions: Array[Int] = expectedRDD.glom.map(_.length).collect
    actualPartitions.toSet should be(expectedPartitions.toSet)
  }
}
