

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


import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGValidateReturnNo implements YIFCustomApi {
	
	private Properties _properties;
	private static Logger log = Logger.getLogger(NWCGValidateReturnNo.class.getName());
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		_properties = arg0;
	}
	    
	//Entry point method	
	public Document ValidateReturnNo(YFSEnvironment env, Document inXML) throws Exception {
		
	   	if(log.isVerboseEnabled()){
    		log.verbose("Entering the NWCGValidateReturnNo API , input document is:"+XMLUtil.getXMLString(inXML));
		}//End log
	   	
	   	//System.out.println("Validate Return No Input XML "+ XMLUtil.getXMLString(inXML));
	   	  
	   	Document ResultDoc = null;
	   	Element Result = null;
	   	try{
	   	 	ResultDoc = XMLUtil.newDocument();
			Result=ResultDoc.createElement("Result");
	        ResultDoc.appendChild(Result);
	        String SeqNoStr = "";
	    	Result.setAttribute("ValidReturnNo","False");
	        //String OrgID = inXML.getDocumentElement().getAttribute("OrganizationID");
	        String SeqNo = inXML.getDocumentElement().getAttribute("SequenceNo");
	        String SeqType = inXML.getDocumentElement().getAttribute("SequenceType");
	        SeqNo = SeqNo.trim();
	        	        
	        int orglen = 5;
	        int seqlen = 11;
	        if ( SeqNo.length() != seqlen )
	        	return ResultDoc;
	        
	        String OrgID = SeqNo.substring(0,5);
	        //System.out.println("OrgId "+ OrgID);
	        //System.out.println("SeqNo Len"+ SeqNo.length());

	        int Seqlen = SeqNo.length();
			int pos = Seqlen - 6;
			SeqNoStr = SeqNo.substring(pos);

			if (!validateNumber(SeqNoStr)) 
	        	return ResultDoc;

			int UserSeq = Integer.parseInt(SeqNoStr);
			
			String CurrentSeqNo = GetSeqNo(env,OrgID);

			if (CurrentSeqNo.length() == 0) 
	        	return ResultDoc;
			
			// Need to check existing Return No
			int rcnt = GetReceiptList(env,SeqNo);
			if (rcnt > 0)
			{
				Result.setAttribute("ValidReturnNo","Received");
				return ResultDoc;
			}
			
			pos = CurrentSeqNo.length() - 6;
			SeqNoStr = CurrentSeqNo.substring(pos);
			int CurrentSeq = Integer.parseInt(SeqNoStr);

			if (UserSeq > CurrentSeq)
				return ResultDoc;
	   	  }
	   	 catch(ParserConfigurationException pce){
			log.error("NWCGValidateReturnNo::, ParserConfigurationException Msg : " + pce.getMessage());
			log.error("NWCGValidateReturnNo::, StackTrace : " + pce.getStackTrace());
		 }
	   	 catch(Exception e)
	   	 {
	   		log.error("NWCGValidateReturnNo::Exception Msg : " + e.getMessage());
			log.error("NWCGValidateReturnNo::StackTrace : " + e.getStackTrace());
	   	 }
	   	Result.setAttribute("ValidReturnNo","True");
		return ResultDoc;
	 }
	
	public String GetSeqNo(YFSEnvironment env, String CacheID){
		String SeqNo = "";
		String Cache = "";
		
		Document SeqDtls = null;
		Document getSeqInput = null;
		try {
			getSeqInput = XMLUtil.createDocument("NWCGSequence");
			getSeqInput.getDocumentElement().setAttribute("SequenceType","RETURN");
			getSeqInput.getDocumentElement().setAttribute("OrganizationID",CacheID);
			SeqDtls = CommonUtilities.invokeService(env, "NWCGGetSequenceListService", getSeqInput);
	        //System.out.println("Seq Detail " + XMLUtil.getXMLString(SeqDtls));
		}
		catch(Exception e){
			log.error("NWCGValidateReturnNo::GetSeqNo, Exception Msg : " + e.getMessage());
			log.error("NWCGValidateReturnNo::GetSeqNo, StackTrace : " + e.getStackTrace());
		}
		
		NodeList SeqElmList = SeqDtls.getDocumentElement().getElementsByTagName("NWCGSequence");
		
		if (SeqElmList.getLength() > 0)
		{
			Element rootElem  = (Element) SeqElmList.item(0);
			SeqNo = rootElem.getAttribute("SequenceNo");
			Cache = rootElem.getAttribute("OrganizationID");
			if (!Cache.equals(CacheID))
			{
			  SeqNo="";
			  return SeqNo;
			} 
    	}

		return SeqNo;
	}	
	
	public int GetReceiptList(YFSEnvironment env, String ReceiptNo){
		int retcnt = 0;
		
		Document ReceiptDtls = null;
		Document ReceiptInput = null;
		try {
			ReceiptInput = XMLUtil.createDocument("Receipt");
			ReceiptInput.getDocumentElement().setAttribute("ReceiptNo",ReceiptNo);
			ReceiptDtls = CommonUtilities.invokeAPI(env,"getReceiptList", ReceiptInput);
	        //System.out.println("Receipt List " + XMLUtil.getXMLString(ReceiptDtls));
		}
		catch(Exception e){
			log.error("NWCGValidateReturnNo::GetReceiptList, Exception Msg : " + e.getMessage());
			log.error("NWCGValidateReturnNo::GetReceiptList, StackTrace : " + e.getStackTrace());
		}
		
		NodeList ReceiptElmList = ReceiptDtls.getDocumentElement().getElementsByTagName("Receipt");
		retcnt = ReceiptElmList.getLength();
		//System.out.println("Return Count "+retcnt);
				
		return retcnt;
	}	
	
	public static boolean validateNumber(String num) {
        try {
            Integer.parseInt(num);
            return true;
        } catch (Exception e) {
            return false;
        }
    }	
}//End Class
	   
	
    
    


