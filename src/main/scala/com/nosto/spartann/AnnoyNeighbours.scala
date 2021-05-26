package com.nosto.spartann

case class AnnoyNeighbours[IdType](override val id: IdType, override val related: Seq[AnnoyRelation[IdType]])
  extends Neighbour[IdType]
