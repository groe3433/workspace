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

package com.nwcg.icbs.yantra.api.adjlocninv;

import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfs.japi.YFSEnvironment;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;

/**
 * This API will be called from the Adjust Location Inventory, 
 * when a user clicks the "Remove" action after selecting 1 or more Trackable Items. 
 * It's intent is to adjust these selected trackable items out of the location. 
 * 
 * @author Lightwell
 * @version 1.0
 * @date October 2, 2014
 */
public class NWCGRemoveTrackableItems implements YIFCustomApi {
	
	private Properties _properties;
	
	public void setProperties(Properties arg0) throws Exception {
		_properties = arg0;
	}

	/**
	 * This is called from the NWCGRemoveTrackableItems service. 
	 * The Service is invoked ONLY when the user is adjusting out trackable non-kits. 
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document removeTrackedId(YFSEnvironment env, Document inXML) throws Exception {
		System.out.println("@@@@@ Entering com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems::removeTrackedId @@@@@");
		System.out.println("@@@@@ inXML :: " + XMLUtil.getXMLString(inXML));
		
		try {
			NodeList listSerialList = inXML.getElementsByTagName("SerialList");
			if(listSerialList.getLength() == 1) {
				NodeList listSerialDetail = inXML.getElementsByTagName("SerialDetail");
				if(listSerialDetail.getLength() > 0) {
					// For each SerialNo that the user selected and wanted to adjust out of inventory. 
					for(int i = 0; i < listSerialDetail.getLength(); i++) {
						//		1. Copy the inXML. 
						Document inXML_Copy = copyInputDocument(env, inXML);
					
						//		2. get Serial Number from inXML. 
						String strSerialNo = getSerialNumber(env, listSerialDetail, i);
						System.out.println("@@@@@ strSerialNo :: " + strSerialNo);
					
						//		3. trim the copy so that it ONLY has THAT SerialNo. 
						inXML_Copy = trimCopiedDocument(env, inXML_Copy, strSerialNo);
					
						//		4. Populate the TagDetail Node by calling getSerialList with the SerialNo.
						inXML_Copy = getTagDetails(env, strSerialNo, inXML_Copy);
					
						//		5. Populate the Adjust Location Inventory document number by calling the service (NWCGAdjLocnInvReference) to get that Document number that is next in the sequence. 
						inXML_Copy = getDocumentNumber(env, inXML_Copy);
					
						//		6. Pass this modified XML that is specific to just 1 SerialNo into the service (NWCGAdjustLocationInventoryService) that is the exact same thing we do when we click SAVE.
						System.out.println("@@@@@ Calling NWCGAdjustLocationInventoryService with inXML_Copy :: " + XMLUtil.getXMLString(inXML_Copy));
						CommonUtilities.invokeService(env, "NWCGAdjustLocationInventoryService", inXML_Copy);
					}
				}
			} else {
				throw new NWCGNoCheckboxesChecked("...The user did not select trackable items to adjust out of inventory...");
			}
		} catch (NWCGNoCheckboxesChecked ncbc) {
			System.out.println("!!!!! Caught NWCGNoCheckboxesChecked Exception: " + ncbc);
			ncbc.printStackTrace();
		} catch(Exception ex) {
			System.out.println("!!!!! Caught General Exception :: " + ex);
			ex.printStackTrace();
		}
		
		System.out.println("@@@@@ Exiting com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems::removeTrackedId @@@@@");
		return inXML;
	}
	
	/**
	 * This method returns the trackable id for the item we will adjust during the current iteration. 
	 * 
	 * @param env
	 * @param listSerialDetail
	 * @param i
	 * @return
	 * @throws Exception
	 */
	public String getSerialNumber(YFSEnvironment env, NodeList listSerialDetail, int i) throws Exception {
		Node nodeSerialDetail = listSerialDetail.item(i);
		Element elemSerialDetail = (Element)nodeSerialDetail;
		String strSerialNo = elemSerialDetail.getAttribute("SerialNo");
		//System.out.println("@@@@@ strSerialNo :: " + strSerialNo);
		return strSerialNo;
	}
	
