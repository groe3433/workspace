package gov.nwcg.services.ross.resource_order.wsdl._1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
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

public class ICBSRInboundAsyncPortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncService _service = null;
        private gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncInterface _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getICBSRInboundAsyncPort();
        }

        public gov.nwcg.services.ross.resource_order.wsdl._1.ICBSRInboundAsyncInterface getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://nwcg.gov/services/ross/resource_order/wsdl/1.1", "ICBSRInboundAsyncPort");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if(!dispatchEndpointUrl.equals(proxyEndpointUrl))
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, proxyEndpointUrl);
            }
            return _dispatch;
        }

        public String getEndpoint() {
            BindingProvider bp = (BindingProvider) _proxy;
            return (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
        }

        public void setEndpoint(String endpointUrl) {
            BindingProvider bp = (BindingProvider) _proxy;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            if(_dispatch != null ) {
            bp = (BindingProvider) _dispatch;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }
    }

    public ICBSRInboundAsyncPortProxy() {
        _descriptor = new Descriptor();
    }

    public ICBSRInboundAsyncPortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public ServicePingResp servicePing(ServicePingReq body) {
        return _getDescriptor().getProxy().servicePing(body);
    }

    public StatusNFESResourceRequestResp statusNFESResourceRequest(StatusNFESResourceRequestReq body) {
        return _getDescriptor().getProxy().statusNFESResourceRequest(body);
    }

    public DeliverOperationResultsResp deliverOperationResult(DeliverOperationResultsReq body) {
        return _getDescriptor().getProxy().deliverOperationResult(body);
    }

    public MessageAcknowledgement placeResourceRequestExternal(PlaceResourceRequestExternalReq body) {
        return _getDescriptor().getProxy().placeResourceRequestExternal(body);
    }

    public GetOperationResultsResp getOperationResults(GetOperationResultsReq body) {
        return _getDescriptor().getProxy().getOperationResults(body);
    }

    public ResourceOrderResponseType cancelResourceRequest(CancelResourceRequestReq body) {
        return _getDescriptor().getProxy().cancelResourceRequest(body);
    }

    public RetrieveResourceRequestResp retrieveResourceRequest(RetrieveResourceRequestReq body) {
        return _getDescriptor().getProxy().retrieveResourceRequest(body);
    }

    public DeliverNotificationResp deliverNotification(DeliverNotificationReq body) {
        return _getDescriptor().getProxy().deliverNotification(body);
    }

}