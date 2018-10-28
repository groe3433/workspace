package com.nwcg.icbs.yantra.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author gacharya
 */

public class NWCGCommandProcessor {


	private static NWCGCommandProcessor processor = new NWCGCommandProcessor();

	/**
	 * Logger Instance.
	 */
	private static Logger logger = Logger.getLogger(NWCGCommandProcessor.class
			.getName());

	private NWCGCommandProcessor() {
	}

	public static NWCGCommandProcessor getInstance() {
		return processor;
	}

	/*
	 * Extract the binding parameters and populate the map of the parameters
	 * 
	 * public void extractBindingParameters(HttpServletRequest request){
	 * Enumeration paramNames = request.getParameterNames();
	 * while(paramNames.hasMoreElements()){ String param =(String)
	 * paramNames.nextElement(); if(param.startsWith(BINDING_IDENTIFIER)){
	 * String value =request.getParameter(param);
	 * commandParamMap.put(param,value); } } }
	 */

	/*
	 * Binds the input variable to the command Input
	 */
	private void bindInput(Element root, HttpServletRequest request) {
		if(logger.isVerboseEnabled()) logger.verbose("Entering bindInput(" + root.getLocalName() + ")");
		NamedNodeMap attrMap = root.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) {
			Attr attr = (Attr) attrMap.item(i);
			// get the binding key from the template
			String key = attr.getValue();
			if (!StringUtil.isEmpty(key) && key.indexOf(NWCGCommandConstants.BINDING_IDENTIFIER) != -1) {
				// set the attribute from the http get/post request
				String requestValue = request.getParameter(key);
				if (null != requestValue && requestValue.trim().length() > 0)
					attr.setValue(requestValue);
				else
					attr.setValue("");
			}
		}
		NodeList childList = root.getChildNodes();
		for (int j = 0; j < childList.getLength(); j++) {
			Object temp = childList.item(j);
			Element child = null;
			if (temp instanceof Element) {
				child = (Element) childList.item(j);
				// recurse through the entire input DOM to set values
				bindInput(child, request);
			}
		}
		if(logger.isVerboseEnabled()) logger.verbose("Exiting bindInput(" + root.getLocalName() + ")");
	}

	public Document executeCommand(NWCGCommand command, HttpServletRequest request)
			throws Exception {
		Document commandOutput = XMLUtil.newDocument();
		try {
			//clone the input template document
			Document commandInput = XMLUtil.createDocument(command
					.getInputTemplate().getDocumentElement());
			// bind the input dom with the request parameters
			if(logger.isVerboseEnabled()) logger.verbose("command processor == > "+ XMLUtil.getXMLString(commandInput));
			bindInput(commandInput.getDocumentElement(), request);
			if(logger.isVerboseEnabled()) logger.verbose(" Post binding inputs  "+ XMLUtil.getXMLString(commandInput));
			YFSEnvironment env = getEnvironment(request);
			if(logger.isVerboseEnabled()) logger.verbose(" environment "+ env);
			// invoke yantra API
			if (command.getApiType().equals(NWCGCommandConstants.API_TYPE_API)) {
				
				// set the API template
				boolean bTemplateSet = false ;
				
				if(!StringUtil.isEmpty(command.getApiOutputTemplatePath()))
				{
					env.setApiTemplate(command.getApiName(),command.getApiOutputTemplatePath());
					bTemplateSet = true ;
				}
				else
				{
					// Jay : Make sure to add the template if and only if the template is set
					if(command.getApiOutputTemplate() != null)
					{
						env.setApiTemplate(command.getApiName(),command.getApiOutputTemplate());
						bTemplateSet = true ;
					}
				}
				if(logger.isVerboseEnabled()) logger.verbose("setting up the template " + command.getApiOutputTemplate() + " document "+ XMLUtil.getXMLString(command.getApiOutputTemplate()));
				// invoke the api
				if(logger.isVerboseEnabled()) logger.verbose("invoking api with " + command.getApiName() + " and input as  " + XMLUtil.getXMLString(commandInput));
				commandOutput = CommonUtilities.invokeAPI(env,command.getApiName(),commandInput);
				
				if(bTemplateSet) env.clearApiTemplate(command.getApiName());
				
				
			}
			// invoke custom API
			else if (command.getApiType().equals(NWCGCommandConstants.API_TYPE_CUSTOM_API)) 
			{
				Class obj = Class.forName(command.getApiName());
				Object customAjaxAPI = obj.newInstance();
				if(customAjaxAPI instanceof NWCGAjaxAPI)
				{
					NWCGAjaxAPI hndlr = (NWCGAjaxAPI) customAjaxAPI;
					NWCGLoggerUtil.Log.finer("NWCGOBResponseProcessor::processResponse, Invoking handler...");
					commandOutput = hndlr.processAjaxRequest(env, commandInput);
				}
				else
				{
					throw new YFSException(ResourceUtil.get("NWCG_AJAX_COMMAND_005"));
				}
				
			}
			else {
				// invoke the service
				commandOutput = CommonUtilities.invokeService(env,command.getApiName(),commandInput);
			}
		} catch (Exception e) {
			if(logger.isVerboseEnabled()) logger.verbose("Exception e " + e);
			e.printStackTrace();
			// TODO handle this and return the error XML
			if (e instanceof NWCGException || e instanceof YFSException) {
				YFSException ye = (YFSException) e;
				logger.error(ye.getMessage());
				commandOutput = XMLUtil.getDocument(ye.getMessage());
			} else {
				throw e;
			}

		}
		if(logger.isVerboseEnabled()){
			logger.verbose("Command Output"
				+ XMLUtil.getXMLString(commandOutput));
		}
		return commandOutput;

	}

	/**
	 * Check the environment and session and create a new environment
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private YFSEnvironment getEnvironment(HttpServletRequest request)
			throws Exception {
		HttpSession session = request.getSession(false);
		//System.out.println("NWCGCommandProcessor::getEnv :: session => "+session);
		if (session == null)
			throw new YFSException(ResourceUtil.get("NWCG_AJAX_COMMAND_004"));
		Object env = request.getAttribute(NWCGCommandConstants.YFS_ENVIRONMENT);
		//System.out.println("NWCGCommandProcessor::getEnv :: env => "+env);
		if (null == env) {
			env = CommonUtilities
					.createEnvironment(NWCGCommandConstants.YANTRA_AJAX_ENV_UID, NWCGCommandConstants.YANTRA_AJAX_ENV_UID);
		}
		return (YFSEnvironment) env;
	}

}