	/**
	 * This method returns a clone of the input document. 
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document copyInputDocument(YFSEnvironment env, Document inXML) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Node originalRoot = inXML.getDocumentElement();
        Document inXML_Copy = db.newDocument();
        Node copiedRoot = inXML_Copy.importNode(originalRoot, true);
        inXML_Copy.appendChild(copiedRoot);
        return inXML_Copy;
	}
	
	/**
	 * In order to adjust some trackable items, we need to get the tag details. 
	 * This method calls getSerialList to retrieve these tag details and populates them ont he "copy" of the input document. 
	 * It uses an output document from getSerialList like the following:
		<SerialList>
		<Serial SerialNo="" SecondarySerial1="" SecondarySerial2="" SecondarySerial3="" SecondarySerial4="" SecondarySerial5="" SecondarySerial6="" SecondarySerial7="" SecondarySerial8="" SecondarySerial9="">
		<TagDetail LotAttribute1="" LotAttribute2="" LotAttribute3="" LotKeyReference="" LotNumber="" ManufacturingDate="" RevisionNo=""/>
		</Serial>
		</SerialList>
	 * 
	 * @param env
	 * @param strSerialNo
	 * @return
	 * @throws Exception
	 */
	public Document getTagDetails(YFSEnvironment env, String strSerialNo, Document inXML_Copy) throws Exception {
		Document docInSerialList = XMLUtil.newDocument();
		Element elemSerialList = docInSerialList.createElement("Serial");
		docInSerialList.appendChild(elemSerialList);
		elemSerialList.setAttribute("SerialNo", strSerialNo);
		//System.out.println("@@@@@ docInSerialList :: " + XMLUtil.getXMLString(docInSerialList));
		Document docOutSerialList = CommonUtilities.invokeAPI(env, "NWCGGetSerialList_getSerialList", "getSerialList", docInSerialList);
		//System.out.println("@@@@@ docOutSerialList :: " + XMLUtil.getXMLString(docOutSerialList));
		
		String strSecondarySerial1 = "";
		String strSecondarySerial2 = "";
		String strSecondarySerial3 = "";
		String strSecondarySerial4 = "";
		String strSecondarySerial5 = "";
		String strSecondarySerial6 = "";
		String strSecondarySerial7 = "";
		String strSecondarySerial8 = "";
		String strSecondarySerial9 = "";
		NodeList listOutSerial = docOutSerialList.getElementsByTagName("Serial");
		if(listOutSerial.getLength() > 0) {
			Node nodeOutSerial = listOutSerial.item(0);
			Element elemOutSerial = (Element)nodeOutSerial;
			strSecondarySerial1 = elemOutSerial.getAttribute("SecondarySerial1");	
			strSecondarySerial2 = elemOutSerial.getAttribute("SecondarySerial2");	
			strSecondarySerial3 = elemOutSerial.getAttribute("SecondarySerial3");	
			strSecondarySerial4 = elemOutSerial.getAttribute("SecondarySerial4");	
			strSecondarySerial5 = elemOutSerial.getAttribute("SecondarySerial5");	
			strSecondarySerial6 = elemOutSerial.getAttribute("SecondarySerial6");	
			strSecondarySerial7 = elemOutSerial.getAttribute("SecondarySerial7");	
			strSecondarySerial8 = elemOutSerial.getAttribute("SecondarySerial8");	
			strSecondarySerial9 = elemOutSerial.getAttribute("SecondarySerial9");	
		}
		String strLotAttribute1 = "";
		String strLotAttribute2 = "";	
		String strLotAttribute3 = "";	
		String strLotNumber = "";
		String strManufacturingDate = "";	
		String strRevisionNo = "";	
		NodeList listOutTagDetail = docOutSerialList.getElementsByTagName("TagDetail");
		if(listOutTagDetail.getLength() > 0) {
			Node nodeOutTagDetail = listOutTagDetail.item(0);
			Element elemOutTagDetail = (Element)nodeOutTagDetail;
			strLotAttribute1 = elemOutTagDetail.getAttribute("LotAttribute1");
			strLotAttribute2 = elemOutTagDetail.getAttribute("LotAttribute2");	
			strLotAttribute3 = elemOutTagDetail.getAttribute("LotAttribute3");	
			strLotNumber = elemOutTagDetail.getAttribute("LotNumber");	
			strManufacturingDate = elemOutTagDetail.getAttribute("ManufacturingDate");	
			strRevisionNo = elemOutTagDetail.getAttribute("RevisionNo");		
		}
		
		NodeList listInXMLSerialDetail = inXML_Copy.getElementsByTagName("SerialDetail");
		if(listInXMLSerialDetail.getLength() > 0) {
			Node nodeInXMLSerialDetail = listInXMLSerialDetail.item(0);
			Element elemInXMLSerialDetail = (Element)nodeInXMLSerialDetail;
			elemInXMLSerialDetail.setAttribute("SecondarySerial1", strSecondarySerial1);
			if(!strSecondarySerial2.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial2", strSecondarySerial2);
			} if(!strSecondarySerial3.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial3", strSecondarySerial3);
			} if(!strSecondarySerial4.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial4", strSecondarySerial4);
			} if(!strSecondarySerial5.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial5", strSecondarySerial5);
			} if(!strSecondarySerial6.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial6", strSecondarySerial6);
			} if(!strSecondarySerial7.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial7", strSecondarySerial7);
			} if(!strSecondarySerial8.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial8", strSecondarySerial8);
			} if(!strSecondarySerial9.trim().equals("")) {
				elemInXMLSerialDetail.setAttribute("SecondarySerial9", strSecondarySerial9);
			}
		}
		NodeList listInXMLInventory = inXML_Copy.getElementsByTagName("Inventory");
		if(listInXMLInventory.getLength() == 1) {
			Node nodeInXMLInventory = listInXMLInventory.item(0);
			Element elemInXMLTagDetail = inXML_Copy.createElement("TagDetail");
			nodeInXMLInventory.appendChild(elemInXMLTagDetail);
			elemInXMLTagDetail.setAttribute("LotAttribute1", strLotAttribute1);
			elemInXMLTagDetail.setAttribute("LotAttribute2", strLotAttribute2);
			elemInXMLTagDetail.setAttribute("LotAttribute3", strLotAttribute3);
			elemInXMLTagDetail.setAttribute("LotNumber", strLotNumber);
			elemInXMLTagDetail.setAttribute("ManufacturingDate", strManufacturingDate);
			elemInXMLTagDetail.setAttribute("RevisionNo", strRevisionNo);
		}
		//System.out.println("@@@@@ inXML_Copy :: " + XMLUtil.getXMLString(inXML_Copy));
		return inXML_Copy;
	}
	
