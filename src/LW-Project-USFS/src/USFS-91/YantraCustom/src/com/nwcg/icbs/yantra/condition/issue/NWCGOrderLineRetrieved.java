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

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import org.w3c.dom.Document;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if a OL.ConditionVariable2 == 'XLD-Retrieval'
 * 
 * @author drodriguez
 */
public class NWCGOrderLineRetrieved implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGOrderLineRetrieved.class);
	
	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

	/**
	 * Returns true if OL.ConditionVariable2 == 'XLD-Retrieval'
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the condition as configured in Configurator.
	 * @param mapData
	 *            Properties configured in Configurator. Map contains input XML
	 * @param doc
	 *            Input Document.
	 * @return true if OL.ConditionVariable2 == 'XLD-Retrieval'
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {

		Object cv2 = null;
		String condVar2 = null;
		boolean result = false;

		logger.debug("Entering NWCGOrderLineRetrieved.evaluateCondition()");

		// Get ConditionVariable2 from map entries
		if (mapData != null && mapData.size() > 0) {
			cv2 = mapData.get(NWCGConstants.CONDITION_VAR2);
		}
		
		if (cv2 != null) {
			condVar2 = cv2.toString();
			if (condVar2.equals(NWCGConstants.CONDVAR2_XLD_REASON_RETRV)){
				logger.debug("ConditionVariable2 =["+condVar2+"]");
				result = true;
			}			
		}		
		logger.debug("Result of NWCGOrderLineRetrieved: " + result);
		logger.debug("Exiting NWCGOrderLineRetrieved.evaluateCondition()");
		return result;
	}

	/**
	 * Stores properties configured in configurator.
	 * 
	 * @param map
	 *            Properties configured in Configurator.
	 */
	public void setProperties(Map map) {
		this.map = map;
		CommonUtilities.logProperties(map, logger);
	}
}