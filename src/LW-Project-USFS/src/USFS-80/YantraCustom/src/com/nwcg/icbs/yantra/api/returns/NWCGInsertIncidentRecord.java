package com.nwcg.icbs.yantra.api.returns;

//Gomathi - please make the appropriate changes wherever comment "//GS" is present
// Please also change the methods to private other than the entry method

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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


public class NWCGInsertIncidentRecord implements YIFCustomApi{

			 int shipmentLineCount = 0;
			 int ShipmentTagSerialCount = 0;
			 List serialList = new ArrayList();
			 //private static Logger logger = Logger.getLogger(NWCGgetReservedItems.class.getName());
			 
		      /**
			  * Logger Instance.
			  */
			private static Logger log = Logger.getLogger(NWCGInsertIncidentRecord.class.getName());

			private Properties props = null;

			public void setProperties(Properties props) throws Exception {
				this.props = props;
			}//setProperties

		     
		    public Document insertRecord(YFSEnvironment env, Document inXML) throws Exception
		    {
		    	
		    	if(log.isVerboseEnabled()){
		    		log.verbose("Entering the NWCGInsertIncidentRecord , input document is:"+XMLUtil.getXMLString(inXML));
				}//End log

		    	//System.out.println("In XML:- "+XMLUtil.getXMLString(inXML));
		        log.debug("Start of NWCGInsertIncidentRecord");
		        if (null == env)
		        {
		            log.error("YFSEnvironment is null");
		            throw new NWCGException("NWCG_RETURN_ENV_IS_NULL");
		        }
		        if (null == inXML)
		        {
		            log.error("Input Document is null");
		            throw new NWCGException("NWCG_RETURN_INPUT_DOC_NULL");
		        }
		        
		        if (log.isVerboseEnabled()) {
					log.verbose("checkForIssuesIncidentDelete()->inDoc:"+ XMLUtil.getXMLString(inXML));
				}
		    
		        String issueDate = inXML.getDocumentElement().getAttribute("ActualShipmentDate");
		        		        
		        NodeList ShipmentLines = inXML.getDocumentElement().getElementsByTagName("ShipmentLine");
		        
		        //GS - before checking the length.. check if the list is not equal to null.. ie ShipmentLines!=null
		        shipmentLineCount = ShipmentLines.getLength();
		        
		        //System.out.println("The inxml has lines :"+shipmentLineCount);
		        
		        //GS - also check ShipmentLines!=null
		        if(shipmentLineCount>0){
			        for(int cnt=0;cnt<shipmentLineCount;cnt++){
			        		
			        	//check if the item is serially tracked
			        	Element shipmentLineEl = (Element)ShipmentLines.item(cnt);
			        	Element ShipmentTagSerialsEL = null;
			        	//get the serialNo
			        	//Element ShipmentTagSerialsEL = (Element) XPathUtil.getNode(ShipmentLines.item(cnt),"./ShipmentTagSerials/ShipmentTagSerial");
			        	if(XPathUtil.getNode(shipmentLineEl,"./ShipmentTagSerials/ShipmentTagSerial")!=null)
			        	 ShipmentTagSerialsEL = (Element)XPathUtil.getNode(shipmentLineEl,"./ShipmentTagSerials/ShipmentTagSerial");

			        	String Serial = ""; 
			        	if(ShipmentTagSerialsEL != null)
			        		Serial = ShipmentTagSerialsEL.getAttribute("SerialNo");
			        	
			        
			        	if(Serial!=null && !(Serial.equals(""))){
			        		//System.out.println("t0.5");
			        		
			        		NodeList ShipmentTagSerial = XPathUtil.getNodeList(shipmentLineEl,"./ShipmentTagSerials/ShipmentTagSerial");  
				        	ShipmentTagSerialCount = ShipmentTagSerial.getLength();
				        	//System.out.println("The number of serial nodes in the shipmentline:- :"+ShipmentTagSerialCount);
				        	serialList=new ArrayList();
				        	
				        	for(int cnt2=0;cnt2<ShipmentTagSerialCount;cnt2++){
				        		//System.out.println("Inside for loop");
				        		Element ShipmentTagSerialEl = (Element)ShipmentTagSerial.item(cnt2);
				        		//System.out.println("tagname:-"+ShipmentTagSerialEl.getNodeName());
				        		//System.out.println("Serial# again-"+ShipmentTagSerialEl.getAttribute("SerialNo"));
				        		if(ShipmentTagSerialEl.getAttribute("SerialNo")!=null){
				        			//System.out.println("Correct");
				        			serialList.add(ShipmentTagSerialEl.getAttribute("SerialNo").toString());
				        		}else{
				        			//System.out.println("****Should not come here****");
				        		}
				        		////System.out.println("SerialNo:"+serialList.get(cnt2).toString());
				        	}//End For

				        	//Iterator i = serialList.iterator();
				        	//while (i.hasNext()) {
				                //System.out.println("Serial stored:-"+i.next());
				            //}//End While


			        	}//End IF 
			        	
			        	//System.out.println("t1");
			        	String IncidentNo  	= shipmentLineEl.getAttribute("IncidentNo");
			        	String IncidentYear = shipmentLineEl.getAttribute("IncidentYear");
		        		String Cache 		= shipmentLineEl.getAttribute("CacheID");
		        		String ItemID		= shipmentLineEl.getAttribute("ItemID");
		        		String Price		= shipmentLineEl.getAttribute("UnitPrice");
		        		String issueQTY 	= shipmentLineEl.getAttribute("ActualQuantity");
		        		//Added on Jays request for some reporting - after code review
		        		String orderNo 		= shipmentLineEl.getAttribute("OrderNo");

		        		//if(serialList.isEmpty() || Serial==""){
		        		//changed after code review 
		        		if(serialList.isEmpty() || Serial.equals("")){	
			        			//call function to return the records for exact combo ItemID,Incident#,Cache and price
			        			//if(return<>null)
			        			//ie.If any record is obtained then update the record
			        			//else create new record
			        		Document outDoc = queryTable(env,IncidentNo,IncidentYear,Cache,ItemID,Price);
			        		
			        		Element NWCGIncidentReturn = (Element)XPathUtil.getNode(outDoc.getDocumentElement(),"./NWCGIncidentReturn");
			        		  		
			        		
			        		if(NWCGIncidentReturn==null){
			        			////System.out.println("right choice");
			        			//make a new entry for non-serial item
			        			makeEntry(env,Cache,IncidentNo,IncidentYear,ItemID,Price,issueDate,issueQTY,"",orderNo);
			        		}else{
			        			//update the shipped qty with appropriate amount
			        			//Final issueQTY = QuantityShipped in previous shipment for this incident +
			        			//						issueQTY in the shipment line		
			        			Element NWCGIncidentReturnEl = (Element)outDoc.getDocumentElement().getFirstChild();
			        			
			        			String QuantityShipped	 = NWCGIncidentReturnEl.getAttribute("QuantityShipped");
			        			String IncidentReturnKey = NWCGIncidentReturnEl.getAttribute("IncidentReturnKey");
			        			
			        			//System.out.println("IncidentKey :-"+IncidentReturnKey);
			        			//System.out.println("Quantity Shipped :-"+QuantityShipped);
			        			//System.out.println("issueQTY:-"+issueQTY);
			        			
			        			double finalShippedQty = Double.parseDouble(QuantityShipped)+ Double.parseDouble(issueQTY);
			        			//System.out.println("finalShippedQty:-"+finalShippedQty);
			        			updateEntry(env,IncidentReturnKey,IncidentNo,IncidentYear,Cache,ItemID,
			        					Double.toString(finalShippedQty),Price,orderNo);
			        			
			        		}//if(outDoc==null)
	
			        	}else{
			        			//System.out.println("t2");
			        		
			        			//System.out.println("Make New Entry");
			        			Iterator i = serialList.iterator();
					        	while (i.hasNext()) {
					                
					                String SerialNo = (String)i.next();
					                //System.out.println("Serial stored2:-"+SerialNo);
					                makeEntry(env,Cache,IncidentNo,IncidentYear,ItemID,Price,issueDate,"1",SerialNo,orderNo);
					                //System.out.println("T5");
					            }//End While
			        			
			        	}//IF(SerialNo==Null)
			        	
			        }//End For Loop
		        }else{
		        		
		        		//System.out.println("No Records to process.... Input xml is empty");
		        		throw new NWCGException("NWCG_RETURN_INPUT_XML_NO_RECORDS_TO_PROCESS");
		        	
		        }//End IF 
		        
		        if(log.isVerboseEnabled()){
		    		log.verbose("Exiting the NWCGInsertIncidentRecord , output document is:"+XMLUtil.getXMLString(inXML));
				}//End log

		        return inXML;
		    }//InsertRecord
		    
