
package de.tfsw.accounting.io.xml;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für xmlExpenseTemplate complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xmlExpenseTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="expenseType" type="{}xmlExpenseType" minOccurs="0"/>
 *         &lt;element name="netAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="taxRate" type="{}xmlTaxRate" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rule" type="{}xmlRecurrenceRule"/>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="firstApplication" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="lastApplication" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="numberOfApplication" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xmlExpenseTemplate", propOrder = {
    "description",
    "expenseType",
    "netAmount",
    "taxRate",
    "category",
    "rule",
    "active",
    "firstApplication",
    "lastApplication",
    "numberOfApplications"
})
public class XmlExpenseTemplate {

    @XmlElement(required = true)
    protected String description;
    @XmlSchemaType(name = "string")
    protected XmlExpenseType expenseType;
    @XmlElement(required = true)
    protected BigDecimal netAmount;
    protected XmlTaxRate taxRate;
    protected String category;
    @XmlElement(required = true)
    protected XmlRecurrenceRule rule;
    protected boolean active;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar firstApplication;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar lastApplication;
    protected int numberOfApplications;

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
     * Ruft den Wert der expenseType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XmlExpenseType }
     *     
     */
    public XmlExpenseType getExpenseType() {
        return expenseType;
    }

    /**
     * Legt den Wert der expenseType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlExpenseType }
     *     
     */
    public void setExpenseType(XmlExpenseType value) {
        this.expenseType = value;
    }

    /**
     * Ruft den Wert der netAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    /**
     * Legt den Wert der netAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNetAmount(BigDecimal value) {
        this.netAmount = value;
    }

    /**
     * Ruft den Wert der taxRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XmlTaxRate }
     *     
     */
    public XmlTaxRate getTaxRate() {
        return taxRate;
    }

    /**
     * Legt den Wert der taxRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlTaxRate }
     *     
     */
    public void setTaxRate(XmlTaxRate value) {
        this.taxRate = value;
    }

    /**
     * Ruft den Wert der category-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Legt den Wert der category-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Ruft den Wert der rule-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XmlRecurrenceRule }
     *     
     */
    public XmlRecurrenceRule getRule() {
        return rule;
    }

    /**
     * Legt den Wert der rule-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlRecurrenceRule }
     *     
     */
    public void setRule(XmlRecurrenceRule value) {
        this.rule = value;
    }

    /**
     * Ruft den Wert der active-Eigenschaft ab.
     * 
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Legt den Wert der active-Eigenschaft fest.
     * 
     */
    public void setActive(boolean value) {
        this.active = value;
    }

    /**
     * Ruft den Wert der firstApplication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstApplication() {
        return firstApplication;
    }

    /**
     * Legt den Wert der firstApplication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstApplication(XMLGregorianCalendar value) {
        this.firstApplication = value;
    }

    /**
     * Ruft den Wert der lastApplication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastApplication() {
        return lastApplication;
    }

    /**
     * Legt den Wert der lastApplication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastApplication(XMLGregorianCalendar value) {
        this.lastApplication = value;
    }

    /**
     * Ruft den Wert der numberOfApplication-Eigenschaft ab.
     * 
     */
    public int getNumberOfApplications() {
        return numberOfApplications;
    }

    /**
     * Legt den Wert der numberOfApplication-Eigenschaft fest.
     * 
     */
    public void setNumberOfApplications(int value) {
        this.numberOfApplications = value;
    }

}