	/**
	 * In order to adjust some trackable items, 
	 * we need to generate a new document number for each time we try to adjust a trackable item out of inventory. 
	 * The method will get the document number and populate it on the "copy" of the input document. 
	 * Gets the document number from the NWCGAdjLocnInvReference service call output like this:
		<AdjustLocationInventory DocumentNumber="A100143429"/>
	 * 
	 * @param env
	 * @return
	 * @throws Exception
	 */
	public Document getDocumentNumber(YFSEnvironment env, Document inXML_Copy) throws Exception {
		Document docInNWCGAdjLocnInvReference = XMLUtil.newDocument();
		Element elemroot = docInNWCGAdjLocnInvReference.createElement("AdjustLocationInventory");
		docInNWCGAdjLocnInvReference.appendChild(elemroot);
		elemroot.setAttribute("IgnoreOrdering", "Y");
		//System.out.println("@@@@@ docInNWCGAdjLocnInvReference :: " + XMLUtil.getXMLString(docInNWCGAdjLocnInvReference));
		Document docOutNWCGAdjLocnInvReference = CommonUtilities.invokeService(env, "NWCGAdjLocnInvReference", docInNWCGAdjLocnInvReference);
		//System.out.println("@@@@@ docOutNWCGAdjLocnInvReference :: " + XMLUtil.getXMLString(docOutNWCGAdjLocnInvReference));
		
		String strDocumentNumber = "";
		NodeList listOutNWCGAdjLocnInvReference = docOutNWCGAdjLocnInvReference.getElementsByTagName("AdjustLocationInventory");
		if(listOutNWCGAdjLocnInvReference.getLength() > 0) {
			Node nodeOutNWCGAdjLocnInvReference = listOutNWCGAdjLocnInvReference.item(0);
			Element elemOutNWCGAdjLocnInvReference = (Element)nodeOutNWCGAdjLocnInvReference;
			strDocumentNumber = elemOutNWCGAdjLocnInvReference.getAttribute("DocumentNumber");	
		}
		
		NodeList listInXMLAudit = inXML_Copy.getElementsByTagName("Audit");
		if(listInXMLAudit.getLength() > 0) {
			Node nodeInXMLAudit = listInXMLAudit.item(0);
			Element elemInXMLAudit = (Element)nodeInXMLAudit;
			elemInXMLAudit.setAttribute("Reference1", strDocumentNumber);
		}
		//System.out.println("@@@@@ inXML_Copy :: " + XMLUtil.getXMLString(inXML_Copy));
		return inXML_Copy;
	}
	
	/**
	 * This method needs to take the "copy" of the input document and trim off all trackable items EXCEPT the one we need to adjust. 
	 * Each iteration will trim a different trackable item. 
	 * 
	 * @param env
	 * @param inXML_Copy
	 * @param strSerialNo
	 * @return
	 * @throws Exception
	 */
	public Document trimCopiedDocument(YFSEnvironment env, Document inXML_Copy, String strSerialNo) throws Exception {
		NodeList listSerialDetailCopy = inXML_Copy.getElementsByTagName("SerialDetail");
		if(listSerialDetailCopy.getLength() > 0) {
			for(int j = 0; j < listSerialDetailCopy.getLength(); j++) {
				Node nodeSerialDetailCopy = listSerialDetailCopy.item(j);
				Element elemSerialDetailCopy = (Element)nodeSerialDetailCopy;
				String strSerialNoCopy = elemSerialDetailCopy.getAttribute("SerialNo");	
				if(strSerialNo.equals(strSerialNoCopy)) {
					//System.out.println("@@@@@@@@@@ KEEPING NODE " + strSerialNo + " " + strSerialNoCopy);
					// do nothing ...
				} else {
					//System.out.println("@@@@@@@@@@ REMOVING NODE " + strSerialNo + " " + strSerialNoCopy);
					nodeSerialDetailCopy.getParentNode().removeChild(nodeSerialDetailCopy);
					j--;
				}
			}
		}
		//System.out.println("@@@@@ inXML_Copy :: " + XMLUtil.getXMLString(inXML_Copy));
		return inXML_Copy;
	}
}

class NWCGNoCheckboxesChecked extends Exception {
	//Parameterless Constructor
	public NWCGNoCheckboxesChecked() {
	}

	//Constructor that accepts a message
	public NWCGNoCheckboxesChecked(String message) {
		super(message);
	}
}