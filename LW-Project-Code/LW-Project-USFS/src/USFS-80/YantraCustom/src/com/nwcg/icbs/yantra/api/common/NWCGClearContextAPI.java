/*
 * ClearContextAPI.java
 *
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */ 

package com.nwcg.icbs.yantra.api.common;

import java.util.Properties;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API clears the environment of user objects stored
 * by upstream components.
 * @author ssankar
 */
public class NWCGClearContextAPI implements YIFCustomApi {
    
    /**
     * Instance to store the properties configured for the condition in Configurator.
     */
    private Properties props ;
    /**
     * Logger Instance.
     */
    private static Logger logger = Logger.getLogger();
    
    /** Creates a new instance of ClearContextAPI */
    public NWCGClearContextAPI() { }
    
    /**
     * Stores properties configured in configurator. This API does not expect
     * any property.
     * @param props Properties configured in Configurator.
     */
    public void setProperties(Properties props) {
        this.props = props;
        logger.verbose("NWCGClearContextAPI API Properties: "+this.props.toString());
    }

    /**
     * Clears the environment context of any object stored by upstream components.
     * This API should be used as the final component in a SDF to clear the
     * environment.
     * @return Returns the input document.
     * @param env Yantra Environment Context.
     * @param inDoc Input Document. Not used by this API.
     */    
    public Document clearContext(YFSEnvironment env, Document inDoc) {
        logger.verbose("Entering ClearContextAPI.clearContext()");
        CommonUtilities.clearContextObjects(env);
        logger.verbose("Exiting ClearContextAPI.clearContext()");
        return inDoc;
    }
    
}
