package com.nosto.spartann

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import java.io.InputStreamReader
import scala.io.Source
import scala.reflect.ClassTag

case class JacksonRDD[T](sc: SparkContext)(implicit ctag: ClassTag[T], atag: ClassTag[Array[T]]) {

  final val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  def of(path: String): RDD[T] = {
    val items: Array[T] = objectMapper.readValue(read(path), atag.runtimeClass)
      .asInstanceOf[Array[T]]
    sc.parallelize(items, 1)
  }

  private def read(path: String): InputStreamReader = {
    Source.fromResource(path).reader()
  }
}
