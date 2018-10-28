/*
 * IBM Confidential 
 *
 * OCO Source Materials
 *
 * IBM Sterling Selling and Fullfillment Suite
 *
 * (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
 *
 * The source code for this program is not published or otherwise divested of its trade secrets,
 * irrespective of what has been deposited with the U.S. Copyright Office.
 */

package com.fanatics.sterling.api;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fanatics.sterling.constants.FANUserAuthConstants;
import com.fanatics.sterling.stub.FANBastilleResponse;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.util.YFSAuthenticator;


public class FANBastilleAuthenticator implements YFSAuthenticator {

    /*String ldapURL = null;
    String ldapOU = null;
    String ldapO = null;
    String ldapDN = null;*/

    private static YFCLogCategory cat = YFCLogCategory.instance(FANBastilleAuthenticator.class);
    
    private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	@Override
	public Map authenticate(String sLoginID, String sPassword) throws Exception {
		
		logger.verbose("Inside authenticate");
		logger.verbose("Input id is: "+ sLoginID);
		logger.verbose("Input id is: "+ sPassword);
		
        String inputXml = "<Login  LoginID='"+sLoginID+"' Password='"+sPassword+"' > </Login>" ;
    	
    	Document docInputXml = XMLUtil.getDocument(inputXml);
    	
    	logger.verbose("Input xml to Basteel: "+ XMLUtil.getXMLString(docInputXml));
    	
    	FANBastilleResponse basteelAuthenticate = new FANBastilleResponse();
    	
    	Document responseXml = basteelAuthenticate.performAuthentication(docInputXml);
    	
    	logger.verbose("Response Xml from Basteel is: "+XMLUtil.getXMLString(responseXml));
    	
    	Element eleBasteelResponseRoot = responseXml.getDocumentElement();
    	
    	if (eleBasteelResponseRoot.getAttribute(FANUserAuthConstants.ATT_LoginStatus).equals(FANConstants.Y))
    	return null;
    	
    	else{
    		YFCException ex = new YFCException(YCPErrorCodes.YCP_INVALID_LDAP_AUTHENTICATOR_CONFIGURATION);
    		ex.setErrorDescription(eleBasteelResponseRoot.getAttribute(FANUserAuthConstants.ATT_FailureReasonMessage));
    		ex.setAttribute(FANUserAuthConstants.ATT_FailureReasonCode, eleBasteelResponseRoot.getAttribute(FANUserAuthConstants.ATT_FailureReasonCode));
    		throw ex;
    	}
		
		}

}
/*
 * IBM Confidential 
 *
 * OCO Source Materials
 *
 * IBM Sterling Selling and Fullfillment Suite
 *
 * (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
 *
 * The source code for this program is not published or otherwise divested of its trade secrets,
 * irrespective of what has been deposited with the U.S. Copyright Office.
 

package com.fanatics.sterling.api;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogManager;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.util.YFSAuthenticator;




public class YFSLdapAuthenticator implements YFSAuthenticator {

    String ldapURL = null;
    String ldapOU = null;
    String ldapO = null;
    String ldapDN = null;
	//_an intentional compilation error: this class demonstrates how to make a simple LDAP call and integrate
	//_with YFSAuthenticator, but it is not intended for production use.

    private static YFCLogCategory cat = YFCLogCategory.instance(YFSLdapAuthenticator.class);
    
	@Override
	public Map authenticate(String sLoginID, String sPassword) throws Exception {

		System.out.println("login ID is "+ sLoginID);
		System.out.println("pwd is "+ sPassword);
		
        cat.beginTimer("authenticate");
        if (YFCLogManager.verboseEnabled) cat.verbose("Parameters" + ":" + sLoginID );
        
        String ldapFactory = YFSSystem.getProperty("yfs.security.ldap.factory");
        
        // Read ldap properties
        ldapURL = YFSSystem.getProperty("yfs.security.ldap.url");
        ldapOU  = YFSSystem.getProperty("yfs.security.ldap.ou");
        ldapO   = YFSSystem.getProperty("yfs.security.ldap.o");
        
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
            env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);

            ldapDN = "uid=" + sLoginID + ",dc=" + ldapOU + ",dc=" + ldapO;
            
            env.put(Context.PROVIDER_URL, ldapURL);
            env.put("java.naming.ldap.version", "3"); // specify version. default may be V3. and some ldap servers
            // may not support this yet.
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, ldapDN);
            env.put(Context.SECURITY_CREDENTIALS, sPassword);
            
            System.out.println("LDAP Url " + ldapURL);
            System.out.println("LDAP Security Principal: " + ldapDN);
            
            // Create the initial context
            DirContext ctx = new InitialDirContext(env);
            //            InitialContext ctx = new InitialContext(env);
            
            // do we need to do anything here? Can we assume if ctx is created without any exception
            // user exists in ldap with that userid and password?
            ctx.close();
            
            System.out.println("Authenticated");
            
            return null;
        }
    
	}

}*/