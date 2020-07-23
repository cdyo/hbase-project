package com.bonc.hbase.utils;

import com.sun.security.auth.callback.TextCallbackHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

/**
 * @program: hbasetrain
 * @description: krb认证工具类
 * @author: chendeyong
 * @create: 2019-10-17 13:50
 */
public class KrbTools {
    private static Logger LOG = LoggerFactory.getLogger(KrbTools.class);
    // KERBEROS Related.
    private String KERBEROS_REALM = null;
    private String KERBEROS_KDC = null;
    private String KERBEROS_PRINCIPAL = null;
    private String KERBEROS_PASSWORD = null;;
    private static final String jaasConfigFilePath = "login.conf";

    private static final String sep = System.getProperty("line.separator");
    private static final String loginConf = "SampleClient {"+sep+
            " com.sun.security.auth.module.Krb5LoginModule required"+sep+
            " debug=true  debugNative=true;"+sep+
            "};";

    public KrbTools(String KDCServerName, String KDCRealm){
        this.KERBEROS_KDC=KDCServerName;
        this.KERBEROS_REALM=KDCRealm;
        LOG.debug("kdc server host is "+KDCServerName+",kerberos realm name is"+KDCRealm+"!");
    }

    private void  init(){
        try {
            createLoginConfFile(loginConf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperty("java.security.auth.login.config", jaasConfigFilePath);
        System.setProperty("java.security.krb5.realm", KERBEROS_REALM );
        System.setProperty("java.security.krb5.kdc", KERBEROS_KDC);
        System.setProperty("sun.security.krb5.debug", "false");
    }

    private static String createLoginConfFile(String loginConf) throws IOException {
        String filename = jaasConfigFilePath;
        File f = new File(filename);
        LOG.info("java.security.auth.login.config="+f.getAbsolutePath());
        FileUtils.writeStringToFile(f,loginConf, "UTF-8");
        return filename;
    }

    private class MyCallbackHandler implements CallbackHandler {
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
            for (int i = 0; i < callbacks.length; i++) {
                if (callbacks[i] instanceof NameCallback) {
                    NameCallback nc = (NameCallback)callbacks[i];
                    nc.setName(KERBEROS_PRINCIPAL);
                } else if (callbacks[i] instanceof PasswordCallback) {
                    PasswordCallback pc = (PasswordCallback)callbacks[i];
                    pc.setPassword(KERBEROS_PASSWORD.toCharArray());
                } else throw new UnsupportedCallbackException
                        (callbacks[i], "Unrecognised callback");
            }
        }
    }

    /**
    *@Description: kerberos 账号/密码认证
    *@Param:
    *@return:
    *@date: 2019/10/17
    */
    public Subject getSubject(String principal,String password) {
        this.KERBEROS_PRINCIPAL=principal;
        this.KERBEROS_PASSWORD=password;
        init();

        Subject signedOnUserSubject = null;
        // create a LoginContext based on the entry in the login.conf file
        LoginContext lc;
        try {
            lc = new LoginContext("SampleClient", new MyCallbackHandler());
            // login (effectively populating the Subject)
            lc.login();
            // get the Subject that represents the signed-on user
            signedOnUserSubject = lc.getSubject();
        } catch (LoginException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.exit(0);
        }
        return signedOnUserSubject;
    }

    /**
    *@Description: kerberos 交互式认证
    *@Param: void
    *@return: Subject
    *@date: 2019/10/17
    */
    public Subject getSubject() {
        init();
        LoginContext lc = null;
        try {
            lc = new LoginContext("SampleClient", new TextCallbackHandler());
            try {
                lc.login();
            } catch (LoginException le) {
                le.printStackTrace();
                LOG.error("Authentication failed:");
                LOG.error("  " + le.getMessage());
                System.exit(-1);
            }

        } catch (LoginException le) {
            LOG.error("Cannot create LoginContext. " + le.getMessage());

        } catch (SecurityException se) {
            LOG.error("Cannot create LoginContext. " + se.getMessage());
            System.exit(-1);
        }finally {
            if(lc==null){
                return null;
            }else{
                LOG.info("Authentication principal:" + lc.getSubject().getPrincipals() + "succeeded!");
                return lc.getSubject();
            }
        }
    }
}
