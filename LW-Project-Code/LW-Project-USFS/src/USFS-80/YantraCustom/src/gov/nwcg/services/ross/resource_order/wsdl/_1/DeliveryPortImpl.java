package gov.nwcg.services.ross.resource_order.wsdl._1;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.nwcg.icbs.yantra.webservice.ob.msg.NWCGDeliverOperationResultsOB;

import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationReq;
import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationResp;

//@javax.jws.HandlerChain(file = "ICBSRInboundAsyncHandler.xml")
/*@javax.jws.WebService (
		endpointInterface="gov.nwcg.services.ross.resource_order.wsdl._1.DeliveryInterface",                                                        
			 targetNamespace="http://nwcg.gov/services/ross/resource_order/wsdl/1.1",                                                                    
			 serviceName="DeliveryService",                                        
			 portName="DeliveryPort",                                              
			 wsdlLocation="WEB-INF/wsdl/Delivery_Synchronous.wsdl")*/                
public class DeliveryPortImpl {
	@Resource
	WebServiceContext wsContext;

    public DeliverOperationResultsResp deliverOperationResult(DeliverOperationResultsReq body) {
    	/*Jay: sleeping the current thread because it has been observed from our log files that we are getting delivery response from ROSS
    	 * before getting message ack and as per ROSS team they typically send response after 3-4 secs.
    	
    	try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        //System.out.println("Deliver operation result, distId="+body.getDistributionID());
        //NWCGDeliverOperationResultsOB ob = new NWCGDeliverOperationResultsOB();
        //if (wsContext.getMessageContext() instanceof SOAPMessageContext) {
        //	ob.setContext((SOAPMessageContext)wsContext.getMessageContext());
        //}        
        
        //DeliverOperationResultsResp resp = ob.process(body);
        //System.out.println("Deliver operation result returns"+resp.toString());
        System.out.println("FATAL ERROR: You should never see this.");
        System.err.println("FATAL ERROR: You should never see this.");
    	return null;
    }

    public DeliverNotificationResp deliverNotification(DeliverNotificationReq body) {
        return null;
    }
}