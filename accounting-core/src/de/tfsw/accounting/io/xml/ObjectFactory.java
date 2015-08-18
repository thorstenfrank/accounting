
package de.tfsw.accounting.io.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tfsw.accounting.io.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _User_QNAME = new QName("", "user");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tfsw.accounting.io.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlUser }
     * 
     */
    public XmlUser createXmlUser() {
        return new XmlUser();
    }

    /**
     * Create an instance of {@link XmlAddress }
     * 
     */
    public XmlAddress createXmlAddress() {
        return new XmlAddress();
    }

    /**
     * Create an instance of {@link XmlPaymentTerms }
     * 
     */
    public XmlPaymentTerms createXmlPaymentTerms() {
        return new XmlPaymentTerms();
    }

    /**
     * Create an instance of {@link XmlClients }
     * 
     */
    public XmlClients createXmlClients() {
        return new XmlClients();
    }

    /**
     * Create an instance of {@link XmlExpenseTemplate }
     * 
     */
    public XmlExpenseTemplate createXmlExpenseTemplate() {
        return new XmlExpenseTemplate();
    }

    /**
     * Create an instance of {@link XmlExpenses }
     * 
     */
    public XmlExpenses createXmlExpenses() {
        return new XmlExpenses();
    }

    /**
     * Create an instance of {@link XmlInvoice }
     * 
     */
    public XmlInvoice createXmlInvoice() {
        return new XmlInvoice();
    }

    /**
     * Create an instance of {@link XmlClient }
     * 
     */
    public XmlClient createXmlClient() {
        return new XmlClient();
    }

    /**
     * Create an instance of {@link XmlTaxRates }
     * 
     */
    public XmlTaxRates createXmlTaxRates() {
        return new XmlTaxRates();
    }

    /**
     * Create an instance of {@link XmlTaxRate }
     * 
     */
    public XmlTaxRate createXmlTaxRate() {
        return new XmlTaxRate();
    }

    /**
     * Create an instance of {@link XmlInvoicePosition }
     * 
     */
    public XmlInvoicePosition createXmlInvoicePosition() {
        return new XmlInvoicePosition();
    }

    /**
     * Create an instance of {@link XmlBankAccount }
     * 
     */
    public XmlBankAccount createXmlBankAccount() {
        return new XmlBankAccount();
    }

    /**
     * Create an instance of {@link XmlInvoices }
     * 
     */
    public XmlInvoices createXmlInvoices() {
        return new XmlInvoices();
    }

    /**
     * Create an instance of {@link XmlRecurrenceRule }
     * 
     */
    public XmlRecurrenceRule createXmlRecurrenceRule() {
        return new XmlRecurrenceRule();
    }

    /**
     * Create an instance of {@link XmlInvoicePositions }
     * 
     */
    public XmlInvoicePositions createXmlInvoicePositions() {
        return new XmlInvoicePositions();
    }

    /**
     * Create an instance of {@link XmlExpense }
     * 
     */
    public XmlExpense createXmlExpense() {
        return new XmlExpense();
    }

    /**
     * Create an instance of {@link XmlExpenseTemplates }
     * 
     */
    public XmlExpenseTemplates createXmlExpenseTemplates() {
        return new XmlExpenseTemplates();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "user")
    public JAXBElement<XmlUser> createUser(XmlUser value) {
        return new JAXBElement<XmlUser>(_User_QNAME, XmlUser.class, null, value);
    }

}
