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
import gov.nwcg.services.ross.resource_order._1.AddIncidentDocumentationReq;
import gov.nwcg.services.ross.resource_order._1.AuthenticateUserReq;
import gov.nwcg.services.ross.resource_order._1.AuthenticateUserResp;
import gov.nwcg.services.ross.resource_order._1.CancelResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.CreateIncidentAndRequestReq;
import gov.nwcg.services.ross.resource_order._1.CreateIncidentReq;
import gov.nwcg.services.ross.resource_order._1.CreateIncidentRequestAndFillReq;
import gov.nwcg.services.ross.resource_order._1.CreateIncidentRequestAndPlaceReq;
import gov.nwcg.services.ross.resource_order._1.CreateRequestAndFillReq;
import gov.nwcg.services.ross.resource_order._1.CreateRequestAndPlaceReq;
import gov.nwcg.services.ross.resource_order._1.CreateResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.FillResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentAndRequestsReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentAviationHazardsReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentDocumentationReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentRadioFrequenciesReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentReq;
import gov.nwcg.services.ross.resource_order._1.GetIncidentsRequestsByStatusReq;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsReq;
import gov.nwcg.services.ross.resource_order._1.GetOperationResultsResp;
import gov.nwcg.services.ross.resource_order._1.GetResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.NotifyResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.PlaceResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.RegisterIncidentInterestReq;
import gov.nwcg.services.ross.resource_order._1.ReplaceIncidentAviationHazardsReq;
import gov.nwcg.services.ross.resource_order._1.ReplaceIncidentRadioFrequenciesReq;
import gov.nwcg.services.ross.resource_order._1.SetIncidentActivationReq;
import gov.nwcg.services.ross.resource_order._1.UpdateIncidentDetailsReq;
import gov.nwcg.services.ross.resource_order._1.UpdateIncidentNumberReq;
import gov.nwcg.services.ross.resource_order._1.UpdateNFESResourceRequestReq;
import gov.nwcg.services.ross.resource_order._1.UtfResourceRequestReq;

