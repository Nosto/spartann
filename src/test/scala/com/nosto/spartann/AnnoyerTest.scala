package com.nosto.spartann

import annoy4s.{Angular, Euclidean, Hamming, Manhattan}
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import org.scalatest.{FunSuite, Matchers}

@RunWith(classOf[JUnitRunner])
class AnnoyerTest extends FunSuite with Matchers {

  /**
   * Test the lookups via the Euclidean distance works as expected. This test
   * has been cannibalised from:
   *
   * https://github.com/spotify/annoy/blob/master/test/euclidean_index_test.py
   *
   * <pre>
   * +-----+-----+-----+-----+-------------------------------------------------+
   * | Id. | V¹  | V²  | V³  |                 Should Relate To                |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 0   | 2.0 | 2.0 |     | 1 - 1.0000000000000000 | 2 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 1   | 3.0 | 2.0 |     | 0 - 1.0000000000000000 | 2 - 1.0000000000000000 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 2   | 3.0 | 3.0 |     | 1 - 1.0000000000000000 | 0 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * </pre>
   *
   */
  test("that a-NN lookup via a Euclidean distance works") {
    val embeddings: Iterator[TestEmbeddings[String]] =
      Iterator(TestEmbeddings("0", Seq(2.0f, 2.0f)),
        TestEmbeddings("1", Seq(3.0f, 2.0f)),
        TestEmbeddings("2", Seq(3.0f, 3.0f)))

    val result: Seq[AnnoyNeighbours[String]] = Seq(
      AnnoyNeighbours[String]("2", Seq(AnnoyRelation("1", 1.0f), AnnoyRelation("0", 1.4142135f))),
      AnnoyNeighbours[String]("1", Seq(AnnoyRelation("0", 1.0f), AnnoyRelation("2", 1.0f))),
      AnnoyNeighbours[String]("0", Seq(AnnoyRelation("1", 1.0f), AnnoyRelation("2", 1.4142135f)))
    ).toList

    Annoyer
      .createFor(embeddings, AnnoyConfig(10, 2, Euclidean))
      .toList shouldBe result
  }

  /**
   * Test the lookups via the Angular distance works as expected. This test
   * has been cannibalised from:
   *
   * https://github.com/spotify/annoy/blob/master/test/angular_index_test.py
   *
   * <pre>
   * +-----+-----+-----+-----+-------------------------------------------------+
   * | Id. | V¹  | V²  | V³  |                 Should Relate To                |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 0   | 2.0 | 1.0 | 0.0 | 1 - 0.6324555277824402 | 2 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 1   | 1.0 | 2.0 | 0.0 | 0 - 0.6324555277824402 | 2 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 2   | 0.0 | 0.0 | 1.0 | 0 - 1.4142135381698608 | 1 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * </pre>
   */
  test("that a-NN lookup via an Angular distance works") {
    val embeddings: Iterator[TestEmbeddings[String]] =
      Iterator(TestEmbeddings("0", Seq(2.0f, 1.0f, 0.0f)),
        TestEmbeddings("1", Seq(1.0f, 2.0f, 0.0f)),
        TestEmbeddings("2", Seq(0.0f, 0.0f, 1.0f)))

    val result: Seq[AnnoyNeighbours[String]] = Seq(
      AnnoyNeighbours[String]("2", Seq(AnnoyRelation("0", 1.4142135f), AnnoyRelation("1", 1.4142135f))),
      AnnoyNeighbours[String]("1", Seq(AnnoyRelation("0", 0.6324555f), AnnoyRelation("2", 1.4142135f))),
      AnnoyNeighbours[String]("0", Seq(AnnoyRelation("1", 0.6324555f), AnnoyRelation("2", 1.4142135f)))
    ).toList

    Annoyer
      .createFor(embeddings, AnnoyConfig(10, 3, Angular))
      .toList shouldBe result
  }

  /**
   * Test the lookups via the Manhattan distance works as expected. This test
   * has been cannibalised from:
   *
   * https://github.com/spotify/annoy/blob/master/test/manhattan_index_test.py
   *
   * <pre>
   * +-----+-----+-----+-----+-------------------------------------------------+
   * | Id. | V¹  | V²  | V³  |                 Should Relate To                |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 0   | 2.0 | 1.0 | 0.0 | 1 - 1.0000000000000000 | 0 - 2.0000000000000000 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 1   | 1.0 | 2.0 | 0.0 | 0 - 1.0000000000000000 | 2 - 1.0000000000000000 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 2   | 0.0 | 0.0 | 1.0 | 0 - 1.0000000000000000 | 1 - 2.0000000000000000 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * </pre>
   */
  test("that a-NN lookup via an Manhattan distance works") {
    val embeddings: Iterator[TestEmbeddings[String]] =
      Iterator(TestEmbeddings("0", Seq(2.0f, 2.0f)),
        TestEmbeddings("1", Seq(3.0f, 2.0f)),
        TestEmbeddings("2", Seq(3.0f, 3.0f)))

    val result: Seq[AnnoyNeighbours[String]] = Seq(
      AnnoyNeighbours[String]("2", Seq(AnnoyRelation("1", 1.0f), AnnoyRelation("0", 2.0f))),
      AnnoyNeighbours[String]("1", Seq(AnnoyRelation("0", 1.0f), AnnoyRelation("2", 1.0f))),
      AnnoyNeighbours[String]("0", Seq(AnnoyRelation("1", 1.0f), AnnoyRelation("2", 2.0f)))
    ).toList

    Annoyer
      .createFor(embeddings, AnnoyConfig(10, 2, Manhattan))
      .toList shouldBe result
  }

  /**
   * Test the lookups via the Hamming distance works as expected. This test
   * has been cannibalised from:
   *
   * https://github.com/spotify/annoy/blob/master/test/hamming_index_test.py
   *
   * <pre>
   * +-----+-----+-----+-----+-------------------------------------------------+
   * | Id. | V¹  | V²  | V³  |                 Should Relate To                |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 0   | 2.0 | 1.0 | 0.0 | 1 - 0.6324555277824402 | 2 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 1   | 1.0 | 2.0 | 0.0 | 0 - 0.6324555277824402 | 2 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * | 2   | 0.0 | 0.0 | 1.0 | 0 - 1.4142135381698608 | 1 - 1.4142135381698608 |
   * +-----+-----+-----+-----+------------------------+------------------------+
   * </pre>
   */
  ignore("that a-NN lookup via an Hamming distance works") {
    val embeddings: Iterator[TestEmbeddings[String]] =
      Iterator(TestEmbeddings("0", Seq(2.0f, 1.0f, 0.0f)),
        TestEmbeddings("1", Seq(1.0f, 2.0f, 0.0f)),
        TestEmbeddings("2", Seq(0.0f, 0.0f, 1.0f)))

    val result: Seq[AnnoyNeighbours[String]] = Seq(
      AnnoyNeighbours[String]("2", Seq(AnnoyRelation("1", 1.4142135f), AnnoyRelation("0", 1.4142135f))),
      AnnoyNeighbours[String]("1", Seq(AnnoyRelation("0", 0.6324555f), AnnoyRelation("2", 1.4142135f))),
      AnnoyNeighbours[String]("0", Seq(AnnoyRelation("1", 0.6324555f), AnnoyRelation("2", 1.4142135f)))
    ).toList

    Annoyer
      .createFor(embeddings, AnnoyConfig(10, 3, Hamming))
      .toList shouldBe result
  }
}
