package com.nwcg.icbs.yantra.api.issue.util;

import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateIssueLineUtil 
{
	public static void publishUnpublishedItems(YFSEnvironment env, Document inDoc)
	{
		//System.out.println("NWCGValidateIssueLineUtil: publishUnpublishedItems: inDoc: \n " + XMLUtil.getXMLString(inDoc));
		try
		{
			Element orderElement = inDoc.getDocumentElement();			
			Element orderLinesElement = (Element)orderElement.getElementsByTagName(NWCGConstants.ORDER_LINES).item(0);			
			NodeList orderLines = orderLinesElement.getElementsByTagName(NWCGConstants.ORDER_LINE);

			for(int i = 0; i < orderLines.getLength(); i++)
			{
				Element currentOrderLine = (Element)orderLines.item(i);
				NodeList orderLineChildNodeList = currentOrderLine.getChildNodes();
				String itemId = NWCGConstants.EMPTY_STRING;;
				String unitOfMeasure = NWCGConstants.EMPTY_STRING;

				for(int j = 0; j < orderLineChildNodeList.getLength(); j++)
				{
					Element currentElement = (Element)orderLineChildNodeList.item(j); 
					if(currentElement.getNodeName().equalsIgnoreCase(NWCGConstants.ITEM))
					{
						itemId = currentElement.getAttribute(NWCGConstants.ITEM_ID);						
					}
					else if(currentElement.getNodeName().equalsIgnoreCase(NWCGConstants.ORDER_LINE_TRAN_QTY_ELM))
					{
						unitOfMeasure = currentElement.getAttribute(NWCGConstants.TRANSACTIONAL_UOM);
					}
				}

				Document gidIpDoc = XMLUtil.getDocument("<Item ItemID=\"" + itemId + "\" OrganizationCode=\"NWCG\" UnitOfMeasure=\"" + unitOfMeasure + "\"  />");
				Document gidOpDoc = CommonUtilities.invokeAPI(env, NWCGConstants.API_GET_ITEM_DETAILS, gidIpDoc);
				Element itemElement = gidOpDoc.getDocumentElement();
				
				NodeList gidOpChildNodes = itemElement.getChildNodes();
				String extnPublishToRoss = null;
				Element extnElement = null;
				
				for(int k = 0; k < gidOpChildNodes.getLength(); k++)
				{
					Node currentNode = gidOpChildNodes.item(k);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element currentElement = (Element) currentNode;
						if (currentElement.getNodeName().equalsIgnoreCase("Extn"))
						{
							extnElement = currentElement;
							extnPublishToRoss = currentElement.getAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS);
							break;
						}
					}
				}

				if(extnPublishToRoss != null && extnPublishToRoss.equals("N"))
				{
					itemElement.setAttribute("Action", "MODIFY");
					extnElement.setAttribute(NWCGConstants.EXTN_PUBLISH_TO_ROSS, "Y");

					Document miIpDoc = XMLUtil.createDocument("ItemList");
					Element itemListElement = miIpDoc.getDocumentElement();
					Element newItemElement = miIpDoc.createElement("Item");
					itemListElement.appendChild(newItemElement);
					XMLUtil.copyElement(miIpDoc, itemElement, newItemElement);
					
					CommonUtilities.invokeAPI(env,NWCGConstants.API_MODIFY_ITEM, miIpDoc);

					//raise alert					
					HashMap<String, String> alertReferences = new HashMap<String, String>();
					alertReferences.put(NWCGConstants.ITEM_ID, itemId);
					alertReferences.put("AlertType", "Substitution/Consolidation with unpublished item");
					alertReferences.put("Request Initiated By", "ROSS");

					Document orderDetailsDoc = getOrderDetails(env, orderElement.getAttribute("OrderHeaderKey"));
					if(orderDetailsDoc != null)
					{
						Element oDetailsElement = orderDetailsDoc.getDocumentElement();
						Element orderExtnElement = (Element)oDetailsElement.getFirstChild();
						alertReferences.put("Issue No", oDetailsElement.getAttribute("OrderNo"));
						alertReferences.put("Incident No", orderExtnElement.getAttribute("ExtnIncidentNo"));
						alertReferences.put("Incident Year", orderExtnElement.getAttribute("ExtnIncidentYear"));
						alertReferences.put("Ross Dispatch Id", orderExtnElement.getAttribute("ExtnRossDispatchId"));
					}

					String errorDescription = "Item " + itemId + " has been published to Ross.";
					//String userIdToAssign = CommonUtilities.getAdminUserForCache(env, cacheId);			
					//String userIdToAssign = "jbilliard";			
					//CommonUtilities.raiseAlertAndAssigntoUser(env, NWCGAAConstants.QUEUEID_ROSS_PUBLISH, errorDescription, userIdToAssign, null, null);
					CommonUtilities.raiseAlert(env, NWCGAAConstants.QUEUEID_INCIDENT_FAILURE, errorDescription, null, null, alertReferences);
				}
			}
		}
		catch(ParserConfigurationException pce){
			System.out.println("NWCGValidateIssueLineUtil: publishUnpublishedItems: ParserConfigurationException: " + pce.getMessage());
		}
		catch (Exception e){
			System.out.println("NWCGValidateIssueLineUtil: publishUnpublishedItems: Exception: " + e.getMessage());
		}
	}
	
	public static Document getOrderDetails(YFSEnvironment env, String orderHeaderKey)
	{
		Document orderDetailsDoc = null;
		try
		{
			Document opTemplate = XMLUtil.getDocument("<Order OrderNo=\"\" ><Extn/></Order>");
			Document ipDoc = XMLUtil.getDocument("<Order OrderHeaderKey=\"" + orderHeaderKey + "\" />");
			orderDetailsDoc = CommonUtilities.invokeAPI(env, opTemplate, "getOrderDetails", ipDoc);
		}
		catch(ParserConfigurationException pce){
			System.out.println("NWCGValidateIssueLineUtil: getOrderDetails: ParserConfigurationException: " + pce.getMessage());
		}
		catch (Exception e){
			System.out.println("NWCGValidateIssueLineUtil: getOrderDetails: Exception: " + e.getMessage());
		}
		return orderDetailsDoc;
	}
}
