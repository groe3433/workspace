//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceRequestKeyType;
import gov.nwcg.services.ross.common_types._1.ResourceRequestFilledReturnType;
import gov.nwcg.services.ross.common_types._1.ResourceRequestPendingReturnType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResponseStatus" type="{http://nwcg.gov/services/ross/common_types/1.1}ResponseStatusType"/>
 *         &lt;element name="RequestKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeResourceRequestKeyType" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="PendingRequest" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceRequestPendingReturnType" minOccurs="0"/>
 *           &lt;element name="FilledRequest" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceRequestFilledReturnType" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "responseStatus",
    "requestKey",
    "pendingRequest",
    "filledRequest"
})
@XmlRootElement(name = "GetResourceRequestResp")
public class GetResourceRequestResp {

    @XmlElement(name = "ResponseStatus", required = true)
    protected ResponseStatusType responseStatus;
    @XmlElement(name = "RequestKey")
    protected CompositeResourceRequestKeyType requestKey;
    @XmlElement(name = "PendingRequest")
    protected ResourceRequestPendingReturnType pendingRequest;
    @XmlElement(name = "FilledRequest")
    protected ResourceRequestFilledReturnType filledRequest;

    /**
     * Gets the value of the responseStatus property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseStatusType }
     *     
     */
    public ResponseStatusType getResponseStatus() {
        return responseStatus;
    }

    /**
     * Sets the value of the responseStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseStatusType }
     *     
     */
    public void setResponseStatus(ResponseStatusType value) {
        this.responseStatus = value;
    }

    public boolean isSetResponseStatus() {
        return (this.responseStatus!= null);
    }

    /**
     * Gets the value of the requestKey property.
     * 
     * @return
     *     possible object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public CompositeResourceRequestKeyType getRequestKey() {
        return requestKey;
    }

    /**
     * Sets the value of the requestKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public void setRequestKey(CompositeResourceRequestKeyType value) {
        this.requestKey = value;
    }

    public boolean isSetRequestKey() {
        return (this.requestKey!= null);
    }

    /**
     * Gets the value of the pendingRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceRequestPendingReturnType }
     *     
     */
    public ResourceRequestPendingReturnType getPendingRequest() {
        return pendingRequest;
    }

    /**
     * Sets the value of the pendingRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceRequestPendingReturnType }
     *     
     */
    public void setPendingRequest(ResourceRequestPendingReturnType value) {
        this.pendingRequest = value;
    }

    public boolean isSetPendingRequest() {
        return (this.pendingRequest!= null);
    }

    /**
     * Gets the value of the filledRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceRequestFilledReturnType }
     *     
     */
    public ResourceRequestFilledReturnType getFilledRequest() {
        return filledRequest;
    }

    /**
     * Sets the value of the filledRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceRequestFilledReturnType }
     *     
     */
    public void setFilledRequest(ResourceRequestFilledReturnType value) {
        this.filledRequest = value;
    }

    public boolean isSetFilledRequest() {
        return (this.filledRequest!= null);
    }

}
