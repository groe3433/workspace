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



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogManager;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.util.YFSAuthenticator;


/**
 * This class provides a sample of how to implement LDAP V2 authentication using JNDI.
 */

public class NWCGLdapAuthenticator implements YFSAuthenticator {

    String ldapURL = null;
    String ldapOU = null;
    String ldapO = null;
    String ldapDN = null;
    String ExpTimeStr = "";
    String ExpStr = "";
    String MaxAgeStr = "";
    String WarningStr = "";
	
    private static YFCLogCategory cat = YFCLogCategory.instance(NWCGLdapAuthenticator.class);
    
    public Map authenticate(String sLoginID, String sPassword) throws Exception {
        cat.beginTimer("authenticate");
        if (YFCLogManager.verboseEnabled) cat.verbose("Parameters" + ":" + sLoginID );

        Hashtable lMap = new Hashtable();        
        
        String ldapFactory = YFSSystem.getProperty("yfs.security.ldap.factory");
        
        // Read ldap properties
        ldapURL = YFSSystem.getProperty("yfs.security.ldap.url");
        ldapOU  = YFSSystem.getProperty("yfs.security.ldap.ou");
        ldapO   = YFSSystem.getProperty("yfs.security.ldap.o");
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
            Hashtable env = new Hashtable();
            //System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
            env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
 
            //ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + ",dc=nwcg,dc=gov";
            ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + "," + ldapO;
            
            env.put(Context.PROVIDER_URL, ldapURL);
            env.put(Context.SECURITY_PROTOCOL, "ssl");
            env.put("java.naming.ldap.version", "3"); // specify version. default may be V3. and some ldap servers
            
            env.put(Context.SECURITY_PRINCIPAL, ldapDN);
            env.put(Context.SECURITY_CREDENTIALS, sPassword);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            
            /* TrustStore Parameters */
            String KeyStoreFile = YFSSystem.getProperty("nwcg.ldap.keystore.file");
            String KeyStorePasswd = YFSSystem.getProperty("nwcg.ldap.keystore.password");
            System.out.println("KeyStoreFile=" + KeyStoreFile + ";KeyStorePasswd=" + KeyStorePasswd);
            // As part of NAP LDAP change, added above three lines and commented below two lines so that 
            // we can easily change properties per region (above lines read from customer_overrides.properties)
            // String KeyStoreFile = ResourceUtil.get("nwcg.ldap.keystore.file");
            // String KeyStorePasswd = ResourceUtil.get("nwcg.ldap.keystore.password");
            System.setProperty("javax.net.ssl.keyStoreType","jks");
            System.setProperty("javax.net.ssl.keyStore",KeyStoreFile);
            System.setProperty("javax.net.ssl.keyStorePassword",KeyStorePasswd);
            System.setProperty("javax.net.ssl.trustStoreType","jks");
            System.setProperty("javax.net.ssl.trustStore",KeyStoreFile);
            System.setProperty("javax.net.ssl.trustStorePassword",KeyStorePasswd);
            
            cat.debug("LDAP Url " + ldapURL);
            cat.debug("LDAP Security Principal" + ldapDN);
            
            System.out.println("LDAP Url "+ ldapURL);
            System.out.println("LDAP Security Principal "+ ldapDN);
            
            // Create the initial context 
            DirContext ctx = null;
            try
            {
              ctx = new InitialDirContext(env);
            }
            catch (Exception e)
            {
            	//Error Mesg [LDAP: error code 49 - Invalid Credentials] - Invalid LDAP Password
            	//Error Mesg [LDAP: error code 32 - No Such Object] - Invalid User in LDAP
            	//Error Mesg [LDAP: error code 19 - Exceed password retry limit. Please try later.]
            	//Error Mesg [LDAP: error code 49 - password expired!]

            	String ErrMsg = e.getMessage();
            	System.out.println("Error Mesg "+ ErrMsg);
            	if ( (ErrMsg.indexOf("error code 49") != -1) && (ErrMsg.indexOf("password expired") != -1) ) {
                 throw new Exception("Your password has expired. Please contact security administrator to reset your password.");
        		}
               	else if ( (ErrMsg.indexOf("error code 19") != -1) && (ErrMsg.indexOf("Exceed password retry limit") != -1) ) {
                 throw new Exception("Your account has been locked. Please wait 15 minutes for your account to be reset.");
           		}
               	else
               	{
                 throw new Exception("Login failed");
               	}
            }
            
            // If Ctx is Created, User ID and Password Exist in LDAP Server
            
            // Getting modifyTimestamp for the User ID  to calculate the Password Expiry Days       
            try {  	
            	
                /*Control[] respControls; 
                if ((respControls = ctx.getResponseControls()) != null) 
                { 
                  // Loop through the results... 
                  long secPwdExpire = 0; 
                  for (int i = 0; i < respControls.length; i++) 
                  { 
                 	 if (respControls[i] instanceof PasswordExpiringResponseControl) 
                	 { 
                		// Password is set to expire, set error message... 
                	       secPwdExpire = ((PasswordExpiringResponseControl) respControls[i]).timeRemaining(); 
                    	   System.out.println("Password expires in " + secPwdExpire + " seconds."); 
                		   secPwdExpire = ( new Date().getTime() + (secPwdExpire * 1000)); 
                	       System.out.println("Date password expires is " + new Date(secPwdExpire)); 
                	 }
                 	 
                     if (respControls[i] instanceof PasswordExpiredResponseControl) 
                     { 
                        // Password expired, return... 
                        System.out.println("Password Expired."); 
                        return true; 
                     } 
                   }
                }*/
            	
            	String MY_FILTER = "uid=" + sLoginID;
            	String MY_SEARCHBASE = "ou=" + ldapOU + "," + ldapO;
            	System.out.println("Getting Password Expiry Controls");
            	//String MY_SEARCHBASE= "cn=config";
            	//String MY_FILTER = "(&(cn=Password Policy))";
            	SearchControls constraints = new SearchControls(); 
            	// Apr 26th 2013 - NAP integration change, parameters changed suit IBM LDAP Server.  Old parameters are commented
            	// String[] getAttrs = {"passwordExpirationTime","passwordExp","passwordMaxAge","passwordWarning"};
            	String[] getAttrs = {"pwdGraceUseTime","pwdExpirationWarned","pwdChangedTime", "employeeNumber"};
            	constraints.setReturningAttributes(getAttrs);
            	NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);
 
             	while (results.hasMore()) {
                     SearchResult result = (SearchResult) results.next();
                     Attributes attrs = result.getAttributes();
                     NamingEnumeration<String> nameAttrs = attrs.getIDs();
                     while(nameAttrs.hasMoreElements())	{
                    	 System.out.println("LDAP attributes:" + nameAttrs.nextElement());
                     }
                     // throwing Exception with NAP LDAP, so commented before we fix the problem
                     // Attribute ExpTime = attrs.get("passwordExpirationTime");
                     // ExpTimeStr = (String)ExpTime.get();
                     ExpTimeStr = "20161026224439Z";
                     System.out.println("ExpTimeStr "+ ExpTimeStr);
                     //20091026224439Z

                     /*System.out.println("Getting passwordExp");
                     Attribute PExp = attrs.get("passwordExp");
                     ExpStr = (String)PExp.get();
                     System.out.println("ExpStr "+ ExpStr);
                     
                     System.out.println("Getting passwordMaxAge");
                     Attribute MaxAge = attrs.get("passwordMaxAge");
                     MaxAgeStr = (String)MaxAge.get();
                     System.out.println("MaxAgeStr "+ MaxAgeStr);
                     
                     System.out.println("Getting passwordWarning");
                     Attribute Warning = attrs.get("passwordWarning");
                     WarningStr = (String)Warning.get();
                     System.out.println("WarningStr "+ WarningStr);
                     
                      NamingEnumeration e = attrs.getAll();
                      while (e.hasMore()) {
                       Attribute attr = (Attribute) e.next();
                       System.out.println("Attribute "+attr);                   
                     }*/                     
                 }
            	 
            }
            catch (NamingException e) 
            {
        	  System.err.println("Search Failed");
        	  e.printStackTrace();
        	}
            
