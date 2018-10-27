/* Copyright 2006, Sterling Commerce, Inc. All rights reserved. */
/*
                     LIMITATION OF LIABILITY
THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL YANTRA CORPORATION BE LIABLE UNDER ANY THEORY OF 
LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
FORESEEABLE AND WHETHER OR NOT YANTRA OR ITS REPRESENTATIVES HAVE 
BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
*/

package com.nwcg.icbs.yantra.api.ldap;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogManager;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This class provides a sample of how to implement LDAP V2 authentication using JNDI.
 */

public class NWCGLdapAuthenticatorAPI implements YIFCustomApi {

	/*
    String ldapURL = null;
    String ldapOU = null;
    String ldapO = null;
    String ldapDN = null;
	*/
    
    private static YFCLogCategory cat = YFCLogCategory.instance(NWCGLdapAuthenticator.class);
    
    public Document authenticate(YFSEnvironment env, Document inputXml) throws Exception 
    
    {
    	/*Input XML is as follows
    	 * <LDAP LoginID="" Password="" JKSPath="" JKSPassword="" LDAPFactory=""
    	 * LDAPDN="" LDAPURL="" LDAPOU="" LDAPO="" SSLEnabled="Y|N"/>
    	 * 
    	 */
 	
    	Element eleRoot = inputXml.getDocumentElement();
    	String sLoginID = eleRoot.getAttribute("LoginID");
    	String sPassword = eleRoot.getAttribute("Password");
    	String ldapDN = eleRoot.getAttribute("LDAPDN");
    	String ldapURL = eleRoot.getAttribute("LDAPURL");
    	String ldapOU = eleRoot.getAttribute("LDAPOU");
    	String ldapO = eleRoot.getAttribute("LDAPO");
    	String sJKSPath = eleRoot.getAttribute("JKSPath");
    	String sJKSPassword = eleRoot.getAttribute("JKSPassword");
    	String ldapFactory = eleRoot.getAttribute("LDAPFactory");
    	String sSSLEnabled = eleRoot.getAttribute("SSLEnabled");
    	
    	
        cat.beginTimer("authenticate");
        
        if (YFCLogManager.verboseEnabled) cat.verbose("Parameters" + ":" + sLoginID );
        System.out.println("User ID "+ sLoginID);
        System.out.println("Password "+ sPassword);
        
        //String ldapFactory = YFSSystem.getProperty("yfs.security.ldap.factory");
        
        // Read ldap properties
        /*
        ldapURL = YFSSystem.getProperty("yfs.security.ldap.url");
        ldapOU  = YFSSystem.getProperty("yfs.security.ldap.ou");
        ldapO   = YFSSystem.getProperty("yfs.security.ldap.o");
        */
        //ldapOU="Dev";
        
        // if any of the ldap params are not set, throw exception
        if (YFCObject.isVoid(ldapURL) || YFCObject.isVoid(ldapOU) || YFCObject.isVoid(ldapO) || YFCObject.isVoid(ldapFactory)) {
            YFCException ex = new YFCException(YCPErrorCodes.YCP_INVALID_LDAP_AUTHENTICATOR_CONFIGURATION);
            ex.setAttribute("yfs.security.ldap.factory",ldapFactory);
            ex.setAttribute("yfs.security.ldap.url",ldapURL);
            ex.setAttribute("yfs.security.ldap.ou",ldapOU);
            ex.setAttribute("yfs.security.ldap.o",ldapO);
            throw ex;
        }
        else {
            Hashtable hashEnv = new Hashtable();
            if (sSSLEnabled.equals("Y")){
            System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
            }
            hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);

            //ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + ", o=" + ldapO;
            //uid=sgomathinayagam,ou=people,ou=Dev,ou=ICBS,ou=Applications,dc=nwcg,dc=gov
            
            if(ldapDN != null){
            if (ldapDN.equals("")){
            ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + ",dc=nwcg,dc=gov";
            }}
            hashEnv.put(Context.PROVIDER_URL, ldapURL);
            if (sSSLEnabled.equals("Y")){
            hashEnv.put(Context.SECURITY_PROTOCOL, NWCGConstants.LDAP_SECURITY_PROTOCOL_SSL);
            }
            hashEnv.put("java.naming.ldap.version", NWCGConstants.LDAP_VERSION); // specify version. default may be V3. and some ldap servers
            // may not support this yet.
            
            hashEnv.put(Context.SECURITY_PRINCIPAL, ldapDN);
            hashEnv.put(Context.SECURITY_CREDENTIALS, sPassword);
            //hashEnv.put(Context.SECURITY_AUTHENTICATION, "EXTERNAL");
            hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
            
            /* Need to change these parameters*/
            System.out.println("GOT THE Resource key "+ ResourceUtil.get("nwcg.ldap.keystore.path"));
            System.out.println("GOT THE Resource password "+ ResourceUtil.get("nwcg.ldap.truststore.password"));
           
            /*//Reading from a properties file
            System.setProperty("javax.net.ssl.keyStore",ResourceUtil.get("nwcg.ldap.keystore.path","/opt/WebSphere/Dev/AppServer/Key/LDAP/NWCGLdapTrustStore.jks"));
            //System.setProperty("javax.net.ssl.keyStoreType", "jks");
            System.setProperty("javax.net.ssl.keyStorePassword",ResourceUtil.get("nwcg.ldap.keystore.password","ICBS123"));
            System.setProperty("javax.net.ssl.trustStore",ResourceUtil.get("nwcg.ldap.truststore.path","/opt/WebSphere/Dev/AppServer/Key/LDAP/NWCGLdapTrustStore.jks"));
            System.setProperty("javax.net.ssl.trustStorePassword",ResourceUtil.get("nwcg.ldap.truststore.password","ICBS123"));
            */
            System.setProperty("javax.net.ssl.keyStoreType", "jks");
            System.setProperty("javax.net.debug", "true");
            
            //The following read from the input XML
            if (sSSLEnabled.equals("Y")){
	            /*
	            System.setProperty("javax.net.ssl.keyStore",sJKSPath);
	            System.setProperty("javax.net.ssl.keyStorePassword",sJKSPassword);
	            System.setProperty("javax.net.ssl.trustStore",sJKSPath);
	            System.setProperty("javax.net.ssl.trustStorePassword",sJKSPassword);
	            */
	            
	            System.setProperty("com.ibm.ssl.keyStore",sJKSPath);
	            System.setProperty("com.ibm.ssl.keyStorePassword",sJKSPassword);
	            System.setProperty("com.ibm.ssl.trustStore",sJKSPath);
	            System.setProperty("com.ibm.ssl.trustStorePassword",sJKSPassword);
	            
            }
            
            cat.debug("LDAP Url " + ldapURL);
            cat.debug("LDAP Security Principal" + ldapDN);
            
            System.out.println("LDAP Url "+ ldapURL);
            System.out.println("LDAP Security Principal "+ ldapDN);
            
            // Create the initial context
            DirContext ctx = null;
            try
            {
            	System.out.println("** initializing the context ***");
            	ctx = new InitialDirContext(hashEnv);
            }
            catch (Exception e)
            {
            	//Error Mesg [LDAP: error code 49 - Invalid Credentials] - Invalid LDAP Password
            	//Error Mesg [LDAP: error code 32 - No Such Object] - Invalid User in LDAP

            	String ErrMsg = e.getMessage();
            	System.out.println("Error Mesg "+ ErrMsg);
            	System.out.println(" error to string  "+e.toString());
            	e.printStackTrace();
            	//return ErrMsg;
            	//throw new NWCGException("NWCG_LDAP_LOGIN_001",new Object[] {sLoginID,ErrMsg}); 	
            }
            System.out.println("Authenticating User "+ sLoginID);
            //            InitialContext ctx = new InitialContext(hashEnv);
            
            // do we need to do anything here? Can we assume if ctx is created without any exception
            // user exists in ldap with that userid and password?
            ctx.close();
            
            cat.debug("Authenticated");
            System.out.println("User is Authenticated "+ sLoginID);
            
            return inputXml;
        }
    }

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public boolean ldapTest(String sSSLEnabled, String ldapFactory,String ldapDN
	, String ldapURL, String sPassword, String sJKSPath, String sJKSPassword)  
    
    {
    	
            Hashtable hashEnv = new Hashtable();
            if (sSSLEnabled.equals("Y")){
            System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
            }
            hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);

            
            
            hashEnv.put(Context.PROVIDER_URL, ldapURL);
            if (sSSLEnabled.equals("Y")){
            	hashEnv.put(Context.SECURITY_PROTOCOL, "ssl");
            }
            hashEnv.put("java.naming.ldap.version", "3"); // specify version. default may be V3. and some ldap servers
            
            hashEnv.put(Context.SECURITY_PRINCIPAL, ldapDN);
            hashEnv.put(Context.SECURITY_CREDENTIALS, sPassword);
            hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
            
                 
            System.setProperty("javax.net.ssl.keyStoreType", "jks");
            System.setProperty("javax.net.debug", "true");
            
            
            //The following read from the input XML
            if (sSSLEnabled.equals("Y")){
	            /*
	            System.setProperty("javax.net.ssl.keyStore",sJKSPath);
	            System.setProperty("javax.net.ssl.keyStorePassword",sJKSPassword);
	            System.setProperty("javax.net.ssl.trustStore",sJKSPath);
	            System.setProperty("javax.net.ssl.trustStorePassword",sJKSPassword);
	            */
                
	            System.setProperty("com.ibm.ssl.keyStore",sJKSPath);
	            System.setProperty("com.ibm.ssl.keyStorePassword",sJKSPassword);
	            System.setProperty("com.ibm.ssl.trustStore",sJKSPath);
	            System.setProperty("com.ibm.ssl.trustStorePassword",sJKSPassword);
	            
            }
            
            
            
            // Create the initial context
            DirContext ctx = null;
            try
            {
            	System.out.println("** initializing the context ***");
            	ctx = new InitialDirContext(hashEnv);
            	System.out.println("** get the context , authentication success ***");
            	ctx.close();
            }
            catch (Exception e)
            {
            	//Error Mesg [LDAP: error code 49 - Invalid Credentials] - Invalid LDAP Password
            	//Error Mesg [LDAP: error code 32 - No Such Object] - Invalid User in LDAP

            	String ErrMsg = e.getMessage();
            	System.out.println("Error Mesg "+ ErrMsg);
            	System.out.println(" error to string  "+e.toString());
            	e.printStackTrace();
            	return false;
            	//return ErrMsg;
            	//throw new NWCGException("NWCG_LDAP_LOGIN_001",new Object[] {sLoginID,ErrMsg}); 	
            }
            
            
            
            return true;
    }
    
    public static void main(String[] argv) {
    	NWCGLdapAuthenticatorAPI ldapAuth = new NWCGLdapAuthenticatorAPI();
        
        //String url = "ldaps://kato.lw-lmco.com:636";
        String url = "ldap://192.168.254.17:389";
        //String keyPath = "/opt/WebSphere/Dev/AppServer/Key/key.jks";
        String keyPath="C:/Documents and Settings/yfu/key.jks";
        //String DN = "uid=taccount,o=people,dc=nwcg,dc=gov";
        String DN = "uid=admin,ou=Groups, dc=sterling,dc=com";
        
        System.out.println("begin ldap testing...");
    	ldapAuth.ldapTest("Y", "com.sun.jndi.ldap.LdapCtxFactory", DN,
    	url, "admin",keyPath ,"password");
    }

}


