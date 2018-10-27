//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-08/21/2008 01:11 PM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order.wsdl._1;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import gov.nwcg.services.ross.common_types._1.MessageAcknowledgement;
import gov.nwcg.services.ross.common_types._1.ServicePingReq;
import gov.nwcg.services.ross.common_types._1.ServicePingResp;
import gov.nwcg.services.ross.resource_order._1.CancelResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.PlaceResourceRequestExternalReq;
import gov.nwcg.services.ross.resource_order._1.ResourceOrderResponseType;
import gov.nwcg.services.ross.resource_order._1.RetrieveResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.RetrieveResourceRequestResp;
import gov.nwcg.services.ross.resource_order._1.StatusNFESResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.StatusNFESResourceRequestResp;
import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationReq;
import gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationResp;

@WebService(name = "ICBSRInboundAsyncInterface", targetNamespace = "http://nwcg.gov/services/ross/resource_order/wsdl/1.1")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ICBSRInboundAsyncInterface {


    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.common_types._1.ServicePingResp
     */
    @WebMethod(operationName = "ServicePing", action = "ServicePing")
    @WebResult(name = "ServicePingResp", targetNamespace = "http://nwcg.gov/services/ross/common_types/1.1", partName = "body")
    public ServicePingResp servicePing(
        @WebParam(name = "ServicePingReq", targetNamespace = "http://nwcg.gov/services/ross/common_types/1.1", partName = "body")
        ServicePingReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order._1.StatusNFESResourceRequestResp
     */
    @WebMethod(operationName = "StatusNFESResourceRequest", action = "StatusNFESResourceRequest")
    @WebResult(name = "StatusNFESResourceRequestResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
    public StatusNFESResourceRequestResp statusNFESResourceRequest(
        @WebParam(name = "StatusNFESResourceRequestReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        StatusNFESResourceRequestReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order._1.DeliverOperationResultsResp
     */
    @WebMethod(operationName = "DeliverOperationResult", action = "DeliverOperationResult")
    @WebResult(name = "DeliverOperationResultsResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
    public DeliverOperationResultsResp deliverOperationResult(
        @WebParam(name = "DeliverOperationResultsReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        DeliverOperationResultsReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.common_types._1.MessageAcknowledgement
     */
    @WebMethod(operationName = "PlaceResourceRequestExternal", action = "PlaceResourceRequestExternal")
    @WebResult(name = "MessageAcknowledgement", targetNamespace = "http://nwcg.gov/services/ross/common_types/1.1", partName = "body")
    public MessageAcknowledgement placeResourceRequestExternal(
        @WebParam(name = "PlaceResourceRequestExternalReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        PlaceResourceRequestExternalReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp
     */
    @WebMethod(operationName = "GetOperationResults", action = "GetOperationResults")
    @WebResult(name = "GetOperationResultsResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
    public GetOperationResultsResp getOperationResults(
        @WebParam(name = "GetOperationResultsReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        GetOperationResultsReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order._1.ResourceOrderResponseType
     */
    @WebMethod(operationName = "CancelResourceRequest", action = "CancelResourceRequest")
    @WebResult(name = "CancelResourceRequestResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
    public ResourceOrderResponseType cancelResourceRequest(
        @WebParam(name = "CancelResourceRequestReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        CancelResourceRequestReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order._1.RetrieveResourceRequestResp
     */
    @WebMethod(operationName = "RetrieveResourceRequest", action = "RetrieveResourceRequest")
    @WebResult(name = "RetrieveResourceRequestResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
    public RetrieveResourceRequestResp retrieveResourceRequest(
        @WebParam(name = "RetrieveResourceRequestReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order/1.1", partName = "body")
        RetrieveResourceRequestReq body);

    /**
     * 
     * @param body
     * @return
     *     returns gov.nwcg.services.ross.resource_order_notification._1.DeliverNotificationResp
     */
    @WebMethod(operationName = "DeliverNotification", action = "DeliverNotification")
    @WebResult(name = "DeliverNotificationResp", targetNamespace = "http://nwcg.gov/services/ross/resource_order_notification/1.1", partName = "body")
    public DeliverNotificationResp deliverNotification(
        @WebParam(name = "DeliverNotificationReq", targetNamespace = "http://nwcg.gov/services/ross/resource_order_notification/1.1", partName = "body")
        DeliverNotificationReq body);

}
