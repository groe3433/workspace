/**
 * 
 */
package com.nwcg.icbs.yantra.api.refurb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.constant.common.NWCGConstants;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.StringUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.nwcg.icbs.yantra.util.common.XPathUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author jvishwakarma
 *
 */
/*creates a yantra work order from master work order line 
 * <WorkOrder IgnoreOrdering="Y" ItemID="000067" Node="CORMK" DocumentType="7001" EnterpriseCode=NWCGConstants.ENTERPRISE_CODE 
    NodeKey="CORMK" ProductClass="Supply" QuantityRequested="1"  EnterpriseCodeForComponent=NWCGConstants.ENTERPRISE_CODE EnterpriseInvOrg=NWCGConstants.ENTERPRISE_CODE 
    SerialNo="" Uom="EA" UserID="cormk1">
    <NWCGMasterWorkOrderLine DestinationInventoryStatus="NRFI-RFB"
        LocationID="FIRST-AID-1" 
        MasterWorkOrderLineKey="20070619170807630140" QuantityRequested="1"/>
    <WorkOrderComponents>
        <WorkOrderComponent ComponentQuantity="1.0" ItemID="000001"
            ProductClass="Supply" Uom="EA" YFC_NODE_NUMBER="1">
            <Extn RefurbCost="111"/>
        </WorkOrderComponent>
        <WorkOrderComponent ComponentQuantity="1.0" ItemID="000002"
            ProductClass="Supply" Uom="EA" YFC_NODE_NUMBER="2">
            <Extn NewSerial="000159-001" RefurbCost="111" SerialNo="000159-001"/>
        </WorkOrderComponent>
    </WorkOrderComponents>
</WorkOrder>
before returning - update the mwo line with refurb cost, refurb qty  
*/

