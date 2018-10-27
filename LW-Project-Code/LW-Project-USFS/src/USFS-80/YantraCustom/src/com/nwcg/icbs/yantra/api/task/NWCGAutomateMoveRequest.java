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

package com.nwcg.icbs.yantra.api.task;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.billingtransaction.NWCGGetFBMSCodes;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This API will have the primary responsibility for completing 
 * Move Request tasks that get created and "released" for NRFI
 * Items involved in a Return process. This API is meant to
 * automate the previous manual task of "completing" such Move Requests. 
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date March 21, 2013
 */
public class NWCGAutomateMoveRequest implements YIFCustomApi {
	
	private static Logger logger = Logger.getLogger(NWCGAutomateMoveRequest.class.getName());

	private Properties props;

	public void setProperties(Properties arg0) throws Exception {
		this.props = arg0;
	}
	
	/**
	 * This method will handle all exceptions and log an Alert for them as they occur. 
	 * 
	 * @param env
	 * @param Message
	 * @throws Exception
	 */
	public void throwAlert(YFSEnvironment env, StringBuffer Message) throws Exception {
		System.out.println("@@@@@ Entering NWCGAutomateMoveRequest::throwAlert @@@@@");
		Message.append(" ExceptionType='" + NWCGConstants.NWCG_RETURN_ALERTTYPE
				+ "'" + " InboxType='" + NWCGConstants.NWCG_RETURN_INBOXTYPE
				+ "' QueueId='" + NWCGConstants.NWCG_RETURN_QUEUEID + "' />");
		try {
			System.out.println("@@@@@ Trying to log the Alert...");
			Document inTemplate = XMLUtil.getDocument(Message.toString());
			CommonUtilities.invokeAPI(env, NWCGConstants.NWCG_CREATE_EXCEPTION, inTemplate);
			System.out.println("@@@@@ Logged the Alert.");
		} catch (Exception ex1) {
			System.out.println("@@@@@ Caught exception while trying to log the Alert, choosing to do nothing about it...");
			ex1.printStackTrace();
		}
		System.out.println("@@@@@ Exiting NWCGAutomateMoveRequest::throwAlert @@@@@");
	}
	
