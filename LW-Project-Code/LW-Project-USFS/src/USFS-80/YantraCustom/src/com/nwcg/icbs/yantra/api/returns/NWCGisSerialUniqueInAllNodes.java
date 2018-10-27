/*
 * ClearContextAPI.java
 *
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */ 

/*Author :- Suresh Pillai*/

package com.nwcg.icbs.yantra.api.returns;

//Gomathi - Please make appropriate changes wherever comment "//GS" is present
// 			also please change all the methods escept entry method to private.

import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.exception.common.NWCGException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class NWCGisSerialUniqueInAllNodes implements YIFCustomApi {
    	
	private Properties myProperties = null;
	private static Logger logger = Logger.getLogger();
	
	public void setProperties(Properties arg0) throws Exception {
		this.myProperties = arg0;
		if (logger.isDebugEnabled()) logger.debug("Set properties"+this.myProperties);
	}
	    
    String IsSerialTracked = NWCGConstants.EMPTY_STRING;
    String TagControlFlag  = NWCGConstants.EMPTY_STRING;
    String ShortDescription = NWCGConstants.EMPTY_STRING;
    String TimeSensitive    = NWCGConstants.EMPTY_STRING;
    
    //Entry point method	
    public Document getSerialDetails(YFSEnvironment env, Document inXML) throws TransformerException, YFSException
    {
    	//Document returnDoc =null;
    	Document getSerialList_Out = null;
    	Document verify_out = null;
    	Document verify_incident_serial = null;
    	Element TSerial = null;

    	logger.verbose("Entering the NWCGisSerialUniqueInAllNodes API , input document is:"+XMLUtil.extractStringFromDocument(inXML));

        try {
	        logger.verbose("****Inside IsSerialUnique API (passOnForVerification)******* :- "+ XMLUtil.extractStringFromDocument(inXML));
	        //FLOW-----------------
	        //First call getitemList using the itemID and calling organizationCode and get the item details like PC
	        //PC and UOM 
	        //Then call getitemdetails to get the component and other required information.
	        
	        NodeList InvItemList 		= inXML.getDocumentElement().getElementsByTagName("InventoryItem");

	        //GS - check InvItemList!=null && InvItemList.getLength()>0
	        Element InvAttEl 			= (Element)InvItemList.item(0);
	        String ItemID 				= InvAttEl.getAttribute("ItemID");
	        String SerialNo	= inXML.getDocumentElement().getAttribute("SerialNo");
	    	String IncidentNo = inXML.getDocumentElement().getAttribute("IncidentNo");
	    	String IncidentYear = inXML.getDocumentElement().getAttribute("IncidentYear");
	    	String IssueNo = inXML.getDocumentElement().getAttribute("IssueNo");
            
			logger.debug("test ItemID:-"+ItemID+"SerialNo:-"+SerialNo);
			
			//if(ItemID==""||ItemID==null||SerialNo==null || SerialNo==""){
			//Changed the line after code review 
			if(ItemID.equals(NWCGConstants.EMPTY_STRING)||ItemID==null||SerialNo==null || SerialNo.equals(NWCGConstants.EMPTY_STRING)){
				getSerialList_Out = XMLUtil.newDocument();

		    	Element Result=getSerialList_Out.createElement("Result");
		    	getSerialList_Out.appendChild(Result);
		    	Result.setAttribute("SerialPresent","Invalid Input");
		    	return getSerialList_Out;
			}//End IF

			verify_out = verfiySerialList(env,inXML);
			logger.verbose("Verify Output XML : " + XMLUtil.extractStringFromDocument(verify_out));
			Element verifyElm = verify_out.getDocumentElement();
			String SecSerial = verifyElm.getAttribute("SecondarySerial1");
			TSerial = (Element) verify_out.getDocumentElement().getElementsByTagName("TagDetail").item(0);
			String serialValid = verifyElm.getAttribute("SerialPresent");
			
			int trackablecnt = 0;
    	    trackablecnt = getTrackablecnt(env,SerialNo,SecSerial,IncidentNo,IncidentYear,ItemID);
    	    
			if (!serialValid.equals("valid") )
			{
				return verify_out;
			}
			String ParentSerialKey = verifyElm.getAttribute("ParentSerialKey");
			getSerialList_Out = callgetSerialList(env,inXML);
			logger.verbose("getSerialList_Out XML : " + XMLUtil.extractStringFromDocument(getSerialList_Out));
			Element verifyElm1 = getSerialList_Out.getDocumentElement();
			String serialValid1 = verifyElm1.getAttribute("SerialPresent");
			if (serialValid1.equals("True"))
			{
				return getSerialList_Out;
			}
			logger.debug("trackablecnt : " + trackablecnt);
			verify_incident_serial = XMLUtil.newDocument();
			if (trackablecnt == 0)
			{
				Element IncidentResult=verify_incident_serial.createElement("Result");
				verify_incident_serial.appendChild(IncidentResult);
				IncidentResult.setAttribute("SerialPresent","NotInIncident");
				IncidentResult.setAttribute("SecondarySerial1",SecSerial);
				Element root1 = verify_incident_serial.getDocumentElement();
                Element TElem1 = verify_incident_serial.createElement("TagDetail");
                if (TSerial != null)
			       XMLUtil.copyElement(verify_incident_serial,TSerial,TElem1);
                root1.appendChild(TElem1);
			}
			else
			{
				Element IncidentResult=verify_incident_serial.createElement("Result");
				verify_incident_serial.appendChild(IncidentResult);
				IncidentResult.setAttribute("SerialPresent","False");
				IncidentResult.setAttribute("SecondarySerial1",SecSerial);
				Element root1 = verify_incident_serial.getDocumentElement();
                Element TElem1 = verify_incident_serial.createElement("TagDetail");
                root1.appendChild(TElem1);
                if (TSerial != null)
			       XMLUtil.copyElement(verify_incident_serial,TSerial,TElem1);
			}

		    logger.verbose("Serial Output XML : " + XMLUtil.extractStringFromDocument(verify_incident_serial));
	    	logger.verbose("Exiting the NWCGisSerialUniqueInAllNodes API , output document is:"+XMLUtil.extractStringFromDocument(getSerialList_Out));
        }
        catch(Exception E){
	    	 logger.error("Generated exception "+E.toString());
	    	 throw new NWCGException("NWCG_RETURN_INPUT_VALUES_ERROR");
        }//End Try catch
        return verify_incident_serial;
        //return getSerialList_Out;
    }//End Function getDetails
	
    //This method is to make sure that the serial is is valid	
    public Document verfiySerialList(YFSEnvironment env,Document inXML)throws Exception{
    	Document getSerialList_Out = null;
   		logger.debug("Entering verfiySerialList method in NWCGisSerialUniqueInAllNodes API");
    	
    	//----------call the same api by removing the 'Y' from AtNode attribute it with AtNode=""
    	//-------It any results are obtained the the serial is valid.
    	logger.verbose("Input xml----passOnForVerification:-"+XMLUtil.extractStringFromDocument(inXML));
    	Element SerialEl = inXML.getDocumentElement();
    	SerialEl.setAttribute("AtNode",NWCGConstants.EMPTY_STRING);
    	logger.verbose("The input xml for getSerialList[check if the serial iis VALID] is:- "+XMLUtil.extractStringFromDocument(inXML));
    	//logger.verbose("getSerial Input XML : " + XMLUtil.extractStringFromDocument(inXML));
    	getSerialList_Out = CommonUtilities.invokeAPI(env,"getSerialList",inXML);
    	//logger.verbose("getSerial Output XML : " + XMLUtil.extractStringFromDocument(getSerialList_Out));
    	//GS - check if getSerialList_Out!=null
    	NodeList SerialList2 = getSerialList_Out.getDocumentElement().getElementsByTagName("Serial");
    	    	
    	//GS - get length after checking .. ie. add if(SerialList2!=null)
    	int nodeCount2 = SerialList2.getLength();
    	
    	if(nodeCount2==0){
    		logger.debug("No records found");
    		getSerialList_Out = XMLUtil.newDocument();

	    	Element ResultEl=getSerialList_Out.createElement("Result");
	    	getSerialList_Out.appendChild(ResultEl);
	    	ResultEl.setAttribute("SerialPresent","Invalid Input");
	    	return getSerialList_Out;
    	}//End IF 
    	Element PSerial = (Element) SerialList2.item(0);
    	String ParentSerialKey = PSerial.getAttribute("ParentSerialKey");
    	String SecSerial = PSerial.getAttribute("SecondarySerial1");
    	Element TSerial = (Element) getSerialList_Out.getDocumentElement().getElementsByTagName("TagDetail").item(0);
    	getSerialList_Out = XMLUtil.newDocument();
    	Element ResultEl=getSerialList_Out.createElement("Result");
    	getSerialList_Out.appendChild(ResultEl);
    	ResultEl.setAttribute("SerialPresent","valid");
    	ResultEl.setAttribute("ParentSerialKey",ParentSerialKey);
    	ResultEl.setAttribute("SecondarySerial1",SecSerial);
        Element TElem = getSerialList_Out.createElement("TagDetail");
        if (TSerial != null)
          XMLUtil.copyElement(getSerialList_Out,TSerial,TElem);

        ResultEl.appendChild(TElem);
        
    	logger.debug("Exiting verfiySerialList method in NWCGisSerialUniqueInAllNodes API");
    	return getSerialList_Out;    	
    	//------------End verifying that the serialNum exists in the system
    }//End verfiySerialList
    
    //------------------callGetItemDetails--
    //This method is to check if the serial is at node
    public Document callgetSerialList(YFSEnvironment env,Document inXML) throws Exception {
    	Document SerialListDoc_out = null;
    	Document SerialList_Template = null;
    	logger.debug("Entering callgetSerialList method in NWCGisSerialUniqueInAllNodes API");

    	//---------------------Template
    	SerialList_Template = XMLUtil.newDocument();

    	Element Result=SerialList_Template.createElement("Result");
    	SerialList_Template.appendChild(Result);
    	
    	logger.verbose("The input xml for getSerialList[check if the serial is at node] is:- "+XMLUtil.extractStringFromDocument(inXML));
    	//logger.verbose("Input Serial XML "+ XMLUtil.extractStringFromDocument(inXML));
    	Element SerialEl = inXML.getDocumentElement();
    	SerialEl.setAttribute("AtNode","Y");
    	SerialListDoc_out = CommonUtilities.invokeAPI(env,"getSerialList",inXML);
    	//logger.verbose("Output Serial XML "+ XMLUtil.extractStringFromDocument(SerialListDoc_out));
    	logger.verbose("The output xml from getSerialList is:- "+XMLUtil.extractStringFromDocument(SerialListDoc_out));
    	//GS - check SerialListDoc_out!=null 
    	NodeList SerialList = SerialListDoc_out.getDocumentElement().getElementsByTagName("Serial");
    	logger.debug("node length:- "+SerialList.getLength());
    	//GS - add if(SerialList!=null)
    	int nodeCount = SerialList.getLength();
    	
    	if(nodeCount>0){
    		Result.setAttribute("SerialPresent","True");//serial is present in node--cannot be received again
    	}else{
    		Result.setAttribute("SerialPresent","False"); //Means that the serialID is unique[among those present in node]
    	}//End IF condition
    	logger.verbose("The RETURN xml from callgetSerialList method is:-"+XMLUtil.extractStringFromDocument(SerialList_Template));
   		logger.debug("Exiting callgetSerialList method in NWCGisSerialUniqueInAllNodes API");    	
	    return SerialList_Template;
    }//End callgetSerialList()
    
    public Document verifyIncident (YFSEnvironment env, String IncidentNo, String IncidentYear, String IssueNo, String SerialNo,String ParentSerialKey) throws Exception {
    	Document inDoc = XMLUtil.newDocument();
    	Document outDoc = XMLUtil.newDocument();
    	Document outDoc1 = XMLUtil.newDocument();
    	Document SerialList_Template = XMLUtil.newDocument();
		
		Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		inDoc.appendChild(el_NWCGIncidentReturn);
		if (IncidentNo.length() > 0)
		{
		  el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
		}
		
		if (IncidentYear.length() > 0)
		{
		  el_NWCGIncidentReturn.setAttribute("IncidentYear",IncidentYear);
		} 
		
		if (IssueNo.length() > 0)
		{
		  el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
		}
		
		el_NWCGIncidentReturn.setAttribute("TrackableID",SerialNo);
		el_NWCGIncidentReturn.setAttribute("QuantityReturned","0");
		//el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","N");

		//logger.verbose("Input xml for querying the table is:-"+XMLUtil.extractStringFromDocument(inDoc));

		outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,inDoc);
		
		//logger.verbose("Output xml after querying the table is:-"+XMLUtil.extractStringFromDocument(outDoc));
		
		Element NWCGIncidentReturn = (Element)XPathUtil.getNode(outDoc.getDocumentElement(),"./NWCGIncidentReturn");
  		   		
		if(NWCGIncidentReturn==null)
		{
			if (ParentSerialKey.length() > 0)
			{
				String PSerialNo = getParentSerialNo(env,ParentSerialKey);
				el_NWCGIncidentReturn.setAttribute("TrackableID",PSerialNo);
				outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,inDoc);
				Element NWCGIncidentReturnP = (Element)XPathUtil.getNode(outDoc.getDocumentElement(),"./NWCGIncidentReturn");
				
				if (NWCGIncidentReturnP==null)
				{
	    			Element Result=SerialList_Template.createElement("Result");
	    	    	SerialList_Template.appendChild(Result);
	    	    	Result.setAttribute("SerialPresent","NotInIncident");
				}
				else
				{
		    		Element Result=SerialList_Template.createElement("Result");
			    	SerialList_Template.appendChild(Result);
			        Result.setAttribute("SerialPresent","False");
				}
			}
			else
			{
				Element Result=SerialList_Template.createElement("Result");
    	    	SerialList_Template.appendChild(Result);
    	    	Result.setAttribute("SerialPresent","NotInIncident");
			}
		}
		else
		{
		   Element Result=SerialList_Template.createElement("Result");
    	   SerialList_Template.appendChild(Result);
           Result.setAttribute("SerialPresent","False");
		}
		return SerialList_Template;
    }
	    
    public String getParentSerialNo(YFSEnvironment env,String ParentSerialKey)throws Exception{
    	
    	String PSerialNo = NWCGConstants.EMPTY_STRING;
    	String PSerialKey1 = NWCGConstants.EMPTY_STRING;
    	String PSerialKey2 = NWCGConstants.EMPTY_STRING;
    	
    	Document inDoc = XMLUtil.newDocument();
    	Document outDoc = XMLUtil.newDocument();

		Element el_PSerial=inDoc.createElement("Serial");
		inDoc.appendChild(el_PSerial);
		el_PSerial.setAttribute("GlobalSerialKey",ParentSerialKey);
		outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
		
		Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
    	PSerialNo = PSerialOut.getAttribute("SerialNo");
    	PSerialKey1 = PSerialOut.getAttribute("ParentSerialKey");
    	
    	if (PSerialKey1.length() > 0)
    	{
    	  el_PSerial.setAttribute("GlobalSerialKey",PSerialKey1);
		  outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
		
		  PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
    	  PSerialNo = PSerialOut.getAttribute("SerialNo");
    	  PSerialKey2 = PSerialOut.getAttribute("ParentSerialKey");
    	  if (PSerialKey2.length() > 0)
	    	{
	    	  el_PSerial.setAttribute("GlobalSerialKey",PSerialKey2);
 			  outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
 			
 			  PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
	    	  PSerialNo = PSerialOut.getAttribute("SerialNo");
	    	} 
    	}
        return PSerialNo;
    }
	    
    public int getTrackablecnt(YFSEnvironment env,String SerialNo,String SecSerialNo,String IncidentNo,String IncidentYear,String ItemID)throws Exception{
    	
      	Document trackableDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element elemTrackable = trackableDoc.getDocumentElement();
		Document outDoc = XMLUtil.newDocument();
		
		elemTrackable.setAttribute("SerialNo",SerialNo);
		elemTrackable.setAttribute("SecondarySerial",SecSerialNo);
		elemTrackable.setAttribute("SerialStatus",NWCGConstants.SERIAL_STATUS_ISSUED);
		elemTrackable.setAttribute("StatusIncidentNo",IncidentNo);
		elemTrackable.setAttribute("StatusIncidentYear",IncidentYear);
		elemTrackable.setAttribute("ItemID",ItemID);
		
		outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,trackableDoc);
		
		Element elemTrackableListRoot = outDoc.getDocumentElement();
		NodeList TrackableList = elemTrackableListRoot.getElementsByTagName("NWCGTrackableItem");

		return TrackableList.getLength();
    }
}