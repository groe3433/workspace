//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  2.6 - Structure describing a resources availability
 * 
 * <p>Java class for ResourceAvailabilityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceAvailabilityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AvailabilityIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="UnavailabilityReason" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceUnavailabilityReasonTypeSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceAvailabilityType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "availabilityIndicator",
    "unavailabilityReason"
})
public class ResourceAvailabilityType {

    @XmlElement(name = "AvailabilityIndicator")
    protected boolean availabilityIndicator;
    @XmlElement(name = "UnavailabilityReason")
    protected ResourceUnavailabilityReasonTypeSimpleType unavailabilityReason;

    /**
     * Gets the value of the availabilityIndicator property.
     * 
     */
    public boolean isAvailabilityIndicator() {
        return availabilityIndicator;
    }

    /**
     * Sets the value of the availabilityIndicator property.
     * 
     */
    public void setAvailabilityIndicator(boolean value) {
        this.availabilityIndicator = value;
    }

    public boolean isSetAvailabilityIndicator() {
        return true;
    }

    /**
     * Gets the value of the unavailabilityReason property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceUnavailabilityReasonTypeSimpleType }
     *     
     */
    public ResourceUnavailabilityReasonTypeSimpleType getUnavailabilityReason() {
        return unavailabilityReason;
    }

    /**
     * Sets the value of the unavailabilityReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceUnavailabilityReasonTypeSimpleType }
     *     
     */
    public void setUnavailabilityReason(ResourceUnavailabilityReasonTypeSimpleType value) {
        this.unavailabilityReason = value;
    }

    public boolean isSetUnavailabilityReason() {
        return (this.unavailabilityReason!= null);
    }

}
