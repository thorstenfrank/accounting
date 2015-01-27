
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlPaymentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="xmlPaymentType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NET"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "xmlPaymentType")
@XmlEnum
public enum XmlPaymentType {

    NET;

    public String value() {
        return name();
    }

    public static XmlPaymentType fromValue(String v) {
        return valueOf(v);
    }

}
