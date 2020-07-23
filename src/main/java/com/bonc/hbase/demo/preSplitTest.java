package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;
import com.bonc.hbase.utils.HbaseUtils;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.util.ArrayList;
import java.util.UUID;

import static com.bonc.hbase.utils.HbaseOpt.*;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName preSplitTest.java
 * @Description TODO
 * @createTime 2019-12-28 16:11:00
 */
public class preSplitTest {
    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "hadoop");


        String[] cf = {"cf"};
        HbaseOpt.hashPreSplitCreateTable("tel_num_test",cf,100000,9);
        //HbaseOpt.deleteTable("hashsplittable");
       // HbaseOpt.createTable("nosplittable",cf);

        Long start = System.currentTimeMillis();
        //HTable table = (HTable) HbaseUtils.getTable("hashsplittable");//耗时111217
       HTable table = (HTable) HbaseUtils.getTable("tel_num_test2");//耗时213439
        System.out.println(table.isAutoFlush());
        //table.setAutoFlush(true); //耗时118794
        table.setAutoFlush(false);//耗时6641 效率最高   table.flushCommits();
        System.out.println(table.isAutoFlush());


       for(int i=0;i<100000000;i++){
            String randTel = getTel();
            String rowKey = new StringBuffer(randTel).reverse().toString();

            HbaseOpt.putRow(table,rowKey,"cf","tel-number",randTel);
            System.out.println(rowKey);
        }

        table.flushCommits();


       /* for(int i=0;i<1000000;i++){
            ArrayList putList = new ArrayList<Put>();
            for(int n=0;i<10000;i++){
                String rowKey = UUID.randomUUID().toString().replaceAll("-", "");
                Put put = new Put(rowKey.getBytes());
                put.add("info".getBytes(),"email".getBytes(),rowKey.substring(0,5).getBytes());
                putList.add(put);
                // HbaseOpt.putRows(table,putList);
                HbaseOpt.asyncput("renameTest",putList);
            }
        }//18420*/

        Long end = System.currentTimeMillis();
        System.out.println(end-start);//215384;2867*/
    }

    private static String[] telFirst="134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
    private static String getTel() {
        int index=getNum(0,telFirst.length-1);
        String first=telFirst[index];
        String second=String.valueOf(getNum(1,888)+10000).substring(1);
        String third=String.valueOf(getNum(1,9100)+10000).substring(1);
        return first+second+third;
    }

    public static int getNum(int start,int end) {
        return (int)(Math.random()*(end-start+1)+start);
    }
}
