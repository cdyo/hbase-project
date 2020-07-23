package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;

public class DropTable {
    public static void main(String[] args) {
       // System.setProperty("HADOOP_USER_NAME", "hadoop");
        HbaseOpt.deleteTable("tel_num_test");
    }
}
