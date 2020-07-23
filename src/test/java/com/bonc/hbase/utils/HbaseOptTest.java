package com.bonc.hbase.utils; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* HbaseOpt Tester. 
* 
* @author <Authors name> 
* @since <pre>11ÔÂ 28, 2019</pre> 
* @version 1.0 
*/ 
public class HbaseOptTest { 

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: creatTable(String tableName, String[] cfs) 
* 
*/ 
@Test
public void testCreatTable() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteTable(String tableName) 
* 
*/ 
@Test
public void testDeleteTable() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: putRow(Table table, String rowKey, String cfName, String qualifier, String data) 
* 
*/ 
@Test
public void testPutRow() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: putRows(Table table, List<Put> puts) 
* 
*/ 
@Test
public void testPutRows() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getRow(Table table, String rowKey) 
* 
*/ 
@Test
public void testGetRowForTableRowKey() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getRow(Table table, String rowKey, FilterList filterList) 
* 
*/ 
@Test
public void testGetRowForTableRowKeyFilterList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getScanner(Table table) 
* 
*/ 
@Test
public void testGetScannerTable() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getScanner(Table table, Filter filter) 
* 
*/ 
@Test
public void testGetScannerForTableFilter() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getScanner(Table table, String startRowKey, String endRowKey) 
* 
*/ 
@Test
public void testGetScannerForTableStartRowKeyEndRowKey() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getScanner(Table table, String startRowKey, String endRowKey, FilterList filterList) 
* 
*/ 
@Test
public void testGetScannerForTableStartRowKeyEndRowKeyFilterList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteRow(Table table, String rowKey) 
* 
*/ 
@Test
public void testDeleteRow() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteColumFamily(Table table, String cfName) 
* 
*/ 
@Test
public void testDeleteColumFamily() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: deleteQualifier(Table table, String rowKey, String cfName, String qualifier) 
* 
*/ 
@Test
public void testDeleteQualifier() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: renameTable(String oldName, String newName) 
* 
*/ 
@Test
public void testRenameTable() throws Exception { 
//TODO: Test goes here... 
} 


} 
