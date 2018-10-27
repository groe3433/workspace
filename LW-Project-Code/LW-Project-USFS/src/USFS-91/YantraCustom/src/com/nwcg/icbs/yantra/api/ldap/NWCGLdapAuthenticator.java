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

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogManager;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.util.YFSAuthenticator;

/**
 * This class provides a sample of how to implement LDAP V2 authentication using
 * JNDI.
 */
public class NWCGLdapAuthenticator implements YFSAuthenticator {

	private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGLdapAuthenticator.class);
	
	String ldapURL = null;
	String ldapOU = null;
	String ldapO = null;
	String ldapDN = null;
	String ExpTimeStr = "";
	String ExpStr = "";
	String MaxAgeStr = "";
	String WarningStr = "";

	/**
	 * NWCGLdapAuthenticator::authenticate
	 */
	public Map authenticate(String sLoginID, String sPassword) throws Exception {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator::authenticate");
		logger.verbose("@@@@@ sLoginID :: " + sLoginID);
		Hashtable lMap = new Hashtable();
		// Read ldap properties
		String ldapFactory = YFSSystem.getProperty("yfs.security.ldap.factory");
		logger.verbose("@@@@@ ldapFactory :: " + ldapFactory);
		ldapURL = YFSSystem.getProperty("yfs.security.ldap.url");
		logger.verbose("@@@@@ ldapURL :: " + ldapURL);
		ldapOU = YFSSystem.getProperty("yfs.security.ldap.ou");
		logger.verbose("@@@@@ ldapOU :: " + ldapOU);
		ldapO = YFSSystem.getProperty("yfs.security.ldap.o");
		logger.verbose("@@@@@ ldapO :: " + ldapO);
		// if any of the ldap params are not set, throw exception
		if (YFCObject.isVoid(ldapURL) || YFCObject.isVoid(ldapOU) || YFCObject.isVoid(ldapO) || YFCObject.isVoid(ldapFactory)) {
			logger.verbose("@@@@@ In IF ...");
			YFCException ex = new YFCException(YCPErrorCodes.YCP_INVALID_LDAP_AUTHENTICATOR_CONFIGURATION);
			ex.setAttribute("yfs.security.ldap.factory", ldapFactory);
			ex.setAttribute("yfs.security.ldap.url", ldapURL);
			ex.setAttribute("yfs.security.ldap.ou", ldapOU);
			ex.setAttribute("yfs.security.ldap.o", ldapO);
			throw ex;
		} else {
			logger.verbose("@@@@@ In ELSE ...");
			Hashtable env = new Hashtable();
			System.setProperty("java.protocol.handler.pkgs", "com.ibm.net.ssl.internal.www.protocol");
			java.security.Security.addProvider(new com.ibm.jsse.JSSEProvider());
			env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
			ldapDN = "uid=" + sLoginID + ", ou=" + ldapOU + "," + ldapO;
			logger.verbose("@@@@@ ldapDN :: " + ldapDN);
			// specify version. default may be V3. and some ldap servers
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_PROTOCOL, "ssl");
			env.put("java.naming.ldap.version", "3"); 
			env.put(Context.SECURITY_PRINCIPAL, ldapDN);
			env.put(Context.SECURITY_CREDENTIALS, sPassword);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			// TrustStore Parameters 
			String KeyStoreFile = YFSSystem.getProperty("nwcg.ldap.keystore.file");
			logger.verbose("@@@@@ KeyStoreFile :: " + KeyStoreFile);
			String KeyStorePasswd = YFSSystem.getProperty("nwcg.ldap.keystore.password");
			String trustStoreType = YFSSystem.getProperty("nwcg.ob.truststore.trustStoreType");
			logger.verbose("@@@@@ trustStoreType :: " + trustStoreType);
			String keyStoreType = YFSSystem.getProperty("nwcg.ob.keystore.keyStoreType");
			logger.verbose("@@@@@ keyStoreType :: " + keyStoreType);
			System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
			System.setProperty("javax.net.ssl.keyStore", KeyStoreFile);
			System.setProperty("javax.net.ssl.keyStorePassword", KeyStorePasswd);
			System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
			System.setProperty("javax.net.ssl.trustStore", KeyStoreFile);
			System.setProperty("javax.net.ssl.trustStorePassword", KeyStorePasswd);
	        System.setProperty("com.ibm.ssl.keyStore", KeyStoreFile);
	        System.setProperty("com.ibm.ssl.keyStorePassword", KeyStorePasswd);
	        System.setProperty("com.ibm.ssl.trustStoreType", trustStoreType);
	        System.setProperty("com.ibm.ssl.trustStore", KeyStoreFile);
	        System.setProperty("com.ibm.ssl.trustStorePassword", KeyStorePasswd);
			logger.verbose("@@@@@ LDAP Url :: " + ldapURL);
			logger.verbose("@@@@@ LDAP Security Principal :: " + ldapDN);
			// Create the initial context
			DirContext ctx = null;
			try {
				logger.verbose("@@@@@ InitialDirContext...");
				ctx = new InitialDirContext(env);
			} catch (Exception ex) {
				logger.error("!!!!! Caught General Exception :: " + ex);
				// Error Mesg [LDAP: error code 49 - Invalid Credentials] - Invalid LDAP Password
				// Error Mesg [LDAP: error code 32 - No Such Object] - Invalid User in LDAP
				// Error Mesg [LDAP: error code 19 - Exceed password retry limit. Please try later.]
				// Error Mesg [LDAP: error code 49 - password expired!]
				String ErrMsg = ex.getMessage();
				if ((ErrMsg.indexOf("error code 49") != -1) && (ErrMsg.indexOf("password expired") != -1)) {
					logger.error("@@@@@ ErrMsg :: " + ErrMsg);
					throw new Exception("Your password has expired. Please contact security administrator to reset your password.");
				} else if ((ErrMsg.indexOf("error code 19") != -1) && (ErrMsg.indexOf("Exceed password retry limit") != -1)) {
					logger.error("@@@@@ ErrMsg :: " + ErrMsg);
					throw new Exception("Your account has been locked. Please wait 15 minutes for your account to be reset.");
				} else {
					logger.error("@@@@@ ErrMsg :: " + ErrMsg);
					throw new Exception("Login failed");
				}
			}
			try {
				logger.verbose("@@@@@ In TRY 2...");
				String MY_FILTER = "uid=" + sLoginID;
				String MY_SEARCHBASE = "ou=" + ldapOU + "," + ldapO;
				SearchControls constraints = new SearchControls();
				String[] getAttrs = { "pwdGraceUseTime", "pwdExpirationWarned", "pwdChangedTime", "employeeNumber" };
				constraints.setReturningAttributes(getAttrs);
				NamingEnumeration results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);
				while (results.hasMore()) {
					SearchResult result = (SearchResult) results.next();
					Attributes attrs = result.getAttributes();
					NamingEnumeration<String> nameAttrs = attrs.getIDs();
					ExpTimeStr = "20161026224439Z";
				}
			} catch (NamingException e) {
				logger.error("!!!!! In CATCH 2...");
				logger.error("!!!!! Caught NamingException :: Search Failed :: ", e);
			}
			logger.verbose("@@@@@ Authenticated!!!");
			ctx.close();
			long days = getPasswordExpiryDays(ExpTimeStr);
			if (days > 0 && days < 8) {
				String EID = new Long(days).toString();
				lMap.put("ExpiresInDays", EID);
				logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator::authenticate 1");
				return lMap;
			} else {
				logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator::authenticate 2");
				return null;
			}
		}
	}

	/**
	 * 
	 * @param ModDate
	 * @return
	 */
	public long getPasswordExpiryDays(String ModDate) {
		logger.verbose("@@@@@ Entering NWCGLdapAuthenticator::getPasswordExpiryDays");
		SimpleDateFormat ldapDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		long daydiff = 0;
		Date today = new java.util.Date();
		long time1 = today.getTime();
		boolean UTCFlag = false;
		if (ModDate.endsWith("Z")) {
			UTCFlag = true;
		}
		if (UTCFlag) {
			ldapDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		} else {
			ldapDateFormat.setTimeZone(TimeZone.getDefault());
		}
		try {
			Date tdate = ldapDateFormat.parse(ModDate);
			long time2 = tdate.getTime();
			long diff = time2 - time1;
			daydiff = diff / (1000 * 60 * 60 * 24);
		} catch (Exception e) {
			logger.error("!!!!! Date Format Error",e);
		}
		logger.verbose("@@@@@ Exiting NWCGLdapAuthenticator::getPasswordExpiryDays");
		return daydiff;
	}
}