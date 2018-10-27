package com.nwcg.icbs.yantra.api.returns;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author Gomathi
 *
 */
public class NWCGGenerateReceiptNo implements YIFCustomApi {
	
	private Properties _properties;
	private static Logger log = Logger.getLogger(NWCGGenerateReceiptNo.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}

	public Document getReceiptNo(YFSEnvironment env, Document inXML)
			throws Exception {
		//System.out.println("NWCGGenerateReceiptNo::getReceiptNo:Generate Return No inXML "+ XMLUtil.getXMLString(inXML));
		String OrgID = inXML.getDocumentElement().getAttribute("CacheID");
		String SeqType = inXML.getDocumentElement().getAttribute("SequenceType");
		
//Begin CR844 11302012
                            //String ReceiptNo = GenerateSeqNo(env,OrgID,SeqType);
                            int OrgIDlength = OrgID.length();
		String ReceiptNo = "";
                            if(OrgIDlength > 0) { 
                                ReceiptNo = GenerateSeqNo(env, OrgID, SeqType);
                                //System.out.println("NWCGGenerateReceiptNo::getReceiptNo::ReceiptNo "+ReceiptNo);
                                                                
                                String strFirstCharInReceiptNo = ReceiptNo.substring(0,1);
                                if(strFirstCharInReceiptNo.equals("-")) {
                                    System.out.println("NWCGGenerateReceiptNo::getReceiptNo:: Exception:Generated ReceiptNo " + ReceiptNo + " should not start with a -");
                                    return null;
                                } 

                                String strPrefixInReceiptNo = ReceiptNo.substring(0, OrgIDlength);
                                if(!strPrefixInReceiptNo.equals(OrgID)) {
                                    System.out.println("NWCGGenerateReceiptNo::getReceiptNo:: Exception:Prefix in generated ReceiptNo " + ReceiptNo +" does not match OrgID " + OrgID);
                                    return null;                             
                                }        
                            }
//End CR844 11302012

	    Document UpdDoc = UpdateSeqNo(env,OrgID,ReceiptNo,SeqType);
	    Document Receipt_Output = XMLUtil.newDocument();
	    Element el_Receipt=Receipt_Output.createElement("NextReceiptNo");
	    Receipt_Output.appendChild(el_Receipt);
	    el_Receipt.setAttribute("ReceiptNo",ReceiptNo);
		
	    //System.out.println("NWCGGenerateReceiptNo::getReceiptNo:: Receipt_Output="+XMLUtil.getXMLString(Receipt_Output));
	    return Receipt_Output;
	}

	public String GenerateSeqNo(YFSEnvironment env, String CacheID,String SeqType){
		String SeqNo = "";
		String SeqNoStr = "";
		
		Document SeqDtls = null;
		Document getSeqInput = null;
		try {
			getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",SeqType);
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",CacheID);
			SeqDtls = CommonUtilities.invokeService(env, "NWCGGetSequenceListService", getSeqInput);
	                            //System.out.println("NWCGGenerateReceiptNo::GenerateSeqNo:Seq Detail " + XMLUtil.getXMLString(SeqDtls));
		}
		catch(ParserConfigurationException pce){
			log.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo Msg : " + pce.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo : " + pce.getStackTrace());
		}
		catch(Exception e){
			log.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
		}
		
		NodeList SeqElmList = SeqDtls.getDocumentElement().getElementsByTagName("NWCGSequence");
		
		if (SeqElmList.getLength() > 0)
		{
			Element rootElem  = (Element) SeqElmList.item(0);
			SeqNo = rootElem.getAttribute("SequenceNo");
			int Seqlen = SeqNo.length();
			int pos = Seqlen - 6;
			SeqNoStr = SeqNo.substring(pos);
			int CurrentSeq = Integer.parseInt(SeqNoStr);
			CurrentSeq++;
			SeqNoStr = Integer.toString(CurrentSeq);
			if(SeqType.equalsIgnoreCase("LPN"))
			{
				SeqNoStr = CacheID + SeqType+StringUtil.prepadStringWithZeros(SeqNoStr,6);
			}
			else
			{
				SeqNoStr = CacheID + StringUtil.prepadStringWithZeros(SeqNoStr,6);
			}
			//System.out.println("NWCGGenerateReceiptNo::GenerateSeqNo: SeqNoStr " + SeqNoStr);
			
		}else
		{
			int seqNo = 1;
			SeqNoStr = Integer.toString(seqNo);
			if(SeqType.equalsIgnoreCase("LPN"))
			{
				SeqNoStr = CacheID + SeqType+StringUtil.prepadStringWithZeros(SeqNoStr,6);
			}
			else
			{
				SeqNoStr = CacheID + StringUtil.prepadStringWithZeros(SeqNoStr,6);
			}
			getSeqInput.getDocumentElement().setAttribute("SequenceNo",SeqNoStr);
			try 
			{
			  SeqDtls = CommonUtilities.invokeService(env, "NWCGCreateSequenceListService", getSeqInput);
			}
			catch(Exception e){
				log.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
				log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
			}
		}
		
		
		return SeqNoStr;
	}

	public Document UpdateSeqNo(YFSEnvironment env, String CacheID,String ReturnNo,String SeqType){
		String SeqNo = "";
		String SeqNoStr = "";
				
		Document SeqDtls = null;
		//System.out.println("In Update Seq No ");
		try {
			Document getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType",SeqType);
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",CacheID);
			getSeqInput.getDocumentElement().setAttribute("SequenceNo",ReturnNo);
			//System.out.println("Update XML " + XMLUtil.getXMLString(getSeqInput));
			SeqDtls = CommonUtilities.invokeService(env, "NWCGUpdateSequenceListService", getSeqInput);
	        //System.out.println("Seq Doc " + XMLUtil.getXMLString(SeqDtls));
		}
		catch(ParserConfigurationException pce){
			log.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo Msg : " + pce.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, GenerateSeqNo : " + pce.getStackTrace());
		}
		catch(Exception e){
			log.error("NWCGReportsIssue::getIncidentDetails, Exception Msg : " + e.getMessage());
			log.error("NWCGReportsIssue::getIncidentDetails, StackTrace : " + e.getStackTrace());
		}
		return SeqDtls;

	}
}