public class ResourceOrderPortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService _service = null;
        private gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderInterface _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getResourceOrderPort();
        }

        public gov.nwcg.services.ross.resource_order.wsdl._1.ResourceOrderInterface getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://nwcg.gov/services/ross/resource_order/wsdl/1.1", "ResourceOrderPort");
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

    public ResourceOrderPortProxy() {
        _descriptor = new Descriptor();
    }

    public ResourceOrderPortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public MessageAcknowledgement addIncidentDocumentation(AddIncidentDocumentationReq body) {
        return _getDescriptor().getProxy().addIncidentDocumentation(body);
    }

    public AuthenticateUserResp authenticateUser(AuthenticateUserReq body) {
        return _getDescriptor().getProxy().authenticateUser(body);
    }

    public MessageAcknowledgement cancelResourceRequest(CancelResourceRequestReq body) {
        return _getDescriptor().getProxy().cancelResourceRequest(body);
    }

    public MessageAcknowledgement createIncident(CreateIncidentReq body) {
        return _getDescriptor().getProxy().createIncident(body);
    }

    public MessageAcknowledgement createIncidentAndRequest(CreateIncidentAndRequestReq body) {
        return _getDescriptor().getProxy().createIncidentAndRequest(body);
    }

    public MessageAcknowledgement createIncidentRequestAndFill(CreateIncidentRequestAndFillReq body) {
        return _getDescriptor().getProxy().createIncidentRequestAndFill(body);
    }

    public MessageAcknowledgement createIncidentRequestAndPlace(CreateIncidentRequestAndPlaceReq body) {
        return _getDescriptor().getProxy().createIncidentRequestAndPlace(body);
    }

    public MessageAcknowledgement createRequestAndFill(CreateRequestAndFillReq body) {
        return _getDescriptor().getProxy().createRequestAndFill(body);
    }

    public MessageAcknowledgement createRequestAndPlace(CreateRequestAndPlaceReq body) {
        return _getDescriptor().getProxy().createRequestAndPlace(body);
    }

    public MessageAcknowledgement createResourceRequest(CreateResourceRequestReq body) {
        return _getDescriptor().getProxy().createResourceRequest(body);
    }

    public MessageAcknowledgement fillResourceRequest(FillResourceRequestReq body) {
        return _getDescriptor().getProxy().fillResourceRequest(body);
    }

    public MessageAcknowledgement getIncident(GetIncidentReq body) {
        return _getDescriptor().getProxy().getIncident(body);
    }

    public MessageAcknowledgement getIncidentAndRequests(GetIncidentAndRequestsReq body) {
        return _getDescriptor().getProxy().getIncidentAndRequests(body);
    }

    public MessageAcknowledgement getIncidentAviationHazards(GetIncidentAviationHazardsReq body) {
        return _getDescriptor().getProxy().getIncidentAviationHazards(body);
    }

    public MessageAcknowledgement getIncidentDocumentation(GetIncidentDocumentationReq body) {
        return _getDescriptor().getProxy().getIncidentDocumentation(body);
    }

    public MessageAcknowledgement getIncidentRadioFrequencies(GetIncidentRadioFrequenciesReq body) {
        return _getDescriptor().getProxy().getIncidentRadioFrequencies(body);
    }

    public MessageAcknowledgement getIncidentsRequestsByStatus(GetIncidentsRequestsByStatusReq body) {
        return _getDescriptor().getProxy().getIncidentsRequestsByStatus(body);
    }

    public GetOperationResultsResp getOperationResults(GetOperationResultsReq body) {
        return _getDescriptor().getProxy().getOperationResults(body);
    }

    public MessageAcknowledgement getResourceRequest(GetResourceRequestReq body) {
        return _getDescriptor().getProxy().getResourceRequest(body);
    }

    public MessageAcknowledgement notifyResourceRequest(NotifyResourceRequestReq body) {
        return _getDescriptor().getProxy().notifyResourceRequest(body);
    }

    public MessageAcknowledgement placeResourceRequest(PlaceResourceRequestReq body) {
        return _getDescriptor().getProxy().placeResourceRequest(body);
    }

    public MessageAcknowledgement registerIncidentInterest(RegisterIncidentInterestReq body) {
        return _getDescriptor().getProxy().registerIncidentInterest(body);
    }

    public MessageAcknowledgement replaceIncidentAviationHazards(ReplaceIncidentAviationHazardsReq body) {
        return _getDescriptor().getProxy().replaceIncidentAviationHazards(body);
    }

    public MessageAcknowledgement replaceIncidentRadioFrequencies(ReplaceIncidentRadioFrequenciesReq body) {
        return _getDescriptor().getProxy().replaceIncidentRadioFrequencies(body);
    }

    public MessageAcknowledgement setIncidentActivation(SetIncidentActivationReq body) {
        return _getDescriptor().getProxy().setIncidentActivation(body);
    }

    public ServicePingResp servicePing(ServicePingReq body) {
        return _getDescriptor().getProxy().servicePing(body);
    }

    public MessageAcknowledgement updateIncidentDetails(UpdateIncidentDetailsReq body) {
        return _getDescriptor().getProxy().updateIncidentDetails(body);
    }

    public MessageAcknowledgement updateIncidentNumber(UpdateIncidentNumberReq body) {
        return _getDescriptor().getProxy().updateIncidentNumber(body);
    }

    public MessageAcknowledgement utfResourceRequest(UtfResourceRequestReq body) {
        return _getDescriptor().getProxy().utfResourceRequest(body);
    }

    public MessageAcknowledgement updateNFESResourceRequest(UpdateNFESResourceRequestReq body) {
        return _getDescriptor().getProxy().updateNFESResourceRequest(body);
    }

}