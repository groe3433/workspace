 /* This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */

package com.nwcg.icbs.yantra.condition.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This condition will inspect all the condition properties configured in configurator
 * for the condition and validates the keys = value condition from the Resources
 * Multiple properties are evaluated using "and" condition
 * @author gacharya
 */
public class CheckResourceProperty implements YCPDynamicConditionEx {
    
    /**
     * Instance to store the properties configured for the condition in Configurator.
     */
    private Map map = null;
    /**
     * Logger Instance.
     */
    private static Logger logger = Logger.getLogger(CheckResourceProperty.class.getName());
    
    /**
     * Returns true all the key=value conditions evaluates to true as checked from Resources file.
     * @param env Yantra Environment Context.
     * @param name Name of the condition as configured in Configurator.
     * @param mapData Properties configured in Configurator.
     * @param doc Input Document.
     * @return true if all the key=value conditions evaluates to true as checked from Resources file.
     */
    public boolean evaluateCondition(YFSEnvironment env, String name, Map mapData, Document doc) {
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
     * @param map Properties configured in Configurator.
     */
    public void setProperties(Map map) {
        this.map = map;
        CommonUtilities.logProperties(map, logger);
    }
    
}
