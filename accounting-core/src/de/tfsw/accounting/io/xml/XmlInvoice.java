
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for xmlInvoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlInvoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="invoiceDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="paymentTerms" type="{}xmlPaymentTerms" minOccurs="0"/>
 *         &lt;element name="sentDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="cancelledDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="paymentDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="client" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="invoicePositions" type="{}xmlInvoicePositions"/>
 *       &lt;/sequence>
 *       &lt;attribute name="number" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlInvoice", propOrder = {
    "creationDate",
    "invoiceDate",
    "paymentTerms",
    "sentDate",
    "cancelledDate",
    "paymentDate",
    "client",
    "invoicePositions"
})
public class XmlInvoice {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar creationDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar invoiceDate;
    protected XmlPaymentTerms paymentTerms;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar sentDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar cancelledDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentDate;
    @XmlElement(required = true)
    protected String client;
    @XmlElement(required = true)
    protected XmlInvoicePositions invoicePositions;
    @XmlAttribute(name = "number", required = true)
    protected String number;

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInvoiceDate(XMLGregorianCalendar value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the paymentTerms property.
     * 
     * @return
     *     possible object is
     *     {@link XmlPaymentTerms }
     *     
     */
    public XmlPaymentTerms getPaymentTerms() {
        return paymentTerms;
    }

    /**
     * Sets the value of the paymentTerms property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlPaymentTerms }
     *     
     */
    public void setPaymentTerms(XmlPaymentTerms value) {
        this.paymentTerms = value;
    }

    /**
     * Gets the value of the sentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSentDate(XMLGregorianCalendar value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the cancelledDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCancelledDate() {
        return cancelledDate;
    }

    /**
     * Sets the value of the cancelledDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCancelledDate(XMLGregorianCalendar value) {
        this.cancelledDate = value;
    }

    /**
     * Gets the value of the paymentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentDate(XMLGregorianCalendar value) {
        this.paymentDate = value;
    }

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClient(String value) {
        this.client = value;
    }

    /**
     * Gets the value of the invoicePositions property.
     * 
     * @return
     *     possible object is
     *     {@link XmlInvoicePositions }
     *     
     */
    public XmlInvoicePositions getInvoicePositions() {
        return invoicePositions;
    }

    /**
     * Sets the value of the invoicePositions property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlInvoicePositions }
     *     
     */
    public void setInvoicePositions(XmlInvoicePositions value) {
        this.invoicePositions = value;
    }

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

}
