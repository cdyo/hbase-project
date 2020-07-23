package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;
import com.bonc.hbase.utils.HbaseUtils;
import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapred.SequenceFileAsTextRecordReader;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Filter;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName HbaseTest.java
 * @Description TODO
 * @createTime 2019-11-21 19:12:00
 */
public class HbasePutTest {
    public static void main(String[] args) throws IOException {
       /* ResultScanner scanner = HbaseOpt.getScanner(HbaseUtils.getTable("TestTable"));
        Iterator<Result> iterable = scanner.iterator();
        while(iterable.hasNext()){
            System.out.println(new String(iterable.next().getValue(Bytes.toBytes("info0"), Bytes.toBytes("0"))));
        }*/

       /* String[] familyCol = {"info","address"};
        HbaseOpt.creatTable("cdy_test",familyCol);*/

       /* Table table = HbaseUtils.getTable("cdy_test");
        HbaseOpt.putRow(table,"001","info","name","zhangsan");
        table.close();*/

       //Admin admin = HbaseUtils.getAadmin();
       /*
        ClusterStatus clusterStatus = admin.getClusterStatus();
        Collection<ServerName> deadList = clusterStatus.getDeadServerNames();
        System.out.println(deadList.size());
        for(ServerName serverName:deadList){
            System.out.println(serverName.getHostname());
        }*/

       /* Collection<ServerName> liveList = clusterStatus.getServers();
        System.out.println(liveList.size());
        for(ServerName serverName:liveList){
            System.out.println(serverName.getHostname());
        }*/

       /* System.out.println(clusterStatus.getRegionsCount());
        String[] coprocessors = admin.getMasterCoprocessors();
        for(String str:coprocessors){
            System.out.println(str);
        }*/

        /*admin.disableTable(TableName.valueOf("TestTable"));
        admin.truncateTable(TableName.valueOf("TestTable"),true);*/

       /* HbaseOpt.renameTable("cdy_test","renameTest");*/



        //admin.disableTable(TableName.valueOf("renameTest"));
        //admin.truncateTable(TableName.valueOf("renameTest"),true);




        //table.close();
        //HbaseOpt.testConnect();
        //HbaseOpt.descTable("renameTest");
        //HbaseOpt.printRegions("user");
    }
}
