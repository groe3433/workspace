//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResourceQualificationStatusSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResourceQualificationStatusSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Qualified"/>
 *     &lt;enumeration value="Trainee"/>
 *     &lt;enumeration value="Unqualified"/>
 *     &lt;enumeration value="Blocked"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
@XmlType(name = "ResourceQualificationStatusSimpleType", namespace = "http://nwcg.gov/services/ross/common_types/1.1")
public enum ResourceQualificationStatusSimpleType {

    @XmlEnumValue("Qualified")
    QUALIFIED("Qualified"),
    @XmlEnumValue("Trainee")
    TRAINEE("Trainee"),
    @XmlEnumValue("Unqualified")
    UNQUALIFIED("Unqualified"),
    @XmlEnumValue("Blocked")
    BLOCKED("Blocked"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    ResourceQualificationStatusSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResourceQualificationStatusSimpleType fromValue(String v) {
        for (ResourceQualificationStatusSimpleType c: ResourceQualificationStatusSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
