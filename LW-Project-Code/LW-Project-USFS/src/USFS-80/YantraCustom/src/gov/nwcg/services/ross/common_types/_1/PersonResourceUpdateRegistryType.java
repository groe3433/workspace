//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Person (overhead) resource registration data (for update)
 * 
 * <p>Java class for PersonResourceUpdateRegistryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonResourceUpdateRegistryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FirstName" type="{http://nwcg.gov/services/ross/common_types/1.1}PersonNamePartSimpleType" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://nwcg.gov/services/ross/common_types/1.1}PersonNamePartSimpleType" minOccurs="0"/>
 *         &lt;element name="MiddleName" type="{http://nwcg.gov/services/ross/common_types/1.1}PersonNamePartSimpleType" minOccurs="0"/>
 *         &lt;element name="ProviderUnitID" type="{http://nwcg.gov/services/ross/common_types/1.1}UnitIDType" minOccurs="0"/>
 *         &lt;element name="HomeDispatchUnitID" type="{http://nwcg.gov/services/ross/common_types/1.1}UnitIDType" minOccurs="0"/>
 *         &lt;element name="ResourceContactInfo" type="{http://nwcg.gov/services/ross/common_types/1.1}ResourceContactInfoSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonResourceUpdateRegistryType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "firstName",
    "lastName",
    "middleName",
    "providerUnitID",
    "homeDispatchUnitID",
    "resourceContactInfo"
})
public class PersonResourceUpdateRegistryType {

    @XmlElement(name = "FirstName")
    protected String firstName;
    @XmlElement(name = "LastName")
    protected String lastName;
    @XmlElement(name = "MiddleName")
    protected String middleName;
    @XmlElement(name = "ProviderUnitID")
    protected UnitIDType providerUnitID;
    @XmlElement(name = "HomeDispatchUnitID")
    protected UnitIDType homeDispatchUnitID;
    @XmlElement(name = "ResourceContactInfo")
    protected String resourceContactInfo;

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    public boolean isSetFirstName() {
        return (this.firstName!= null);
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    public boolean isSetLastName() {
        return (this.lastName!= null);
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    public boolean isSetMiddleName() {
        return (this.middleName!= null);
    }

    /**
     * Gets the value of the providerUnitID property.
     * 
     * @return
     *     possible object is
     *     {@link UnitIDType }
     *     
     */
    public UnitIDType getProviderUnitID() {
        return providerUnitID;
    }

    /**
     * Sets the value of the providerUnitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitIDType }
     *     
     */
    public void setProviderUnitID(UnitIDType value) {
        this.providerUnitID = value;
    }

    public boolean isSetProviderUnitID() {
        return (this.providerUnitID!= null);
    }

    /**
     * Gets the value of the homeDispatchUnitID property.
     * 
     * @return
     *     possible object is
     *     {@link UnitIDType }
     *     
     */
    public UnitIDType getHomeDispatchUnitID() {
        return homeDispatchUnitID;
    }

    /**
     * Sets the value of the homeDispatchUnitID property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitIDType }
     *     
     */
    public void setHomeDispatchUnitID(UnitIDType value) {
        this.homeDispatchUnitID = value;
    }

    public boolean isSetHomeDispatchUnitID() {
        return (this.homeDispatchUnitID!= null);
    }

    /**
     * Gets the value of the resourceContactInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceContactInfo() {
        return resourceContactInfo;
    }

    /**
     * Sets the value of the resourceContactInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceContactInfo(String value) {
        this.resourceContactInfo = value;
    }

    public boolean isSetResourceContactInfo() {
        return (this.resourceContactInfo!= null);
    }

}
