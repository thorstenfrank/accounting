
package de.tfsw.accounting.io.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für xmlUser complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
 *         &lt;element name="expenseTemplates" type="{}xmlExpenseTemplates" minOccurs="0"/>
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
    "expenses",
    "expenseTemplates"
})
@XmlRootElement
public class XmlUser {

    protected String description;
    protected XmlAddress address;
    protected XmlBankAccount bankAccount;
    protected XmlTaxRates taxRates;
    protected XmlClients clients;
    protected XmlInvoices invoices;
    protected XmlExpenses expenses;
    protected XmlExpenseTemplates expenseTemplates;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "taxId")
    protected String taxId;

    /**
     * Ruft den Wert der description-Eigenschaft ab.
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
     * Legt den Wert der description-Eigenschaft fest.
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
     * Ruft den Wert der address-Eigenschaft ab.
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
     * Legt den Wert der address-Eigenschaft fest.
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
     * Ruft den Wert der bankAccount-Eigenschaft ab.
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
     * Legt den Wert der bankAccount-Eigenschaft fest.
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
     * Ruft den Wert der taxRates-Eigenschaft ab.
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
     * Legt den Wert der taxRates-Eigenschaft fest.
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
     * Ruft den Wert der clients-Eigenschaft ab.
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
     * Legt den Wert der clients-Eigenschaft fest.
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
     * Ruft den Wert der invoices-Eigenschaft ab.
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
     * Legt den Wert der invoices-Eigenschaft fest.
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
     * Ruft den Wert der expenses-Eigenschaft ab.
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
     * Legt den Wert der expenses-Eigenschaft fest.
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
     * Ruft den Wert der expenseTemplates-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XmlExpenseTemplates }
     *     
     */
    public XmlExpenseTemplates getExpenseTemplates() {
        return expenseTemplates;
    }

    /**
     * Legt den Wert der expenseTemplates-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlExpenseTemplates }
     *     
     */
    public void setExpenseTemplates(XmlExpenseTemplates value) {
        this.expenseTemplates = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
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
     * Legt den Wert der name-Eigenschaft fest.
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
     * Ruft den Wert der taxId-Eigenschaft ab.
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
     * Legt den Wert der taxId-Eigenschaft fest.
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
