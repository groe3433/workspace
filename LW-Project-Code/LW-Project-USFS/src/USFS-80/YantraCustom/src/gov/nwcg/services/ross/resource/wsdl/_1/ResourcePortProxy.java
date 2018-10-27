package gov.nwcg.services.ross.resource.wsdl._1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Holder;
import gov.nwcg.services.ross.common_types._1.ServicePingReq;
import gov.nwcg.services.ross.common_types._1.ServicePingResp;
import gov.nwcg.services.ross.resource._1.AddPersonResourceQualificationReq;
import gov.nwcg.services.ross.resource._1.AddPersonResourceQualificationResp;
import gov.nwcg.services.ross.resource._1.CreatePersonResourceReq;
import gov.nwcg.services.ross.resource._1.CreatePersonResourceResp;
import gov.nwcg.services.ross.resource._1.DeleteDuplicatePersonResourceReq;
import gov.nwcg.services.ross.resource._1.DeletePersonResourceQualificationReq;
import gov.nwcg.services.ross.resource._1.DeletePersonResourceQualificationResp;
import gov.nwcg.services.ross.resource._1.DeletePersonResourceReq;
import gov.nwcg.services.ross.resource._1.DeletePersonResourceResp;
import gov.nwcg.services.ross.resource._1.ReleaseResourceReq;
import gov.nwcg.services.ross.resource._1.ReplaceMasterRosterReq;
import gov.nwcg.services.ross.resource._1.ResourceExistsReq;
import gov.nwcg.services.ross.resource._1.ResourceExistsResp;
import gov.nwcg.services.ross.resource._1.ResourceOperationResponseType;
import gov.nwcg.services.ross.resource._1.UpdatePersonResourceQualificationReq;
import gov.nwcg.services.ross.resource._1.UpdatePersonResourceQualificationResp;
import gov.nwcg.services.ross.resource._1.UpdatePersonResourceReq;
import gov.nwcg.services.ross.resource._1.UpdatePersonResourceResp;
import gov.nwcg.services.ross.resource._1.UpdateResourceAvailabilityReq;
import gov.nwcg.services.ross.resource._1.UpdateResourceTravelReq;

public class ResourcePortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private gov.nwcg.services.ross.resource.wsdl._1.ResourceService _service = null;
        private gov.nwcg.services.ross.resource.wsdl._1.ResourceInterface _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new gov.nwcg.services.ross.resource.wsdl._1.ResourceService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new gov.nwcg.services.ross.resource.wsdl._1.ResourceService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getResourcePort();
        }

        public gov.nwcg.services.ross.resource.wsdl._1.ResourceInterface getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://nwcg.gov/services/ross/resource/wsdl/1.1", "ResourcePort");
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

    public ResourcePortProxy() {
        _descriptor = new Descriptor();
    }

    public ResourcePortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public AddPersonResourceQualificationResp addPersonResourceQualification(AddPersonResourceQualificationReq body) {
        return _getDescriptor().getProxy().addPersonResourceQualification(body);
    }

    public CreatePersonResourceResp createPersonResource(CreatePersonResourceReq body) {
        return _getDescriptor().getProxy().createPersonResource(body);
    }

    public void deleteDuplicatePersonResource(Holder<DeleteDuplicatePersonResourceReq> body) {
        _getDescriptor().getProxy().deleteDuplicatePersonResource(body);
    }

    public DeletePersonResourceResp deletePersonResource(DeletePersonResourceReq body) {
        return _getDescriptor().getProxy().deletePersonResource(body);
    }

    public DeletePersonResourceQualificationResp deletePersonResourceQualification(DeletePersonResourceQualificationReq body) {
        return _getDescriptor().getProxy().deletePersonResourceQualification(body);
    }

    public ResourceOperationResponseType releaseResource(ReleaseResourceReq body) {
        return _getDescriptor().getProxy().releaseResource(body);
    }

    public ResourceOperationResponseType replaceMasterRoster(ReplaceMasterRosterReq body) {
        return _getDescriptor().getProxy().replaceMasterRoster(body);
    }

    public ResourceExistsResp resourceExists(ResourceExistsReq body) {
        return _getDescriptor().getProxy().resourceExists(body);
    }

    public ServicePingResp servicePing(ServicePingReq body) {
        return _getDescriptor().getProxy().servicePing(body);
    }

    public UpdatePersonResourceQualificationResp updatePersonResourceQualification(UpdatePersonResourceQualificationReq body) {
        return _getDescriptor().getProxy().updatePersonResourceQualification(body);
    }

    public ResourceOperationResponseType updateResourceAvailability(UpdateResourceAvailabilityReq body) {
        return _getDescriptor().getProxy().updateResourceAvailability(body);
    }

    public UpdatePersonResourceResp updatePersonResource(UpdatePersonResourceReq body) {
        return _getDescriptor().getProxy().updatePersonResource(body);
    }

    public ResourceOperationResponseType updateResourceTravel(UpdateResourceTravelReq body) {
        return _getDescriptor().getProxy().updateResourceTravel(body);
    }

}