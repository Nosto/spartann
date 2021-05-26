package com.nosto.spartann

import annoy4s.Annoy.annoyLib
import annoy4s.{Angular, Euclidean, Hamming, Manhattan}

import scala.collection.mutable
import scala.util.{Failure, Success, Using}

/**
  * A wrapper for Annoy that consumes an iterator of embedding and returns an
  * iterator of items with the "n" closest related items along with the scores.
  *
  * @author mridang
  */
object Annoyer extends LazyLogging {

  /**
    * Created an Annoy index from an iterator by conveniently zipping it
    * for you.
    *
    * @author mridang
    * @see `com.nosto.product.image.similarity.annoy.Annoyer.create`
    */
  def createFor[IdType](allItems: Iterator[Embeddings[IdType]], annoyConfig: AnnoyConfig):
  Iterator[Neighbour[IdType]] = {

    //TODO: Check if zipWithIndex causes extra iteration
    val zippedIterator: Iterator[(Long, Embeddings[IdType])] = {
      allItems.zipWithIndex.map { row =>
        (row._2.toLong, row._1)
      }
    }

    Annoyer.create[IdType](zippedIterator, annoyConfig)
  }

  /**
    * Creates an Annoy index from a zipped iterator and returns a new iterator
    * with "n" nearest neighbours.
    * <p>
    * Note: The iterator MUST be a zipped iterator. You cannot substitute it by
    * running the ids through a hash function like MurMur3. While a MurMur3 hash
    * function will yield ints/longs - Annoy requires the items to be indexed by
    * a running counter. The index insertion order is not relevant and therefore,
    * the iterator need not be ordered.
    * </p>
    *
    * <p>
    * If an iterator of "n" items with "m" embeddings each is provided to this
    * method, it will yield an iterator of "n" items with the closest <= "o"
    * items (where "o" is defined by the maxResults parameter).
    * </p>
    *
    *
    * @author mridang
    * @param allItems the zipped iterator of if the ids and associated embeddings.
    * @param annoyConfig the annoy configuration to be used for the index
    * @param verbose if verbose logging is to be done. default disabled.
    * @param maxResults the number of results to be returned. default 10.
    * @return an iterator of all the entries with the "n" nearest neighbours.
    */
  def create[IdType](allItems: Iterator[(Long, Embeddings[IdType])],
                     annoyConfig: AnnoyConfig, verbose: Boolean = false, //TODO:
                     maxResults: Int = 10):
  Iterator[Neighbour[IdType]] = {

    val annoyIndex = annoyConfig.metric match {
      case Angular => annoyLib.createAngular(annoyConfig.numDimensions)
      case Euclidean => annoyLib.createEuclidean(annoyConfig.numDimensions)
      case Manhattan => annoyLib.createManhattan(annoyConfig.numDimensions)
      case Hamming => annoyLib.createHamming(annoyConfig.numDimensions)
    }
    annoyLib.verbose(annoyIndex, verbose)

    val indexedItems: mutable.Map[Int, IdType] = mutable.Map[Int, IdType]()
    allItems
      .foreach((item: (Long, Embeddings[IdType])) => {
        val vectors: Array[Float] = item._2.getVec.map { vector =>
          vector.floatValue()
        }.toArray

        println(s"Adding item ${item._2} to index")
        indexedItems.put(item._1.toInt, item._2.getId)
        annoyLib.addItem(annoyIndex, item._1.toInt, vectors)
      })

    annoyLib.build(annoyIndex, annoyConfig.numTrees)

    // Since the annoy index needs to be closed once the lookups are done, we
    // use the new Using monad to close the index as closing the index will
    // free up the native memory.
    //
    // At the time of writing, this `Using` has been back-ported from Scala 2.13
    // and can be removed after the 2.13 migration.
    //
    // Refer to: https://www.baeldung.com/scala/try-with-resources
    Using(new BetterAnnoy(annoyIndex, annoyConfig.numDimensions, annoyConfig.metric))
    { annIndex: BetterAnnoy => {
        indexedItems.map { entry: (Int, IdType) =>
          val relatedItems: Seq[AnnoyRelation[IdType]] = annIndex
            .queryAll(entry._1, maxResults)
            .filter { row: (Int, Float) =>
              row._1 != entry._1
            }
            .map { row: (Int, Float) =>
              indexedItems.get(row._1) match {
                case Some(value: IdType) => (value, row._2)
                case None => throw new RuntimeException
              }
            }
            .map { row: (IdType, Float) =>
              AnnoyRelation(row._1, row._2)
            }

          AnnoyNeighbours[IdType](entry._2, relatedItems)
        }
      }
    } match {
      case Success(n: Iterable[Neighbour[IdType]]) => n.iterator
      // This should not happen, ever; but let's just throw up anyways.
      case Failure(e: Throwable) => throw new RuntimeException(e)
    }
  }
}
