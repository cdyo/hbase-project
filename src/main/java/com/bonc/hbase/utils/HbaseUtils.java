package com.bonc.hbase.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.io.IOException;
import java.util.Properties;

public class HbaseUtils {
    private static Logger LOG = LoggerFactory.getLogger(HbaseUtils.class);
    private static Connection connection= null;
    private static Configuration configuration=null;
    private static final  String CONF_FILE = "application.properties";
    private static Properties properties=ReadPropertyFile.getPropertyByFileName(CONF_FILE);


    public static Connection getConnect() throws IOException {

        if(configuration == null) {
            configuration = HBaseConfiguration.create();
          //  configuration.addResource("/data/code/project/hbasetraining/src/main/resources/hbase-site.xml");
            configuration.set("hadoop.security.authentication",properties.getProperty("hadoop.security.authentication"));
        }
        if (connection == null || connection.isClosed()) {
            LOG.info("hbase.security.authentication ="+configuration.get("hbase.security.authentication"));
            boolean securityEnabled = "kerberos".equalsIgnoreCase(configuration.get("hbase.security.authentication"));
            if(securityEnabled){

                UserGroupInformation.setConfiguration(configuration);
                UserGroupInformation ugi = null;

                String kdcHost = properties.getProperty("kerberos.kdc.host");
                String kerberosRealm= properties.getProperty("kerberos.kdc.realm");
                String principal= properties.getProperty("kerberos.principal");

                System.setProperty("java.security.krb5.realm", kerberosRealm);
                System.setProperty("java.security.krb5.kdc", kdcHost);

                String principalPasswd = properties.getProperty("kerberos.principal.password");
                String keytab = properties.getProperty("kerberos.keytab.path");

                String authType=properties.getProperty("kerberos.authentication.mode");
                LOG.info("kerberos.authentication.mode="+authType);

                if(authType.equalsIgnoreCase("password")){
                    LOG.info("kdc host:"+kdcHost+"; kerberosRealm:"+kerberosRealm);
                    KrbTools krbTools = new KrbTools(kdcHost,kerberosRealm);

                    LOG.info("principal:"+principal+"; principalPasswd:"+principalPasswd);
                    Subject subject = krbTools.getSubject(principal,principalPasswd);

                    ugi = UserGroupInformation.getUGIFromSubject(subject);

                }else if(authType.equalsIgnoreCase("keytab")){
                    UserGroupInformation.setConfiguration(configuration);
                    ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal,keytab);
                    LOG.info("principal:"+principal+"; keytab file:"+keytab);
                }else {
                    LOG.error("kerberos.authentication.mode is error type "+authType);
                }

                UserGroupInformation.setLoginUser(ugi);

                connection = ConnectionFactory.createConnection(configuration);

            }

        }else{
            connection = ConnectionFactory.createConnection(configuration);
        }
        return connection;
    }

    public static Table getTable(String tabName){
        Table table = null;
        try {
            connection = getConnect();
            table = connection.getTable(TableName.valueOf(tabName)) ;
            //HTable的一种实现
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return table;
    }

    public static Admin getAadmin(){
        Admin admin = null;
        try {
            connection = getConnect();
            admin = connection.getAdmin() ;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return admin;
    }
}
