package gov.nwcg.services.ross.common_types.wsdl._1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import gov.nwcg.services.ross.common_types._1.VerifySecuritySessionReq;
import gov.nwcg.services.ross.common_types._1.VerifySecuritySessionResp;

public class VerifySecuritySessionPortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private gov.nwcg.services.ross.common_types.wsdl._1.VerifySecuritySessionService _service = null;
        private gov.nwcg.services.ross.common_types.wsdl._1.VerifySecuritySessionInterface _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new gov.nwcg.services.ross.common_types.wsdl._1.VerifySecuritySessionService();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new gov.nwcg.services.ross.common_types.wsdl._1.VerifySecuritySessionService(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getVerifySecuritySessionPort();
        }

        public gov.nwcg.services.ross.common_types.wsdl._1.VerifySecuritySessionInterface getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://nwcg.gov/services/ross/common_types/wsdl/1.1", "VerifySecuritySessionPort");
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

    public VerifySecuritySessionPortProxy() {
        _descriptor = new Descriptor();
    }

    public VerifySecuritySessionPortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public VerifySecuritySessionResp verifySecuritySessionId(VerifySecuritySessionReq body) {
        return _getDescriptor().getProxy().verifySecuritySessionId(body);
    }

}