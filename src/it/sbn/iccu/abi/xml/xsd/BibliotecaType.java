//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.03.20 at 03:22:19 PM CET 
//


package it.sbn.iccu.abi.xml.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Nei casi più elementari si è ritenuto opportuno fissare
 * 				l'obbligatorietà e la ripetibilità degli elementi.
 * 			
 * 
 * <p>Java class for bibliotecaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bibliotecaType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="anagrafica" type="{}anagraficaType" minOccurs="0"/>
 *         &lt;element name="cataloghi" type="{}cataloghiType" minOccurs="0"/>
 *         &lt;element name="patrimonio" type="{}patrimonioType" minOccurs="0"/>
 *         &lt;element name="specializzazione" type="{}specializzazioneType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="servizi" type="{}serviziType" minOccurs="0"/>
 *         &lt;element name="amministrativa" type="{}amministrativaType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bibliotecaType", propOrder = {
    "anagrafica",
    "cataloghi",
    "patrimonio",
    "specializzazione",
    "servizi",
    "amministrativa"
})
public class BibliotecaType {

    protected AnagraficaType anagrafica;
    protected CataloghiType cataloghi;
    protected PatrimonioType patrimonio;
    protected List<SpecializzazioneType> specializzazione;
    protected ServiziType servizi;
    protected AmministrativaType amministrativa;

    /**
     * Gets the value of the anagrafica property.
     * 
     * @return
     *     possible object is
     *     {@link AnagraficaType }
     *     
     */
    public AnagraficaType getAnagrafica() {
        return anagrafica;
    }

    /**
     * Sets the value of the anagrafica property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnagraficaType }
     *     
     */
    public void setAnagrafica(AnagraficaType value) {
        this.anagrafica = value;
    }

    /**
     * Gets the value of the cataloghi property.
     * 
     * @return
     *     possible object is
     *     {@link CataloghiType }
     *     
     */
    public CataloghiType getCataloghi() {
        return cataloghi;
    }

    /**
     * Sets the value of the cataloghi property.
     * 
     * @param value
     *     allowed object is
     *     {@link CataloghiType }
     *     
     */
    public void setCataloghi(CataloghiType value) {
        this.cataloghi = value;
    }

    /**
     * Gets the value of the patrimonio property.
     * 
     * @return
     *     possible object is
     *     {@link PatrimonioType }
     *     
     */
    public PatrimonioType getPatrimonio() {
        return patrimonio;
    }

    /**
     * Sets the value of the patrimonio property.
     * 
     * @param value
     *     allowed object is
     *     {@link PatrimonioType }
     *     
     */
    public void setPatrimonio(PatrimonioType value) {
        this.patrimonio = value;
    }

    /**
     * Gets the value of the specializzazione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specializzazione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecializzazione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecializzazioneType }
     * 
     * 
     */
    public List<SpecializzazioneType> getSpecializzazione() {
        if (specializzazione == null) {
            specializzazione = new ArrayList<SpecializzazioneType>();
        }
        return this.specializzazione;
    }

    /**
     * Gets the value of the servizi property.
     * 
     * @return
     *     possible object is
     *     {@link ServiziType }
     *     
     */
    public ServiziType getServizi() {
        return servizi;
    }

    /**
     * Sets the value of the servizi property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiziType }
     *     
     */
    public void setServizi(ServiziType value) {
        this.servizi = value;
    }

    /**
     * Gets the value of the amministrativa property.
     * 
     * @return
     *     possible object is
     *     {@link AmministrativaType }
     *     
     */
    public AmministrativaType getAmministrativa() {
        return amministrativa;
    }

    /**
     * Sets the value of the amministrativa property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmministrativaType }
     *     
     */
    public void setAmministrativa(AmministrativaType value) {
        this.amministrativa = value;
    }

}
