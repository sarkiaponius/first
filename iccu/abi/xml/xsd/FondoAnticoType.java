//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 03:22:19 PM CET 
//


package it.sbn.iccu.abi.xml.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				I fondi antichi vanno classificati in base al posseduto:
 * 				fino a 1000, da 1000 a 5000, oltre 5000. Non è quindi
 * 				possibile specificare esattamente un posseduto.
 * 			
 * 
 * <p>Java class for fondoAnticoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fondoAnticoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="volumi" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="-1000"/>
 *             &lt;enumeration value="1000-5000"/>
 *             &lt;enumeration value="5000-"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fondoAnticoType")
public class FondoAnticoType {

    @XmlAttribute(required = true)
    protected String volumi;

    /**
     * Gets the value of the volumi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolumi() {
        return volumi;
    }

    /**
     * Sets the value of the volumi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolumi(String value) {
        this.volumi = value;
    }

}