# Spartann

Hyper performant kNN using Annoy for Apache Spark. 

## Installation

Unfortunately, Spartann is not available in any public Maven repositories except the GitHub Package Registry. For more information on how to install packages
from the GitHub Package
Registry, [https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages#installing-a-package][see the GitHub docs]

## Usage

The usage of Annoy is simple. Spartann accepts a dataset of identifier and vector pairs. To take the simplest object.

```scala
case class Book(isbn: String, features: Array[Double]) extends Embeddings[String]
```

All classes must implement the `Embeddings[T]` interface where `T` is the identifer of the object. `T` in this case is a `String` but may be anything. You must also specify the Annoy configuration. 

```scala
final val annoyConfig: AnnoyConfig = AnnoyConfig(50, 256, annoy4s.Euclidean)
```

`AnnoyConfig` accepts the number of trees, the dimensionality of the the data and the distance algorithm. An increase in the dimensionality or the number of tress will also degrade performance.

Annoy indexes all items with a non-negative integer value and allocates memory for max(i)+1 items. As shown in both examples, the iterators are zipped so that each item is indexed. The order of the items is not relevant.

### In Dataframes

Assume you have a dataset of `Dataset[Book]` and you need to find the similar books published by a publisher. 

```scala
val records: List[Book] = List(...)
sqlContext.createDataset(records)(Encoders.kryo[Book])
  .mapPartitions((books: Iterator[Book]) => {
     val vectors: Iterator[(Long, Book)] = books.zipWithIndex.map(_.swap)
       .map(item => (item._1.toLong, item._2))
     Annoyer.create(vectors, annoyConfig = annoyConfig, verbose = false, maxResults = 50)
  })(Encoders.kryo[Neighbour[String]])
  .show()
```

⚠ SpartAnn has been tested with `mapPartitions` and the code currently lacks comprehensive tests against `mapGroups`.

### In RDDs

Assume you have a dataset of `RDD[Book]` and you need to find the similar books published by a publisher. 

```scala
sc.parallelize(..)
  .mapPartitions((books: Iterator[Book]) => {
     val vectors: Iterator[(Long, Book)] = books.zipWithIndex.map(_.swap)
       .map(item => (item._1.toLong, item._2))
     Annoyer.create(vectors, annoyConfig = annoyConfig, verbose = false, maxResults = 50)
  })
```

## Authors

* Mridang Agarwalla <mridang@nosto.com>

## License

Apache-2.0 License

[see the GitHub docs]: https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages#installing-a-package
