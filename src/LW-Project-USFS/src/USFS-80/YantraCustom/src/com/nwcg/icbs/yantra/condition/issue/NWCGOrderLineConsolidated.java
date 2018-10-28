/* This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if a OL.ConditionVariable2 == 'XLD-Consolidated'
 * (NWCGConstants.CONDVAR2_XLD_REASON_CONS)
 * 
 * @author drodriguez
 */
public class NWCGOrderLineConsolidated implements YCPDynamicConditionEx {

	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGOrderLineConsolidated.class.getName());

	/**
	 * Returns true if OL.ConditionVariable2 == 'XLD-Consolidated'
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the condition as configured in Configurator.
	 * @param mapData
	 *            Properties configured in Configurator. Map contains input XML
	 * @param doc
	 *            Input Document.
	 * @return true if OL.ConditionVariable2 == 'XLD-Consolidated'
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {

		Object cv2 = null;
		String condVar2 = null;
		boolean result = false;

		logger.debug("Entering NWCGOrderLineConsolidated.evaluateCondition()");

		// Get ConditionVariable2 from map entries
		if (mapData != null && mapData.size() > 0) {
			cv2 = mapData.get(NWCGConstants.CONDITION_VAR2);
		}
		
		if (cv2 != null) {
			condVar2 = cv2.toString();
			if (condVar2.equals(NWCGConstants.CONDVAR2_XLD_REASON_CONS)){
				logger.debug("ConditionVariable2 =["+condVar2+"]");
				result = true;
			}			
		}		
		logger.verbose("Result of NWCGOrderLineConsolidated: " + result);
		logger.verbose("Exiting NWCGOrderLineConsolidated.evaluateCondition()");
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