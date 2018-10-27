//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource_order._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceKeyType;
import gov.nwcg.services.ross.common_types._1.ResponseStatusType;


/**
 * Common response structure for "get xx resources" operations (returns multiple resources)
 * 
 * <p>Java class for ResourcesOperationResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourcesOperationResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResponseStatus" type="{http://nwcg.gov/services/ross/common_types/1.1}ResponseStatusType"/>
 *         &lt;element name="ResourceKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeResourceKeyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourcesOperationResponseType", namespace = "http://nwcg.gov/services/ross/resource_order/1.1", propOrder = {
    "responseStatus",
    "resourceKey"
})
public class ResourcesOperationResponseType {

    @XmlElement(name = "ResponseStatus", required = true)
    protected ResponseStatusType responseStatus;
    @XmlElement(name = "ResourceKey")
    protected List<CompositeResourceKeyType> resourceKey;

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
     * Gets the value of the resourceKey property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceKey property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceKey().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositeResourceKeyType }
     * 
     * 
     */
    public List<CompositeResourceKeyType> getResourceKey() {
        if (resourceKey == null) {
            resourceKey = new ArrayList<CompositeResourceKeyType>();
        }
        return this.resourceKey;
    }

    public boolean isSetResourceKey() {
        return ((this.resourceKey!= null)&&(!this.resourceKey.isEmpty()));
    }

    public void unsetResourceKey() {
        this.resourceKey = null;
    }

}
