package com.nosto.spartann

case class MyEmbeddings(id: String, vectors: Seq[Float])
  extends Embeddings[String] {

  override def getId: String = id

  override def getVec: List[Float] = vectors.toList
}