            cat.debug("Authenticated");
            System.out.println("Authenticated User ID : "+ sLoginID);
            
            //lMap.put("ChangePasswordLink","http://www.stercomm.com");
            ctx.close();
           
            //String ExpDays = ResourceUtil.get("nwcg.ldap.password.expirydays");
            long days = getPasswordExpiryDays(ExpTimeStr);

            if (days > 0 && days < 8)
             {  
               String EID = new Long(days).toString();
               lMap.put("ExpiresInDays",EID);
               return lMap;
             }
             else
             {
               return null;
             }
         }
    }
    
    public long getPasswordExpiryDays(String ModDate)
    {
	  SimpleDateFormat ldapDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	  long daydiff = 0;
	  Date today = new java.util.Date();
	  System.out.println("Today's Date is " +  today.toString());
	  long time1 = today.getTime();
	  System.out.println("Time1 " + time1);
	  
	  boolean UTCFlag = false;
      if (ModDate.endsWith("Z")) {
    	  UTCFlag = true;
      }
      
      if (UTCFlag) {
          ldapDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      }
      else {
          ldapDateFormat.setTimeZone(TimeZone.getDefault());
      }
      
      try {
           Date tdate = ldapDateFormat.parse(ModDate);
	       long time2 = tdate.getTime();
	       System.out.println("Time2 " + time2);
	       long diff = time2 - time1;
	       daydiff = diff/(1000*60*60*24);
	       System.out.println("Diff " + diff);
	       System.out.println("Days Difference " + daydiff);
       }
       catch (Exception e)
       {
    	System.err.println("Date Format Error");
      	e.printStackTrace();
       }
	  
	  return daydiff;
    }

}


