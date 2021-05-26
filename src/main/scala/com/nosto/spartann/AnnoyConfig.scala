package com.nosto.spartann

import annoy4s.{Angular, Metric}

/**
  * Case class encapsulate common Annoy-related indexing parameters
  *
  * @param numTrees the number of trees to use. Use < 100 for performance
  * @param metric the approximation metric to be used. default angular.
  * @param numDimensions the number of dimensions. Each record must have the same amount.
 * @author mridang
  */
//noinspection ScalaStyle
case class AnnoyConfig(numTrees: Int = 100, numDimensions: Int = 1280, metric: Metric = Angular)
