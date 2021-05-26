package com.nosto.spartann

trait Related[IdType] {

  def getId: IdType

  def getScore: Float
}
