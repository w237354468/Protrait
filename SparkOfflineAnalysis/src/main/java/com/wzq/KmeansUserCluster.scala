package com.wzq

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

object KmeansUserCluster {
  def main(args: Array[String]) {
    if (args.length < 5) {
      println("Usage:KMeansClustering trainingDataFilePath testDataFilePath numClusters numIterations runTimes")
      sys.exit(1)
    }

    val conf = new
        SparkConf().setAppName("Spark MLlib Exercise:K-Means Clustering")
    val sc = new SparkContext(conf)

    /**
     * Channel Region Fresh Milk Grocery Frozen Detergents_Paper Delicassen
     * 2 3
     * 12669 9656 7561 214 2674 1338
     * 2 3 7057 9810 9568 1762 3293 1776
     * 2 3 6353 8808
     * 7684 2405 3516 7844
     */

    val rawTrainingData = sc.textFile(args(0))
    val parsedTrainingData =
      rawTrainingData.filter(!isColumnNameLine(_)).map(line => {

        Vectors.dense(line.split("\t").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
      }).cache()

    // Cluster the data into two classes using KMeans

    val numClusters = args(2).toInt
    val numIterations = args(3).toInt
    val runTimes =
      args(4).toInt
    var clusterIndex: Int = 0
    val clusters: KMeansModel =
      KMeans.train(parsedTrainingData, numClusters, numIterations)

    println("Cluster Number:" + clusters.clusterCenters.length)

    println("Cluster Centers Information Overview:")
    clusters.clusterCenters.foreach(
      x => {

        println("Center Point of Cluster " + clusterIndex + ":")

        println(x)
        clusterIndex += 1
      })

    //begin to check which cluster each test data belongs to based on the clustering result

    val rawTestData = sc.textFile(args(1))
    val parsedTestData = rawTestData.map(line => {

      Vectors.dense(line.split("\t").map(_.trim).filter(!"".equals(_)).map(_.toDouble))

    })
    parsedTestData.collect().foreach(testDataLine => {
      val predictedClusterIndex:
        Int = clusters.predict(testDataLine)

      println("The data " + testDataLine.toString + " belongs to cluster " +
        predictedClusterIndex)
    })

    println("Spark MLlib K-means clustering test finished.")
  }

  private def
  isColumnNameLine(line: String): Boolean = {
    if (line != null &&
      line.contains("Channel")) true
    else false
  }
}