//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-08/21/2008 01:11 PM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order_notification._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.IncidentRadioFrequencyReturnType;
import gov.nwcg.services.ross.common_types._1.NotificationBaseType;


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
 *         &lt;element name="NotificationBase" type="{http://nwcg.gov/services/ross/common_types/1.1}NotificationBaseType"/>
 *         &lt;element name="IncidentRadioFrequency" type="{http://nwcg.gov/services/ross/common_types/1.1}IncidentRadioFrequencyReturnType"/>
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
    "notificationBase",
    "incidentRadioFrequency"
})
@XmlRootElement(name = "AddIncidentRadioFrequencyNotification")
public class AddIncidentRadioFrequencyNotification {

    @XmlElement(name = "NotificationBase", required = true)
    protected NotificationBaseType notificationBase;
    @XmlElement(name = "IncidentRadioFrequency", required = true)
    protected IncidentRadioFrequencyReturnType incidentRadioFrequency;

    /**
     * Gets the value of the notificationBase property.
     * 
     * @return
     *     possible object is
     *     {@link NotificationBaseType }
     *     
     */
    public NotificationBaseType getNotificationBase() {
        return notificationBase;
    }

    /**
     * Sets the value of the notificationBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link NotificationBaseType }
     *     
     */
    public void setNotificationBase(NotificationBaseType value) {
        this.notificationBase = value;
    }

    public boolean isSetNotificationBase() {
        return (this.notificationBase!= null);
    }

    /**
     * Gets the value of the incidentRadioFrequency property.
     * 
     * @return
     *     possible object is
     *     {@link IncidentRadioFrequencyReturnType }
     *     
     */
    public IncidentRadioFrequencyReturnType getIncidentRadioFrequency() {
        return incidentRadioFrequency;
    }

    /**
     * Sets the value of the incidentRadioFrequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentRadioFrequencyReturnType }
     *     
     */
    public void setIncidentRadioFrequency(IncidentRadioFrequencyReturnType value) {
        this.incidentRadioFrequency = value;
    }

    public boolean isSetIncidentRadioFrequency() {
        return (this.incidentRadioFrequency!= null);
    }

}
