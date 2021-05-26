package com.nosto.spartann.spark

import com.nosto.spartann.{AnnoyConfig, Annoyer, Embeddings, Neighbour}
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

class SpartannFunctions[T](dataRDD: RDD[_ <: Embeddings[T]])(implicit classTag: ClassTag[T])
  extends Serializable {

  def related(annoyConfig: AnnoyConfig): RDD[_ <: Neighbour[T]] = {
    // Fetches the relations for each tenant and joins it with
    // the tenant.
    // If there are n tenants, there should be n partitions in
    // both RDDs. This is a absolute requisite as each tenant
    // must be processed on one single node. As Annoy is interfaced
    // via JNA, the memory pointers to unsafe memory cannot be
    // shuffled.
    dataRDD
      // To understand this construct of mapPartitionsWithIndex, refer to:
      // https://stackoverflow.com/q/33655920/304151
      .mapPartitionsWithIndex {
        (_: Int, partition: Iterator[Embeddings[T]]) => {
          val vectors: Iterator[(Long, Embeddings[T])] = partition.zipWithIndex
            .map { iterator: (Embeddings[T], Int) =>
              (iterator._2, iterator._1)
            }

          //println(s"Building Annoy index with ${annoyConfig.numTrees} trees")
          Annoyer.create(vectors, annoyConfig)
        }
      }
  }
}