	/**
	 * This method will invoke a service that will complete move request tasks that get 
	 * created and "released" for NRFI items involved in a Return process. 
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 * @return inXML
	 */
	public Document automateMoveRequest(YFSEnvironment env, Document inXML) throws Exception {
		System.out.println("@@@@@ Entering NWCGAutomateMoveRequest::automateMoveRequest @@@@@");
		System.out.println("@@@@@ inXML : " + XMLUtil.getXMLString(inXML));
		try {
			if (inXML != null) {
				Element elemInXML_MoveRequest = inXML.getDocumentElement();
				
				// get Node from input XML
				String strNode = elemInXML_MoveRequest.getAttribute("Node");
				System.out.println("@@@@@ strNode: " + strNode);
				
				// get MoveRequestLines Details from input XML
				NodeList listInXML_MoveRequest_MoveRequestLines = inXML.getElementsByTagName("MoveRequestLines");
				System.out.println("@@@@@ length of listInXML_MoveRequest_MoveRequestLines: " + listInXML_MoveRequest_MoveRequestLines.getLength());
				String strSourceLocationId = "";
				String strInventoryStatus = "";
				String strItemId = "";
				String strProductClass = "";
				String strReleasedQuantity = "";
				String strUnitOfMeasure = "";
				String strSerialNo = "";
				if(listInXML_MoveRequest_MoveRequestLines.getLength() == 1) {
					NodeList listInXML_MoveRequest_MoveRequestLines_MoveRequestLine = inXML.getElementsByTagName("MoveRequestLine");
					System.out.println("@@@@@ length of listInXML_MoveRequest_MoveRequestLines_MoveRequestLine: " + listInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getLength());
					if(listInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getLength() == 1) {
						Node nodeInXML_MoveRequest_MoveRequestLines_MoveRequestLine = listInXML_MoveRequest_MoveRequestLines_MoveRequestLine.item(0);
						Element elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine = (Element)nodeInXML_MoveRequest_MoveRequestLines_MoveRequestLine;
						// get SourceLocationId from input XML
						strSourceLocationId = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("SourceLocationId");
						System.out.println("@@@@@ strSourceLocationId: " + strSourceLocationId);
						// get InventoryStatus from input XML
						strInventoryStatus = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("InventoryStatus");
						System.out.println("@@@@@ strInventoryStatus: " + strInventoryStatus);
						// get ItemId from input XML
						strItemId = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("ItemId");
						System.out.println("@@@@@ strItemId: " + strItemId);
						// get ProductClass from input XML
						strProductClass = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("ProductClass");
						System.out.println("@@@@@ strProductClass: " + strProductClass);
						// get ReleasedQuantity from input XML
						strReleasedQuantity = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("ReleasedQuantity");
						System.out.println("@@@@@ strReleasedQuantity: " + strReleasedQuantity);
						// get UnitOfMeasure from input XML
						strUnitOfMeasure = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("UnitOfMeasure");
						System.out.println("@@@@@ strUnitOfMeasure: " + strUnitOfMeasure);
						// get SerialNo from input XML
						strSerialNo = elemInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getAttribute("SerialNo");
						System.out.println("@@@@@ strSerialNo: " + strSerialNo);
					} else {
						throw new NWCGMissingInputElement("listInXML_MoveRequest_MoveRequestLines.getLength() either no items or too many. ");
					}
				} else {
					throw new NWCGMissingInputElement("listInXML_MoveRequest_MoveRequestLines_MoveRequestLine.getLength() either no items or too many. ");
				}
				
				// get ShipmentNo from input XML
				NodeList listInXML_MoveRequest_Shipment = inXML.getElementsByTagName("Shipment");
				System.out.println("@@@@@ length of listInXML_MoveRequest_Shipment: " + listInXML_MoveRequest_Shipment.getLength());
				String strShipmentNo = "";
				if(listInXML_MoveRequest_Shipment.getLength() == 1) {
					Node nodeInXML_MoveRequest_Shipment = listInXML_MoveRequest_Shipment.item(0);
					Element elemInXML_MoveRequest_Shipment = (Element)nodeInXML_MoveRequest_Shipment;
					strShipmentNo = elemInXML_MoveRequest_Shipment.getAttribute("ShipmentNo");
					System.out.println("@@@@@ strShipmentNo: " + strShipmentNo);
				} else {
					throw new NWCGMissingInputElement("listInXML_MoveRequest_Shipment.getLength() either no items or too many. ");
				}
				
				// set other needed values
				String strIsSummaryTask = "N";
				System.out.println("@@@@@ strIsSummaryTask: " + strIsSummaryTask);
				String strTaskStatus = "1100";
				System.out.println("@@@@@ strTaskStatus: " + strTaskStatus);
				String strisHistory = "N";
				System.out.println("@@@@@ isHistory: " + strisHistory);
					
				// throw new NWCGMissingInputString exception if 1 of the required strings is missing, 
				// otherwise set the values in the input xml document. 
				Document docInXML_getTaskList = null;
				if(!strIsSummaryTask.equals("") && !strNode.equals("") && !strTaskStatus.equals("") &&
						!strisHistory.equals("") && !strSourceLocationId.equals("") && !strInventoryStatus.equals("") &&
						!strItemId.equals("") && !strProductClass.equals("") && !strReleasedQuantity.equals("") &&
						!strUnitOfMeasure.equals("") && !strShipmentNo.equals("")) {
					// create an input XML for the API getTaskList with the ShipmentNo from the original input XML
					docInXML_getTaskList = XMLUtil.newDocument();
					Element elemInXML_getTaskList_Task = docInXML_getTaskList.createElement("Task");
					docInXML_getTaskList.appendChild(elemInXML_getTaskList_Task);
					// set the values directly under Task element onto the getTaskList input API
					elemInXML_getTaskList_Task.setAttribute("IsSummaryTask", strIsSummaryTask);
					elemInXML_getTaskList_Task.setAttribute("Node", strNode);
					elemInXML_getTaskList_Task.setAttribute("TaskStatus", strTaskStatus);
					elemInXML_getTaskList_Task.setAttribute("isHistory", strisHistory);
					elemInXML_getTaskList_Task.setAttribute("SourceLocationId", strSourceLocationId);
					// set the values under Inventory element onto the getTaskList input API
					Element elemInXML_getTaskList_Task_Inventory = docInXML_getTaskList.createElement("Inventory");
					elemInXML_getTaskList_Task.appendChild(elemInXML_getTaskList_Task_Inventory);
					elemInXML_getTaskList_Task_Inventory.setAttribute("InventoryStatus", strInventoryStatus);
					elemInXML_getTaskList_Task_Inventory.setAttribute("ItemId", strItemId);
					elemInXML_getTaskList_Task_Inventory.setAttribute("ProductClass", strProductClass);
					elemInXML_getTaskList_Task_Inventory.setAttribute("ReleasedQuantity", strReleasedQuantity);
					elemInXML_getTaskList_Task_Inventory.setAttribute("UnitOfMeasure", strUnitOfMeasure);
					// if the item is trackable, set the serial number. 
					System.out.println("@@@@@ strSerialNo: (" + strSerialNo + ") :" + strSerialNo.length());
					if(!strSerialNo.equals("")) {
						// checking for a whitespace character as is the case if item is NOT trackable and hence we will NOT want to set the Serial Number
						if(strSerialNo.length() > 0 && strSerialNo.charAt(0) != ' ') {
							elemInXML_getTaskList_Task_Inventory.setAttribute("SerialNo", strSerialNo);
						}
					}
					// set ShipmentNo onto getTaskList input API
					Element elemInXML_getTaskList_Task_TaskReferences = docInXML_getTaskList.createElement("TaskReferences");
					elemInXML_getTaskList_Task.appendChild(elemInXML_getTaskList_Task_TaskReferences);
					elemInXML_getTaskList_Task_TaskReferences.setAttribute("ShipmentNo", strShipmentNo);
					System.out.println("@@@@@ docInXML_getTaskList : " + XMLUtil.getXMLString(docInXML_getTaskList));
				} else {
					throw new NWCGMissingInputString("1 or more of the required input strings for the getTaskList API was missing. ");
				}
				
				// invoke the API getTaskList
				if(docInXML_getTaskList != null) {
					Document docOutXML_getTaskList = CommonUtilities.invokeAPI(env, "getTaskList", docInXML_getTaskList);
					if (docOutXML_getTaskList != null) {
						System.out.println("@@@@@ docOutXML_getTaskList : " + XMLUtil.getXMLString(docOutXML_getTaskList));
			
						// get TaskKey from the Output XML that came from the invoked API getTaskList
						Element elemOutXML_getTaskList_TaskList = docOutXML_getTaskList.getDocumentElement();
						NodeList listOutXML_getTaskList_TaskList_Task = elemOutXML_getTaskList_TaskList.getElementsByTagName("Task");
						System.out.println("@@@@@ length of listOutXML_getTaskList_TaskList_Task: " + listOutXML_getTaskList_TaskList_Task.getLength());
				
						if(listOutXML_getTaskList_TaskList_Task.getLength() == 1) {
							Node nodeInXML_getTaskList_TaskList_Task = listOutXML_getTaskList_TaskList_Task.item(0);
							Element elemOutXML_getTaskList_TaskList_Task = (Element)nodeInXML_getTaskList_TaskList_Task;
							String strTaskKey = elemOutXML_getTaskList_TaskList_Task.getAttribute("TaskKey");
							System.out.println("@@@@@ strTaskKey: " + strTaskKey);
							String strEnterpriseKey = elemOutXML_getTaskList_TaskList_Task.getAttribute("EnterpriseKey");
							System.out.println("@@@@@ strEnterpriseKey: " + strEnterpriseKey);
							String strOrganizationCode = elemOutXML_getTaskList_TaskList_Task.getAttribute("OrganizationCode");
							System.out.println("@@@@@ strOrganizationCode: " + strOrganizationCode);
							String strAssignedToUserId = env.getUserId();
							System.out.println("@@@@@ strAssignedToUserId: " + strAssignedToUserId);
				
							// create an input XML for the API registerTaskCompletion with the Task details retrieved from the previous output XML
							if(!strTaskKey.equals("") && !strEnterpriseKey.equals("") && !strOrganizationCode.equals("") && !strAssignedToUserId.equals("")) {
								Document docInXML_registerTaskCompletion = XMLUtil.newDocument();
								Element elemInXML_registerTaskCompletion_Task = docInXML_registerTaskCompletion.createElement("Task");
								docInXML_registerTaskCompletion.appendChild(elemInXML_registerTaskCompletion_Task);
								elemInXML_registerTaskCompletion_Task.setAttribute("TaskKey", strTaskKey);
								elemInXML_registerTaskCompletion_Task.setAttribute("EnterpriseKey", strEnterpriseKey);
								elemInXML_registerTaskCompletion_Task.setAttribute("OrganizationCode", strOrganizationCode);
								elemInXML_registerTaskCompletion_Task.setAttribute("AssignedToUserId", strAssignedToUserId);
								// if the item is trackable, set the serial number. 
								System.out.println("@@@@@ strSerialNo: (" + strSerialNo + ") :" + strSerialNo.length());
								if(!strSerialNo.equals("")) {
									// checking for a whitespace character as is the case if item is NOT trackable and hence we will NOT want to set the Serial Number
									if(strSerialNo.length() > 0 && strSerialNo.charAt(0) != ' ') {
										Element elemInXML_registerTaskCompletion_Task_Inventory = docInXML_registerTaskCompletion.createElement("Inventory");
										elemInXML_registerTaskCompletion_Task.appendChild(elemInXML_registerTaskCompletion_Task_Inventory);
										Element elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails = docInXML_registerTaskCompletion.createElement("SerialNumberDetails");
										elemInXML_registerTaskCompletion_Task_Inventory.appendChild(elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails);
										Element elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails_SerialNumberDetail = docInXML_registerTaskCompletion.createElement("SerialNumberDetail");
										elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails.appendChild(elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails_SerialNumberDetail);
										elemInXML_registerTaskCompletion_Task_Inventory_SerialNumberDetails_SerialNumberDetail.setAttribute("SerialNo", strSerialNo.trim());
										System.out.println("@@@@@ strSerialNo: " + strSerialNo + " :: Length: " + strSerialNo.length());
									}
								}  
								System.out.println("@@@@@ docInXML_registerTaskCompletion : " + XMLUtil.getXMLString(docInXML_registerTaskCompletion));
				
								// invoke the API registerTaskCompletion
								Document docOutXML_registerTaskCompletion = CommonUtilities.invokeAPI(env, "registerTaskCompletion", docInXML_registerTaskCompletion);
								if (docOutXML_registerTaskCompletion != null) {
									System.out.println("@@@@@ docOutXML_registerTaskCompletion : " + XMLUtil.getXMLString(docOutXML_registerTaskCompletion));
								} else {
									throw new NullPointerException("docOutXML_registerTaskCompletion returned null. ");
								}
							} else {
								throw new NWCGMissingInputString("1 or more of the required input strings for the registerTaskCompletion API was missing. ");
							}
						} else {
							throw new NWCGMissingInputElement("listOutXML_getTaskList_TaskList_Task.getLength() either no items or too many. ");
						}
					} else {
						throw new NullPointerException("docOutXML_getTaskList returned null. ");
					}
				} else {
					throw new NullPointerException("docInXML_getTaskList returned null. ");
				} 
			} else {
				throw new NullPointerException("inXML returned null. ");
			}
		} catch (NWCGMissingInputElement mie) {
			System.out.println("!!!!! Caught NWCGMissingInputElement Exception: " + mie);
			mie.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGAutomateMoveRequest' " + "Missing Input Element Exception! ");
			throwAlert(env, stbuf);
		} catch (NWCGMissingInputString mis) {
			System.out.println("!!!!! Caught IndexOutOfBounds Exception: " + mis);
			mis.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGAutomateMoveRequest' " + "Missing Input String Exception! ");
			throwAlert(env, stbuf);
		} catch (NullPointerException npe) {
			System.out.println("!!!!! Caught Null Pointer Exception: " + npe);
			npe.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGAutomateMoveRequest' " + "Null Pointer Exception! ");
			throwAlert(env, stbuf);
		} catch (Exception exp) {
			System.out.println("!!!!! Caught General Exception: " + exp);
			exp.printStackTrace();
			StringBuffer stbuf = new StringBuffer("<Inbox ActiveFlag='' ApiName='NWCGAutomateMoveRequest' " + "Null General Exception! ");
			throwAlert(env, stbuf);
		}
		System.out.println("@@@@@ Exiting NWCGAutomateMoveRequest::automateMoveRequest @@@@@");
		return inXML;
	}
}

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date March 21, 2013
 */
class NWCGMissingInputString extends Exception {
	//Parameterless Constructor
	public NWCGMissingInputString() {
	}

	//Constructor that accepts a message
	public NWCGMissingInputString(String message) {
		super(message);
	}
}

/**
 * 
 * @author Oxford Consulting Group
 * @version 1.0
 * @date March 21, 2013
 */
class NWCGMissingInputElement extends Exception {
	//Parameterless Constructor
	public NWCGMissingInputElement() {
	}

	//Constructor that accepts a message
	public NWCGMissingInputElement(String message) {
		super(message);
	}
}