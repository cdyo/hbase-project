package com.bonc.hbase.demo;

import com.bonc.hbase.utils.HbaseOpt;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName ClusterPrint.java
 * @Description TODO
 * @createTime 2019-12-24 19:28:00
 */
public class ClusterPrint {
    public static void main(String[] args) {
        HbaseOpt.getClusterStatus();
    }
}
