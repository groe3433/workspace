//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.CompositeIncidentKeyType;
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
 *         &lt;element name="IncidentKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeIncidentKeyType" minOccurs="0"/>
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
    "incidentKey"
})
@XmlRootElement(name = "AddIncidentDocumentationResp")
public class AddIncidentDocumentationResp {

    @XmlElement(name = "ResponseStatus", required = true)
    protected ResponseStatusType responseStatus;
    @XmlElement(name = "IncidentKey")
    protected CompositeIncidentKeyType incidentKey;

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
     * Gets the value of the incidentKey property.
     * 
     * @return
     *     possible object is
     *     {@link CompositeIncidentKeyType }
     *     
     */
    public CompositeIncidentKeyType getIncidentKey() {
        return incidentKey;
    }

    /**
     * Sets the value of the incidentKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositeIncidentKeyType }
     *     
     */
    public void setIncidentKey(CompositeIncidentKeyType value) {
        this.incidentKey = value;
    }

    public boolean isSetIncidentKey() {
        return (this.incidentKey!= null);
    }

}
