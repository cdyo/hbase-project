package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;
import com.bonc.hbase.utils.HbaseUtils;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName PutTest.java
 * @Description TODO
 * @createTime 2019-12-28 14:35:00
 */
public class PutTest {
    public static void main(String[] args) throws Exception {
        Long start = System.currentTimeMillis();
        HTable table = (HTable) HbaseUtils.getTable("test");
        System.out.println(table.isAutoFlush());
        //table.setAutoFlush(true); //耗时118794
        //table.setAutoFlush(false);//耗时6641 效率最高
        table.setWriteBufferSize(209715200);
        System.out.println(table.isAutoFlush());

        for(int i=0;i<100;i++){
            String rowKey = UUID.randomUUID().toString().replaceAll("-", "");
            HbaseOpt.putRow(table,rowKey,"info","email",rowKey.substring(0,5));
            System.out.println(rowKey);
        }



       /* for(int i=0;i<100000;i++){
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
}
