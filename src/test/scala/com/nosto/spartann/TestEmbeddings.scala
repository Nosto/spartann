package com.nosto.spartann

case class TestEmbeddings[IdType](id: IdType, vectors: Seq[java.lang.Float])
  extends Embeddings[IdType] {

  override def getId: IdType = id

  override def getVec: List[Float] = {
    vectors.map {
      _.floatValue()
    }.toList
  }
}
