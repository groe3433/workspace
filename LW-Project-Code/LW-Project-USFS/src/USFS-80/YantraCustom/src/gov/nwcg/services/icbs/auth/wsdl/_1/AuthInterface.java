//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-08/21/2008 01:11 PM(foreman)-fcs)
//

package gov.nwcg.services.icbs.auth.wsdl._1;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import gov.nwcg.services.ross.common_types._1.AuthUserReq;
import gov.nwcg.services.ross.common_types._1.AuthUserResp;

@WebService(name = "AuthInterface", targetNamespace = "http://nwcg.gov/services/icbs/auth/wsdl/1.1")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface AuthInterface {

	/**
	 * 
	 * @param body
	 * @return returns gov.nwcg.services.ross.common_types._1.AuthUserResp
	 */
	@WebMethod(operationName = "AuthUser", action = "AuthUser")
	@WebResult(name = "AuthUserResp", targetNamespace = "http://nwcg.gov/services/ross/common_types/1.1", partName = "body")
	public AuthUserResp authUser(
			@WebParam(name = "AuthUserReq", targetNamespace = "http://nwcg.gov/services/ross/common_types/1.1", partName = "body") AuthUserReq body);
}
