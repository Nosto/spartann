package com.nosto.spartann

trait Neighbour[IdType] {
  def id: IdType

  def items: Seq[Related[IdType]]
}
