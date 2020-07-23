package com.bonc.hbase.utils;

import com.bonc.hbase.utils.presplit.HashChoreWoker;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.hbase.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ChenDeyong
 * @version 1.0.0
 * @ClassName HbaseOpt.java
 * @Description Hbase操作工具类
 * @createTime 2019-11-21 19:15:00
 */
public class HbaseOpt {
    private static Logger LOG = LoggerFactory.getLogger(HbaseOpt.class);

    public static void testConnect(){
        try(Admin admin = HbaseUtils.getAadmin()){
            HTableDescriptor[] listTables = admin.listTables();
            for(HTableDescriptor hTableDescriptor:listTables){
                LOG.info(hTableDescriptor.getNameAsString());
            }
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

    public static void getClusterStatus(){
        try(Admin admin = HbaseUtils.getAadmin()){
            ClusterStatus clusterStatus = admin.getClusterStatus();

            System.out.println("\nCluster status:-------------");
            System.out.println("HBase version: "+clusterStatus.getHBaseVersion());
            System.out.println("Version: "+clusterStatus.getVersion());
            System.out.println("Num Live Servers: "+clusterStatus.getServersSize() );
            System.out.println("Cluster ID: "+clusterStatus.getClusterId());
            System.out.println("Servers: "+clusterStatus.getServers());
            System.out.println("Num Dead Servers: "+clusterStatus.getDeadServerNames());
            System.out.println("Num Regions: "+clusterStatus.getRegionsCount());
            System.out.println("Region in Transition: "+clusterStatus.getRegionsInTransition());
            System.out.println("Num Requests: "+clusterStatus.getRequestsCount());
            System.out.println("Avg Load: "+clusterStatus.getAverageLoad());

            System.out.println("Server Info---------");
            for(ServerName server:clusterStatus.getServers()){
                System.out.println("HostName:Port: "+server.getHostAndPort()+"\n"
                                +"Server Name: "+server.getServerName()+"\n"
                                +"RPC Port: "+server.getPort()+"\n"
                                +"Start Time: "+stampToDate(server.getStartcode())
                        );
                ServerLoad load = clusterStatus.getLoad(server);
                System.out.println("\tServer load---------");
                System.out.println("\tLoad: "+load.getLoad());
                System.out.println("\tMax Heap(MB): "+load.getMaxHeapMB());
                System.out.println("\tMemstore Size(MB): "+load.getMemstoreSizeInMB());
                System.out.println("\tNum Regions: "+load.getNumberOfRegions());
                System.out.println("\tNum Requests: "+load.getNumberOfRequests());
                System.out.println("\tStoreFile Index Size(MB): "+load.getStorefileIndexSizeInMB());
                System.out.println("\tNum Storefiles: "+load.getStorefiles());
                System.out.println("\tStorefile Size(MB): "+load.getStorefileSizeInMB());
                System.out.println("\tUsed Heap(MB): "+load.getUsedHeapMB());

                System.out.println("\t\tRegion Load--------");
                for(Map.Entry<byte[],RegionLoad> entry:load.getRegionsLoad().entrySet()){
                    System.out.println("\t\tRegion: "+Bytes.toStringBinary(entry.getKey()));
                    RegionLoad regionLoad=entry.getValue();
                    System.out.println("\t\tName: "+Bytes.toStringBinary(regionLoad.getName()));
                    System.out.println("\t\tNum Stores: "+regionLoad.getStores());
                    System.out.println("\t\tNum Storefiles: "+regionLoad.getStorefiles());
                    System.out.println("\t\tStorefile Size(MB): "+regionLoad.getStorefileSizeMB());
                    System.out.println("\t\tStorefile index Size(MB): "+regionLoad.getStorefileIndexSizeMB());
                    System.out.println("\t\tMemstore Size(MB): "+regionLoad.getMemStoreSizeMB());
                    System.out.println("\t\tNum Requests: "+regionLoad.getRequestsCount());
                    System.out.println("\t\tNum Read Requests: "+regionLoad.getReadRequestsCount());
                    System.out.println("\t\tNum Write Requests: "+regionLoad.getWriteRequestsCount());
                    System.out.println();
                }

            }
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

    private static String stampToDate(long stamp){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(stamp);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static boolean createTable(String tableName,String[] cfs,byte[][] splitKeys){
        try(Admin admin = HbaseUtils.getAadmin()){
            if(admin.tableExists(TableName.valueOf(tableName))){
                LOG.warn("table "+tableName+" is exits");
                admin.close();
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            Arrays.stream(cfs).forEach(cf->{
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(cf);
                columnDescriptor.setMaxVersions(1);
                columnDescriptor.setBloomFilterType(BloomType.NONE);
                columnDescriptor.setDataBlockEncoding(DataBlockEncoding.FAST_DIFF);
                columnDescriptor.setCompressionType(Compression.Algorithm.SNAPPY);
                tableDescriptor.addFamily(columnDescriptor);
            });
            admin.createTable(tableDescriptor,splitKeys);
            return true;
        }catch (Exception e){
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static boolean createTable(String tableName, String[] cfs) throws Exception {
        try(Admin admin = HbaseUtils.getAadmin()){
            if(admin.tableExists(TableName.valueOf(tableName))){
                LOG.warn("table "+tableName+" is exits");
                admin.close();
                return false;
            }
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
            for (int i = 0; i < cfs.length; i++) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cfs[i]);
                hColumnDescriptor.setCompressionType(Compression.Algorithm.SNAPPY);
                hColumnDescriptor.setBloomFilterType(BloomType.NONE);
                hColumnDescriptor.setDataBlockEncoding(DataBlockEncoding.FAST_DIFF);
                hColumnDescriptor.setMaxVersions(1);
                tableDesc.addFamily(hColumnDescriptor);

            }
            admin.createTable(tableDesc);
            LOG.info("Table: {} create success!", tableName);
            return true;
        }
    }

    public static void createTable(String tableName, String[] columnFamilies, boolean preBuildRegion) throws Exception {
        if (preBuildRegion) {
            String[] s = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
            int partition = 16;
            byte[][] splitKeys = new byte[partition - 1][];
            for (int i = 1; i < partition; i++) {
                splitKeys[i - 1] = Bytes.toBytes(s[i - 1]);
            }
            createTable(tableName,columnFamilies,splitKeys);
        } else {
            createTable(tableName, columnFamilies);
        }
    }

    /*
    * 根据hash预分区
    * */
    public static void hashPreSplitCreateTable(String tableName,String[] columnFamilies,int baseNum, int numRegions){
        HashChoreWoker worker = new HashChoreWoker(baseNum,numRegions);
        byte [][] splitKeys = worker.calcSplitKeys();
        createTable(tableName,columnFamilies,splitKeys);
    }

    public static boolean deleteTable(String tableName){
        try(Admin admin = HbaseUtils.getAadmin()){
            if(!admin.tableExists(TableName.valueOf(tableName))){
                LOG.warn("table "+tableName+" is not exits");
                return false;
            }
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
        return true;
    }

    public static void descTable(String tableName){
        try(Admin admin = HbaseUtils.getAadmin()){
            if(!admin.tableExists(TableName.valueOf(tableName))){
                LOG.warn("table "+tableName+" is not exits");
            }
            HTableDescriptor tableDescriptor = admin.getTableDescriptor(TableName.valueOf(tableName));
            LOG.info(String.valueOf(tableDescriptor));
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }

    public static void printRegions(String tableName){
        HTable table = (HTable) HbaseUtils.getTable(tableName);
        try {
            final List<HRegionLocation> allRegionLocations = table.getAllRegionLocations();
            Pair<byte[][],byte[][]> pair = table.getStartEndKeys();
            for(int n=0;n<pair.getFirst().length;n++){
                byte[] startKey = pair.getFirst()[n];
                byte[] endKey   = pair.getSecond()[n];
                LOG.info("region["+(n+1)+"] start key:"+Bytes.toString(startKey)
                +", end key:"+Bytes.toString(endKey)+" regionLocation: "+allRegionLocations.get(n));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean putRow(Table table,String rowKey,String cfName,String qualifier,String data){
            Put put = new Put(Bytes.toBytes(rowKey));
            put.setWriteToWAL(false);
            put.addColumn(Bytes.toBytes(cfName),Bytes.toBytes(qualifier),Bytes.toBytes(data));
        try {
            table.put(put);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }

    }

    public static boolean putRows(Table table, List<Put> puts){
        try {
            table.put(puts);
            return true;
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    /**
     * 异步往指定表添加数据 效率一般
     *
     * @param tablename 表名
     * @param puts      需要添加的数据
     * @return long                返回执行时间
     * @throws IOException
     */
    public static void asyncput(String tablename, List<Put> puts) throws Exception {
       // long currentTime = System.currentTimeMillis();
        Connection conn = HbaseUtils.getConnect();
        final BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {
            @Override
            public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
                for (int i = 0; i < e.getNumExceptions(); i++) {
                    System.out.println("Failed to sent put " + e.getRow(i) + ".");
                    LOG.error("Failed to sent put " + e.getRow(i) + ".");
                }
            }
        };
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tablename))
                .listener(listener);
        params.writeBufferSize(50 * 1024 * 1024);

        final BufferedMutator mutator = conn.getBufferedMutator(params);
        try {
            mutator.mutate(puts);
            mutator.flush();
        } finally {
            mutator.close();
            //conn.close();
        }
       // return System.currentTimeMillis() - currentTime;
    }

    public static Result getRow(Table table, String rowKey){
        Result result = null;
        Get get = new Get(Bytes.toBytes(rowKey));
        try {
            result = table.get(get);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    public static Result getRow(Table table, String rowKey, FilterList filterList){
        Result result = null;
        Get get = new Get(Bytes.toBytes(rowKey));
        get.setFilter(filterList);
        try {
            result = table.get(get);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    public static ResultScanner getScanner(Table table){
        ResultScanner result = null;
        Scan scan = new Scan();
        scan.setCaching(1000);
        try {
            result = table.getScanner(scan);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }



    public static ResultScanner getScanner(Table table, Filter filter){
        ResultScanner result = null;
        Scan scan = new Scan();
        scan.setFilter(filter);
        scan.setCaching(1000);
        try {
            result = table.getScanner(scan);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
        return result;
    }

    public static ResultScanner getScanner(Table table,String startRowKey,String endRowKey){
        ResultScanner result = null;
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));
        scan.setCaching(1000);
        try {
            result = table.getScanner(scan);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
        return result;
    }

    public static ResultScanner getScanner(Table table,String startRowKey,String endRowKey,FilterList filterList){
        ResultScanner result = null;
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));
        scan.setFilter(filterList);
        scan.setCaching(1000);
        try {
            table.getScanner(scan);
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
        return result;
    }

    public static boolean deleteRow(Table table,String rowKey){
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        try {
            table.delete(delete);
            return true;
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    public static boolean deleteColumFamily(Table table,String cfName){
        try(Admin admin = HbaseUtils.getAadmin()){
            if(!admin.tableExists(table.getName())){
                LOG.warn("table "+table.getName().getNameAsString()+" is not exits");
                return false;
            }
            admin.deleteColumn(table.getName(),cfName.getBytes());
            return true;
        }catch (IOException e){
            LOG.error(e.getMessage());
            return false;
        }
    }

    public static boolean deleteQualifier(Table table,String rowKey,String cfName,String qualifier){
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumn(Bytes.toBytes(cfName),Bytes.toBytes(qualifier));
        try {
            table.delete(delete);
            return true;
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
    }

    public static void renameTable(String oldName, String newName) {
        String snapshotName = "SnapRename-" + System.currentTimeMillis();
        try {
            try(Admin admin = HbaseUtils.getAadmin()){
                admin.disableTable(TableName.valueOf(oldName));
                admin.snapshot(snapshotName, TableName.valueOf(oldName));
                if (! admin.tableExists(TableName.valueOf(newName))) {
                    try {
                        admin.cloneSnapshot(snapshotName, TableName.valueOf(newName));
                        admin.deleteTable(TableName.valueOf(oldName));
                    } finally {
                        admin.deleteSnapshot(snapshotName);
                    }
                }else{
                    LOG.warn("table "+newName +" is exist");
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }
}
