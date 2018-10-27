package com.nwcg.icbs.yantra.api.returns;
//Gomathi please add appropriate changes whereever comment "//GS" is presnt
// also change all the methods except entry method to private.

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGReceiveIncidentReturns implements YIFCustomApi{

			 int ReturnLineCount = 0;
			 int ShipmentTagSerialCount = 0;
			 List serialList = new ArrayList();
			 
		      /**
			  * Logger Instance.
			  */
			private static Logger log = Logger.getLogger(NWCGInsertIncidentRecord.class.getName());

			private Properties props = null;

			public void setProperties(Properties props) throws Exception {
				this.props = props;
			}//setProperties

		    //Entry method - Perform virtual return.. ie. just updates the incident_return tabel meant for reporting
			//REFER:- DESIGN DOC FOR THE FLOWCHART
		    public Document receiveReturns(YFSEnvironment env, Document inXML) throws Exception
		    {
		    	if(log.isVerboseEnabled()){
		    		log.debug("Start of NWCGReceiveIncidentReturns...."+XMLUtil.getXMLString(inXML));
		    	}//End logging 
		    	
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
		        
		        //GS - add check if(inXML!=null) for all the four lines below
		        String IncidentNo 		= inXML.getDocumentElement().getAttribute("IncidentNo"); 
		        String IncidentYear		= inXML.getDocumentElement().getAttribute("IncidentYear");
		        String IssueNo          = inXML.getDocumentElement().getAttribute("IssueNo");
     	        String Cache	  		= inXML.getDocumentElement().getAttribute("CacheID");
		        String ReceivingLoc 	= inXML.getDocumentElement().getAttribute("LocationId");
		        String EnterpriseCode   = inXML.getDocumentElement().getAttribute("EnterpriseCode");
		        
		        //GS - add check if(inXML!=null)
		        NodeList ReturnLines = inXML.getDocumentElement().getElementsByTagName("ReturnLine");
		        //GS - add check if(ReturnLines!=null && ReturnLines.getLength()>0)
		        ReturnLineCount = ReturnLines.getLength();
		        
		        if(log.isVerboseEnabled()) log.verbose("The inxml has lines :"+ReturnLineCount);
		        
		        if(ReturnLineCount>0){
		        	//--------------------------FOR Loop - Receipt Lines
			        for(int cnt=0;cnt<ReturnLineCount;cnt++){
			        		
			        	//check if the item is serially tracked
			        	Element ReturnLineEl = (Element)ReturnLines.item(cnt);
			        	
			        	//get the serialNo
			        	String Serial = ReturnLineEl.getAttribute("TrackableID");
			        	
			        	if(log.isVerboseEnabled()) log.verbose("SerialNo is :-"+Serial);
			        	
			        	  	
			        	//Attributes in the input xml
			        	//String IncidentNo  			= 	ReturnLineEl.getAttribute("IncidentNo");
		        		//String Cache 				= 	ReturnLineEl.getAttribute("CacheID");
		        		String ItemID				= 	ReturnLineEl.getAttribute("ItemID");
		        		String Price				= 	ReturnLineEl.getAttribute("UnitPrice");
		        		String TotalQtyReturned 	= 	ReturnLineEl.getAttribute("TotalQtyReturned");
		        		String BatchNo 				=	ReturnLineEl.getAttribute("BatchNo");
		        		String DispositionCode		=	ReturnLineEl.getAttribute("DispositionCode"); 
		        		//String EnterpriseCode		= 	ReturnLineEl.getAttribute("EnterpriseCode");
		        		String LotAttribute1		=	ReturnLineEl.getAttribute("LotAttribute1");
		        		String LotAttribute2		=	ReturnLineEl.getAttribute("LotAttribute2");
		        		String LotAttribute3		=	ReturnLineEl.getAttribute("LotAttribute3");
		        		String LotKeyReference		=	ReturnLineEl.getAttribute("LotKeyReference");
		        		String LotNumber			=	ReturnLineEl.getAttribute("LotNumber");
		        		String OriginalQuantity		=	ReturnLineEl.getAttribute("OriginalQuantity");
		        		String ProductClass			=	ReturnLineEl.getAttribute("ProductClass");
		        		if(log.isVerboseEnabled()) log.verbose("ProductClass:-"+ProductClass);
		        		String Quantity				=	ReturnLineEl.getAttribute("Quantity");	
		        		String TagNumber			=	ReturnLineEl.getAttribute("TagNumber");
		        		String UnitOfMeasure		=	ReturnLineEl.getAttribute("UnitOfMeasure");
		        		double NRFI					=	Double.parseDouble(ReturnLineEl.getAttribute("QuantityNRFI"));
		        		if(log.isVerboseEnabled()) log.verbose("NRFI"+NRFI);
		        		double RFI					=	Double.parseDouble(ReturnLineEl.getAttribute("QuantityRFI"));
		        		if(log.isVerboseEnabled()) log.verbose("RFI"+RFI);
		        		double TQR					=	Double.parseDouble(ReturnLineEl.getAttribute("QuantityReturned"));
		        		if(log.isVerboseEnabled()) log.verbose("TQR"+TQR);
		        		double UnsRet 				=	Double.parseDouble(ReturnLineEl.getAttribute("QuantityUnsRet"));
		        		if(log.isVerboseEnabled()) log.verbose("UnsRet"+UnsRet);
		        		double UnsNWT 				=	Double.parseDouble(ReturnLineEl.getAttribute("QuantityUnsNwtReturn"));
		        		if(log.isVerboseEnabled()) log.verbose("UnsNWT"+UnsNWT);
		        		String TrackableID			=	ReturnLineEl.getAttribute("TrackableID");
		        		String IsComponent			=	ReturnLineEl.getAttribute("IsComponent");
		        		if(log.isVerboseEnabled()) log.verbose("IsComponent :-"+IsComponent);
		        		
		        		if(TQR !=(RFI+NRFI+UnsRet+UnsNWT)){
		        			Object[] params = new Object[] {ItemID,Double.toString(TQR),Double.toString(RFI),Double.toString(NRFI),Double.toString(UnsRet),Double.toString(UnsNWT) };
		        			throw new NWCGException("NWCG_RETURN_TOTAL_ERR",params);
		        		}//End Exception 
		        		//------------------
		        		//if(Serial!=""){
		        		//Changed after code review 
		        		//GS - add check if(Serial!=null && !(Serial.equals(""))) also to the if condition below
		        		if(!(Serial.equals(""))){
		        					//Search for the item,incident,cache and trackableID entry
		        					//IF record found then - update the record appropriately
		        					//IF not found then make new entry and set over-receipt flag ='Y'
		        					if(log.isVerboseEnabled()) log.verbose("reached 1");
		        					//System.out.println("Trackable ID "+ Serial);
		        					
		        					//------------------LOGIC Start
		        					Document outDoc = queryTable(env,IncidentNo,IncidentYear,IssueNo,Cache,ItemID,TrackableID);
		        					if(log.isVerboseEnabled()) log.verbose("The outdoc is :-"+XMLUtil.getXMLString(outDoc));
		        					//GS - add if(outDoc!=null)
	        						NodeList TableLines = outDoc.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
	        						//GS - add if(TableLines!=null && TableLines.getLength()>0)
	        						int numTableLine = TableLines.getLength();
	        						if(log.isVerboseEnabled()) log.verbose("Total Lines :-"+numTableLine);
	        						
	        						char RecComp = 'N';
	        						String PSerialKey, InvItemKey, PItemID, PSerialNo = "";
	        						Document PSerialOut;
	        						if (IsComponent.equals("True"))
	        						{
	        							RecComp = 'Y';
	        							PSerialKey = getParentSerialKey(env,TrackableID,ItemID);
	        							PSerialOut = getParentSerialNo(env,PSerialKey);
	        							
	        							Element PSerialElem = (Element) PSerialOut.getDocumentElement().getElementsByTagName("Serial").item(0);
	        				    	    PSerialNo = PSerialElem.getAttribute("SerialNo");
	        				    	    //System.out.println("PSerialNo "+PSerialNo);
	        				    	    InvItemKey = PSerialElem.getAttribute("InventoryItemKey");
	        				    	    PItemID = getParentItemID(env,InvItemKey,Cache);
	        				    	    //System.out.println("PItemID "+PItemID);
	        							UpdateParentRecord(env,IncidentNo,IncidentYear,IssueNo,Cache,PSerialNo,PItemID);
	        						}
	        							        						
	        						if(numTableLine==0){
	        							//Means no record for the trackID exists,Make a new entry with overreceipt='Y'
	        							makeEntry(env,Cache,IncidentNo,IncidentYear,IssueNo,ItemID,TrackableID,Double.toString(TQR),Double.toString(RFI),
	        									Double.toString(NRFI),Double.toString(UnsRet),Double.toString(UnsNWT),'Y', RecComp,ProductClass,UnitOfMeasure);
	        							//Continue- Added to resolve bug found during Incident to Incident transfer
	        								continue;
	        						}else{
	        							//Make appropriate update wrt RFI/NRFI/UnsRet/UnsNWT
	        							//Note :- Count is hardcoded
	        							//GS - add if(TableLines!=null && TableLines.getLength()>0)
	        							Element TableLineEl = (Element)TableLines.item(0);
	        							String IncidentReturnKey = TableLineEl.getAttribute("IncidentReturnKey");
	        							//System.out.println("IncidentReturnKey "+ IncidentReturnKey);
	        							updateEntry(env, IncidentReturnKey,Cache, IncidentNo,IncidentYear,ItemID,
												Double.toString(TQR), Double.toString(RFI), Double.toString(NRFI), 
												Double.toString(UnsRet), Double.toString(UnsNWT), 'N','N');
	        						}//No result from the table query
		        					//----------------LOGIC End
		        		}else{
		        			if(log.isVerboseEnabled()) log.verbose("reached 1.1");
		        					if(IsComponent.equalsIgnoreCase("false")){
		        						if(log.isVerboseEnabled()) log.verbose("reached 2");
		        						//----------LOGIC
		        						//Search table for itemID,incident,cache
		        						//If record returned Then 
		        								//Start For Loop
		        								//Get Diff1
		        						//else
		        								//Enter a new record with over receipt flag ='Y'
		        						//----------LOGIC
		        						Document outDoc1 = queryTable(env,IncidentNo,IncidentYear,IssueNo,Cache,ItemID,"");
		        						//GS - add check if(outDoc1!=null)
		        						NodeList TableLines = outDoc1.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
		        						//GS - add check if(TableLines!=null && TableLines.getLength()>0)
		        						int numReceiptLine = TableLines.getLength();
		        						if(numReceiptLine==0){
		        							//Then just make new entry with overreceiptflag='Y'
		        							if(log.isVerboseEnabled()) log.verbose("reached here to call make entry");
		        							makeEntry(env,Cache,IncidentNo,IncidentYear,IssueNo,ItemID,TrackableID,Double.toString(TQR),Double.toString(RFI),
		        									Double.toString(NRFI),Double.toString(UnsRet),Double.toString(UnsNWT),'Y', 'N',ProductClass,UnitOfMeasure);
		        							       //MAde changes to fix bug in Incident to Incident transfer...
		        								   continue;
		        						}else{
		        								if(log.isVerboseEnabled()) log.verbose("test1,receiptlines :-"+numReceiptLine);
		        							
		        							for(int cnt1=0;cnt1<numReceiptLine;cnt1++){
		        								//GS - add check if(TableLines!=null && TableLines.getLength()>0)
		        								Element tableLineEl = (Element)TableLines.item(cnt1);
		        								double QS = Double.parseDouble(tableLineEl.getAttribute("QuantityShipped"));
		        								if(log.isVerboseEnabled()) log.verbose("QS:"+QS);
		        								//if(log.isVerboseEnabled()) log.verbose("Qty returned"+tableLineEl.getAttribute("QuantityReturned"));
		        								double QR = Double.parseDouble(tableLineEl.getAttribute("QuantityReturned"));
		        								if(log.isVerboseEnabled()) log.verbose("QR:"+QR);
		        								double QRFI = Double.parseDouble(tableLineEl.getAttribute("QuantityRFI"));
		        								if(log.isVerboseEnabled()) log.verbose("QRFI"+QRFI);
		        								double QNRFI = Double.parseDouble(tableLineEl.getAttribute("QuantityNRFI"));
		        								if(log.isVerboseEnabled()) log.verbose("QNRFI"+QNRFI);
		        								double QUnsRet = Double.parseDouble(tableLineEl.getAttribute("QuantityUnsRet"));
		        								if(log.isVerboseEnabled()) log.verbose("QUnsRet"+QUnsRet);
		        								double QUnsNWT = Double.parseDouble(tableLineEl.getAttribute("QuantityUnsNwtReturn"));
		        								if(log.isVerboseEnabled()) log.verbose("QUnsNWT"+QUnsNWT);
		        								String IncidentKey = tableLineEl.getAttribute("IncidentReturnKey");
		        								double Adj1 = 0;
		        								double Diff1 = QS - QR;
		        								
		        								if(log.isVerboseEnabled()) log.verbose("Diff1 ..test..............1:"+Diff1);
		        								
		        								if(Diff1>0){
		        									//--------------------------rest of logic goes here 
		        									if(RFI>0){
		        										if(log.isVerboseEnabled()) log.verbose("RFI>0 ................1.1:-"+Diff1+"RFI:"+RFI);
		        										Adj1 = Diff1-RFI;
		        											if(Adj1>0 ||Adj1==0){
		        												if(log.isVerboseEnabled()) log.verbose("RFI>0 ................2.1:-"+Adj1);
		        												//This means that this line line in incident table can handle RFI
		        												//No spillover to next line in the table
		        												QRFI = QRFI + RFI;
		        												QR = QR + RFI;
		        												RFI = 0;
		        												//if(log.isVerboseEnabled()) log.verbose("QRFI:"+QRFI+"RFI:"+RFI);
		        												if(log.isVerboseEnabled()) log.verbose("QR:"+QR);
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache,IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												//This line is added to take care of the scenario wherein 
		        												//The receiptline has more attributes NRFI>0 or UnsRet or UnsNWT>0 and
		        												//The table record is full ie. QS=QR
		        												if(Diff1==0) continue;
		        												
		        											}else{
		        												if(log.isVerboseEnabled()) log.verbose("RFI>0 ................3.1:-"+Adj1);
		        												//this means that there is spillover to process RFI to next row in table
		        												QRFI = QRFI + Diff1;
		        												RFI = RFI - Diff1;
		        												QR = QR + Diff1;
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												//If line in table is received.. GO TO next
		        												if(Diff1 == 0)	continue;
		        											}//End 
		        									}//If(RFI>0)
		        									
		        									if(NRFI>0){
		        										if(log.isVerboseEnabled()) log.verbose("NRFI>0 ................1.2:-"+Diff1);
		        										Adj1 = Diff1-NRFI;
		        											if(Adj1>0 ||Adj1==0){
		        												if(log.isVerboseEnabled()) log.verbose("NRFI>0 ...Adj1>0................2.2:-"+Adj1);
		        												//This means that this line line in incident table can handle RFI
		        												//No spillover to next line in the table
		        												QNRFI = QNRFI + NRFI;
		        												QR = QR + NRFI;
		        												NRFI = 0;		        												
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												if(Diff1==0) continue;
		        											}else{
		        												if(log.isVerboseEnabled()) log.verbose("NRFI>0 ...Adj1<0................3.2:-"+Adj1);
		        												//this means that there is spillover to process RFI to next row in table
		        												
		        												QNRFI = QNRFI + Diff1;
		        												NRFI = NRFI - Diff1;
		        												QR = QR + Diff1;
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												//If line in table is received.. GO TO next
		        												if(Diff1 == 0)	continue;
		        											}//End 
		        									}//If(NRFI>0)
		        									
		        									if(UnsRet>0){
		        										if(log.isVerboseEnabled()) log.verbose("UnsRet>0 ...................1.3:-"+Diff1);
		        										Adj1 = Diff1-UnsRet;
		        											if(Adj1>0 ||Adj1==0){
		        												if(log.isVerboseEnabled()) log.verbose("UnsRet>0 ...Adj1>0................2.3:-"+Adj1);
		        												//This means that this line line in incident table can handle RFI
		        												//No spillover to next line in the table
		        												QUnsRet = QUnsRet + UnsRet;
		        												QR = QR + UnsRet;
		        												UnsRet = 0;		        												
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												if(Diff1==0) continue;
		        											}else{
		        												if(log.isVerboseEnabled()) log.verbose("UnsRet>0 ...Adj1<0................3.3:-"+Adj1);
		        												//this means that there is spillover to process RFI to next row in table
		        												QUnsRet = QUnsRet + Diff1;
		        												UnsRet = UnsRet - Diff1;
		        												QR = QR + Diff1;
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												//If line in table is received.. GO TO next
		        												if(Diff1 == 0)	continue;
		        											}//End 
		        									}//If(UnsRet>0)
		        									
		        									if(UnsNWT>0){
		        										if(log.isVerboseEnabled()) log.verbose("UnsNWT>0 ...................1.4:-"+Diff1);
		        										Adj1 = Diff1-UnsNWT;
		        											if(Adj1>0 ||Adj1==0){
		        												if(log.isVerboseEnabled()) log.verbose("UnsNWT>0 ...Adj1>0................2.4:-"+Adj1);
		        												//This means that this line line in incident table can handle RFI
		        												//No spillover to next line in the table
		        												QUnsNWT = QUnsNWT + UnsNWT;
		        												QR = QR + UnsNWT;
		        												UnsNWT = 0;		        												
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												if(Diff1==0) continue;
		        											}else{
		        												if(log.isVerboseEnabled()) log.verbose("UnsNWT>0 ...Adj1<0................3.4:-"+Adj1);
		        												//this means that there is spillover to process RFI to next row in table
		        												QUnsNWT = QUnsNWT + Diff1;
		        												UnsNWT = UnsNWT - Diff1;
		        												QR = QR + Diff1;
		        												Diff1 = QS - QR;
		        												updateEntry(env, IncidentKey,Cache, IncidentNo,IncidentYear,ItemID,
		        														Double.toString(QR), Double.toString(QRFI), Double.toString(QNRFI), 
		        														Double.toString(QUnsRet), Double.toString(QUnsNWT), 'N','N');
		        												//If line in table is received.. GO TO next
		        												if(Diff1 == 0)	continue;
		        											}//End 
		        											//Will come here only if the recietline has been taken care of
		        											//from one table line and we need not move to next table line
		        											//to process another receipt
		        											break;
		        									}//If(UnsRet>0)
		        									//----------------------------------End Logic
		        								}else{
		        									//Means All items that were shipped have been received
		        									//Go To next record if available
		        									if(log.isVerboseEnabled()) log.verbose("REading next line from the table-");
		        									continue;
		        								}//If(Diff1>0)
		        							}//For Loop for the lines in the table
		        						}//(numReceiptLine==0)
		        						if(RFI>0||NRFI>0||UnsRet>0||UnsNWT>0){
		        							//Make new entry with overreceiptflag set to 'Y'
		        							if(log.isVerboseEnabled()) log.verbose("Making over receipt for normal items");
		        							
		        							double totalOverReceipt = RFI+NRFI+UnsRet+UnsNWT;
		        							makeEntry(env,Cache,IncidentNo,IncidentYear,IssueNo,ItemID,TrackableID,Double.toString(totalOverReceipt),Double.toString(RFI),
		        									Double.toString(NRFI),Double.toString(UnsRet),Double.toString(UnsNWT),'Y', 'N',ProductClass,UnitOfMeasure); 
		        						}//if(lineProcessed=="false")
		        					}else{
		        						//make new entry and set receivedAsComponent='Y'
		        						if(log.isVerboseEnabled()) log.verbose("Receiving components");
		        						makeEntry(env,Cache,IncidentNo,IncidentYear,IssueNo,ItemID,TrackableID,Double.toString(TQR),Double.toString(RFI),
	        									Double.toString(NRFI),Double.toString(UnsRet),Double.toString(UnsNWT),'N', 'Y',ProductClass,UnitOfMeasure); 
		        					}//IF(IsComponent=="False")
		        		}//if(Serial=="")
			        }//End For Loop for each receiptLine
			        //--------------------------FOR Loop - Receipt Lines
		        }else{
	        		if(log.isVerboseEnabled()) log.verbose("No Records to process.... Input xml is empty");
	        		throw new NWCGException("NWCG_RETURN_INPUT_XML_NO_RECORDS_TO_PROCESS");
		        }//End IF(receiptLine>0) 
		        
		        if(log.isVerboseEnabled()){
		    		log.debug("Exiting NWCGReceiveIncidentReturns API...."+XMLUtil.getXMLString(inXML));
		    	}//End logging 
		        
		        return inXML;
		    }//End receiveReturns method
		    //-------------End method - receiveReturns()
		   	//----------------------------------------Methods to manipulate the table-------------------------- 		    
		    public void updateEntry(YFSEnvironment env,String IncidentReturnKey,String Cache, String IncidentNo,String IncidentYear, 
		    		String ItemID,String QtyReturned, String QRFI,String QNRFI, String QUnsRet, String QUnsNWT,  
		    		char OverRcpt, char RcdAsComponent)throws Exception
		    {
		    	Document inDoc = XMLUtil.newDocument();
		    	if(log.isVerboseEnabled()) log.verbose("Inside update entry");
		    	
		    	if(log.isVerboseEnabled()) log.verbose("IncidentRetKey:"+IncidentReturnKey);
		    	if(log.isVerboseEnabled()) log.verbose("Cache:"+Cache);
		    	if(log.isVerboseEnabled()) log.verbose("IncidentNo:"+IncidentNo);
		    	if(log.isVerboseEnabled()) log.verbose("QtyReturned:"+QtyReturned);
		    	if(log.isVerboseEnabled()) log.verbose("QRFI:"+QRFI);
		    	if(log.isVerboseEnabled()) log.verbose("QNRFI:"+QNRFI);
		    	if(log.isVerboseEnabled()) log.verbose("QUnsRet:"+QUnsRet);
		    	if(log.isVerboseEnabled()) log.verbose("QUnsNWT:"+QUnsNWT);
		    	//if(log.isVerboseEnabled()) log.verbose("IncidentRetKey:"+IncidentReturnKey);
		    	
		    	Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		    	inDoc.appendChild(el_NWCGIncidentReturn);
		    	//el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
		    	el_NWCGIncidentReturn.setAttribute("IncidentReturnKey",IncidentReturnKey);
		    	//el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
		    	el_NWCGIncidentReturn.setAttribute("QuantityReturned",QtyReturned);
		    	el_NWCGIncidentReturn.setAttribute("QuantityRFI",QRFI);
		    	el_NWCGIncidentReturn.setAttribute("QuantityNRFI",QNRFI);
		    	el_NWCGIncidentReturn.setAttribute("QuantityUnsRet",QUnsRet);
		    	el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn",QUnsNWT);
		    	el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent",String.valueOf(RcdAsComponent));
		    	el_NWCGIncidentReturn.setAttribute("OverReceipt",String.valueOf(OverRcpt));
		    	
		    	if(log.isVerboseEnabled()) log.verbose("The input xml for updateEntry is:-"+XMLUtil.getXMLString(inDoc));
		    	//CommonUtilities.invokeService(env,"NWCGModifyIncidentReturn",inDoc);
		    	//Changed after code review 
		    	CommonUtilities.invokeService(env,NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE,inDoc);
		     }//UpdateEntry
		    
		    public void makeEntry(YFSEnvironment env,String Cache, String IncidentNo,String IncidentYear,String IssueNo, 		     
		    		 String ItemID, String SerialNo,String QtyReturned, String QRFI,
		    		 String QNRFI, String QUnsRet, String QUnsNWT,char OverRcpt, char RcdAsComponent,
		    		 String ProductClass, String UnitOfMeasure
		    		 )throws Exception
		     {		    
		    	 if(log.isVerboseEnabled()) log.verbose("Inside MakeEntry, serialNo:-"+SerialNo);
		    	 Document inDoc = XMLUtil.newDocument();
			     Document trackinDoc = XMLUtil.newDocument();
			     Document trackoutDoc = XMLUtil.newDocument();
			     String lastissueprice = "";
		    	 
		    	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    	 String date = format.format(new java.util.Date());
		    	 if(log.isVerboseEnabled()) log.verbose("Date is :-"+date);


		    	 Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		    	 inDoc.appendChild(el_NWCGIncidentReturn);
		    	 el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
		    	 el_NWCGIncidentReturn.setAttribute("DateIssued",date);
		    	 el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
		    	 el_NWCGIncidentReturn.setAttribute("IncidentYear",IncidentYear);
		    	 el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
		    	 el_NWCGIncidentReturn.setAttribute("ItemID",ItemID);
		    	 el_NWCGIncidentReturn.setAttribute("TrackableID",SerialNo);
		    	 el_NWCGIncidentReturn.setAttribute("QuantityReturned",QtyReturned);
			     el_NWCGIncidentReturn.setAttribute("QuantityRFI",QRFI);
			     el_NWCGIncidentReturn.setAttribute("QuantityNRFI",QNRFI);
			     el_NWCGIncidentReturn.setAttribute("QuantityUnsRet",QUnsRet);
			     el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn",QUnsNWT);
			     el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent",String.valueOf(RcdAsComponent));
			     el_NWCGIncidentReturn.setAttribute("OverReceipt",String.valueOf(OverRcpt));
			     
			     
			     if (SerialNo.length() > 0 )
			     {
			       /* Added for CR-445 GN */
			       Element el_NWCGTrackableItem = trackinDoc.createElement("NWCGTrackableItem");
			       trackinDoc.appendChild(el_NWCGTrackableItem);
			       el_NWCGTrackableItem.setAttribute("StatusIncidentNo",IncidentNo);
			       el_NWCGTrackableItem.setAttribute("StatusIncidentYear",IncidentYear);
			       el_NWCGTrackableItem.setAttribute("ItemID",ItemID);
			       el_NWCGTrackableItem.setAttribute("SerialNo",SerialNo);
			       el_NWCGTrackableItem.setAttribute("SecondarySerial",CommonUtilities.getSecondarySerial(env,SerialNo,ItemID));
			       //el_NWCGTrackableItem.setAttribute("SerialStatus","I");
			       
			       trackoutDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_GET_TRACKABLE_ITEM_LIST_SERVICE,trackinDoc);
			       
			       Element trackoutelem = null;
			       trackoutelem = (Element) trackoutDoc.getDocumentElement().getElementsByTagName("NWCGTrackableItem").item(0);
			       
			       if (trackoutelem != null)
			       {
			         lastissueprice = trackoutelem.getAttribute("LastIssuePrice");
			       }
			       
			     }
			    
			     if (lastissueprice.length() > 0)
			     {
			    	 el_NWCGIncidentReturn.setAttribute("UnitPrice",lastissueprice);
			     } /* End CR-445 Changes */ 
			     else
			     {
			     //-----------------------------Start making changes to reflect the price when making new entry
			     Document rt_ComputePriceForItem = XMLUtil.getDocument();

			     Element el_ComputePriceForItem=rt_ComputePriceForItem.createElement("ComputePriceForItem");
			     rt_ComputePriceForItem.appendChild(el_ComputePriceForItem);
			     el_ComputePriceForItem.setAttribute("Currency","USD");
			     el_ComputePriceForItem.setAttribute("ItemID",ItemID);
			     el_ComputePriceForItem.setAttribute("OrganizationCode","NWCG");
			     el_ComputePriceForItem.setAttribute("PriceProgramName","NWCG_PRICE_PROGRAM");
			     el_ComputePriceForItem.setAttribute("ProductClass",ProductClass);
			     el_ComputePriceForItem.setAttribute("Quantity","1");
			     el_ComputePriceForItem.setAttribute("Uom",UnitOfMeasure);
			    
			     Document rt_ComputePriceForItemOut = XMLUtil.getDocument();
			     Element el_ComputePriceForItemOut=rt_ComputePriceForItemOut.createElement("ComputePriceForItem");
			     rt_ComputePriceForItemOut.appendChild(el_ComputePriceForItemOut);
			     el_ComputePriceForItemOut.setAttribute("UnitPrice","");
			     
			     if(log.isVerboseEnabled()) log.verbose("The outputtemplate for computePriceForItem is:-"+XMLUtil.getXMLString(rt_ComputePriceForItemOut));
			     
			     //Calling the API to get the price
			     //env.setApiTemplate("computePriceForItem",rt_ComputePriceForItemOut);
			     if(log.isVerboseEnabled()) log.verbose("The input xml for computePriceForItem is:-"+XMLUtil.getXMLString(rt_ComputePriceForItem));
			     Document PriceDocument = CommonUtilities.invokeAPI(env,"computePriceForItem",rt_ComputePriceForItem);
			     if(log.isVerboseEnabled()) log.verbose("The output xml for computePriceForItem is:-"+XMLUtil.getXMLString(PriceDocument));
			     //env.clearApiTemplate("rt_ComputePriceForItem");
			     
			     Element root_elem = PriceDocument.getDocumentElement();
			     if(root_elem != null)
			     {
			    	 	//if(log.isVerboseEnabled()) log.verbose("ITS WORKING LEST SEEEEEEEEEE ===>  u NIT PRICE "+root_elem.getAttribute("UnitPrice"));
//			    	 	Complete the input xml for updating the record in the Incident table
			    	 	el_NWCGIncidentReturn.setAttribute("UnitPrice",root_elem.getAttribute("UnitPrice"));
			     }
//			    			     
			     if(log.isVerboseEnabled()) log.verbose("UnitPrice:--------------"+root_elem.getAttribute("UnitPrice"));
			     
			     //------------End making changes to reflect the price when making new entry
	    	 
		    	 if(log.isVerboseEnabled()) log.verbose("The input xml for makeEntry is:-"+XMLUtil.getXMLString(inDoc));
		    	 //CommonUtilities.invokeService(env,"NWCGCreateIncidentReturn",inDoc);
		    	 //Changed after code review 
			     } //lastissueprice check
			     
		    	 CommonUtilities.invokeService(env,NWCGConstants.NWCG_INSERT_REC_IN_INCIDENT_TABLE_SERVICE,inDoc);
		    	 
		     }//MakeEntry
		    
		    public Document queryTable(YFSEnvironment env,String IncidentNo,String IncidentYear,String IssueNo,String Cache, String ItemID, String SerialNo)throws Exception
			    {
		    	 Document inDoc = XMLUtil.newDocument();
		    	 Document outDoc = XMLUtil.newDocument();
     			
     			Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
     			inDoc.appendChild(el_NWCGIncidentReturn);
     			el_NWCGIncidentReturn.setAttribute("CacheID",Cache);
     			el_NWCGIncidentReturn.setAttribute("IncidentNo",IncidentNo);
     			el_NWCGIncidentReturn.setAttribute("IncidentYear",IncidentYear);
     			el_NWCGIncidentReturn.setAttribute("IssueNo",IssueNo);
     			el_NWCGIncidentReturn.setAttribute("ItemID",ItemID);
     			el_NWCGIncidentReturn.setAttribute("TrackableID",SerialNo);
     			if (SerialNo.length() > 0)
     			{
    	    	  el_NWCGIncidentReturn.setAttribute("QuantityReturned","0");
			      el_NWCGIncidentReturn.setAttribute("QuantityRFI","0");
			      el_NWCGIncidentReturn.setAttribute("QuantityNRFI","0");
			      el_NWCGIncidentReturn.setAttribute("QuantityUnsRet","0");
			      el_NWCGIncidentReturn.setAttribute("QuantityUnsNwtReturn","0");
     			} 

     			//This to make sure over receipt entries and received as components are not selected
     			el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","N");
     			el_NWCGIncidentReturn.setAttribute("OverReceipt","N");
     			
     			//Added this to make sure we get the first created record first
     			Element el_OrderBy=inDoc.createElement("OrderBy");
     			el_NWCGIncidentReturn.appendChild(el_OrderBy);

     			Element el_Attribute=inDoc.createElement("Attribute");
     			el_OrderBy.appendChild(el_Attribute);
     			el_Attribute.setAttribute("Name","Createts");
     			//---------------------End changes for order by

     			if(log.isVerboseEnabled()) log.verbose("Input xml for querying the table is:-"+XMLUtil.getXMLString(inDoc));

     			//outDoc = CommonUtilities.invokeService(env,"NWCGgetIncidentReturns",inDoc);
     			//changed after code review 
     			outDoc = CommonUtilities.invokeService(env,NWCGConstants.NWCG_QUERY_LIST_ON_INCIDENT_TABLE_SERVICE,inDoc);
     			return outDoc;
			    }//queryTable
			    
		    public String getParentSerialKey(YFSEnvironment env,String SerialNo,String strItemID)throws Exception{
		    	
		    	String GSerialKey = "";
		    	
		    	Document inDoc = XMLUtil.newDocument();
		    	Document outDoc = XMLUtil.newDocument();

					Element el_PSerial=inDoc.createElement("Serial");
					inDoc.appendChild(el_PSerial);
					el_PSerial.setAttribute("SerialNo",SerialNo);
					Element el_InvItem=inDoc.createElement("InventoryItem");
					el_PSerial.appendChild(el_InvItem);
					el_InvItem.setAttribute("ItemID",strItemID);
					env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
					outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
					//System.out.println("Parent Serial Key Output "+ XMLUtil.getXMLString(outDoc));
					
					Element PSerialOut = (Element) outDoc.getDocumentElement().getElementsByTagName("Serial").item(0);
					GSerialKey = PSerialOut.getAttribute("ParentSerialKey");
					//System.out.println("GSerialKey "+GSerialKey);
					env.clearApiTemplate("getSerialList");

		        return GSerialKey;
		    }
			    	    
		    public Document getParentSerialNo(YFSEnvironment env,String ParentSerialKey)throws Exception{
		    	
		    	String PSerialNo = "";
		    	
		    	Document inDoc = XMLUtil.newDocument();
		    	Document outDoc = XMLUtil.newDocument();

					Element el_PSerial=inDoc.createElement("Serial");
					inDoc.appendChild(el_PSerial);
					el_PSerial.setAttribute("GlobalSerialKey",ParentSerialKey);
					env.setApiTemplate("getSerialList","NWCGProcessBlindReturn_getSerialList");
					outDoc = CommonUtilities.invokeAPI(env,"getSerialList",inDoc);
	
		    	env.clearApiTemplate("getSerialList");

		        return outDoc;
		    } 
		    
		    public String getParentItemID(YFSEnvironment env,String InvItemKey,String NodeKey)throws Exception{
		    
		    	/* <InventoryItem InventoryItemKey="2008032716065181799" OrganizationCode="CORMK" /> */
		    	
		    	String PItemID = "";
		    	
		    	Document inDoc = XMLUtil.newDocument();
		    	Document outDoc = XMLUtil.newDocument();

					Element el_PItem=inDoc.createElement("InventoryItem");
					inDoc.appendChild(el_PItem);
					el_PItem.setAttribute("InventoryItemKey",InvItemKey);
					el_PItem.setAttribute("OrganizationCode",NodeKey);
		
					outDoc = CommonUtilities.invokeAPI(env,"getInventoryItemList",inDoc);
					Element PItemOut = (Element) outDoc.getDocumentElement().getElementsByTagName("InventoryItem").item(0);
					PItemID = PItemOut.getAttribute("ItemID");
	
		        return PItemID;
		    } 
		    
		    public void UpdateParentRecord(YFSEnvironment env,String IncidentNo,String IncidentYear,String IssueNo,String Cache,String PSerialNo,String PItemID)throws Exception{
		    	
		    	Document outDoc = queryTable(env,IncidentNo,IncidentYear,IssueNo,Cache,PItemID,PSerialNo);
	
				NodeList TableLines = outDoc.getDocumentElement().getElementsByTagName("NWCGIncidentReturn");
				if (TableLines.getLength() > 0)
				{
				 Element TableLineEl = (Element)TableLines.item(0);
				 String IncidentReturnKey = TableLineEl.getAttribute("IncidentReturnKey");
				 //System.out.println("Incident Ret Key "+ IncidentReturnKey);
				
				 Document inDoc = XMLUtil.newDocument();
		    	 Element el_NWCGIncidentReturn=inDoc.createElement("NWCGIncidentReturn");
		    	 inDoc.appendChild(el_NWCGIncidentReturn);
		    	 el_NWCGIncidentReturn.setAttribute("IncidentReturnKey",IncidentReturnKey);
		    	 el_NWCGIncidentReturn.setAttribute("ReceivedAsComponent","Y");
		    	 System.out.println("updateParent InDoc "+ XMLUtil.getXMLString(inDoc));
		    	 CommonUtilities.invokeService(env,NWCGConstants.NWCG_MODIFY_REC_IN_INCIDENT_TABLE_SERVICE,inDoc);
				}
		    } 
}//End Class
