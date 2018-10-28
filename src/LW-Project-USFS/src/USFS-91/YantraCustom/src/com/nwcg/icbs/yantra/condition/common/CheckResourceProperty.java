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

package com.nwcg.icbs.yantra.condition.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.yantra.yfc.log.YFCLogCategory;
import org.w3c.dom.Document;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will inspect all the condition properties configured in
 * configurator for the condition and validates the keys = value condition from
 * the Resources Multiple properties are evaluated using "and" condition
 * 
 * @author gacharya
 */
public class CheckResourceProperty implements YCPDynamicConditionEx {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(CheckResourceProperty.class);
	
	/**
	 * Instance to store the properties configured for the condition in
	 * Configurator.
	 */
	private Map map = null;

	/**
	 * Returns true all the key=value conditions evaluates to true as checked
	 * from Resources file.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param name
	 *            Name of the condition as configured in Configurator.
	 * @param mapData
	 *            Properties configured in Configurator.
	 * @param doc
	 *            Input Document.
	 * @return true if all the key=value conditions evaluates to true as checked
	 *         from Resources file.
	 */
	public boolean evaluateCondition(YFSEnvironment env, String name,
			Map mapData, Document doc) {
		logger.verbose("Entering USFSCheckResourceProperty.evaluateCondition()");
		boolean result = true;
		Set entrySet = map.entrySet();
		Iterator eit = entrySet.iterator();
		while (eit.hasNext()) {
			Map.Entry e = (Map.Entry) eit.next();
			String propKey = (String) e.getKey();
			String refVal = (String) e.getValue();
			String cfgVal = ResourceUtil.get(propKey);
			result &= refVal.equals(cfgVal);
			if (!result)
				break;
		}

		if (logger.isVerboseEnabled())
			logger.verbose("Result of USFSCheckResourceProperty: " + result);
		logger.verbose("Exiting USFSCheckResourceProperty.evaluateCondition()");
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