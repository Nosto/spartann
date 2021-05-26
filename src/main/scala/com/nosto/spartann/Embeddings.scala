package com.nosto.spartann

trait Embeddings[IdType] {

  def getId: IdType

  def getVec: List[Float]

}
