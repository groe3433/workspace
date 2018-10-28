package com.fanatics.sterling.api;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This class contains the Order release process functionality required for the fanatics OMS Implementation.
 * 
 *   Method "processRelease" performs the functions: Spits the order lines into lists containing ship alone and 
 *   non ship alone items. For each of the lists it extracts the Seq number and invokes the FanaticsPublishReleaseMsgToWMS. 
 *  
 * @(#) FanaticsProcessReleaseMsgService.java    
 * Created on   April 15, 2016
 *              
 *
 * Package Declaration: 
 * File Name:       FanaticsProcessReleaseMsgService.java
 * Package Name:    com.fanatics.sterling.api;
 * Project name:    Fanatics
 * Type Declaration:    
 * Class Name:      FanaticsProcessReleaseMsgService
 * 
 * @author KNtagkas
 * @version 1.0
 * @history none
 *     
 * 
 * (C) Copyright 2016-2017 by owner.
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fanatics.sterling.constants.OrderReleaseConstants;
import com.fanatics.sterling.util.CommonUtil;
import com.fanatics.sterling.util.FANDBUtil;
import com.fanatics.sterling.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.dblayer.YFCContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class FanaticsProcessReleaseMsgService implements YIFCustomApi {

	private static YFCLogCategory logger = YFCLogCategory.instance(YFCLogCategory.class);
	private Properties props = null;

	public void processRelease(YFSEnvironment env, Document inDoc){

		logger.verbose("FanaticsProcessReleaseMsgService -> Begin");
		
		String orderNo     		 = SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
				OrderReleaseConstants.XPATH_ORDER_NO);

		try{
			Document inDocCloned = SCXmlUtil.createDocument();
			Node node 			 = inDocCloned.importNode(inDoc.getDocumentElement(), true);

			inDocCloned.appendChild(node);

			/*
			 * For each of the node lists the FanaticsPublishReleaseMsgToWMS service
			 * will be invoked. 
			 */
		
			NodeList shipTogetherList    = SCXmlUtil.getXpathNodes(inDoc.getDocumentElement(),
					OrderReleaseConstants.XPATH_SHIP_ALONE_N);
			NodeList shipAloneList 	     = SCXmlUtil.getXpathNodes(inDoc.getDocumentElement(),
					OrderReleaseConstants.XPATH_SHIP_ALONE_Y);
			String orderReleaseKey       = SCXmlUtil.getXpathAttribute(inDoc.getDocumentElement(),
					OrderReleaseConstants.XPATH_ORDER_RELEASE_KEY);
			
			logger.verbose("FanaticsProcessReleaseMsgService -> Found " + shipAloneList.getLength()
					+ " ship alone lines and " + shipTogetherList.getLength() + " ship together lines");
			
			Element OrderReleaseLevelControlNbr  = SCXmlUtil.createChild(inDocCloned.getDocumentElement(), 
					OrderReleaseConstants.ELE_CUSTOM_ATTR);
			
			if (shipAloneList != null && shipAloneList.getLength() > 0) {
				for (int i = 0; i < shipAloneList.getLength(); i++) {
					if (shipAloneList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						logger.verbose("FanaticsProcessReleaseMsgService -> Creating release for ship alone items");

						Element orderLine = (Element) shipAloneList.item(i);
						String OrderedQty = SCXmlUtil.getXpathAttribute(orderLine, OrderReleaseConstants.XPATH_STATUS_QTY);

						// Removing Order lines each time before creating a release
						removeOrderLines(inDocCloned);
						
						if(null != orderReleaseKey){
							
							Element eleStatus = SCXmlUtil.getXpathElement(orderLine, OrderReleaseConstants.XPATH_ELE_STATUS + orderReleaseKey + OrderReleaseConstants.XPATH_ELE_STATUS_END);
							
							if(!(eleStatus.hasAttributes())||!(eleStatus.hasAttribute(OrderReleaseConstants.ATT_ORDER_RELEASE_KEY))){
								
								/*
								 * in order to setStatusQty, /OrderRelease/OrderLine/OrderStatuses/OrderStatus /@OrderReleaseKey has to be equal to /OrderRelease/@OrderReleaseKey
								 */
								logger.error("FanaticsProcessReleaseMsgService --> ERROR: " + "Order line level Release key not a matching with Order level release key "+ orderReleaseKey);
								throw new YFSException("FanaticsProcessReleaseMsgService -> orderReleaseKey not verified !.");
								
							}
							
							eleStatus.setAttribute(OrderReleaseConstants.ATT_STATUS_QTY, OrderReleaseConstants.ONE);
							
						}else{
							logger.error("FanaticsProcessReleaseMsgService --> ERROR: " + "orderReleaseKey attribute should not be " + orderReleaseKey);
							throw new YFSException("FanaticsProcessReleaseMsgService -> orderReleaseKey null !.");
						}
						
						createReleasePerQuantity(inDocCloned, orderLine, OrderedQty, env);

					}
				}
			} else {
				logger.verbose(
						"FanaticsProcessReleaseMsgService -> Ship Alone list empty .. All order lines are Ship Together");
			}

			if (shipTogetherList != null && shipTogetherList.getLength() > 0) {

				removeOrderLines(inDocCloned);
				
				for (int i = 0; i < shipTogetherList.getLength(); i++) {
					if (shipTogetherList.item(i).getNodeType() == Node.ELEMENT_NODE) {

						Element orderLine = (Element) shipTogetherList.item(i);
						Node tempNode     = inDocCloned.importNode(orderLine, true);

						inDocCloned.getDocumentElement().appendChild(tempNode);

					}
				}

				logger.verbose("FanaticsProcessReleaseMsgService -> Creating release for ship together items");

				String releaseControlNbr 			 = FANDBUtil.getNexSeqNo(env, FanaticsProcessReleaseMsgService.class.getName(), OrderReleaseConstants.SEQ_FANATICS_ORDER_NO);

				//Element OrderReleaseLevelControlNbr  = SCXmlUtil.createChild(inDocCloned.getDocumentElement(), OrderReleaseConstants.ELE_CUSTOM_ATTR);
				OrderReleaseLevelControlNbr.setAttribute(OrderReleaseConstants.ATT_RELEASE_CTRL_NBR, releaseControlNbr);
				
				createRelease(inDocCloned, env);

			} else {
				logger.verbose(
						"FanaticsProcessReleaseMsgService -> Ship together list empty .. All order lines are Ship Alone");
			}
			
			logger.verbose("FanaticsProcessReleaseMsgService -> end"); 
			
		}catch (Exception e) {
			logger.error("FanaticsProcessReleaseMsgService --> ERROR - OrderNo: " + orderNo + " Message: " + e.getMessage(), e);
			throw new YFSException("FanaticsProcessReleaseMsgService");
		}	
	}

	private void createReleasePerQuantity(Document inDocCloned, Element orderLine, String OrderedQty,
			YFSEnvironment env) throws Exception {

		String itemID = SCXmlUtil.getXpathAttribute(orderLine, OrderReleaseConstants.XPATH_ITEM_ID);

		logger.verbose("FanaticsProcessReleaseMsgService -> Creating release per quantity for ItemId: " + itemID);
		
		Node node    = inDocCloned.importNode(orderLine, true);
		inDocCloned.getDocumentElement().appendChild(node);

		int quantity = (int) Double.parseDouble(OrderedQty);

		for (int i = 0; i < quantity; i++) {

			//String releaseControlNbr = getNexSeqNo(env);
			String releaseControlNbr 			 = FANDBUtil.getNexSeqNo(env, FanaticsProcessReleaseMsgService.class.getName(), OrderReleaseConstants.SEQ_FANATICS_ORDER_NO);
			Element OrderReleaseLevelControlNbr	 = SCXmlUtil.getXpathElement(inDocCloned.getDocumentElement(), OrderReleaseConstants.XPATH_CUSTOM_ATTR);
			OrderReleaseLevelControlNbr.setAttribute(OrderReleaseConstants.ATT_RELEASE_CTRL_NBR, releaseControlNbr);
			
			createRelease(inDocCloned, env);
		}

	}

	@SuppressWarnings("unused")
	public void createRelease(Document inDocCloned, YFSEnvironment env) throws Exception {

		logger.info("FanaticsProcessReleaseMsgService -> Invoking " + OrderReleaseConstants.SERVICE_RELEASE_MSG_WMS
				+ "service");

		logger.verbose("inputXML -> " + XMLUtil.getXMLString(inDocCloned));
		
		Document outDoc = CommonUtil.invokeService(env, OrderReleaseConstants.SERVICE_RELEASE_MSG_WMS, inDocCloned);
		
		logger.verbose(OrderReleaseConstants.SERVICE_RELEASE_MSG_WMS + " sercvice Invoked, release created !. ");

	}

	private void removeOrderLines(Document inDocCloned) {

		Element rootMessageNode = inDocCloned.getDocumentElement();

		if (rootMessageNode != null) {

			NodeList orderLines = rootMessageNode.getElementsByTagName(OrderReleaseConstants.EL_ORDER_LINE);

			if (orderLines != null && orderLines.getLength() > 0) {
				while (orderLines.getLength() > 0) {

					Element orderLine = (Element) orderLines.item(0);
					orderLine.getParentNode().removeChild(orderLine);

				}

			}
		}

	}

	@SuppressWarnings({ "unused" })
	public String getNexSeqNo(YFSEnvironment env) throws YFSUserExitException {

		logger.verbose("getNexSeqNo for FanaticsProcessReleaseMsgService -> Begin");
		
		String seqNoStr = OrderReleaseConstants.NO_VALUE;
		
		try {

			YFCContext ctxt = (YFCContext) env;
			Statement stmt  = ctxt.getConnection().createStatement();
			ResultSet rs    = stmt.executeQuery(OrderReleaseConstants.SEQ_EXISTS_SQL);

			while (rs.next()) {
				if (rs.getInt(1) < 1) {

					logger.verbose("DB Sequence does not exist - Creating Default Order No Sequence ...");

					Statement createOrderSeqStmt = ctxt.getConnection().createStatement();
					int createSeq 				 = createOrderSeqStmt.executeUpdate(OrderReleaseConstants.SEQ_CREATE_SQL);

				} else {
					logger.verbose("DB Sequence found - extracting Sequence number ...");
				}
			}

			long seqNo = ((YCPContext) env).getNextDBSeqNo(OrderReleaseConstants.SEQ_FANATICS_ORDER_NO);
			seqNoStr   = Long.toString(seqNo);

		} catch (Exception e) {
			logger.error("FanaticsProcessReleaseMsgService --> ERROR: " + e.getMessage(), e);
			throw new YFSUserExitException("Error_SEQ_FANATICS_ORDER_NO");
		}

		logger.verbose("Returning Sequence no:" + seqNoStr);

		return seqNoStr;

	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {

		props = arg0;
	}
	
	public Properties getProperties() throws Exception {

		return props;
	}

}