public class NWCGProcessRefurbishmentOrders implements YIFCustomApi {
	/* (non-Javadoc)
	 * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties arg0) throws Exception {
	}
	private static Logger log = Logger.getLogger(NWCGProcessRefurbishmentOrders.class.getName());
	
	public Document processRefurbishmentWorkOrder(YFSEnvironment env,Document inXML) throws DOMException, Exception
	{
		if(log.isVerboseEnabled()) log.verbose("processRefurbishmentWorkOrder::Input Doc is:\n********\n"+ XMLUtil.getXMLString(inXML));
		if(log.isVerboseEnabled()) log.verbose("processRefurbishmentWorkOrder::*************");		
		if(log.isVerboseEnabled()) log.verbose("processRefurbishmentWorkOrder::inXML "+XMLUtil.getXMLString(inXML));
		//System.out.println("processRefurbishmentWorkOrder::inXML "+XMLUtil.getXMLString(inXML));

		Element elemWO = inXML.getDocumentElement();
		//System.out.println("elemWO :: in processRefurbishmentWorkOrder" + XMLUtil.getElementXMLString(elemWO));
		
		//added for CR 157 starts here 
		Element workOrderComponentsElem = inXML.createElement("WorkOrderComponents");
		NodeList workOrderComponentNL = elemWO.getElementsByTagName("WorkOrderComponent");
		//System.out.println("workOrderComponentNL length :: " + workOrderComponentNL.getLength());
		
		int workOrderComponentNLLenInt = workOrderComponentNL.getLength();
		Node tempNode;
		Element workOrderComponentElem;
		String strComponentQuantity;
		
		//for each node if component qty is "0" or "" or null remove the node from the NL 
		for(int i=0; i<workOrderComponentNLLenInt; i++) {
			workOrderComponentElem = (Element) workOrderComponentNL.item(i);
			strComponentQuantity = workOrderComponentElem.getAttribute(NWCGConstants.COMPONENT_QUANTITY);
			if(strComponentQuantity.equals("") || strComponentQuantity.equals("0.0") || strComponentQuantity.equals("0")) {
				//workOrderComponentsElem.removeChild(workOrderComponentsElem.getElementsByTagName("WorkOrderComponent").item(i));
				//System.out.println("do not appendchild");
			} else {
				tempNode = workOrderComponentElem.cloneNode(true);
				workOrderComponentsElem.appendChild(tempNode);
			}
		}
		
		if(null != elemWO.getElementsByTagName("WorkOrderComponents").item(0)){
			elemWO.replaceChild(workOrderComponentsElem, elemWO.getElementsByTagName("WorkOrderComponents").item(0));
		}
		
		//System.out.println("elemWO after removechild ::" + XMLUtil.getElementXMLString(elemWO));
		
		//added for CR 157 ends here
		NodeList nlNWCGMasterWorkOrderLine = elemWO.getElementsByTagName("NWCGMasterWorkOrderLine");
		String strDestinationInventoryStatus = "" ;
		String strSourceLocationID = "";
		String strShipBydate = "" ;
		Element elemnlNWCGMasterWorkOrderLine = null ;
		String strSerialNo ="";
		
		// Gaurav added for CR 606 
		String strMasterOrderKey = "";
		
		if(nlNWCGMasterWorkOrderLine != null && nlNWCGMasterWorkOrderLine.getLength() > 0){
			// just one NWCGMasterWorkOrderLine element
			elemnlNWCGMasterWorkOrderLine = (Element) nlNWCGMasterWorkOrderLine.item(0);
			//System.out.println("inside =[" + XMLUtil.getElementXMLString(elemnlNWCGMasterWorkOrderLine) + "]");
			strDestinationInventoryStatus = elemnlNWCGMasterWorkOrderLine.getAttribute("DestinationInventoryStatus");
			strSourceLocationID = elemnlNWCGMasterWorkOrderLine.getAttribute("LocationID");
			strShipBydate = elemnlNWCGMasterWorkOrderLine.getAttribute("ShipByDate");
			strSerialNo = elemnlNWCGMasterWorkOrderLine.getAttribute("PrimarySerialNo");
			//added for CR 462 starts here 
			elemnlNWCGMasterWorkOrderLine.setAttribute("QuantityRequested", elemWO.getAttribute("QuantityRequested"));
			//added for CR 462 ends here
			
			// Gaurav added for CR 606 
			strMasterOrderKey = elemnlNWCGMasterWorkOrderLine.getAttribute("MasterWorkOrderKey");
			//System.out.println("strSerialNo-0 =[" + strSerialNo + "]");
		}
		strSerialNo = strSerialNo.trim();
		//System.out.println("strSerialNo-1 =[" + strSerialNo + "]");
		//System.out.println("elemnlNWCGMasterWorkOrderLine =[" + XMLUtil.getElementXMLString(elemnlNWCGMasterWorkOrderLine) + "]");
		updateMasterWorkOrderLineCostAndQuantity(env,elemnlNWCGMasterWorkOrderLine);

		String strNode = elemWO.getAttribute("Node");
		String strItemID = elemWO.getAttribute("ItemID");
		String strPC = elemWO.getAttribute("ProductClass");
		String strRequestQty = elemWO.getAttribute("QuantityRequested");
		String strUOM = elemWO.getAttribute("Uom");
		String strUserID = elemWO.getAttribute("UserID");
		String RRPFlag = NWCGRefurbHelper.get_RRP_Flag(env,strNode);

		//System.out.println("strUOM=[" + strUOM + "]");

		/* when the destination inventory status is NOT RFI - 
		 * we just need to move the inventory from Loc # 1 to Loc # 2, as soon as we move the inventory with the status 
		 * NRFI to Loc # 2, the inventory transition rule will change the inventory status from NRFI to NRFI-RFB
		 * now change this inventory status from NRFI-RFB to the destination inventory status as enterd by the user
		*/
		//System.out.println("strDestinationInventoryStatus =[" + strDestinationInventoryStatus + "]");
		//System.out.println("NWCGConstants.RFI_STATUS =[" + NWCGConstants.RFI_STATUS + "]");
		if(!strDestinationInventoryStatus.equals(NWCGConstants.RFI_STATUS)){
			//System.out.println("here-001 Status Not RFI");
			// move the inventory to the Virtual location i.e. location # 2 
			// none of the catalog items will be consumed
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strNode = " + strNode);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strSourceLocationID = " + strSourceLocationID);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strDestinationInventoryStatus = " + strDestinationInventoryStatus);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strItemID = " + strItemID);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strPC = " + strPC);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strRequestQty = " + strRequestQty);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::strUOM = " + strUOM);
			
			String strDestInvStatus = "";
			String strDest = "";
			
			if (strDestinationInventoryStatus.equals(NWCGConstants.DISP_UNSERVICE)){
				//strDestInvStatus = NWCGConstants.DISP_UNSERVICE_INT;
				strDest = "UNS-1";
			}
			if (strDestinationInventoryStatus.equals(NWCGConstants.DISP_UNSERVICE_NWT)){
				//strDestInvStatus = NWCGConstants.DISP_UNSERVICE_NWT_INT;
				strDest = "UNSNWT-1";
			}
			
			// passing the status as NRFI-RFB because the inventory will always be in NRFI-RFB status
			Document doc = getMoveLocationUNSXML(env,strNode,strSourceLocationID,NWCGConstants.NRFI_RFB_STATUS,strItemID,strPC,strRequestQty,strUOM,strShipBydate,strSerialNo,strDest );
			//System.out.println("getMoveLocationXML - UNS"+ XMLUtil.getXMLString(doc));
			
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::moveInventory - createMoveRequest IP :: "+ XMLUtil.getXMLString(doc));
			//moveInventory(env,doc,elemnlNWCGMasterWorkOrderLine,strItemID,strPC,strRequestQty,strUOM,strNode,strSourceLocationID,strUserID,NWCGConstants.NRFI_RFB_STATUS);
			Document out_MovDoc = CommonUtilities.invokeAPI(env,"moveLocationInventory",doc);
			// we need to change the inventory status from NRFI-RFB-I to the one entered by the user
			// we need to change the inventory at the location # 2 
			//Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env,elemWO,"NRFI-RFB-I",NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID),false,null,strNode);
			// need to switch this back once the putaway config are set
			//Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env,elemWO,NWCGConstants.RFI_STATUS,NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID),false,strDestInvStatus,strNode);
			Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env,elemWO,NWCGConstants.NRFI_RFB_STATUS,strDest,false,strDestinationInventoryStatus,strNode);
			
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::changeLocationInventory IP " + XMLUtil.getXMLString(docAdjust));
			// change the inventory status to destination inventory status 
			
			//System.out.println("processRefurbishmentWorkOrder InXML"+ XMLUtil.getXMLString(inXML));
			//System.out.println("DocAdjust "+ XMLUtil.getXMLString(docAdjust));

			changeInventoryStatus(env,docAdjust);
			CommonUtilities.invokeService(env,"NWCGRefurbStatusUpdateService",inXML);
			CommonUtilities.invokeService(env,"NWCGPostCreateRefurbishmentIssueFromRefurbWOMessageService",inXML);
			
			return inXML;
		}

		/*
		 * if the inventory is RFI - evident since it has passed the first condition and the item is not a KIT
		 * we will be processing the item as follows
		 * 1. If the item has catalog components - i.e. user has input some items in the popup
		 * we will create a refurb-kitting work order , confirm the work order and then will move the inventory to the Loc # 2
		 * 2. If the item doesnt have the components -
		 * we will simply change the inventory status to RFI and then move the inventory to the Loc # 2
		 * 3. Adjust out the NRFI-RFB inventory of the current item from the location 
		 */
		if((strUOM != null && (!strUOM.equals("KT"))) || (strSerialNo.equals(""))) {
			//System.out.println("here-002 strUOM Non-KT strUOM=["+strUOM+"]");
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders :: ALL CUSTOM NO YANTRA W.O. ");
			// cloning this document as we are modifying that in one of the functions are returning the same
			// this is not a kit 
			Document returnDoc = (Document) inXML.cloneNode(true);
			NodeList nlWorkOrderComponent = elemWO.getElementsByTagName("WorkOrderComponent");

			// Jay : As per discussion with Dave, we won't create any work order if items UOM is not KT
			// we will simply do inventiory adjustments and 
			// will consume inventory for all of the kit/component items
			// change the inventory status from NRFI-RFB to RFI .. and will be moved later on
			
			//System.out.println("elemWO =[" + XMLUtil.getElementXMLString(elemWO) + "]");
			Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env,elemWO,NWCGConstants.NRFI_RFB_STATUS,strSourceLocationID,false,NWCGConstants.RFI_STATUS,strNode);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::adjust location inventory for item which is not KIT and has no catalog items :: docAdjust :: " + XMLUtil.getXMLString(docAdjust));

			//System.out.println("docAdjust =[" + XMLUtil.getXMLString(docAdjust) + "]");
			changeInventoryStatus(env,docAdjust);
			
			// Jay: Get the list of all replaced components
			Map mapReplacedComponents = (Map) NWCGRefurbHelper.getComponentsList(elemWO,false,"OldSerialNo",null,"1");
			
			if(nlWorkOrderComponent != null && nlWorkOrderComponent.getLength() > 0){
				// consume all the components
				for(int index = 0 ; index < nlWorkOrderComponent.getLength() ; index++){
					Element elemComp = (Element) nlWorkOrderComponent.item(index);
					NWCGRefurbHelper.adjustLocationInventory(env,strNode,strSourceLocationID,NWCGConstants.RFI_STATUS,elemComp,"-"+elemComp.getAttribute("ComponentQuantity"),elemComp.getAttribute("ShipByDate"),elemComp.getAttribute("ItemID"),elemComp.getAttribute("ProductClass"),elemComp.getAttribute("Uom"));
				}
			}
			
			// move the inventory from location # 1 to Location # 2 
			Document doc = getMoveLocationXML(env,strNode,strSourceLocationID,NWCGConstants.RFI_STATUS,strItemID,strPC,strRequestQty,strUOM,strShipBydate,strSerialNo );
			
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::moveInventory - createMoveRequest IP :: "+ XMLUtil.getXMLString(doc));
			//moveInventory(env,doc,elemnlNWCGMasterWorkOrderLine,strItemID,strPC,strRequestQty,strUOM,strNode,strSourceLocationID,strUserID,NWCGConstants.RFI_STATUS);
			Document out_MovDoc = CommonUtilities.invokeAPI(env,"moveLocationInventory",doc);
			// adjust out the replaced component at the target location
			NWCGRefurbHelper.adjustInventoryFromMap(env,strNode,NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID),mapReplacedComponents);
			
            //Added by GN - 01/14/2008 - Creating Move Request to GENERAL-REFURB-2
			
			if (RRPFlag.equals("NO")){
				strSourceLocationID = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID);
				doc = getMoveRequestInputXML(env,strNode,strSourceLocationID,strDestinationInventoryStatus,strItemID,strPC,strRequestQty,strUOM,strShipBydate );
				Element rootMoveElem = doc.getDocumentElement();
				rootMoveElem.setAttribute("ForActivityCode","STORAGE");
				//System.out.println("Final Move Request XML "+ XMLUtil.getXMLString(doc));
				doc = CommonUtilities.invokeAPI(env,"createMoveRequest",doc);
	        }
			
			//System.out.println("processRefurbishmentWorkOrder InXML"+ XMLUtil.getXMLString(inXML));
			CommonUtilities.invokeService(env,"NWCGRefurbStatusUpdateService",inXML);
			CommonUtilities.invokeService(env,"NWCGPostCreateRefurbishmentIssueFromRefurbWOMessageService",inXML);
			//System.out.println("@@@@@@@@@ COMING HERE SUCESSFULLY FOR NON KIT @@@@@@@@@@@@@@@@");

			return returnDoc;

		} else {// the item is a KIT
			//System.out.println("here-003 item is KIT=["+strUOM+"]");
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders :: LEVERAGING YANTRA W.O FUNCTIONALITY");
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::Enterd the Else statement ");
			//System.out.println("elemnlNWCGMasterWorkOrderLine =[" + XMLUtil.getElementXMLString(elemnlNWCGMasterWorkOrderLine) + "]");
			//System.out.println("strItemID =[" + strItemID + "]");
			//System.out.println("strUOM =[" + strUOM + "]");
			//System.out.println("strRequestQty =[" + strRequestQty + "]");
			//System.out.println("strNode =[" + strNode + "]");
			Document doc = NWCGRefurbHelper.getRefurbishmentDekittingWorkOrderOrderXML(env,elemnlNWCGMasterWorkOrderLine,strItemID,strUOM,strPC,strRequestQty,strNode);
			//System.out.println("Dekitting WO XML "+ XMLUtil.getXMLString(doc));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::processRefurbishmentWorkOrder :: creating refurb DE KITTING WO with XML "+XMLUtil.getXMLString(doc));
			
			// before dekitting
			// removing all the component items from the xml which are not checked and are not serially tracked - those items will be 
			// reported missing on adjust location inventory console.
			// the items which are replaced will always be part of the last build kit 
			//System.out.println("elemWO XML "+XMLUtil.getElementXMLString(elemWO));
			NWCGRefurbHelper.removeMissingComponentItemsNotSeriallyTrackedfromDekittingWorkOrder(doc,elemWO);
			//System.out.println("Dekitting WO XML After RemoveMissingComponents"+ XMLUtil.getXMLString(doc));

			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::processRefurbishmentWorkOrder :: refurb DE KITTING WO XML after removing items which are reported missing but are serially tracked " +XMLUtil.getXMLString(doc));
			
			stampIncidentAndOtherDetailsOnWorkOrder(env,elemnlNWCGMasterWorkOrderLine,doc);
			NWCGConfirmWorkOrderMultipleItemWrapper oDekitting = new NWCGConfirmWorkOrderMultipleItemWrapper(doc,true);

			//create a dekitting work order
			//System.out.println("Dekitting input XML "+ XMLUtil.getXMLString(oDekitting.getCreateWorkOrderXML()));
			Document docCreateWO = createWorkOrder(env,oDekitting.getCreateWorkOrderXML());
			//System.out.println("Dekitting docCreateWO "+ XMLUtil.getXMLString(docCreateWO));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::create DE KITTING work order  :: " + XMLUtil.getXMLString(docCreateWO));

			// confirm the dekitting work order
			//System.out.println("hmReplacedAndMissingSerialComponents elemWO" + XMLUtil.getElementXMLString(elemWO));
			HashMap hmReplacedAndMissingSerialComponents = getReplacedOrMissingSerialComponentsMap(elemWO);
			//System.out.println("The size of hmReplacedAndMissingSerialComponents = "+hmReplacedAndMissingSerialComponents.size ( ) ) ; 
		    //System.out.println("If hmReplacedAndMissingSerialComponents empty = "+hmReplacedAndMissingSerialComponents.isEmpty ( )  ) ; 
		    //System.out.println("The elements of hmReplacedAndMissingSerialComponents are "+ hmReplacedAndMissingSerialComponents) ; 

			//Change me to add work order tag from custom call to getWorkOrder
			Document confrmWODoc = NWCGRefurbHelper.prepareConfirmWorkderOrderActivityXML(doc, elemnlNWCGMasterWorkOrderLine,docCreateWO,"REFURBISHMENT");
			//System.out.println("Dekitting confrmWODoc 1"+ XMLUtil.getXMLString(confrmWODoc));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::confirmWorkOrderActivity :: DEKITTING  IP "+ XMLUtil.getXMLString(confrmWODoc));
			
			HashMap hmItemComponentKey = NWCGRefurbHelper.prepareMapOfComponentItemAndComponentKey(confrmWODoc);
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::hmItemComponentKey ==> " + hmItemComponentKey);
			//System.out.println("Dekitting hmItemComponentKey ==> " + hmItemComponentKey);

			Element elemWOCs = (Element)XPathUtil.getNode(confrmWODoc,"WorkOrder/WorkOrderActivityDtl/WorkOrderComponents");
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::elemWOCs " + XMLUtil.getElementXMLString(elemWOCs));
			//System.out.println("Dekitting elemWOCs"+ XMLUtil.getElementXMLString(elemWOCs));
			
			if(elemWOCs != null)
				NWCGRefurbHelper.replaceChildElementsFromArray(elemWOCs,oDekitting.prepareWorkOrderComponentListForConfirmOrder(hmItemComponentKey),"WorkOrderComponent");
				
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::confirmWorkOrderActivity :: DEKITTING  IP "+ XMLUtil.getXMLString(confrmWODoc));
			/* GN - 04/19/09 Commented out for 8.0, as WMS 8.0 needs WorkOrderNo */
			//confrmWODoc.getDocumentElement().setAttribute("WorkOrderNo","");
			//System.out.println("Dekitting confrmWODoc 2"+ XMLUtil.getXMLString(confrmWODoc));
			Document DKITDoc = confirmWorkOrderActivity(env,confrmWODoc);
			//System.out.println("Dekitting DKITDoc "+ XMLUtil.getXMLString(DKITDoc));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::****** DE KITTING COMPLETED ******");

			Document docCreateRefurbKitWO = NWCGRefurbHelper.getCreateRefurbishmentKittingWorkOrderForCatalogAndComponentItemsXML(env,oDekitting.getDocumentElement(),inXML);
			//System.out.println("docCreateRefurbKitWO "+ XMLUtil.getXMLString(docCreateRefurbKitWO));

			stampIncidentAndOtherDetailsOnWorkOrder(env,elemnlNWCGMasterWorkOrderLine,docCreateRefurbKitWO);
			NWCGConfirmWorkOrderMultipleItemWrapper rfbKitting = new NWCGConfirmWorkOrderMultipleItemWrapper(docCreateRefurbKitWO,true);
			Document docCreateRefubKitOP = createWorkOrder(env,rfbKitting.getCreateWorkOrderXML());
			//System.out.println("docCreateRefurbKitOP "+ XMLUtil.getXMLString(docCreateRefubKitOP));

			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::createWorkOrder :: FINAL OP "+XMLUtil.getXMLString(docCreateRefubKitOP));
			confrmWODoc = NWCGRefurbHelper.prepareConfirmWorkderOrderActivityXML(doc, elemnlNWCGMasterWorkOrderLine,docCreateRefubKitOP,"REFURBISHMENT");
			//System.out.println("confrmWODoc "+ XMLUtil.getXMLString(confrmWODoc));
			hmItemComponentKey = NWCGRefurbHelper.prepareMapOfComponentItemAndComponentKey(confrmWODoc);
			//System.out.println("The size of hmItemComponentKey = "+hmItemComponentKey.size ( ) ) ; 
		    //System.out.println("If hmItemComponentKey empty = "+hmItemComponentKey.isEmpty ( )  ) ; 
		    //System.out.println("The elements of hmItemComponentKey are "+ hmItemComponentKey) ; 
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::hmItemComponentKey ==> " + hmItemComponentKey);
			elemWOCs = (Element)XPathUtil.getNode(confrmWODoc,"WorkOrder/WorkOrderActivityDtl/WorkOrderComponents");
			//System.out.println("NWCGProcessRefurbishmentOrders::elemWOCs " + XMLUtil.getElementXMLString(elemWOCs));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::elemWOCs " + XMLUtil.getElementXMLString(elemWOCs));
			
			if(elemWOCs != null)
				NWCGRefurbHelper.replaceChildElementsFromArray(elemWOCs,rfbKitting.prepareWorkOrderComponentListForConfirmOrder(hmItemComponentKey),"WorkOrderComponent");
			//System.out.println("Confirm WO "+ XMLUtil.getXMLString(confrmWODoc));
			if(log.isVerboseEnabled())
				log.verbose("NWCGProcessRefurbishmentOrders::confirmWorkOrderActivity :: IP KITTING "+ XMLUtil.getXMLString(confrmWODoc));
			// GN - 04/19/09 Commented out for 8.0, as WMS 8.0 HF37 needs WorkOrderNo 
			//confrmWODoc.getDocumentElement().setAttribute("WorkOrderNo","");
			Document KITDoc = confirmWorkOrderActivity(env,confrmWODoc);
			//System.out.println("Kitting KITDoc "+ XMLUtil.getXMLString(KITDoc));
			// move the inventory
			
			// move the inventory from location # 1 to Location # 2 
			//Document docgetMoveRequestInputXML = getMoveRequestInputXML(env,strNode,strSourceLocationID,NWCGConstants.RFI_STATUS,strItemID,strPC,strRequestQty,strUOM,strShipBydate);
			Document docgetMoveRequestInputXML = getMoveLocationXML(env,strNode,strSourceLocationID,NWCGConstants.RFI_STATUS,strItemID,strPC,strRequestQty,strUOM,strShipBydate,strSerialNo);
			if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::moveInventory - createMoveRequest IP :: "+ XMLUtil.getXMLString(docgetMoveRequestInputXML));
			//moveInventory(env,docgetMoveRequestInputXML,elemnlNWCGMasterWorkOrderLine,strItemID,strPC,strRequestQty,strUOM,strNode,strSourceLocationID,strUserID,NWCGConstants.RFI_STATUS);
			Document out_MovDoc = CommonUtilities.invokeAPI(env,"moveLocationInventory",docgetMoveRequestInputXML);
			// change the status of the left over items from RFI to NRFI-RFB - for component items which are replaced
			// delete the inventory for the components which are serially tracked and reported missing
			
			// Gaurav added strMasterOrderKey for CR 606 
			changeStatusOfReplacedComponentAndDeleteMissingSerialComponents(env,hmReplacedAndMissingSerialComponents,strNode,strSourceLocationID,strMasterOrderKey);
		
            //Added by GN - 01/14/2008 - Creating Move Request to GENERAL-REFURB-2
			if (RRPFlag.equals("NO")){
				strSourceLocationID = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID);
				doc = getMoveRequestInputXML(env,strNode,strSourceLocationID,strDestinationInventoryStatus,strItemID,strPC,strRequestQty,strUOM,strShipBydate );
				Element rootMoveElem = doc.getDocumentElement();
				rootMoveElem.setAttribute("ForActivityCode","STORAGE");
				//System.out.println("Final Move Request XML "+ XMLUtil.getXMLString(doc));
				doc = CommonUtilities.invokeAPI(env,"createMoveRequest",doc);
	        }
		}
		//System.out.println("Calling  NWCGRefurbStatusUpdateService: inXML="+ XMLUtil.getXMLString(inXML));
		CommonUtilities.invokeService(env,"NWCGRefurbStatusUpdateService",inXML); // update nwcgTracakbleItem table happens here

		//System.out.println("Calling  NWCGPostCreateRefurbishmentIssueFromRefurbWOMessageService");
		CommonUtilities.invokeService(env,"NWCGPostCreateRefurbishmentIssueFromRefurbWOMessageService",inXML);

		return inXML ;
	}

	private void updateMasterWorkOrderLineCostAndQuantity(YFSEnvironment env, Element elemnlNWCGMasterWorkOrderLine) throws Exception 
	{
		Document doc = XMLUtil.getDocument();
		
		//added for CR 462 starts here
		String strRequestedQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("QuantityRequested"));
		String strDestinationInventoryStatus = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("DestinationInventoryStatus"));
		String strAppendQuantity = "";
		double dAppendQuantity = 0.0;
		double dRequestedQuantity = 0.0;
		
		//System.out.println("strRequestedQuantity ::" + strRequestedQuantity);
		//System.out.println("strDestinationInventoryStatus ::" + strDestinationInventoryStatus);
		
		if(strRequestedQuantity.equals(""))
			strRequestedQuantity = "0.0";
		
		dRequestedQuantity = Double.parseDouble(strRequestedQuantity);
		
		if(strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.NWCG_RFI_DISPOSITION_CODE)){
			//System.out.println("RFI Disposistion qty update");
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("RFIRefurbQuantity"));
			//System.out.println("strAppendQuantity before add :: " + strAppendQuantity);
			if(strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			//System.out.println("dAppendQuantity before add :: " + dAppendQuantity);
			dAppendQuantity = dAppendQuantity+dRequestedQuantity;	
			//System.out.println("dAppendQuantity after add :: " + dAppendQuantity);
			strAppendQuantity = Double.toString(dAppendQuantity);
			//System.out.println("strAppendQuantity after add :: " + strAppendQuantity);
			elemnlNWCGMasterWorkOrderLine.setAttribute("RFIRefurbQuantity", strAppendQuantity);
		} else if(strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.SERIAL_STATUS_UNS_DESC)){ 
			//System.out.println("UNServiceable Disposistion qty update");
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("UNSRefurbQuantity"));
			if(strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			dAppendQuantity = dAppendQuantity+dRequestedQuantity;			
			strAppendQuantity = Double.toString(dAppendQuantity);			
			elemnlNWCGMasterWorkOrderLine.setAttribute("UNSRefurbQuantity", strAppendQuantity);
		}else if(strDestinationInventoryStatus.equalsIgnoreCase(NWCGConstants.DISP_UNSERVICE_NWT)){ 
			//System.out.println("DISP_UNSERVICE_NWT Disposistion qty update");
			strAppendQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("UNSNWTRefurbQuantity"));
			if(strAppendQuantity.equals(""))
				strAppendQuantity = "0.0";
			dAppendQuantity = Double.parseDouble(strAppendQuantity);
			dAppendQuantity = dAppendQuantity+dRequestedQuantity;			
			strAppendQuantity = Double.toString(dAppendQuantity);			
			elemnlNWCGMasterWorkOrderLine.setAttribute("UNSNWTRefurbQuantity", strAppendQuantity);
		}
		//added for CR 462 ends here
		
		String strRefurbishedQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("RefurbishedQuantity"));
		String strActualQuantity = StringUtil.nonNull(elemnlNWCGMasterWorkOrderLine.getAttribute("ActualQuantity"));
		
		if(strRefurbishedQuantity.equals(""))
			strRefurbishedQuantity = "0.0";
		
		if(strActualQuantity.equals(""))
			strActualQuantity = "0.0";
		
		double dRefurbishedQuantity = Double.parseDouble(strRefurbishedQuantity);
		double dActualQuantity = Double.parseDouble(strActualQuantity);
		
		if(dRefurbishedQuantity < dActualQuantity && dRefurbishedQuantity != 0){
			elemnlNWCGMasterWorkOrderLine.setAttribute("Status",NWCGConstants.NWCG_REFURB_MWOL_INTERMEDTIATE_STATUS);
		}else{
			elemnlNWCGMasterWorkOrderLine.setAttribute("Status",NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS);
		}
		
		//added for CR 502
		//if UpdateDLT is Y update revision no else set it to oldrevisionno
		String strUpdateDLT = elemnlNWCGMasterWorkOrderLine.getAttribute("UpdateDLT");
		//System.out.println("strUpdateDLT ::" + strUpdateDLT);
		//System.out.println("NWCGConstants.YES.equalsIgnoreCase(strUpdateDLT)=[" + NWCGConstants.YES.equalsIgnoreCase(strUpdateDLT) +"]");
		
		if(NWCGConstants.YES.equalsIgnoreCase(strUpdateDLT)){
			//update the revision no to user updated date , or current date
			String strRevisionNo = elemnlNWCGMasterWorkOrderLine.getAttribute("RevisionNo");
			String strOldRevisionNo = elemnlNWCGMasterWorkOrderLine.getAttribute("OldRevisionNo");
		    //System.out.println("strRevisionNo ::" + strRevisionNo);	
		    //System.out.println("strOldRevisionNo ::" + strOldRevisionNo);
		    //System.out.println("strRevisionNo.equalsIgnoreCase(strOldRevisionNo) ::" + strRevisionNo.equalsIgnoreCase(strOldRevisionNo));
			if(strRevisionNo.equalsIgnoreCase(strOldRevisionNo)){
				//set the revision no to current date if status is work order completed 
				String strStatus = elemnlNWCGMasterWorkOrderLine.getAttribute("Status");
				//System.out.println("strStatus");
				//System.out.println("NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS.equalsIgnoreCase(strStatus)" + NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS.equalsIgnoreCase(strStatus));
				if(NWCGConstants.NWCG_REFURB_MWOL_FINAL_STATUS.equalsIgnoreCase(strStatus)){
					//set todaysDate as the revisionno
					Calendar calendar = Calendar.getInstance();
			        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			        String strTodaysDate = dateFormat.format(calendar.getTime());
			        //System.out.println("Today: " + strTodaysDate);
			        elemnlNWCGMasterWorkOrderLine.setAttribute("RevisionNo", strTodaysDate);
				}
			}
		}  //else leave the updated revision no as is
		//added for CR 502 ends here
		
		Element rootElem = (Element)doc.importNode(elemnlNWCGMasterWorkOrderLine,true);
		doc.appendChild(rootElem);
		
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::Calling updateMasterWorkOrer with IP :: "+ XMLUtil.getXMLString(doc));
		//added for CR 462 testing remove later starts here
		//System.out.println("indoc for NWCGChangeMasterWorkOrderLineService" + XMLUtil.getXMLString(doc));
		//added for CR 462 testing remove later ends here
		CommonUtilities.invokeService(env,"NWCGChangeMasterWorkOrderLineService",doc);
	}

	/*
	 *  <NWCGMasterWorkOrderLine DestinationInventoryStatus=NWCGConstants.RFI_STATUS LocationID="FIRST-AID-1" MasterWorkOrderKey="20070613210522612666" 
	 *  MasterWorkOrderLineKey="20070619170807630140" QuantityRequested="1" /> 
	 */
	private void stampIncidentAndOtherDetailsOnWorkOrder(YFSEnvironment env,Element elemMWOLine, Document docCreateWOInput) throws Exception
	{
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders:: elemMWOLine ::" + elemMWOLine);
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders:: docCreateWOInput ::" + docCreateWOInput);
		
		Document inDoc = XMLUtil.createDocument("NWCGMasterWorkOrder");
		Element elem = inDoc.getDocumentElement();
		
		elem.setAttribute("MasterWorkOrderKey",elemMWOLine.getAttribute("MasterWorkOrderKey"));
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders:: elem ::" +XMLUtil.getXMLString(elem.getOwnerDocument()));
		Document docMWOD = CommonUtilities.invokeService(env,"NWCGGetMasterWorkOrderService",inDoc);
		
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders:: docMWOD ::" + XMLUtil.getXMLString(docMWOD));
		Element elemExtn = docCreateWOInput.createElement("Extn");
		docCreateWOInput.getDocumentElement().appendChild(elemExtn);
		
		elemExtn.setAttribute("ExtnRefurbCost",StringUtil.nonNull(elemMWOLine.getAttribute("RefurbCost")));
		elemExtn.setAttribute("ExtnMasterWorkOrderKey",StringUtil.nonNull(elemMWOLine.getAttribute("MasterWorkOrderKey")));
		elemExtn.setAttribute("ExtnMasterWorkOrderLineKey",StringUtil.nonNull(elemMWOLine.getAttribute("MasterWorkOrderLineKey")));
		
		Element elemMWOD = docMWOD.getDocumentElement();
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders:: elemMWOD ::" + elemMWOD);
		
		elemExtn.setAttribute("ExtnIncidentNo",StringUtil.nonNull(elemMWOD.getAttribute("IncidentNo")));
		elemExtn.setAttribute("ExtnIncidentYear",StringUtil.nonNull(elemMWOD.getAttribute("IncidentYear")));
		elemExtn.setAttribute("ExtnFsAcctCode",StringUtil.nonNull(elemMWOD.getAttribute("FSAccountCode")));
		elemExtn.setAttribute("ExtnBlmAcctCode",StringUtil.nonNull(elemMWOD.getAttribute("BLMAccountCode")));
		elemExtn.setAttribute("ExtnOtherAcctCode",StringUtil.nonNull(elemMWOD.getAttribute("OtherAccountCode")));
		elemExtn.setAttribute("ExtnIsRefurb","Y");
		elemExtn.setAttribute("ExtnOverrideCode",StringUtil.nonNull(elemMWOD.getAttribute("OverrideCode")));
		elemExtn.setAttribute("ExtnIncidentName",StringUtil.nonNull(elemMWOD.getAttribute("IncidentName")));
		
	}

	// Gaurav added strMasterOrderKey for CR 606 
	private void changeStatusOfReplacedComponentAndDeleteMissingSerialComponents(YFSEnvironment env, HashMap hmReplacedAndMissingSerialComponents, String strNode, String strSourceLocationID,String strMasterOrderKey) throws DOMException, Exception 
	{
		/*
		 * 1. Read all the lines one by one
		 * 2. If the element is a replaced item - change inventory status
		 * 3. If the element is a serially tracked missing item - delete the inventory
		 */
		if(log.isVerboseEnabled())
			log.verbose("NWCGProcessRefurbishmentOrders::changeStatusOfReplacedComponentAndDeleteMissingSerialComponents entered " + hmReplacedAndMissingSerialComponents);
		if(hmReplacedAndMissingSerialComponents != null){
			Collection collection = hmReplacedAndMissingSerialComponents.values();
			if(collection != null){
				Iterator itr = collection.iterator();
				while(itr.hasNext()){
					Element elem = (Element) itr.next();
					//System.out.println("elem :: in changeStatusOfReplacedComponentAndDeleteMissingSerialComponents" + XMLUtil.getElementXMLString(elem));
					String strReplaceComponent = StringUtil.nonNull(elem.getAttribute("ReplaceComponent"));
					String strSerialNo = StringUtil.nonNull(elem.getAttribute("OldSerialNo"));
					if((strReplaceComponent.equals("NA") && (!strSerialNo.equals(""))) || strReplaceComponent.equals("Y") ){
						// this is a replaced component - change the inventory status 
						Document docAdjust = NWCGRefurbHelper.getChangeLocationInventoryAttributesIP(env,elem,NWCGConstants.RFI_STATUS,strSourceLocationID,true,NWCGConstants.NWCG_NRFI_DISPOSITION_CODE,strNode); 
						if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::changeLocationInventory IP " + XMLUtil.getXMLString(docAdjust));
						// change the inventory status to destination inventory status 
						//System.out.println("docAdjust " + XMLUtil.getXMLString(docAdjust));
						changeInventoryStatus(env,docAdjust);
						
						// Gaurav added for CR 606
						createMasterWorkOrderLine(env,elem,strMasterOrderKey,strNode);
					}else if(strReplaceComponent.equals("N")){
						NWCGRefurbHelper.adjustLocationInventory(env,strNode,strSourceLocationID,NWCGConstants.RFI_STATUS,elem,"-"+"1",
								elem.getAttribute("ShipByDate"),elem.getAttribute("ItemID"),elem.getAttribute("ProductClass"),elem.getAttribute("Uom"));
					}
				}
			}// end if collection is not null
		}// end if map is not null
	}

	private HashMap getReplacedOrMissingSerialComponentsMap(Element documentElement) 
	{
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::getReplacedAndMissingSerialComponentsMap :: Entered calling generic getComponentList");
		return (HashMap) NWCGRefurbHelper.getComponentsList(documentElement,false,"OldSerialNo",null,"5");
	}

	private Document confirmWorkOrderActivity(YFSEnvironment env, Document confrmWODoc) throws Exception {
		//return CommonUtilities.invokeAPI(env,"NWCGProcessRefurbishmentOrders_confirmWorkOrderActivity","confirmWorkOrderActivity",confrmWODoc);
		return CommonUtilities.invokeAPI(env,"confirmWorkOrderActivity",confrmWODoc);
	}

	/*
	 * purely mapping method
	 */
	private Document getMoveRequestInputXML(YFSEnvironment env,String strNode, String strSourceLocationID, String strCurrentInvStatus, 
			String strItemID, String strPC, String strRequestQty, String strUOM,String strShipBydate) throws DOMException, Exception {
		Document rt_MoveRequest = XMLUtil.getDocument();
		Element el_MoveRequest=rt_MoveRequest.createElement("MoveRequest");
		rt_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("EnterpriseCode",NWCGConstants.ENTERPRISE_CODE);
		el_MoveRequest.setAttribute("ForActivityCode","VAS");
		el_MoveRequest.setAttribute("FromActivityGroup","VAS");
		el_MoveRequest.setAttribute("IgnoreOrdering","Y");
		el_MoveRequest.setAttribute("Node",strNode);
		el_MoveRequest.setAttribute("Priority","3");
		el_MoveRequest.setAttribute("Release","Y");
		el_MoveRequest.setAttribute("SourceLocationId",strSourceLocationID);
		String strDestinationLoc = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID);
		el_MoveRequest.setAttribute("TargetLocationId",strDestinationLoc);

		Element el_MoveRequestLines=rt_MoveRequest.createElement("MoveRequestLines");
		el_MoveRequest.appendChild(el_MoveRequestLines);

		Element el_MoveRequestLine=rt_MoveRequest.createElement("MoveRequestLine");
		el_MoveRequestLines.appendChild(el_MoveRequestLine);
		el_MoveRequestLine.setAttribute("EnterpriseCode",NWCGConstants.ENTERPRISE_CODE);
		el_MoveRequestLine.setAttribute("ShipByDate",strShipBydate);
		el_MoveRequestLine.setAttribute("InventoryStatus",strCurrentInvStatus);
		el_MoveRequestLine.setAttribute("ItemId",strItemID);
		el_MoveRequestLine.setAttribute("ProductClass",strPC);
		el_MoveRequestLine.setAttribute("RequestQuantity",strRequestQty);
		el_MoveRequestLine.setAttribute("SourceLocationId",strSourceLocationID);
		el_MoveRequestLine.setAttribute("TargetLocationId",strDestinationLoc);
		el_MoveRequestLine.setAttribute("UnitOfMeasure",strUOM);
		
		return rt_MoveRequest;
	}
	
	private Document getMoveLocationUNSXML(YFSEnvironment env,String strNode, String strSourceLocationID, String strCurrentInvStatus, 
			String strItemID, String strPC, String strRequestQty, String strUOM,String strShipBydate,String strSerialNo,String strDest) throws DOMException, Exception {
		/***************************************************/
		Document rt_MoveRequest = XMLUtil.getDocument();
		Element el_MoveRequest=rt_MoveRequest.createElement("MoveLocationInventory");
		String strDestinationLoc = strDest;
		rt_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("Node",strNode);
		el_MoveRequest.setAttribute("EnterpriseCode",NWCGConstants.ENTERPRISE_CODE);

		Element ElemSource = rt_MoveRequest.createElement("Source");
		el_MoveRequest.appendChild(ElemSource);
		Element ElemDest = rt_MoveRequest.createElement("Destination");
		el_MoveRequest.appendChild(ElemDest);
		  
		ElemSource.setAttribute("LocationId",strSourceLocationID);
		  	  
		Element ElemInv = rt_MoveRequest.createElement("Inventory");
		ElemSource.appendChild(ElemInv);
		ElemInv.setAttribute("ShipByDate",strShipBydate);
		ElemInv.setAttribute("InventoryStatus",strCurrentInvStatus);
		ElemInv.setAttribute("Quantity",strRequestQty);
		ElemDest.setAttribute("LocationId",strDestinationLoc);
		  
		  
		Element ElemInvItem = rt_MoveRequest.createElement("InventoryItem");
		ElemInv.appendChild(ElemInvItem);
		ElemInvItem.setAttribute("ItemID",strItemID);
		ElemInvItem.setAttribute("ProductClass",strPC);
		ElemInvItem.setAttribute("UnitOfMeasure",strUOM);
		  
    
	 	if(strSerialNo!=""){
			Element ElemSerialList = rt_MoveRequest.createElement("SerialList");
			ElemInv.appendChild(ElemSerialList);
			Element ElemSerialDetail = rt_MoveRequest.createElement("SerialDetail");
			ElemSerialList.appendChild(ElemSerialDetail);
			ElemSerialDetail.setAttribute("SerialNo",strSerialNo);
			ElemInv.setAttribute("Quantity","1.00");
		}//End if ---strSerialNo!=""
		//System.out.println("MoveLocationInv Doc "+ XMLUtil.getXMLString(rt_MoveRequest));			    	 
		return rt_MoveRequest;
		/**************************************************************************/
	}

	private Document getMoveLocationXML(YFSEnvironment env,String strNode, String strSourceLocationID, String strCurrentInvStatus, 
		String strItemID, String strPC, String strRequestQty, String strUOM,String strShipBydate,String strSerialNo) throws DOMException, Exception {
		
		/***************************************************/
		Document rt_MoveRequest = XMLUtil.getDocument();
		Element el_MoveRequest=rt_MoveRequest.createElement("MoveLocationInventory");
		String strDestinationLoc = NWCGRefurbHelper.deriveTargetLocationFromSourceLocation(env,strSourceLocationID);
		rt_MoveRequest.appendChild(el_MoveRequest);
		el_MoveRequest.setAttribute("Node",strNode);
		el_MoveRequest.setAttribute("EnterpriseCode",NWCGConstants.ENTERPRISE_CODE);

		Element ElemSource = rt_MoveRequest.createElement("Source");
		el_MoveRequest.appendChild(ElemSource);
		Element ElemDest = rt_MoveRequest.createElement("Destination");
		el_MoveRequest.appendChild(ElemDest);
		  
		ElemSource.setAttribute("LocationId",strSourceLocationID);
		  	  
		Element ElemInv = rt_MoveRequest.createElement("Inventory");
		ElemSource.appendChild(ElemInv);
		ElemInv.setAttribute("ShipByDate",strShipBydate);
		ElemInv.setAttribute("InventoryStatus",strCurrentInvStatus);
		ElemInv.setAttribute("Quantity",strRequestQty);
		ElemDest.setAttribute("LocationId",strDestinationLoc);
		  
		Element ElemInvItem = rt_MoveRequest.createElement("InventoryItem");
		ElemInv.appendChild(ElemInvItem);
		ElemInvItem.setAttribute("ItemID",strItemID);
		ElemInvItem.setAttribute("ProductClass",strPC);
		ElemInvItem.setAttribute("UnitOfMeasure",strUOM);
		  
	    //FYI - TagAttribute1 is "LotAttribute1" represents 'Manufacturers name'
		//	- TagAttribute2 is "LotAttribute3" represents 'Manufacturer Model'
		//	- TagAttribute3 is "Lot Number" for tracking purpose
		//	- TagAttribute4 is "Revision No" for Last Tested Date
		  
		/*
		Element ElemTag=doc_MoveRequest.createElement("TagDetail");
		ElemInv.appendChild(ElemTag);
	   	if(itemInfo.get("LotAttribute1")!=null)
	   		ElemTag.setAttribute("LotAttribute1",itemInfo.get("LotAttribute1").toString());
	    if(itemInfo.get("LotAttribute2")!=null)
	    	ElemTag.setAttribute("LotAttribute2",itemInfo.get("LotAttribute2").toString());
	    if(itemInfo.get("LotAttribute3")!=null)
	    	ElemTag.setAttribute("LotAttribute3",itemInfo.get("LotAttribute3").toString());
	    if(itemInfo.get("LotNumber")!=null)
	    	ElemTag.setAttribute("LotNumber",itemInfo.get("LotNumber").toString());
	    if(itemInfo.get("RevisionNo")!=null)
			ElemTag.setAttribute("RevisionNo",itemInfo.get("RevisionNo").toString());
	    if(itemInfo.get("BatchNo")!=null)
			ElemTag.setAttribute("BatchNo",itemInfo.get("BatchNo").toString()); 
	    */
	      
	 	if(strSerialNo!=""){
			Element ElemSerialList = rt_MoveRequest.createElement("SerialList");
			ElemInv.appendChild(ElemSerialList);
			Element ElemSerialDetail = rt_MoveRequest.createElement("SerialDetail");
			ElemSerialList.appendChild(ElemSerialDetail);
			ElemSerialDetail.setAttribute("SerialNo",strSerialNo);
			ElemInv.setAttribute("Quantity","1.00");
		}//End if ---strSerialNo!="" 
		//System.out.println("MoveLocationInv Doc "+ XMLUtil.getXMLString(rt_MoveRequest));			    	 
		return rt_MoveRequest;
		/**************************************************************************/
	}

	private void moveInventory(YFSEnvironment env, Document inDocCreateMoveReq, Element elemnlNWCGMasterWorkOrderLine, String strItemID,String strPC, String strRequestQty, String strUOM,String strNode,String strSourceLocationID,String strUserID,String strDestinationInventoryStatus) throws Exception {
		//	move the inventory
		String strMoveRequestKey = createMoveRequest(env,inDocCreateMoveReq);
		Document doc = NWCGRefurbHelper.getCompleteTaskXML(env,elemnlNWCGMasterWorkOrderLine,strMoveRequestKey,strItemID,strPC,strUOM,strNode,
				strSourceLocationID,strUserID,strDestinationInventoryStatus);
		// complete task
		if(log.isVerboseEnabled())
			log.verbose("NWCGProcessRefurbishmentOrders::moveInventory :: completeTask document " + XMLUtil.getXMLString(doc));
		completeTask(env,doc);
		// release move request
		//releaseMoveRequest(env,strMoveRequestKey);
	}
	
	private void changeInventoryStatus(YFSEnvironment env,Document inXML) throws Exception {
		if(log.isVerboseEnabled())
			log.verbose("NWCGProcessRefurbishmentOrders::changeInventoryStatus inXML=" + XMLUtil.getXMLString(inXML));
		//System.out.println("changeInventoryStatus inXML=[" + XMLUtil.getXMLString(inXML) + "]");
		//CR 502 change starts here 
		//get the revision No and oldrevision No if not equal then add tagdetail to toinventory
		
		Element fromInventoryElem = (Element) inXML.getDocumentElement().getElementsByTagName("FromInventory").item(0);
		Element tagDetailElem = (Element) fromInventoryElem.getElementsByTagName("TagDetail").item(0);
		
		if(null != tagDetailElem) {
			String strRevisionNo = tagDetailElem.getAttribute("RevisionNo");
			String strOldRevisionNo = tagDetailElem.getAttribute("OldRevisionNo");
			//System.out.println("strRevisionNo :: " + strRevisionNo);
			//System.out.println("strOldRevisionNo :: " + strOldRevisionNo);
			//System.out.println("if :: [" + !strRevisionNo.equalsIgnoreCase(strOldRevisionNo) + "]");
	
			if(!strRevisionNo.equalsIgnoreCase(strOldRevisionNo)){
				//set fromInventory revisionno to oldrevisionno
				tagDetailElem.setAttribute("RevisionNo", strOldRevisionNo);
				Element toInventoryElem = (Element) inXML.getDocumentElement().getElementsByTagName("ToInventory").item(0);
				Element tagDetailToElem = (Element) tagDetailElem.cloneNode(true);
				tagDetailToElem.setAttribute("RevisionNo", strRevisionNo);
				toInventoryElem.appendChild(tagDetailToElem);
			}
		}
		//System.out.println("changeLocationInventoryAttributes input XML = " + XMLUtil.getXMLString(inXML));
		//CR 502 change ends here
		CommonUtilities.invokeAPI(env,"changeLocationInventoryAttributes",inXML);
	}
	
	private void completeTask(YFSEnvironment env,Document inXML) throws Exception {
		/* the yantra executes the task in two steps - registerTaskInProgress
		 * Step 1 : picking - task status In pProgress - registerTaskCompletion
		 * Step 2 : Disposition - Task completion
		 * 
		*/
		//if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::completeTask calling registerTaskInProgress" );
		//CommonUtilities.invokeAPI(env,"registerTaskInProgress",inXML);
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::completeTask calling registerTaskCompletion" );
		//System.out.println("registerTaskCompletion XML*** "+ XMLUtil.getXMLString(inXML));
		CommonUtilities.invokeAPI(env,"registerTaskCompletion",inXML);
		//System.out.println("After registerTaskCompletion XML*** ");
		
	}

	private String createMoveRequest(YFSEnvironment env, Document inXML) throws Exception {
		//System.out.println("MOVE Request XML*** "+ XMLUtil.getXMLString(inXML));
		Document doc = CommonUtilities.invokeAPI(env,"createMoveRequest",inXML);
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::createMoveRequest :: " + XMLUtil.getXMLString(doc));
		return XPathUtil.getString(doc.getDocumentElement(),"/MoveRequest/@MoveRequestKey");
	}

	private Document createWorkOrder(YFSEnvironment env,Document inXML) throws Exception {
		if(log.isVerboseEnabled()) log.verbose("NWCGProcessRefurbishmentOrders::createWorkOrder :: IP "+ XMLUtil.getXMLString(inXML));
		return CommonUtilities.invokeAPI(env,"createWorkOrder",inXML);
	}
	
	// Gaurav added for CR 606 START
	private static void  createMasterWorkOrderLine(YFSEnvironment env,Element elemWO,String strMasterWorkOrderKey,String strNode) throws Exception {
		String strSecondrySerialNo1 = "";
		String strManufactureName = "";
		String strManufacturerModel= "";
		String strOwnerUnitId= "";
		String strLotNo= "";
		String strRevisionNo= "";
		String strShortDescription= "";
		String strManufacturingDate = "";
		String strOldSerialNo = elemWO.getAttribute("OldSerialNo");
		String strItemId = elemWO.getAttribute("ItemID");
		
		Document serialDoc = XMLUtil.createDocument("Serial");
		Element serialRoot = serialDoc.getDocumentElement();
		serialRoot.setAttribute("SerialNo", strOldSerialNo);
		
		Document resultDoc = CommonUtilities.invokeAPI(env, "getSerialList", serialDoc);
		NodeList serial = resultDoc.getElementsByTagName("Serial");
		if(serial != null && serial.getLength() > 0){	
			Element eleSerial = (Element)serial.item(0);
			strSecondrySerialNo1 = eleSerial.getAttribute("SecondarySerial1");
			NodeList tag = eleSerial.getElementsByTagName("TagDetail");
			if(tag!= null && tag.getLength() > 0){
				Element eleTag = (Element)tag.item(0);
				strManufactureName = eleTag.getAttribute("LotAttribute1");
				strManufacturerModel = eleTag.getAttribute("LotAttribute3");
				strOwnerUnitId = eleTag.getAttribute("LotAttribute2");
				strLotNo = eleTag.getAttribute("LotNumber");
				strRevisionNo = eleTag.getAttribute("RevisionNo");
				strManufacturingDate = eleTag.getAttribute("ManufacturingDate");
			}
		}
		Document itemDoc = XMLUtil.createDocument("Item");
		Element itemRoot = itemDoc.getDocumentElement();
		itemRoot.setAttribute("ItemID", strItemId);
		itemRoot.setAttribute("UnitOfMeasure", elemWO.getAttribute("Uom"));
		itemRoot.setAttribute("OrganizationCode","NWCG");
		Document itemResultDoc = CommonUtilities.invokeAPI(env, "getItemDetails", itemDoc);
		NodeList primaryInfo = itemResultDoc.getElementsByTagName("PrimaryInformation");
		if(primaryInfo != null && primaryInfo.getLength() > 0){
			Element elePrimaryInfo = (Element)primaryInfo.item(0);
			strShortDescription = elePrimaryInfo.getAttribute("ShortDescription");
		}
		Document retDoc = XMLUtil.createDocument("NWCGMasterWorkOrderLine");
		Element eleRoot = retDoc.getDocumentElement();
		eleRoot.setAttribute("MasterWorkOrderKey", strMasterWorkOrderKey);
		eleRoot.setAttribute("Node", strNode);
		eleRoot.setAttribute("ItemID", strItemId);
		eleRoot.setAttribute("ItemDesc",strShortDescription);
		eleRoot.setAttribute("PrimarySerialNo", strOldSerialNo);
		eleRoot.setAttribute("ProductClass", elemWO.getAttribute("ProductClass"));
		eleRoot.setAttribute("UnitOfMeasure", elemWO.getAttribute("Uom"));
		eleRoot.setAttribute("Status", "Awaiting Work Order Creation");
		eleRoot.setAttribute("ActualQuantity", elemWO.getAttribute("ComponentQuantity"));
		eleRoot.setAttribute("SecondrySerialNo1", strSecondrySerialNo1);
		eleRoot.setAttribute("ManufacturerName", strManufactureName);
		eleRoot.setAttribute("ManufacturerModel", strManufacturerModel);
		eleRoot.setAttribute("OwnerUnitID", strOwnerUnitId);
		eleRoot.setAttribute("LotNo", strLotNo);
		eleRoot.setAttribute("RevisionNo", strRevisionNo);
		eleRoot.setAttribute("ManufacturingDate", strManufacturingDate);
		eleRoot.setAttribute("IsReplacedItem", "Y");
		CommonUtilities.invokeService(env, "NWCGCreateMasterWorkOrderLineService", retDoc);
		
		//Updating status of the new  WorkOrderline serial no
		//System.out.println("Updating status of the new  WorkOrderline serial no");
		Document updateDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element eleUpdate = updateDoc.getDocumentElement();
		eleUpdate.setAttribute("ItemID", strItemId);
		eleUpdate.setAttribute("SerialNo", strOldSerialNo);
		eleUpdate.setAttribute("SecondarySerial",strSecondrySerialNo1);
		eleUpdate.setAttribute("SerialStatus","W");
		eleUpdate.setAttribute("SerialStatusDesc", "Workordered");
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, updateDoc);
		
		//Updating kit information for newly replaced item
		
		//System.out.println("Updating kit information for newly replaced item");
		Document inDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element inDocRoot = inDoc.getDocumentElement();
		inDocRoot.setAttribute("ItemID", strItemId);
		inDocRoot.setAttribute("SerialNo", strOldSerialNo);
		inDocRoot.setAttribute("SecondarySerial", strSecondrySerialNo1);
		Document outDoc = CommonUtilities.invokeService(env,"NWCGGetTrackableInventoryRecordService", inDoc);
		
		Element resultRoot = outDoc.getDocumentElement();
		String strKitItemId = resultRoot.getAttribute("KitItemID");
		String strNewSerialNo = elemWO.getAttribute("SerialNo");
		String strKitSerialNo = resultRoot.getAttribute("KitSerialNo");
		Document trackDoc = XMLUtil.createDocument("NWCGTrackableItem");
		Element eleTrackableItemRoot = trackDoc.getDocumentElement();
		eleTrackableItemRoot.setAttribute("ItemID", strItemId);
		eleTrackableItemRoot.setAttribute("SerialNo", strNewSerialNo);
		eleTrackableItemRoot.setAttribute("SecondarySerial",elemWO.getAttribute("SecondarySerialNo0"));
		eleTrackableItemRoot.setAttribute("KitItemID", strKitItemId);
		eleTrackableItemRoot.setAttribute("KitSerialNo", strKitSerialNo);
		CommonUtilities.invokeService(env, NWCGConstants.NWCG_UPDATE_TRACKABLE_INVENTORY_SERVICE, trackDoc);
	}
   // Gaurav added for CR 606 END
}
