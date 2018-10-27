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
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogManager;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class provides a sample of how to implement LDAP V2 authentication using
 * JNDI.
 */
public class NWCGLdapAuthenticatorAPI implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGLdapAuthenticatorAPI.class);

	/**
	 * 
	 * @param env
	 * @param inputXml
	 * @return
	 * @throws Exception
	 */
	public Document authenticate(YFSEnvironment env, Document inputXml) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticatorAPI::authenticate");
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
		logger.verbose("authenticate");
		if (YFCLogManager.verboseEnabled)
			logger.verbose("Parameters" + ":" + sLoginID);
		// if any of the ldap params are not set, throw exception
		if (YFCObject.isVoid(ldapURL) || YFCObject.isVoid(ldapOU) || YFCObject.isVoid(ldapO) || YFCObject.isVoid(ldapFactory)) {
			YFCException ex = new YFCException(YCPErrorCodes.YCP_INVALID_LDAP_AUTHENTICATOR_CONFIGURATION);
			ex.setAttribute("yfs.security.ldap.factory", ldapFactory);
			ex.setAttribute("yfs.security.ldap.url", ldapURL);
			ex.setAttribute("yfs.security.ldap.ou", ldapOU);
			ex.setAttribute("yfs.security.ldap.o", ldapO);
			throw ex;
		} else {
			Hashtable hashEnv = new Hashtable();
			if (sSSLEnabled.equals("Y")) {
				System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
				java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
			}
			hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
			if (ldapDN != null) {
				if (ldapDN.equals("")) {
					ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + ",dc=nwcg,dc=gov";
				}
			}
			hashEnv.put(Context.PROVIDER_URL, ldapURL);
			if (sSSLEnabled.equals("Y")) {
				hashEnv.put(Context.SECURITY_PROTOCOL, NWCGConstants.LDAP_SECURITY_PROTOCOL_SSL);
			}
			hashEnv.put("java.naming.ldap.version", NWCGConstants.LDAP_VERSION);
			hashEnv.put(Context.SECURITY_PRINCIPAL, ldapDN);
			hashEnv.put(Context.SECURITY_CREDENTIALS, sPassword);
			hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			String keyStoreType = YFSSystem.getProperty("nwcg.ob.keystore.keyStoreType");
			System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
			System.setProperty("javax.net.debug", "true");
			// The following read from the input XML
			if (sSSLEnabled.equals("Y")) {
				System.setProperty("com.ibm.ssl.keyStore", sJKSPath);
				System.setProperty("com.ibm.ssl.keyStorePassword", sJKSPassword);
				System.setProperty("com.ibm.ssl.trustStore", sJKSPath);
				System.setProperty("com.ibm.ssl.trustStorePassword", sJKSPassword);
			}
			logger.debug("LDAP Url " + ldapURL);
			logger.debug("LDAP Security Principal" + ldapDN);
			// Create the initial context
			DirContext ctx = null;
			try {
				ctx = new InitialDirContext(hashEnv);
			} catch (Exception e) {
			}
			ctx.close();
			logger.debug("Authenticated");
			logger.verbose("@@@@@ Exiting NWCGLdapAuthenticatorAPI::authenticate");
			return inputXml;
		}
	}

	/**
	 * 
	 */
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}

	/**
	 * 
	 * @param sSSLEnabled
	 * @param ldapFactory
	 * @param ldapDN
	 * @param ldapURL
	 * @param sPassword
	 * @param sJKSPath
	 * @param sJKSPassword
	 * @return
	 */
	public boolean ldapTest(String sSSLEnabled, String ldapFactory, String ldapDN, String ldapURL, String sPassword, String sJKSPath, String sJKSPassword) {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticatorAPI::ldapTest");
		Hashtable hashEnv = new Hashtable();
		if (sSSLEnabled.equals("Y")) {
			System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
			java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
		}
		hashEnv.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
		hashEnv.put(Context.PROVIDER_URL, ldapURL);
		if (sSSLEnabled.equals("Y")) {
			hashEnv.put(Context.SECURITY_PROTOCOL, "ssl");
		}
		// specify version. default may be V3. and some ldap servers
		hashEnv.put("java.naming.ldap.version", "3"); 
		hashEnv.put(Context.SECURITY_PRINCIPAL, ldapDN);
		hashEnv.put(Context.SECURITY_CREDENTIALS, sPassword);
		hashEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		String keyStoreType = YFSSystem.getProperty("nwcg.ob.keystore.keyStoreType");
		System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
		System.setProperty("javax.net.debug", "true");
		// The following read from the input XML
		if (sSSLEnabled.equals("Y")) {
			System.setProperty("com.ibm.ssl.keyStore", sJKSPath);
			System.setProperty("com.ibm.ssl.keyStorePassword", sJKSPassword);
			System.setProperty("com.ibm.ssl.trustStore", sJKSPath);
			System.setProperty("com.ibm.ssl.trustStorePassword", sJKSPassword);
		}
		// Create the initial context
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(hashEnv);
			ctx.close();
		} catch (Exception e) {
			// Error Mesg [LDAP: error code 49 - Invalid Credentials] - Invalid LDAP Password
			// Error Mesg [LDAP: error code 32 - No Such Object] - Invalid User in LDAP
			logger.error("!!!!! Exiting NWCGLdapAuthenticatorAPI::ldapTest (false)");
			return false;
		}
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticatorAPI::ldapTest (true)");
		return true;
	}

	/**
	 * For Testing Only :: Put in your own settings here. 
	 * @param argv
	 */
	public static void main(String[] argv) {
		NWCGLdapAuthenticatorAPI ldapAuth = new NWCGLdapAuthenticatorAPI();
		String url = "ldap://192.168.254.17:389";
		String keyPath = "C:/Documents and Settings/yfu/key.jks";
		String DN = "uid=admin,ou=Groups, dc=sterling,dc=com";
		ldapAuth.ldapTest("Y", "com.sun.jndi.ldap.LdapCtxFactory", DN, url, "admin", keyPath, "password");
	}
}