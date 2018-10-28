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

package com.nwcg.icbs.yantra.api.returns;

import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.api.adjlocninv.NWCGRemoveTrackableItems;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGisSerialUniqueInAllNodes implements YIFCustomApi {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGisSerialUniqueInAllNodes.class);
	
	private Properties myProperties = null;

	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
	}

	String IsSerialTracked = NWCGConstants.EMPTY_STRING;
	String TagControlFlag = NWCGConstants.EMPTY_STRING;
	String ShortDescription = NWCGConstants.EMPTY_STRING;
	String TimeSensitive = NWCGConstants.EMPTY_STRING;

	// Entry point method
	public Document getSerialDetails(YFSEnvironment env, Document inXML)
			throws TransformerException, YFSException {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getSerialDetails");
		// Document returnDoc =null;
		Document getSerialList_Out = null;
		Document verify_out = null;
		Document verify_incident_serial = null;
		Element TSerial = null;

		logger.verbose("@@@@@ Entering the NWCGisSerialUniqueInAllNodes API , input document is:"
				+ XMLUtil.extractStringFromDocument(inXML));

		try {
			// FLOW-----------------
			// First call getitemList using the itemID and calling
			// organizationCode and get the item details like PC
			// PC and UOM
			// Then call getitemdetails to get the component and other required
			// information.

			NodeList InvItemList = inXML.getDocumentElement().getElementsByTagName("InventoryItem");

			// GS - check InvItemList!=null && InvItemList.getLength()>0
			Element InvAttEl = (Element) InvItemList.item(0);
			String ItemID = InvAttEl.getAttribute("ItemID");
			String SerialNo = inXML.getDocumentElement().getAttribute("SerialNo");
			String IncidentNo = inXML.getDocumentElement().getAttribute("IncidentNo");
			String IncidentYear = inXML.getDocumentElement().getAttribute("IncidentYear");
			String IssueNo = inXML.getDocumentElement().getAttribute("IssueNo");

			logger.verbose("@@@@@ ItemID:-" + ItemID);
			logger.verbose("@@@@@ SerialNo:-" + SerialNo);
			logger.verbose("@@@@@ IncidentNo:-" + IncidentNo);
			logger.verbose("@@@@@ IncidentYear:-" + IncidentYear);
			logger.verbose("@@@@@ IssueNo:-" + IssueNo);

			// if(ItemID==""||ItemID==null||SerialNo==null || SerialNo==""){
			// Changed the line after code review
			if (ItemID.equals(NWCGConstants.EMPTY_STRING) || ItemID == null
					|| SerialNo == null
					|| SerialNo.equals(NWCGConstants.EMPTY_STRING)) {
				getSerialList_Out = XMLUtil.newDocument();

				Element Result = getSerialList_Out.createElement("Result");
				getSerialList_Out.appendChild(Result);
				Result.setAttribute("SerialPresent", "Invalid Input");
				return getSerialList_Out;
			}// End IF
			
			logger.verbose("@@@@@ inXML before verfiySerialList :: " + XMLUtil.extractStringFromDocument(inXML));
			verify_out = verfiySerialList(env, inXML);
			logger.verbose("@@@@@ inXML after verfiySerialList :: " + XMLUtil.extractStringFromDocument(inXML));
			
			logger.verbose("@@@@@ verify_out after verfiySerialList :: " + XMLUtil.extractStringFromDocument(verify_out));
			Element verifyElm = verify_out.getDocumentElement();
			String SecSerial = verifyElm.getAttribute("SecondarySerial1");
			logger.verbose("@@@@@ verify_out::SecSerial :: " + SecSerial);
			TSerial = (Element) verify_out.getDocumentElement().getElementsByTagName("TagDetail").item(0);
			String serialValid = verifyElm.getAttribute("SerialPresent");
			logger.verbose("@@@@@ verifyElm::serialValid :: " + serialValid);
			int trackablecnt = 0;
			
			logger.verbose("@@@@@ inXML before getTrackablecnt :: " + XMLUtil.extractStringFromDocument(inXML));
			trackablecnt = getTrackablecnt(env, SerialNo, SecSerial, IncidentNo, IncidentYear, ItemID);
			logger.verbose("@@@@@ inXML after getTrackablecnt :: " + XMLUtil.extractStringFromDocument(inXML));
			
			if (!serialValid.equals("valid")) {
				return verify_out;
			}
			String ParentSerialKey = verifyElm.getAttribute("ParentSerialKey");
			logger.verbose("@@@@@ verifyElm::ParentSerialKey :: " + ParentSerialKey);
			
			logger.verbose("@@@@@ inXML before callgetSerialList :: " + XMLUtil.extractStringFromDocument(inXML));
			getSerialList_Out = callgetSerialList(env, inXML);
			logger.verbose("@@@@@ inXML after callgetSerialList :: " + XMLUtil.extractStringFromDocument(inXML));
			
			logger.verbose("@@@@@ getSerialList_Out after callgetSerialList : " + XMLUtil.extractStringFromDocument(getSerialList_Out));
			Element verifyElm1 = getSerialList_Out.getDocumentElement();
			String serialValid1 = verifyElm1.getAttribute("SerialPresent");
			if (serialValid1.equals("True")) {
				return getSerialList_Out;
			}
			logger.verbose("@@@@@ trackablecnt : " + trackablecnt);
			verify_incident_serial = XMLUtil.newDocument();
			if (trackablecnt == 0) {
				Element IncidentResult = verify_incident_serial.createElement("Result");
				verify_incident_serial.appendChild(IncidentResult);
				IncidentResult.setAttribute("SerialPresent", "NotInIncident");
				IncidentResult.setAttribute("SecondarySerial1", SecSerial);
				Element root1 = verify_incident_serial.getDocumentElement();
				Element TElem1 = verify_incident_serial.createElement("TagDetail");
				if (TSerial != null)
					XMLUtil.copyElement(verify_incident_serial, TSerial, TElem1);
				root1.appendChild(TElem1);
			} else {
				Element IncidentResult = verify_incident_serial
						.createElement("Result");
				verify_incident_serial.appendChild(IncidentResult);
				IncidentResult.setAttribute("SerialPresent", "False");
				IncidentResult.setAttribute("SecondarySerial1", SecSerial);
				Element root1 = verify_incident_serial.getDocumentElement();
				Element TElem1 = verify_incident_serial.createElement("TagDetail");
				root1.appendChild(TElem1);
				if (TSerial != null)
					XMLUtil.copyElement(verify_incident_serial, TSerial, TElem1);
			}

			logger.verbose("@@@@@ Serial Output XML : " + XMLUtil.extractStringFromDocument(verify_incident_serial));
			logger.verbose("@@@@@ Exiting the NWCGisSerialUniqueInAllNodes API , output document is:" + XMLUtil.extractStringFromDocument(getSerialList_Out));
		} catch (Exception E) {
			logger.error("!!!!! Generated exception " + E.toString());
			throw new NWCGException("NWCG_RETURN_INPUT_VALUES_ERROR");
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getSerialDetails");
		return verify_incident_serial;
	}

	// This method is to make sure that the serial is is valid
	public Document verfiySerialList(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::verfiySerialList");
		Document getSerialList_Out = null;

		// ----------call the same api by removing the 'Y' from AtNode attribute
		// it with AtNode=""
		// -------It any results are obtained the the serial is valid.
		
		logger.verbose("@@@@@@@@@@ Input xml----passOnForVerification:-" + XMLUtil.extractStringFromDocument(inXML));
		
		Element SerialEl = inXML.getDocumentElement();
		SerialEl.setAttribute("AtNode", NWCGConstants.EMPTY_STRING);
		
		logger.verbose("@@@@@@@@@@ The input xml for getSerialList[check if the serial iis VALID] is:- " + XMLUtil.extractStringFromDocument(inXML));
		
		// START - 9.1 fix for getSerialList API bug, July 25, 2014
		
		//get the Serial Number here
		NodeList listInXML = inXML.getElementsByTagName("Serial");
		Node nodeInXML = listInXML.item(0);
		Element elemInXML = (Element)nodeInXML;
		String SerialNo = elemInXML.getAttribute("SerialNo");
		
		//got to raise an IBM defect here, getSerialList API is dropping the SerialNo attribute. 
		getSerialList_Out = CommonUtilities.invokeAPI(env, "getSerialList", inXML);
		
		//set THAT serial number back here
		NodeList listInXML1 = inXML.getElementsByTagName("Serial");
		Node nodeInXML1 = listInXML1.item(0);
		Element elemInXML1 = (Element)nodeInXML1;
		elemInXML1.setAttribute("SerialNo", SerialNo);
		
		// END - 9.1 fix for getSerialList API bug, July 25, 2014
		
		logger.verbose("@@@@@@@@@@ getSerial Input  XML after calling getSerialList in verifyList : " + XMLUtil.extractStringFromDocument(inXML));
		
		// GS - check if getSerialList_Out!=null
		NodeList SerialList2 = getSerialList_Out.getDocumentElement().getElementsByTagName("Serial");

		// GS - get length after checking .. ie. add if(SerialList2!=null)
		int nodeCount2 = SerialList2.getLength();

		if (nodeCount2 == 0) {
			logger.verbose("!!!!! No records found");
			getSerialList_Out = XMLUtil.newDocument();
			Element ResultEl = getSerialList_Out.createElement("Result");
			getSerialList_Out.appendChild(ResultEl);
			ResultEl.setAttribute("SerialPresent", "Invalid Input");
			return getSerialList_Out;
		}
		
		Element PSerial = (Element) SerialList2.item(0);
		String ParentSerialKey = PSerial.getAttribute("ParentSerialKey");
		logger.verbose("@@@@@@@@@@ ParentSerialKey :: " + ParentSerialKey);
		String SecSerial = PSerial.getAttribute("SecondarySerial1");
		logger.verbose("@@@@@@@@@@ SecSerial :: " + SecSerial);
		Element TSerial = (Element) getSerialList_Out.getDocumentElement().getElementsByTagName("TagDetail").item(0);
		
		getSerialList_Out = XMLUtil.newDocument();
		Element ResultEl = getSerialList_Out.createElement("Result");
		getSerialList_Out.appendChild(ResultEl);
		ResultEl.setAttribute("SerialPresent", "valid");
		ResultEl.setAttribute("ParentSerialKey", ParentSerialKey);
		ResultEl.setAttribute("SecondarySerial1", SecSerial);
		Element TElem = getSerialList_Out.createElement("TagDetail");
		if (TSerial != null)
			XMLUtil.copyElement(getSerialList_Out, TSerial, TElem);
		ResultEl.appendChild(TElem);

		logger.verbose("@@@@@@@@@@ BEFORE LAST RETURN :: getSerialList_Out XML : " + XMLUtil.extractStringFromDocument(getSerialList_Out));
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::verfiySerialList");
		return getSerialList_Out;
	}

	// ------------------callGetItemDetails--
	// This method is to check if the serial is at node
	public Document callgetSerialList(YFSEnvironment env, Document inXML)
			throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::callgetSerialList");
		Document SerialListDoc_out = null;
		Document SerialList_Template = null;
		logger.verbose("Entering callgetSerialList method in NWCGisSerialUniqueInAllNodes API");

		// ---------------------Template
		SerialList_Template = XMLUtil.newDocument();

		Element Result = SerialList_Template.createElement("Result");
		SerialList_Template.appendChild(Result);

		logger.verbose("@@@@@ The input xml for getSerialList[check if the serial is at node] is:- "
				+ XMLUtil.extractStringFromDocument(inXML));
		// logger.verbose("Input Serial XML "+
		// XMLUtil.extractStringFromDocument(inXML));
		Element SerialEl = inXML.getDocumentElement();
		SerialEl.setAttribute("AtNode", "Y");
		logger.verbose("@@@@@ getSerialList after setting AtNode :- " + XMLUtil.extractStringFromDocument(inXML));
		SerialListDoc_out = CommonUtilities.invokeAPI(env, "getSerialList", inXML);
		// logger.verbose("Output Serial XML "+
		// XMLUtil.extractStringFromDocument(SerialListDoc_out));
		logger.verbose("@@@@@ The output xml from getSerialList is:- "
				+ XMLUtil.extractStringFromDocument(SerialListDoc_out));
		// GS - check SerialListDoc_out!=null
		NodeList SerialList = SerialListDoc_out.getDocumentElement()
				.getElementsByTagName("Serial");
		logger.verbose("@@@@@ node length:- " + SerialList.getLength());
		// GS - add if(SerialList!=null)
		int nodeCount = SerialList.getLength();

		if (nodeCount > 0) {
			Result.setAttribute("SerialPresent", "True");// serial is present in
															// node--cannot be
															// received again
		} else {
			Result.setAttribute("SerialPresent", "False"); // Means that the
															// serialID is
															// unique[among
															// those present in
															// node]
		}// End IF condition
		logger.verbose("The RETURN xml from callgetSerialList method is:-"
				+ XMLUtil.extractStringFromDocument(SerialList_Template));
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::callgetSerialList");
		return SerialList_Template;
	}// End callgetSerialList()

	public Document verifyIncident(YFSEnvironment env, String IncidentNo,
			String IncidentYear, String IssueNo, String SerialNo,
			String ParentSerialKey) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::verifyIncident");
		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();
		Document outDoc1 = XMLUtil.newDocument();
		Document SerialList_Template = XMLUtil.newDocument();

		Element el_NWCGIncidentReturn = inDoc
				.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		if (IncidentNo.length() > 0) {
			el_NWCGIncidentReturn.setAttribute("IncidentNo", IncidentNo);
		}

		if (IncidentYear.length() > 0) {
			el_NWCGIncidentReturn.setAttribute("IncidentYear", IncidentYear);
		}

		if (IssueNo.length() > 0) {
			el_NWCGIncidentReturn.setAttribute("IssueNo", IssueNo);
		}

		el_NWCGIncidentReturn.setAttribute("TrackableID", SerialNo);
		el_NWCGIncidentReturn.setAttribute("QuantityReturned", "0");
		// el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","N");

		// logger.verbose("Input xml for querying the table is:-"+XMLUtil.extractStringFromDocument(inDoc));

		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE, inDoc);

		// logger.verbose("Output xml after querying the table is:-"+XMLUtil.extractStringFromDocument(outDoc));

		Element NWCGIncidentReturn = (Element) XPathUtil.getNode(
				outDoc.getDocumentElement(), "./NWCGIncidentReturn");

		if (NWCGIncidentReturn == null) {
			if (ParentSerialKey.length() > 0) {
				String PSerialNo = getParentSerialNo(env, ParentSerialKey);
				el_NWCGIncidentReturn.setAttribute("TrackableID", PSerialNo);
				outDoc = CommonUtilities
						.invokeService(
								env,
								NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,
								inDoc);
				Element NWCGIncidentReturnP = (Element) XPathUtil.getNode(
						outDoc.getDocumentElement(), "./NWCGIncidentReturn");

				if (NWCGIncidentReturnP == null) {
					Element Result = SerialList_Template
							.createElement("Result");
					SerialList_Template.appendChild(Result);
					Result.setAttribute("SerialPresent", "NotInIncident");
				} else {
					Element Result = SerialList_Template
							.createElement("Result");
					SerialList_Template.appendChild(Result);
					Result.setAttribute("SerialPresent", "False");
				}
			} else {
				Element Result = SerialList_Template.createElement("Result");
				SerialList_Template.appendChild(Result);
				Result.setAttribute("SerialPresent", "NotInIncident");
			}
		} else {
			Element Result = SerialList_Template.createElement("Result");
			SerialList_Template.appendChild(Result);
			Result.setAttribute("SerialPresent", "False");
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::verifyIncident");
		return SerialList_Template;
	}

	public String getParentSerialNo(YFSEnvironment env, String ParentSerialKey)
			throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getParentSerialNo");
		String PSerialNo = NWCGConstants.EMPTY_STRING;
		String PSerialKey1 = NWCGConstants.EMPTY_STRING;
		String PSerialKey2 = NWCGConstants.EMPTY_STRING;

		Document inDoc = XMLUtil.newDocument();
		Document outDoc = XMLUtil.newDocument();

		Element el_PSerial = inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("GlobalSerialKey", ParentSerialKey);
		outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

		Element PSerialOut = (Element) outDoc.getDocumentElement()
				.getElementsByTagName("Serial").item(0);
		PSerialNo = PSerialOut.getAttribute("SerialNo");
		PSerialKey1 = PSerialOut.getAttribute("ParentSerialKey");

		if (PSerialKey1.length() > 0) {
			el_PSerial.setAttribute("GlobalSerialKey", PSerialKey1);
			outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

			PSerialOut = (Element) outDoc.getDocumentElement()
					.getElementsByTagName("Serial").item(0);
			PSerialNo = PSerialOut.getAttribute("SerialNo");
			PSerialKey2 = PSerialOut.getAttribute("ParentSerialKey");
			if (PSerialKey2.length() > 0) {
				el_PSerial.setAttribute("GlobalSerialKey", PSerialKey2);
				outDoc = CommonUtilities.invokeAPI(env, "getSerialList", inDoc);

				PSerialOut = (Element) outDoc.getDocumentElement()
						.getElementsByTagName("Serial").item(0);
				PSerialNo = PSerialOut.getAttribute("SerialNo");
			}
		}
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getParentSerialNo");
		return PSerialNo;
	}

	public int getTrackablecnt(YFSEnvironment env, String SerialNo,
			String SecSerialNo, String IncidentNo, String IncidentYear,
			String ItemID) throws Exception {
		logger.verbose("@@@@@ Entering com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getTrackablecnt");
		Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackable = trackableDoc.getDocumentElement();
		Document outDoc = XMLUtil.newDocument();

		elemTrackable.setAttribute("SerialNo", SerialNo);
		elemTrackable.setAttribute("SecondarySerial", SecSerialNo);
		elemTrackable.setAttribute("SerialStatus",
				NWCGConstants.SERIAL_STATUS_ISSUED);
		elemTrackable.setAttribute("StatusIncidentNo", IncidentNo);
		elemTrackable.setAttribute("StatusIncidentYear", IncidentYear);
		elemTrackable.setAttribute("ItemID", ItemID);

		outDoc = CommonUtilities.invokeService(env,
				NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,
				trackableDoc);

		Element elemTrackableListRoot = outDoc.getDocumentElement();
		NodeList TrackableList = elemTrackableListRoot
				.getElementsByTagName("NWCGTrackableItem");
		logger.verbose("@@@@@ Exiting com.nwcg.icbs.yantra.api.returns.NWCGisSerialUniqueInAllNodes::getTrackablecnt");
		return TrackableList.getLength();
	}
}