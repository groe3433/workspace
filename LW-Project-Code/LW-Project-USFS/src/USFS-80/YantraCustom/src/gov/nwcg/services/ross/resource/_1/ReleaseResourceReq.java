//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-08/21/2008 01:11 PM(foreman)-fcs)
//


package gov.nwcg.services.ross.resource._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceKeyType;
import gov.nwcg.services.ross.common_types._1.CompositeResourceRequestKeyType;
import gov.nwcg.services.ross.common_types._1.MessageOriginatorType;
import gov.nwcg.services.ross.common_types._1.ResourceReleaseType;


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
 *         &lt;element name="ResourceRequestKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeResourceRequestKeyType"/>
 *         &lt;element name="ResourceKey" type="{http://nwcg.gov/services/ross/common_types/1.1}CompositeResourceKeyType"/>
 *         &lt;element name="ReleaseData" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceReleaseType"/>
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
    "resourceRequestKey",
    "resourceKey",
    "releaseData"
})
@XmlRootElement(name = "ReleaseResourceReq")
public class ReleaseResourceReq {

    @XmlElement(name = "MessageOriginator", required = true)
    protected MessageOriginatorType messageOriginator;
    @XmlElement(name = "ResourceRequestKey", required = true)
    protected CompositeResourceRequestKeyType resourceRequestKey;
    @XmlElement(name = "ResourceKey", required = true)
    protected CompositeResourceKeyType resourceKey;
    @XmlElement(name = "ReleaseData", required = true)
    protected ResourceReleaseType releaseData;

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
     * Gets the value of the resourceRequestKey property.
     * 
     * @return
     *     possible object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public CompositeResourceRequestKeyType getResourceRequestKey() {
        return resourceRequestKey;
    }

    /**
     * Sets the value of the resourceRequestKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositeResourceRequestKeyType }
     *     
     */
    public void setResourceRequestKey(CompositeResourceRequestKeyType value) {
        this.resourceRequestKey = value;
    }

    public boolean isSetResourceRequestKey() {
        return (this.resourceRequestKey!= null);
    }

    /**
     * Gets the value of the resourceKey property.
     * 
     * @return
     *     possible object is
     *     {@link CompositeResourceKeyType }
     *     
     */
    public CompositeResourceKeyType getResourceKey() {
        return resourceKey;
    }

    /**
     * Sets the value of the resourceKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompositeResourceKeyType }
     *     
     */
    public void setResourceKey(CompositeResourceKeyType value) {
        this.resourceKey = value;
    }

    public boolean isSetResourceKey() {
        return (this.resourceKey!= null);
    }

    /**
     * Gets the value of the releaseData property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceReleaseType }
     *     
     */
    public ResourceReleaseType getReleaseData() {
        return releaseData;
    }

    /**
     * Sets the value of the releaseData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceReleaseType }
     *     
     */
    public void setReleaseData(ResourceReleaseType value) {
        this.releaseData = value;
    }

    public boolean isSetReleaseData() {
        return (this.releaseData!= null);
    }

}
