package gov.nwcg.services.ross.catalog.wsdl._1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import gov.nwcg.services.ross.catalog._1.CreateCatalogItemReq;
import gov.nwcg.services.ross.catalog._1.DeleteCatalogItemReq;
import gov.nwcg.services.ross.catalog._1.GetCatalogItemReq;
import gov.nwcg.services.ross.catalog._1.UpdateCatalogItemReq;
import gov.nwcg.services.ross.common_types._1.MessageAcknowledgement;

public class CatalogPortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private gov.nwcg.services.ross.catalog.wsdl._1.CatalogService _service = null;
        private gov.nwcg.services.ross.catalog.wsdl._1.CatalogInterface _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new gov.nwcg.services.ross.catalog.wsdl._1.CatalogService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new gov.nwcg.services.ross.catalog.wsdl._1.CatalogService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getCatalogPort();
        }

        public gov.nwcg.services.ross.catalog.wsdl._1.CatalogInterface getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://nwcg.gov/services/ross/catalog/wsdl/1.1", "CatalogPort");
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

    public CatalogPortProxy() {
        _descriptor = new Descriptor();
    }

    public CatalogPortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public MessageAcknowledgement createCatalogItem(CreateCatalogItemReq body) {
        return _getDescriptor().getProxy().createCatalogItem(body);
    }

    public MessageAcknowledgement deleteCatalogItem(DeleteCatalogItemReq body) {
        return _getDescriptor().getProxy().deleteCatalogItem(body);
    }

    public MessageAcknowledgement getCatalogItem(GetCatalogItemReq body) {
        return _getDescriptor().getProxy().getCatalogItem(body);
    }

    public MessageAcknowledgement updateCatalogItem(UpdateCatalogItemReq body) {
        return _getDescriptor().getProxy().updateCatalogItem(body);
    }

}