package com.nosto.spartann

case class AnnoyNeighbours[IdType](override val id: IdType, override val items: Seq[AnnoyRelation[IdType]])
  extends Neighbour[IdType]
