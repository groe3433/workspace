//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * User account information
 * 
 * <p>Java class for UpdateUserAccountType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdateUserAccountType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserName" type="{http://nwcg.gov/services/ross/common_types/1.1}AccountUserNameSimpleType"/>
 *         &lt;element name="AccountManagerDispatch" type="{http://nwcg.gov/services/ross/common_types/1.1}UnitIDType"/>
 *         &lt;element name="TemporaryPasswordInd" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Password" type="{http://nwcg.gov/services/ross/common_types/1.1}AccountPasswordSimpleType" minOccurs="0"/>
 *         &lt;element name="PreviousUserName" type="{http://nwcg.gov/services/ross/common_types/1.1}AccountUserNameSimpleType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateUserAccountType", namespace = "http://nwcg.gov/services/ross/common_types/1.1", propOrder = {
    "userName",
    "accountManagerDispatch",
    "temporaryPasswordInd",
    "password",
    "previousUserName"
})
public class UpdateUserAccountType {

    @XmlElement(name = "UserName", required = true)
    protected String userName;
    @XmlElement(name = "AccountManagerDispatch", required = true)
    protected UnitIDType accountManagerDispatch;
    @XmlElement(name = "TemporaryPasswordInd")
    protected boolean temporaryPasswordInd;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "PreviousUserName")
    protected String previousUserName;

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    public boolean isSetUserName() {
        return (this.userName!= null);
    }

    /**
     * Gets the value of the accountManagerDispatch property.
     * 
     * @return
     *     possible object is
     *     {@link UnitIDType }
     *     
     */
    public UnitIDType getAccountManagerDispatch() {
        return accountManagerDispatch;
    }

    /**
     * Sets the value of the accountManagerDispatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitIDType }
     *     
     */
    public void setAccountManagerDispatch(UnitIDType value) {
        this.accountManagerDispatch = value;
    }

    public boolean isSetAccountManagerDispatch() {
        return (this.accountManagerDispatch!= null);
    }

    /**
     * Gets the value of the temporaryPasswordInd property.
     * 
     */
    public boolean isTemporaryPasswordInd() {
        return temporaryPasswordInd;
    }

    /**
     * Sets the value of the temporaryPasswordInd property.
     * 
     */
    public void setTemporaryPasswordInd(boolean value) {
        this.temporaryPasswordInd = value;
    }

    public boolean isSetTemporaryPasswordInd() {
        return true;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    public boolean isSetPassword() {
        return (this.password!= null);
    }

    /**
     * Gets the value of the previousUserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviousUserName() {
        return previousUserName;
    }

    /**
     * Sets the value of the previousUserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviousUserName(String value) {
        this.previousUserName = value;
    }

    public boolean isSetPreviousUserName() {
        return (this.previousUserName!= null);
    }

}
