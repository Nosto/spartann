package com.nosto.spartann

case class AnnoyRelation[IdType](id: IdType, score: Float)
  extends Related[IdType] {

  override def getId: IdType = id

  override def getScore: Float = score
}
