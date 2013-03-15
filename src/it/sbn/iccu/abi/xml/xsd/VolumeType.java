//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 03:22:19 PM CET 
//


package it.sbn.iccu.abi.xml.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				La forma "volumeType" deriva dalla generica "formaType"
 * 				con l'aggiunta della citazione bibliografica, specifica
 * 				di questa forma. La citazione è comunque opzionale.
 * 			
 * 
 * <p>Java class for volumeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="volumeType">
 *   &lt;complexContent>
 *     &lt;extension base="{}formaType">
 *       &lt;all>
 *         &lt;element name="citazione-bibliografica" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "volumeType", propOrder = {
    "citazioneBibliografica"
})
public class VolumeType
    extends FormaType
{

    @XmlElement(name = "citazione-bibliografica")
    protected String citazioneBibliografica;

    /**
     * Gets the value of the citazioneBibliografica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCitazioneBibliografica() {
        return citazioneBibliografica;
    }

    /**
     * Sets the value of the citazioneBibliografica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCitazioneBibliografica(String value) {
        this.citazioneBibliografica = value;
    }

}
