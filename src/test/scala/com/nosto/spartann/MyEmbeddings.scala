package com.nosto.spartann

case class MyEmbeddings(productId: String, vectors: Seq[Float])
  extends Embeddings[String] {

  override def getId: String = productId

  override def getVec: List[Float] = vectors.toList
}
