package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;
import com.bonc.hbase.utils.HbaseUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName HbaseFilterTest.java
 * @Description TODO
 * @createTime 2019-11-23 16:33:00
 */
public class HbaseFilterTest {
    public static void main(String[] args) {
        //行过滤器
        //Filter rowFilter = new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL,new BinaryComparator(Bytes.toBytes("a1")));
        //Filter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL,new RegexStringComparator("^a08.*"));
        Filter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL,new SubstringComparator("9022"));


        //特定过滤器
        SingleColumnValueFilter filter = new SingleColumnValueExcludeFilter(
                Bytes.toBytes("info"),
                Bytes.toBytes("email"),
                CompareFilter.CompareOp.NOT_EQUAL,
                new SubstringComparator("80")
                );
        filter.setFilterIfMissing(true);
        Table table = HbaseUtils.getTable("renameTest");
        ResultScanner scanner = HbaseOpt.getScanner(table,rowFilter);
        for(Result result:scanner){
            System.out.println(result);
            byte[] value = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("email"));
            String v = new String(value);
            System.out.println(v);
        }
    }
}
