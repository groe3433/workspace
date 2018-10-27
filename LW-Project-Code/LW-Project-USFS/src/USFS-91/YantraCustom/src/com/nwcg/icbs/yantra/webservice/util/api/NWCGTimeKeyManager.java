/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.webservice.util.api;

import java.util.*;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;

public class NWCGTimeKeyManager {
	
	private static YFCLogCategory logger = NWCGROSSLogger
			.instance(NWCGTimeKeyManager.class);

	public static String createKey(String system_key, String userid,
			String systemName) {
		logger.verbose("@@@@@ Entering NWCGTimeKeyManager::createKey");
		String hashKey = NWCGProperties.getProperty("HASH_KEY");
		long hashCode = (new Long(hashKey.hashCode()).longValue());
		long hashedTimeStamp = (new Date()).getTime() * hashCode;
		String key = system_key + "-" + userid + "-" + Long.toString(hashedTimeStamp);
		logger.verbose("@@@@@ key :: " + key);
		logger.verbose("@@@@@ Exiting NWCGTimeKeyManager::createKey");
		return key;
	}

	public static String extractSystemKey(String key) throws Exception {
		logger.verbose("@@@@@ Entering NWCGTimeKeyManager::extractSystemKey");
		StringTokenizer tok = new StringTokenizer(key, "-");
		String system_key = tok.nextToken();
		String userid = tok.nextToken();
		String hashedTimeStamp_string = tok.nextToken();
		logger.verbose("@@@@@ system_key :: " + system_key);
		logger.verbose("@@@@@ Exiting NWCGTimeKeyManager::extractSystemKey");
		return system_key;
	}

	public static boolean verifyKey(String key) throws Exception {
		logger.verbose("@@@@@ Entering NWCGTimeKeyManager::verifyKey");
		StringTokenizer tok = new StringTokenizer(key, "-");
		String systemID = tok.nextToken();
		logger.verbose("systemID:" + systemID);
		String userid = tok.nextToken();
		logger.verbose("userID:" + userid);
		String hashedTimeStamp_string = tok.nextToken();
		try {
			long hash = Long.parseLong(hashedTimeStamp_string);

			String hashKey = NWCGProperties.getProperty("HASH_KEY");
			if (hashKey == null) {
				hashKey = "10";
			}

			long hashCode = (new Long(hashKey.hashCode()).longValue());

			logger.verbose("HashCode:" + hashCode);

			long lExtractedTimeStamp = hash / hashCode;

			logger.verbose("lExtractedTimeStamp:"
					+ lExtractedTimeStamp);

			long currentTime = (new Date()).getTime();

			logger.verbose("CurrentTime:" + currentTime);

			long timeDiff = currentTime - lExtractedTimeStamp;

			logger.verbose("TimeDiff:" + timeDiff);
			String tkpt_str = NWCGProperties
					.getProperty("TIME_KEY_PERMISSIBLE_TIME");
			if (tkpt_str == null) {
				tkpt_str = "3600000";
			}

			long tkpt = Long.parseLong(tkpt_str);

			logger.verbose("@@@@@ (timeDiff < tkpt) :: " + (timeDiff < tkpt));
			logger.verbose("@@@@@ Exiting NWCGTimeKeyManager::extractSystemKey (true)");
			return (timeDiff < tkpt);
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.toString());
			logger.error("!!!!! Exiting NWCGTimeKeyManager::extractSystemKey (false)");
			return false;
		}
	}
}