		    ///----------------------End main method - entrymethod
		 
		    //This method is called only if the qty is to be updated.
		    // This is the case where the item is shipped for same price ie. itemID-Cache-IncidentNo is same
		    public void updateEntry(YFSEnvironment env,String IncidentReturnKey,String Cache, String IncidentNo, 
		    		String IncidentYear,String ItemID,String issueQTY,String Price,String IssueNo)throws Exception
		    {
		    	
		    	Document inDoc = XMLUtil.newDocument();

		    	Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		    	inDoc.appendChild(el_NWCGIncidentReturn);
		    	//el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
		    	el_NWCGIncidentReturn.setAttribute("IncidentReturnKey",IncidentReturnKey);
		    	//el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
		    	el_NWCGIncidentReturn.setAttribute("QuantityShipped",issueQTY);
		    	//Added on 10/24/07 - GN
		    	el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
		    	
		    	//System.out.println("The input xml for updateEntry is:-"+inDoc);
		    	CommonUtilities.invokeService(env,NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE,inDoc);
		     }//UpdateEntry
		    
		    //This method is called if the item is shiped for different price of if the itemID-cache-IncidentNo
		    // does not exist in the table
		     public void makeEntry(YFSEnvironment env,String Cache, String IncidentNo,String IncidentYear, 
		    		 String ItemID, String Price,String issueDate, String issueQTY,String SerialNo,String IssueNo)throws Exception
		     {		    
		    	 //System.out.println("t3, serialNo:-"+SerialNo);
		    	 Document inDoc = XMLUtil.newDocument();

		    	 Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		    	 inDoc.appendChild(el_NWCGIncidentReturn);
		    	 el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
		    	 el_NWCGIncidentReturn.setAttribute("DateIssued",issueDate);
		    	 el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
		    	 el_NWCGIncidentReturn.setAttribute("IncidentYear",IncidentYear);
		    	 el_NWCGIncidentReturn.setAttribute("ItemID",ItemID);
		    	 el_NWCGIncidentReturn.setAttribute("QuantityShipped",issueQTY);
		    	 el_NWCGIncidentReturn.setAttribute("TrackableID",SerialNo);
		    	 el_NWCGIncidentReturn.setAttribute("UnitPrice",Price);
		    	 //Added on Jays request - required for some reporting
		    	 el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
		    	 
		    	 //System.out.println("The input xml for makeEntry is:-"+XMLUtil.getXMLString(inDoc));
		    	 CommonUtilities.invokeService(env,NWCGConstants.NWCG_INSERT_REC_IN_INCIDENT_TABLE_SERVICE,inDoc);
		    	 
		     }//MakeEntry
		    
		     public Document queryTable(YFSEnvironment env,String IncidentNo,String IncidentYear,String Cache, String ItemID, String Price)throws Exception
			    {
		    	 Document inDoc = XMLUtil.newDocument();
		    	 Document outDoc = XMLUtil.newDocument();
     			
     			Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
     			inDoc.appendChild(el_NWCGIncidentReturn);
     			el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
     			el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
     			el_NWCGIncidentReturn.setAttribute("IncidentYear",IncidentYear);
     			el_NWCGIncidentReturn.setAttribute("ItemID",ItemID);
     			el_NWCGIncidentReturn.setAttribute("UnitPrice",Price);
     			el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","N");
     			el_NWCGIncidentReturn.setAttribute("OverReceipt","N");

     			//System.out.println("Input xml for querying the table is:-"+XMLUtil.getXMLString(inDoc));

     			outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,inDoc);
     			return outDoc;
			    }//queryTable
		     
}//End Class
