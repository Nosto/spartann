package com.nosto.spartann

import annoy4s.Angular
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.rdd.RDD
import org.scalatest.funsuite.AnyFunSuite

import java.io.Reader
import scala.io.Source

class SpartannFunctionsTest extends AnyFunSuite with BetterRDDComparisons with SharedSparkContext {

  import spark.annFunctions
  final val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)
  final val annoyConfig: AnnoyConfig = AnnoyConfig(2, 10, Angular)

  test("that finding relations using the Annoy job works") {

    val file: Reader = Source.fromResource("searchk-test-vector.json").reader()
    val embeddings: Array[MyEmbeddings] = objectMapper.readValue(file, classOf[Array[MyEmbeddings]])

    val inputRDD: RDD[MyEmbeddings] = super.sc.parallelize(embeddings, 1)

    val resultRDD: RDD[_ <: Neighbour[String]] = inputRDD.related(annoyConfig)

    val outFile: Reader = Source.fromResource("resultant-k-related.json").reader()

    val embeddingsOut: Array[AnnoyNeighbours[String]] = objectMapper.readValue(outFile, classOf[Array[AnnoyNeighbours[String]]])

    val expectedRDD: RDD[_ <: Neighbour[String]] = super.sc.parallelize(embeddingsOut, 1)

    //assertRDDEquals[_ <: Neighbour[String], _ <: Neighbour[String]](expectedRDD, resultRDD, record => record)
  }
}
