package com.nosto.spartann

trait Neighbour[IdType] {
  def id: IdType

  def related: Seq[Related[IdType]]
}
