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
import gov.nwcg.services.ross.common_types._1.IncidentStatusSimpleType;
import gov.nwcg.services.ross.common_types._1.MessageOriginatorType;


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
 *         &lt;element name="MessageOriginator" type="{http://nwcg.gov/services/ross/common_types/1.1}MessageOriginatorType"/>
 *         &lt;element name="IncidentKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeIncidentKeyType"/>
 *         &lt;element name="NewStatus" type="{http://nwcg.gov/services/ross/common_types/1.1}IncidentStatusSimpleType"/>
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
    "messageOriginator",
    "incidentKey",
    "newStatus"
})
@XmlRootElement(name = "UpdateIncidentStatusReq")
public class UpdateIncidentStatusReq {

    @XmlElement(name = "MessageOriginator", required = true)
    protected MessageOriginatorType messageOriginator;
    @XmlElement(name = "IncidentKey", required = true)
    protected CompositeIncidentKeyType incidentKey;
    @XmlElement(name = "NewStatus", required = true)
    protected IncidentStatusSimpleType newStatus;

    /**
     * Gets the value of the messageOriginator property.
     * 
     * @return
     *     possible object is
     *     {@link MessageOriginatorType }
     *     
     */
    public MessageOriginatorType getMessageOriginator() {
        return messageOriginator;
    }

    /**
     * Sets the value of the messageOriginator property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageOriginatorType }
     *     
     */
    public void setMessageOriginator(MessageOriginatorType value) {
        this.messageOriginator = value;
    }

    public boolean isSetMessageOriginator() {
        return (this.messageOriginator!= null);
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

    /**
     * Gets the value of the newStatus property.
     * 
     * @return
     *     possible object is
     *     {@link IncidentStatusSimpleType }
     *     
     */
    public IncidentStatusSimpleType getNewStatus() {
        return newStatus;
    }

    /**
     * Sets the value of the newStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentStatusSimpleType }
     *     
     */
    public void setNewStatus(IncidentStatusSimpleType value) {
        this.newStatus = value;
    }

    public boolean isSetNewStatus() {
        return (this.newStatus!= null);
    }

}
