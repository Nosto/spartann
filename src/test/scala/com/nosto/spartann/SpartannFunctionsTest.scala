package com.nosto.spartann

import annoy4s.Angular
import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite

class SpartannFunctionsTest extends AnyFunSuite with BetterRDDComparisons with SharedSparkContext {

  import spark.annFunctions
  final val annoyConfig: AnnoyConfig = AnnoyConfig(2, 10, Angular)

  test("that finding relations using the Annoy job works") {

    val inputRDD: RDD[TestEmbeddings[String]] = JacksonRDD[TestEmbeddings[String]](sc)
      .of("searchk-test-vector.json")

    val resultRDD: RDD[_ <: Neighbour[String]] = inputRDD.related(annoyConfig)

    val expectedRDD: RDD[AnnoyNeighbours[String]] = JacksonRDD[AnnoyNeighbours[String]](sc)
      .of("resultant-k-related.json")

    assertRDDEquals[Neighbour[String], Neighbour[String]](expectedRDD, resultRDD, identity)
  }
}
