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

import com.nwcg.icbs.yantra.commCore.NWCGProperties;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;

public class NWCGTimeKeyManager {

	public static String createKey(String system_key, String userid,
			String systemName) {
		System.out.println("@@@@@ Entering NWCGTimeKeyManager::createKey @@@@@");

		String hashKey = NWCGProperties.getProperty("HASH_KEY");

		long hashCode = (new Long(hashKey.hashCode()).longValue());

		long hashedTimeStamp = (new Date()).getTime() * hashCode;

		String key = system_key + "-" + userid + "-"
				+ Long.toString(hashedTimeStamp);

		System.out.println("@@@@@ Exiting NWCGTimeKeyManager::createKey @@@@@");
		return key;
	}

	public static String extractSystemKey(String key) throws Exception {
		System.out.println("@@@@@ Entering NWCGTimeKeyManager::extractSystemKey @@@@@");
		
		StringTokenizer tok = new StringTokenizer(key, "-");
		String system_key = tok.nextToken();
		String userid = tok.nextToken();
		String hashedTimeStamp_string = tok.nextToken();

		System.out.println("@@@@@ Exiting NWCGTimeKeyManager::extractSystemKey @@@@@");
		return system_key;
	}

	public static boolean verifyKey(String key) throws Exception {
		System.out.println("@@@@@ Entering NWCGTimeKeyManager::verifyKey @@@@@");
		
		StringTokenizer tok = new StringTokenizer(key, "-");
		String systemID = tok.nextToken();
		NWCGLoggerUtil.Log.info("systemID:" + systemID);
		String userid = tok.nextToken();
		NWCGLoggerUtil.Log.info("userID:" + userid);
		String hashedTimeStamp_string = tok.nextToken();
		try {
			long hash = Long.parseLong(hashedTimeStamp_string);

			String hashKey = NWCGProperties.getProperty("HASH_KEY");
			if (hashKey == null) {
				hashKey = "10";
			}

			long hashCode = (new Long(hashKey.hashCode()).longValue());

			NWCGLoggerUtil.Log.info("HashCode:" + hashCode);

			long lExtractedTimeStamp = hash / hashCode;

			NWCGLoggerUtil.Log.info("lExtractedTimeStamp:"
					+ lExtractedTimeStamp);

			long currentTime = (new Date()).getTime();

			NWCGLoggerUtil.Log.info("CurrentTime:" + currentTime);

			long timeDiff = currentTime - lExtractedTimeStamp;

			NWCGLoggerUtil.Log.info("TimeDiff:" + timeDiff);
			String tkpt_str = NWCGProperties
					.getProperty("TIME_KEY_PERMISSIBLE_TIME");
			if (tkpt_str == null) {
				tkpt_str = "3600000";
			}

			long tkpt = Long.parseLong(tkpt_str);

			NWCGLoggerUtil.Log.info("tkpt:" + tkpt);
			
			System.out.println("@@@@@ Exiting NWCGTimeKeyManager::verifyKey @@@@@");
			return (timeDiff < tkpt);
		} catch (Exception e) {
			NWCGLoggerUtil.Log.warning("Got exception as " + e.toString());
			
			System.out.println("@@@@@ Exiting NWCGTimeKeyManager::verifyKey @@@@@");
			return false;
		}
	}
}