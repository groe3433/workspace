package com.nwcg.icbs.yantra.commCore;

import java.util.*;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;


public class NWCGTimeKeyManager {


	public static String createKey(String system_key, String userid, String systemName){

		String hashKey = NWCGProperties.getProperty("HASH_KEY");
				
		long hashCode = (new Long(hashKey.hashCode()).longValue());
					
		long hashedTimeStamp = (new Date()).getTime() * hashCode;
		
		String key = system_key+ "-" + userid + "-" + Long.toString(hashedTimeStamp);
		
		return key;
	}
	
	
	
	public static String extractSystemKey(String key) throws Exception {
		
		StringTokenizer tok = new StringTokenizer(key,"-");
		
		String system_key = tok.nextToken();
		return system_key;
		
	}
	
	public static boolean verifyKey(String key) throws Exception {
		
		StringTokenizer tok = new StringTokenizer(key,"-");
		
		String systemID = tok.nextToken();
		NWCGLoggerUtil.Log.info("systemID:"+systemID);
		String userid = tok.nextToken();
		NWCGLoggerUtil.Log.info("userID:"+userid);
		String hashedTimeStamp_string = tok.nextToken();
		try{
			long hash = Long.parseLong(hashedTimeStamp_string);
			
			String hashKey = NWCGProperties.getProperty("HASH_KEY");
			if(hashKey==null){
				hashKey = "10";
			}
			
			long hashCode = (new Long(hashKey.hashCode()).longValue());
			
			NWCGLoggerUtil.Log.info("HashCode:" + hashCode);
			
			long lExtractedTimeStamp = hash/hashCode;
			
			NWCGLoggerUtil.Log.info("lExtractedTimeStamp:" + lExtractedTimeStamp);
			
			long currentTime = (new Date()).getTime();
			
			NWCGLoggerUtil.Log.info("CurrentTime:" + currentTime);
			
			long timeDiff = currentTime - lExtractedTimeStamp;
			
			NWCGLoggerUtil.Log.info("TimeDiff:" + timeDiff);
			String tkpt_str = NWCGProperties.getProperty("TIME_KEY_PERMISSIBLE_TIME");
			if(tkpt_str == null){
				tkpt_str = "3600000";
			}
			
			long tkpt = Long.parseLong(tkpt_str);
			
			NWCGLoggerUtil.Log.info("tkpt:" + tkpt);
			return (timeDiff < tkpt);
		}catch(Exception e){
			NWCGLoggerUtil.Log.warning("Got exception as " + e.toString());
			return false;
		}
		//return (timeDiff < tkpt);
	}	
}
