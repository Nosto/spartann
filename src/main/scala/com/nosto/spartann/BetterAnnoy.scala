package com.nosto.spartann

import annoy4s.{Annoy, Metric}
import com.sun.jna.Pointer

import java.io.Closeable

/**
 * Wrapper for the original Annoy for Scala library that extends and enhances
 * it by adding support for the `Closeable` interface and also providing
 * convenience methods to allow looking up all indexed items.
 *
 * The changes from this class can (possibly) be committed upstream.
 *
 * @author mridang
 * @inheritdoc
 */
class BetterAnnoy(val annoyIndex: Pointer, override val dimension: Int, override val metric: Metric)
  extends Annoy[String](Map.empty, Seq.empty, annoyIndex, dimension, metric)
    with Closeable {

  def queryAll(indexedId: Int, maxReturnSize: Int): Seq[(Int, Float)] = {
    val result: Array[Int] = Array.fill(maxReturnSize)(-1)
    val distances: Array[Float] = Array.fill(maxReturnSize)(-1.0f)

    Annoy.annoyLib.getNnsByItem(annoyIndex,
      indexedId,
      maxReturnSize,
      -1,
      result,
      distances)
    result.toList.filter(_ != -1).zip(distances.toSeq)
  }

  /**
   * Closeable method as the superclass doesn't implement the `Closeable`
   * interface. While it was possible to manually close the index by
   * invoking `Annoy.close`, it cannot be wrapped in the new Scala 2.13
   * `Using` monad.
   *
   * https://www.scala-lang.org/api/current/scala/util/Using$.html
   *
   * <pre>
   * Using(new BetterAnnoy(annoyIndex, dimCount, metric)) {
   * annIndex: BetterAnnoy =>
   * {
   * // do whatever
   * }
   * } match {
   * case Success(n: Any) => n.iterator
   * // This should not happen, ever; but let's just throw up anyways.
   * case Failure(e: Throwable) => throw new RuntimeException(e)
   * }
   * <pre>
   */
  override def close(): Unit = {
    super.close()
  }
}
