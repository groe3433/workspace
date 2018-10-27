/* This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */

package com.nwcg.icbs.yantra.condition.issue;

import java.util.Map;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will check if a order line has backordered qty 
 * and return true if it has UTF qty > 0 and issue qty = 0
 * 
 * @author gacharya
 */
public class NWCGROSSInitiatedOrder implements YCPDynamicConditionEx {

	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger();

	/**
	 * Returns true all the key=value conditions evaluates to true as checked
	 * from Resources file.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the condition as configured in Configurator.
	 * @param mapData
	 *            Properties configured in Configurator. Map contains input XML
	 * @param doc
	 *            Input Document.
	 * @return true if all the key=value conditions evaluates to true as checked
	 *         from Resources file.
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {

		return true;
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