//
// Generated By:JAX-WS RI IBM 2.0_03-07/07/2008 01:00 PM(foreman)-fcs (JAXB RI IBM 2.0.5-02/25/2009 05:47 AM(foreman)-fcs)
//


package gov.nwcg.services.ross.common_types._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LongitudeDirectionSimpleType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LongitudeDirectionSimpleType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="East"/>
 *     &lt;enumeration value="West"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlEnum
@XmlType(name = "LongitudeDirectionSimpleType", namespace = "http://nwcg.gov/services/ross/common_types/1.1")
public enum LongitudeDirectionSimpleType {

    @XmlEnumValue("East")
    EAST("East"),
    @XmlEnumValue("West")
    WEST("West");
    private final String value;

    LongitudeDirectionSimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LongitudeDirectionSimpleType fromValue(String v) {
        for (LongitudeDirectionSimpleType c: LongitudeDirectionSimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
