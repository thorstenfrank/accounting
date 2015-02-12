
package de.tfsw.accounting.elster.adapter.usta_201501;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tfsw.accounting.elster.adapter.usta_201501 package. 
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

    private final static QName _Anmeldungssteuern_QNAME = new QName("http://www.elster.de/2002/XMLSchema", "Anmeldungssteuern");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tfsw.accounting.elster.adapter.usta_201501
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UstaAnmeldungssteuernCType }
     * 
     */
    public UstaAnmeldungssteuernCType createUstaAnmeldungssteuernCType() {
        return new UstaAnmeldungssteuernCType();
    }

    /**
     * Create an instance of {@link UstaUmsatzsteuersondervorauszahlungCType }
     * 
     */
    public UstaUmsatzsteuersondervorauszahlungCType createUstaUmsatzsteuersondervorauszahlungCType() {
        return new UstaUmsatzsteuersondervorauszahlungCType();
    }

    /**
     * Create an instance of {@link AdresseCType }
     * 
     */
    public AdresseCType createAdresseCType() {
        return new AdresseCType();
    }

    /**
     * Create an instance of {@link UstaDauerfristverlaengerungCType }
     * 
     */
    public UstaDauerfristverlaengerungCType createUstaDauerfristverlaengerungCType() {
        return new UstaDauerfristverlaengerungCType();
    }

    /**
     * Create an instance of {@link UstaSteuerfallCType }
     * 
     */
    public UstaSteuerfallCType createUstaSteuerfallCType() {
        return new UstaSteuerfallCType();
    }

    /**
     * Create an instance of {@link MandantCType }
     * 
     */
    public MandantCType createMandantCType() {
        return new MandantCType();
    }

    /**
     * Create an instance of {@link UstaUmsatzsteuervoranmeldungCType }
     * 
     */
    public UstaUmsatzsteuervoranmeldungCType createUstaUmsatzsteuervoranmeldungCType() {
        return new UstaUmsatzsteuervoranmeldungCType();
    }

    /**
     * Create an instance of {@link UstaDatenlieferantCType }
     * 
     */
    public UstaDatenlieferantCType createUstaDatenlieferantCType() {
        return new UstaDatenlieferantCType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UstaAnmeldungssteuernCType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.elster.de/2002/XMLSchema", name = "Anmeldungssteuern")
    public JAXBElement<UstaAnmeldungssteuernCType> createAnmeldungssteuern(UstaAnmeldungssteuernCType value) {
        return new JAXBElement<UstaAnmeldungssteuernCType>(_Anmeldungssteuern_QNAME, UstaAnmeldungssteuernCType.class, null, value);
    }

}
