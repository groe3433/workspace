/**
 * 
 */
package com.nwcg.icbs.yantra.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.vas.japi.ue.VASBeforeCreateWorkOrderUE;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author jsk
 * *
 */
public class NWCGVASBeforeCreateWorkOrderUE implements VASBeforeCreateWorkOrderUE {

    private static Logger log = Logger.getLogger(NWCGVASBeforeCreateWorkOrderUE.class.getName());
	
/* 
<WorkOrder AutoRelease="Y" DocumentType="7001" EnterpriseCode="NWCG"
    EnterpriseCodeForComponent="NWCG" EnterpriseInvOrg="NWCG"
    FinishNoLaterThan="" IgnoreOrdering="Y" IgnoreRunQuantity=" "
    InvUpdateActivityCode="VAS" ItemID="000870" NodeKey="CORMK"
    NumberOfActivities="" Priority="3" ProductClass="Supply"
    Purpose="STOCK" QtyAvailToBuild="" QuantityRequested="1.0"
    ReasonCode="" ReasonText="" Segment="" SegmentType=""
    ServiceItemGroupCode="KIT" ServiceItemID="KITTING"
    StartNoEarlierThan="2010-03-24T22:37:46" Uom="KT"
    WorkOrderMode="menu" WorkOrderNo="">
    <WorkOrderComponents>
        <WorkOrderComponent ComponentQuantity="1.0" ItemID="000148"
            ProductClass="Supply" Uom="EA" YFC_NODE_NUMBER="1"/>
        <WorkOrderComponent ComponentQuantity="1.0" ItemID="000529"
            ProductClass="Supply" Uom="EA" YFC_NODE_NUMBER="2"/>
        <WorkOrderComponent ComponentQuantity="1.0" ItemID="003870"
            ProductClass="Supply" Uom="KT" YFC_NODE_NUMBER="3"/>
    </WorkOrderComponents>
    <Extn ExtnLocationId="VAS-RETURN"/>
    <WorkOrderActivities>
        <WorkOrderActivity ActivityCode="VAS" ActivitySeqNo="1" YFC_NODE_NUMBER="1"/>
    </WorkOrderActivities>
</WorkOrder>
*/  
	/* requirements
	1. NodeKey="CORMK" is configured for "NWCG_MULTI_VAS" Common Code Type
	2. ExtnLocationId is NOT null or blank
	--> If all conditions are met,

	<WorkOrderActivities>
	  <WorkOrderActivity ActivityCode="" ActivitySeqNo="" WorkOrderActivityKey="">
		<WorkOrderActivityDtls>
		  <WorkOrderActivityDtl ActivityCode="" ActivityLocationId="" QuantityRequested="" SerialNo="" /> 
		</WorkOrderActivityDtls>
	  </WorkOrderActivity>
	</WorkOrderActivities>
	*/

	public Document beforeCreateWorkOrder(YFSEnvironment env, Document inXML) throws YFSUserExitException
	{
		if(log.isVerboseEnabled()) log.verbose("NWCGVASBeforeCreateWorkOrderUE Input XML "+ XMLUtil.getXMLString(inXML));
		//System.out.println("NWCGVASBeforeCreateWorkOrderUE Input ==>" + XMLUtil.getXMLString(inXML));

		String extnLocationId = "";
		String nodeKey = "";
		String quantityRequested = "";
	    String activityCode = "";

		try
		{
			Element root = inXML.getDocumentElement();
			nodeKey = root.getAttribute("NodeKey");
			quantityRequested = root.getAttribute("QuantityRequested");

			NodeList nlExtn = root.getElementsByTagName("Extn");
			if(nlExtn != null && nlExtn.getLength() > 0)
			{
				Element elemExtn = (Element) nlExtn.item(0);
				extnLocationId = elemExtn.getAttribute("ExtnLocationId");
				if(extnLocationId != null && !extnLocationId.equals(""))
				{
					NodeList nlWOActivities = root.getElementsByTagName("WorkOrderActivities");
					if(nlWOActivities != null && nlWOActivities.getLength() > 0)
					{
						Element elemWOActivities = (Element) nlWOActivities.item(0);
						NodeList nlWOActivity = elemWOActivities.getElementsByTagName("WorkOrderActivity");
						Element elemWOActivity = (Element) nlWOActivity.item(0);
						String strActivityCode = elemWOActivity.getAttribute("ActivityCode");

						Element ele_WOActivity = (Element) elemWOActivities.getElementsByTagName("WorkOrderActivity").item(0);
						Element ele_WorkOrderActivityDtls = inXML.createElement("WorkOrderActivityDtls");
						ele_WOActivity.appendChild(ele_WorkOrderActivityDtls);

						Element ele_WorkOrderActivityDtl = inXML.createElement("WorkOrderActivityDtl");
						ele_WorkOrderActivityDtls.appendChild(ele_WorkOrderActivityDtl);
						ele_WorkOrderActivityDtl.setAttribute("ActivityCode",strActivityCode);
						ele_WorkOrderActivityDtl.setAttribute("ActivityLocationId",extnLocationId);
						ele_WorkOrderActivityDtl.setAttribute("QuantityRequested",quantityRequested);
					}
				}
			}
		
		}catch(Exception E){
	    	if(log.isVerboseEnabled()) log.verbose("Generated exception "+E.toString());
	    	//System.out.println("Exception "+E.toString());
	    	throw new YFSUserExitException(E.toString());
	    }
		//System.out.println("NWCGVASBeforeCreateWorkOrderUE Return ==>" + XMLUtil.getXMLString(inXML));
		return inXML;
	}
}
