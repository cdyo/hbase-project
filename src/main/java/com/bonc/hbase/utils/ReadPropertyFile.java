package com.bonc.hbase.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @program: hbase-training
 * @description: 读取property配置文件
 * @author: chendeyong
 * @create: 2019-09-01 13:27
 */
public class ReadPropertyFile {
    private final static Logger log = LoggerFactory.getLogger(ReadPropertyFile.class);
    public static Properties getPropertyByPath(String path) {
        Properties prop = new Properties();
        InputStream inputStream = null;
        File file = new File(path);
        try {
            inputStream=new FileInputStream(file);
            prop.load(new InputStreamReader(inputStream, "UTF-8"));
        } catch (FileNotFoundException e) {
            log.error("property file not found at "+path);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return prop;
    }

    public static Properties getPropertyByFileName(String fileName) {

        String propertiesPath = Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
        /*String path = System.getProperty("user.dir");
        String propertiesPath = path+"/conf/"+fileName;*/

        log.info("properties file is "+propertiesPath);

        return getPropertyByPath(propertiesPath);
    }

    public static ClassLoader getCurrentClassLoader() {
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader == null)       {
            classLoader = ReadPropertyFile.class.getClassLoader();
        }        return classLoader;
    }

    public static void main(String args[]){
        String path = System.getProperty("user.dir");
        log.info(path);
    }


}
