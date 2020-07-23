package com.bonc.hbaseonspark.basic

import com.bonc.hbase.utils.HbaseUtils
import com.my.hbase.utils.HBaseContext
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.util.Bytes
import com.my.hbase.utils.HBaseRDDFunctions._

/**
 * @ClassName BasicDemo.java
 * @author ChenDeyong
 * @version 1.0.0
 * @Description TODO
 * @createTime 2020-01-07 15:17:00
 */


object BasicDemo {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("HBaseBulkPutExample " +
      "test" + " " + "cf")
    val sc = new SparkContext("local", "test", sparkConf)

    val config = HBaseConfiguration.create()
    config.addResource(new Path("hbase-site.xml"))
    config.set("hadoop.security.authentication","kerberos");

    val connection = HbaseUtils.getConnect;
    val hbaseContext = new HBaseContext(sc, config)

    val columnFamily = "cf"
    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("1"),
        Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("1")))),
      (Bytes.toBytes("2"),
        Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("2")))),
      (Bytes.toBytes("3"),
        Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("3")))),
      (Bytes.toBytes("4"),
        Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("4")))),
      (Bytes.toBytes("5"),
        Array((Bytes.toBytes(columnFamily), Bytes.toBytes("a"), Bytes.toBytes("5"))))
    ))

    rdd.hbaseForeachPartition(hbaseContext, (it, connection) => {
      val bufferedMutator = connection.getBufferedMutator(TableName.valueOf("hbaseonspark"))
      it.foreach((putRecord) => {
        val put = new Put(putRecord._1)
        putRecord._2.foreach((putValue) => put.addColumn(putValue._1, putValue._2,
          putValue._3))
        bufferedMutator.mutate(put)
      })
      bufferedMutator.flush()
      bufferedMutator.close()
    })

    println("Dillon hello")
  }
}