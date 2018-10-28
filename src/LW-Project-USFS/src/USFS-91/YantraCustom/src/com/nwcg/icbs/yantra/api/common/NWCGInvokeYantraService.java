/*
 * 
 This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */


package com.nwcg.icbs.yantra.api.common;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

import java.util.Properties;

import org.w3c.dom.Document;

/**
 * This API will store the input document in the environment context under a
 * certain key. The key should be configured in the Configurator as an Argument
 * for the API node.
 * @author ssankar
 */
public class NWCGInvokeYantraService implements YIFCustomApi{
	/**
     * Instance to store the properties configured for the condition in Configurator.
     */
    private Properties props;
    
    private static YFCLogCategory logger = NWCGApplicationLogger.instance(NWCGInvokeYantraService.class);
    
    /**
     * Creates a new instance of InvokeYantraService
     */
    public NWCGInvokeYantraService() {
    }
    
    /**
     * Stores properties configured in configurator. The property expected by this API
     * is: bls.helpdesk.setdoc.env.key
     * @param props Properties configured in Configurator.
     */
    public void setProperties(Properties props) {
        this.props = props;
    }
    
    /**
     * Invokes a Yantra Service. Ignores any exceptions that might be thrown
     * by the service, resulting in the transaction getting committed.
     * The service to be invoked should be configured as an API argument with
     * the key bls.yantra.service.name.
     * @param env Yantra Environment Context.
     * @param inDoc Input Document to be passed to the Service.
     * @return Input document is returned as the output.
     */
    public Document invokeService(YFSEnvironment env, Document inDoc) {
    	logger.verbose("Entering InvokeYantraService.invokeService()");
        
        String serviceName = props.getProperty(NWCGConstants.KEY_YANTRA_SERVICE_NAME);
        
        if (logger.isVerboseEnabled())
        	logger.verbose("Invoking service [ " + serviceName + "] Document [" + XMLUtil.getXMLString(inDoc) + "]");
        
        try {
        	CommonUtilities.invokeService(env, serviceName, inDoc);
        } catch (Exception e) {
            Object[] args = new Object[] {serviceName};
            logger.error("!!!!! Caught General Exception, IOM_SERV_INVOK_0001" + e);
        }
        
        
        logger.verbose("Exiting InvokeYantraService.invokeService()");
        return inDoc;
    }


    /**
     * Invokes a Yantra API. Ignores any exceptions that might be thrown
     * by the service, resulting in the transaction getting committed.
     * The service to be invoked should be configured as an API argument with
     * the key bls.yantra.api.name.
     * @param env Yantra Environment Context.
     * @param inDoc Input Document to be passed to the Service.
     * @return Input document is returned as the output.
     */
    public Document invokeAPI(YFSEnvironment env, Document inDoc) {
    	logger.verbose("Entering InvokeYantraService.invokeAPI()");
        
        String apiName = props.getProperty(NWCGConstants.KEY_YANTRA_API_NAME);
        
        if (logger.isVerboseEnabled())
        	logger.verbose("Invoking service [ " + apiName + "] Document [" + XMLUtil.getXMLString(inDoc) + "]");
        
        try {
        	CommonUtilities.invokeAPI(env, apiName, inDoc);
        } catch (Exception e) {
            Object[] args = new Object[] {apiName};
            logger.error("!!!!! Caught General Exception, IOM_SERV_INVOK_0002" + e);
        }
        
        
        logger.verbose("Exiting InvokeYantraService.invokeAPI()");
        return inDoc;
    }
}
