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

package com.nwcg.icbs.yantra.commCore;

import com.nwcg.icbs.yantra.ue.NWCGGetCustomerIDUE;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * @author sdas
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGJAXRPCWSHandlerUtils {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGJAXRPCWSHandlerUtils.class);

	public static String getStringToken(String userName, String loginID, String pw_key, String message_name) {
		logger.verbose("@@@@@ Entering NWCGJAXRPCWSHandlerUtils::getStringToken");
		if (pw_key == "" || pw_key == null)
			pw_key = " ";
		if (loginID == "" || loginID == null)
			loginID = " ";
		if (userName == "" || userName == null)
			userName = " ";
		if (message_name == "" || message_name == null)
			message_name = " ";
		String sysID = NWCGAAConstants.SYSTEM_ID;
		if (sysID == "" || sysID == null)
			sysID = " ";
		String string_token = "#".concat(pw_key).concat("#").concat(loginID).concat("#").concat(userName).concat("#").concat(message_name).concat("#").concat(sysID).concat("#");
		logger.verbose("@@@@@ Exiting NWCGJAXRPCWSHandlerUtils::getStringToken");
		return string_token;
	}
}