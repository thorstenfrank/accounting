
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlPaymentTerms complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlPaymentTerms">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{}xmlPaymentType"/>
 *         &lt;element name="fullPaymentTargetInDays" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlPaymentTerms", propOrder = {
    "type",
    "fullPaymentTargetInDays"
})
public class XmlPaymentTerms {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected XmlPaymentType type;
    protected int fullPaymentTargetInDays;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link XmlPaymentType }
     *     
     */
    public XmlPaymentType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlPaymentType }
     *     
     */
    public void setType(XmlPaymentType value) {
        this.type = value;
    }

    /**
     * Gets the value of the fullPaymentTargetInDays property.
     * 
     */
    public int getFullPaymentTargetInDays() {
        return fullPaymentTargetInDays;
    }

    /**
     * Sets the value of the fullPaymentTargetInDays property.
     * 
     */
    public void setFullPaymentTargetInDays(int value) {
        this.fullPaymentTargetInDays = value;
    }

}
