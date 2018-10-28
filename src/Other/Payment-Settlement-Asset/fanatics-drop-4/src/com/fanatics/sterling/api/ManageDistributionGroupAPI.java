package com.fanatics.sterling.api;

/**
 * This class contains the Vendor Location Feed functionality required for the fanatics Etaildirect Implementation.
 * 
 *  This code is called when the Vendor Location Feed is received by the Fanatics OMS system, to perform the below mentioned functionalities:
 *  1. Remove Vendor from the Distribution Group before the input Vendor Location is deleted from the OMS.
 *  2. Add Vendor to the  Distribution Group before updating the Vendor information, as per the input xml.
 *  
 *  Note: Method "processNodeForMonitoringDG" performs the above mentioned functions
 *  
 * @(#) ManageDistributionGroupAPI.java    
 * Created on   April 13, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       ManageDistributionGroupAPI.java
 * Package Name:    com.fanatics.sterling.api;
 * Project name:    Fanatics
 * Type Declaration:    
 * Class Name:      ManageDistributionGroupAPI
 * 
 * @author Neeraj Sharda
 * @version 1.0
 * @history none
 *     
 * 
 *  
 *
 * (C) Copyright 2006-2007 by owner.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of the owner. ("Confidential Information").
 * Redistribution of the source code or binary form is not permitted
 * without prior authorization from the owner.
 *
 */

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANConstants;
import com.fanatics.sterling.constants.VendorFeedConstants;
import com.fanatics.sterling.util.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class ManageDistributionGroupAPI implements YIFCustomApi {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props = null;
	private Document docOutXML = null;

	public Document processNodeForMonitoringDG(YFSEnvironment env, Document inXML) throws Exception{

		logger.verbose("Inside processNodeForMonitoringDG");
		logger.verbose("Input xml is: "+ XMLUtil.getXMLString(inXML));
		
		// get the value of RTAM DG and the Calling Organization Code from the Arguments of the service
		final String strRTAMDGCode = getPropertyVal(VendorFeedConstants.ARG_fanatics_RTAM_DISTRIBUTION_GROUP);
		final String strCallingOrgCode = getPropertyVal(FANConstants.ARG_fanatics_CALLING_ORG_CODE);

		logger.verbose("Property value of RTAM DG Code is "+strRTAMDGCode);
		logger.verbose("Property value of Calling Organization Code is "+strCallingOrgCode);

		// set both the properties in the inXML as DefaultDistributionRuleId and CallingOrganizationCode
		Element rootInXML = inXML.getDocumentElement();
		rootInXML.setAttribute(FANConstants.ATT_fanatics_DEFAULT_DISTRIBUTION_RULE_ID, strRTAMDGCode);
		rootInXML.setAttribute(FANConstants.ARG_fanatics_CALLING_ORG_CODE, strCallingOrgCode);
		logger.verbose("Input xml after setting the attributes is: "+ XMLUtil.getXMLString(inXML));

		// find out the required action
		String strOperation = rootInXML.getAttribute(FANConstants.ATT_fanatics_OPERATION);
		logger.verbose("Operation is: "+ strOperation);
		/**
		 *  invoke the service getDistributionList
		 */
		docOutXML = CommonUtil.invokeService(env, VendorFeedConstants.SERVICE_fanatics_GET_DISTRIBUTION_LIST, inXML);
		logger.verbose("Output xml of getDistributionList: "+ XMLUtil.getXMLString(docOutXML));


		// check if the vendor is a part of DG already
		Element rootdocOPgetDistributionList = docOutXML.getDocumentElement();
		logger.verbose("First condition is: " + rootdocOPgetDistributionList.hasChildNodes());
		logger.verbose("Second condition is: "+strOperation.equalsIgnoreCase(VendorFeedConstants.CONSTANT_fanatics_DELETE));

		if (rootdocOPgetDistributionList.hasChildNodes() && strOperation.equalsIgnoreCase(VendorFeedConstants.CONSTANT_fanatics_DELETE)){
			// if the vendor is a part of DG 
			/**
			 *  invoke deleteDistribution
			 */
			logger.verbose("Invoking Delete Distribution");
			docOutXML = CommonUtil.invokeService(env, VendorFeedConstants.SERVICE_fanatics_DELETE_DISTRIBUTION, inXML);	
		}

		// else if the vendor is not present in the DG and the operation is not Delete: Add the vendor to the DG

		else if ( !rootdocOPgetDistributionList.hasChildNodes() && !strOperation.equalsIgnoreCase(VendorFeedConstants.CONSTANT_fanatics_DELETE)){
			/**
			 *  invoke createDistributionList
			 */
			logger.verbose("Invoking Create Distribution");
			docOutXML = CommonUtil.invokeService(env, VendorFeedConstants.SERVICE_fanatics_CREATE_DISTRIBUTION, inXML);
		}
		//Commenting below line as create/deleteDistribution APIs will not have any output XML and this will lead to NPE.
		//logger.info("Output xml is: "+ XMLUtil.getXMLString(docOutXML));
		return docOutXML;
	}


	protected String getPropertyVal(String name) {

		return props.getProperty(name);
	}

	public void setProperties(Properties arg0) throws Exception {

		props = arg0;

	}

}
