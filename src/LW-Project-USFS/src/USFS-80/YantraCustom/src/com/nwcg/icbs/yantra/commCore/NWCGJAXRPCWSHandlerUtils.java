/*
 * Created on Feb 14, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.nwcg.icbs.yantra.commCore;

import com.nwcg.icbs.yantra.ue.NWCGGetCustomerIDUE;
import com.nwcg.icbs.yantra.util.common.Logger;

/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGJAXRPCWSHandlerUtils {
		
	/**
	 * @param userName
	 * @param loginID
	 * @param pw_key
	 * @param message_name
	 * @return
	 */
	

	private static Logger log = Logger.getLogger(NWCGGetCustomerIDUE.class.getName());
	
	public static String getStringToken(String userName, String loginID, String pw_key, String message_name) {
		
		if(pw_key=="" || pw_key==null)pw_key=" ";
    	if(loginID=="" || loginID==null)loginID=" ";
    	if(userName=="" || userName==null)userName=" ";
    	if(message_name=="" || message_name==null)message_name=" ";
    	String sysID = NWCGAAConstants.SYSTEM_ID;
    	if(sysID=="" || sysID==null)sysID=" ";
    	
    	String string_token = "#".concat(pw_key).concat("#").concat(loginID).concat("#").concat(userName).concat("#").concat(message_name).concat("#").concat(sysID).concat("#");
		return string_token;
	}
}
