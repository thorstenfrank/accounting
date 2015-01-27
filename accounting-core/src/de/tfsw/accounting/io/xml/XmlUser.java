
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xmlUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xmlUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="address" type="{}xmlAddress" minOccurs="0"/>
 *         &lt;element name="bankAccount" type="{}xmlBankAccount" minOccurs="0"/>
 *         &lt;element name="taxRates" type="{}xmlTaxRates" minOccurs="0"/>
 *         &lt;element name="clients" type="{}xmlClients" minOccurs="0"/>
 *         &lt;element name="invoices" type="{}xmlInvoices" minOccurs="0"/>
 *         &lt;element name="expenses" type="{}xmlExpenses" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="taxId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlUser", propOrder = {
    "description",
    "address",
    "bankAccount",
    "taxRates",
    "clients",
    "invoices",
    "expenses"
})
public class XmlUser {

    protected String description;
    protected XmlAddress address;
    protected XmlBankAccount bankAccount;
    protected XmlTaxRates taxRates;
    protected XmlClients clients;
    protected XmlInvoices invoices;
    protected XmlExpenses expenses;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "taxId")
    protected String taxId;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the bankAccount property.
     * 
     * @return
     *     possible object is
     *     {@link XmlBankAccount }
     *     
     */
    public XmlBankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the value of the bankAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlBankAccount }
     *     
     */
    public void setBankAccount(XmlBankAccount value) {
        this.bankAccount = value;
    }

    /**
     * Gets the value of the taxRates property.
     * 
     * @return
     *     possible object is
     *     {@link XmlTaxRates }
     *     
     */
    public XmlTaxRates getTaxRates() {
        return taxRates;
    }

    /**
     * Sets the value of the taxRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlTaxRates }
     *     
     */
    public void setTaxRates(XmlTaxRates value) {
        this.taxRates = value;
    }

    /**
     * Gets the value of the clients property.
     * 
     * @return
     *     possible object is
     *     {@link XmlClients }
     *     
     */
    public XmlClients getClients() {
        return clients;
    }

    /**
     * Sets the value of the clients property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlClients }
     *     
     */
    public void setClients(XmlClients value) {
        this.clients = value;
    }

    /**
     * Gets the value of the invoices property.
     * 
     * @return
     *     possible object is
     *     {@link XmlInvoices }
     *     
     */
    public XmlInvoices getInvoices() {
        return invoices;
    }

    /**
     * Sets the value of the invoices property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlInvoices }
     *     
     */
    public void setInvoices(XmlInvoices value) {
        this.invoices = value;
    }

    /**
     * Gets the value of the expenses property.
     * 
     * @return
     *     possible object is
     *     {@link XmlExpenses }
     *     
     */
    public XmlExpenses getExpenses() {
        return expenses;
    }

    /**
     * Sets the value of the expenses property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlExpenses }
     *     
     */
    public void setExpenses(XmlExpenses value) {
        this.expenses = value;
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

    /**
     * Gets the value of the taxId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * Sets the value of the taxId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxId(String value) {
        this.taxId = value;
    }

}
