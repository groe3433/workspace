//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-08/21/2008 01:11 PM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types.wsdl._1;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

@WebServiceClient(name = "VerifySecuritySessionService", targetNamespace = "http://nwcg.gov/services/ross/common_types/wsdl/1.1", wsdlLocation = "WEB-INF/wsdl/RossCommon_Synchronous.wsdl")
public class VerifySecuritySessionService
    extends Service
{

    private final static URL VERIFYSECURITYSESSIONSERVICE_WSDL_LOCATION;

    static {
        URL url = null;
        try {
            url = new URL("WEB-INF/wsdl/RossCommon_Synchronous.wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        VERIFYSECURITYSESSIONSERVICE_WSDL_LOCATION = url;
    }

    public VerifySecuritySessionService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public VerifySecuritySessionService() {
        super(VERIFYSECURITYSESSIONSERVICE_WSDL_LOCATION, new QName("http://nwcg.gov/services/ross/common_types/wsdl/1.1", "VerifySecuritySessionService"));
    }

    /**
     * Integration Test Endpoint
     * 
     * @return
     *     returns VerifySecuritySessionInterface
     */
    @WebEndpoint(name = "VerifySecuritySessionPort")
    public VerifySecuritySessionInterface getVerifySecuritySessionPort() {
        return (VerifySecuritySessionInterface)super.getPort(new QName("http://nwcg.gov/services/ross/common_types/wsdl/1.1", "VerifySecuritySessionPort"), VerifySecuritySessionInterface.class);
    }

}
