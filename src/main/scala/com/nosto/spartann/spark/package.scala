package com.nosto.spartann

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

package object spark {

  implicit def annFunctions[T](dataRDD: RDD[_ <: Embeddings[T]])(implicit classTag: ClassTag[T]):
  SpartannFunctions[T] = {
    new SpartannFunctions[T](dataRDD)(classTag)
  }
}
