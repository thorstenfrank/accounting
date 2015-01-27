
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlClient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="clientNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{}xmlAddress" minOccurs="0"/>
 *         &lt;element name="defaultPaymentTerms" type="{}xmlPaymentTerms" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlClient", propOrder = {
    "clientNumber",
    "address",
    "defaultPaymentTerms"
})
public class XmlClient {

    protected String clientNumber;
    protected XmlAddress address;
    protected XmlPaymentTerms defaultPaymentTerms;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the clientNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNumber() {
        return clientNumber;
    }

    /**
     * Sets the value of the clientNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNumber(String value) {
        this.clientNumber = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link XmlAddress }
     *     
     */
    public XmlAddress getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlAddress }
     *     
     */
    public void setAddress(XmlAddress value) {
        this.address = value;
    }

    /**
     * Gets the value of the defaultPaymentTerms property.
     * 
     * @return
     *     possible object is
     *     {@link XmlPaymentTerms }
     *     
     */
    public XmlPaymentTerms getDefaultPaymentTerms() {
        return defaultPaymentTerms;
    }

    /**
     * Sets the value of the defaultPaymentTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlPaymentTerms }
     *     
     */
    public void setDefaultPaymentTerms(XmlPaymentTerms value) {
        this.defaultPaymentTerms = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
