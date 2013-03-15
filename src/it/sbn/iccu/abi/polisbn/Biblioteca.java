/**
 * 
 */
package it.sbn.iccu.abi.polisbn;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.xpath.XPath;

/**
 * @author agiuliano
 *
 */
public class Biblioteca
{

// Parametri della biblioteca. Non sarebbe meglio un vettore di attributi?
	
	private String 
		denominazione,
		codiceSbnPolo, 
		codiceSbnBiblioteca,
		codiceISIL,
		indirizzo,
		cap,
		località,
		comune,
		provincia,
		regione,
//		nazione,
	//	telefono[],
		//fax[],
		email,
		urlPrincipale,
		//urlSecondarie[],
		emailReferente,
		daconteggiare;
	
	private Element root;

	private Properties attributes;

	private String get(String key)
	{
		return attributes.getProperty(key);
	}
	
	private void set(String key, String value)
	{
		attributes.setProperty(key, value);
	}
	
	private String element(String path)
	{
		String value = null;
		try
		{
			value = ((Element) XPath.selectSingleNode(root, path)).getTextNormalize();
		}
		catch(JDOMException e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
	private Properties xmlMap, outlookMap;
		
	public void setCodiceSbnPolo(String str)
	{
		codiceSbnPolo = str;
		attributes.setProperty("codice-sbn-polo", str);
	}
	
	public String getDenominazione()
	{
		return denominazione;
	}
	
	public String getEmail()
	{
		if(get("email") != null)
			return get("email");
		else	
			return get("emailref");
	}
	
	public String getISIL()
	{
		return codiceISIL;
	}

	public String getIndirizzo()
	{
		return indirizzo;
	}

	public String getCodiceSbnBiblioteca()
	{
		if(codiceSbnBiblioteca != null && codiceSbnBiblioteca.length() != 0)
		return codiceSbnBiblioteca;
		else return (String) null;
	}
	
// Delimitatori per l'export CSV
	
	private String cvsFieldDelim = ",";
	private String cvsStringDelim = "\"";

	/*
	 * Carica una biblioteca usando attributi "dinamici" invece che statici.
	 * Questo permette di gestire i dati con maggiore flessibilità, ma ho
	 * paura che possa anche creare dei casini.
	 */
	
	public void init(File in)
	{
		attributes = new Properties();
		xmlMap = new Properties();
		outlookMap = new Properties();
		FileReader fr = null;
		try
		{
			fr = new FileReader(new File("src/it/sbn/iccu/abi/polisbn/xmlmap.prop")); 
			xmlMap.load(fr);
			fr = new FileReader(new File("src/it/sbn/iccu/abi/polisbn/outlook-from.map"));
			outlookMap.load(fr);
			fr.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Document doc = load(in);
		root = doc.getRootElement();
		Element bib = root.getChild("Biblioteca");
		if(bib != null)
		{
			Enumeration<Object> keys = xmlMap.keys();
			while(keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				String path = xmlMap.getProperty(key);
				try
				{
					set(key, element(path));
				}
				catch(java.lang.NullPointerException ex)
				{
					System.err.println(key + " mancante, file: " + in.getName());
				}
			}
		}
	}
	
	public Properties getAttributes()
	{
		return attributes;
	}
	
	public Document load(File in)
	{
		Document doc = new Document();
		try
		{
			SAXBuilder builder = new SAXBuilder(false);
			doc = builder.build(in);
		}
		catch(JDOMException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return doc;
	}
/*
 * Popola i dati di una biblioteca leggendo un file XML. Da notare che questo 
 * metodo non può impostare anche il codice di polo, che non è contenuto nel 
 * file XML 
 */
	public Biblioteca(String xml)
	{
		init(new File(xml));
		Document doc = load(new File(xml));
		//System.err.println("Elaboro file: " + xml);
		try
		{
			Element root = doc.getRootElement();
			Element bib = root.getChild("Biblioteca");
			if(bib != null)
			{
				denominazione = bib.getChildText("denominazionefront");
				codiceSbnBiblioteca = bib.getChildText("codbiblio");
				codiceISIL = bib.getChildText("codabi");
				indirizzo = bib.getChildText("indirizzo");
				cap = bib.getChildText("cap");
				località = bib.getChildText("localita");
				comune = bib.getChildText("comune");
				provincia = bib.getChildText("provincia");
				regione = bib.getChildText("regione");
				daconteggiare = bib.getChildText("daconteggiare");
				urlPrincipale = bib.getChild("urlprincipale")
					.getChildText("indirizzo");
				if(bib.getChildText("email") != null)
				{
					email = new String(bib.getChildText("email"));
				}
				else if(bib.getChild("referente").getChildText("email_referente") != null)
				{
					email = bib.getChild("referente")
					.getChildText("email_referente");
				}
			}	
		}
		catch(java.lang.NullPointerException ex)
		{
			System.err.println("Dato mancante, file: " + xml);
			System.err.println(ex.toString());
		}
	}
	
/*
 * Esportazione della biblioteca in formato CSV. C'è un metodo pubblico che
 * richiama un metodo privato per aggiungere un campo già nel modo giusto.
 * Si presume sia tutto stringa, delimitatore ";", e stringhe con doppio apice
 */
	private String csvField(String field, boolean last)
	{
		String temp;
		if(field != null)
			temp =  cvsStringDelim + field + cvsStringDelim;
		else
			temp = cvsStringDelim + cvsStringDelim;
		if(!last)
			temp += cvsFieldDelim;
		return temp;
	}
	
	public String csvField(String field)
	{
		return csvField(field, false);
	}
	
	public String exportCSV()
	{
		StringWriter csvSW = new StringWriter();
		PrintWriter csv = new PrintWriter(csvSW);
		csv.print(csvField(codiceSbnPolo + codiceSbnBiblioteca));
		csv.print(csvField(codiceISIL));
		csv.print(csvField(denominazione));
		csv.print(csvField(indirizzo));
		csv.print(csvField(località));
		csv.print(csvField(cap));
		csv.print(csvField(comune));
		csv.print(csvField(provincia));
		csv.print(csvField(regione));
		csv.print(csvField(urlPrincipale));
		csv.print(csvField(email));
		csv.print(csvField(emailReferente, true));
		return csvSW.toString();
	}
	
	public String exportISIL()
	{
		StringWriter csvSW = new StringWriter();
		PrintWriter csv = new PrintWriter(csvSW);
		csv.print(csvField(codiceSbnPolo + codiceSbnBiblioteca));
		csv.print(csvField(codiceISIL));
		csv.print(csvField(denominazione));
		csv.print(csvField(comune));
		csv.print(csvField(provincia));
		csv.print(csvField(regione, true));
		return csvSW.toString();
	}
	
	public String exportCSV2()
	{
		StringWriter csvSW = new StringWriter();
		PrintWriter csv = new PrintWriter(csvSW);
		csv.print(csvField(attributes.getProperty("codice-sbn-polo") 
				+ attributes.getProperty("codice-sbn-biblioteca")));
		csv.print(csvField(attributes.getProperty("codice-isil")));
		csv.print(csvField(attributes.getProperty("denominazione")));
		csv.print(csvField(attributes.getProperty("indirizzo")));
		csv.print(csvField(attributes.getProperty("località")));
		csv.print(csvField(attributes.getProperty("cap")));
		csv.print(csvField(attributes.getProperty("comune")));
		csv.print(csvField(attributes.getProperty("provincia")));
		csv.print(csvField(attributes.getProperty("regione")));
		csv.print(csvField(attributes.getProperty("url")));
		csv.print(csvField(attributes.getProperty("email"), true));
		return csvSW.toString();
	}
	
	public String exportOutlook(String fields[])
	{
		StringWriter biblioSW = new StringWriter();
		PrintWriter biblio = new PrintWriter(biblioSW);
		String attr;
		int i;
		for(i = 0; i < fields.length - 1; i++)
		{
			attr = outlookMap.getProperty(fields[i]);
			System.err.println(i + ": " + fields[i] + " = " + attr);
			if(attr.equals("email"))
			{
				biblio.print(csvField(getEmail()));
			}
			else
			{
				biblio.print(csvField(get(attr)));
			}
		}
		attr = outlookMap.getProperty(fields[i]);
		biblio.print(csvField(get(attr), true));
		return biblioSW.toString();
	}

	public String exportLDIFold()
	{		
		StringWriter biblioSW = new StringWriter();
		PrintWriter biblio = new PrintWriter(biblioSW);
		biblio.println("dn: cn=" + denominazione + ",mail=" + email);
		biblio.println("objectclass: top"); 
		biblio.println("objectclass: person");
		biblio.println("objectclass: organizationalPerson");
		biblio.println("objectclass: inetOrgPerson");
		biblio.println("objectclass: mozillaAbPersonAlpha");
		biblio.println("cn: " + denominazione);
		biblio.println("mail: " + email);
		return biblioSW.toString();
	}

	public String exportLDIF()
	{		
		StringWriter biblioSW = new StringWriter();
		PrintWriter biblio = new PrintWriter(biblioSW);
		//String email = getEmail();//("email");
		//String nome = get("denominazione");
		//if(email == "" || email == null)
		//{
			//email = get("emailref");
		//}
		biblio.println("dn: cn=" + getDenominazione() + ",mail=" + getEmail());
		biblio.println("objectclass: top"); 
		biblio.println("objectclass: person");
		biblio.println("objectclass: organizationalPerson");
		biblio.println("objectclass: inetOrgPerson");
		biblio.println("objectclass: mozillaAbPersonAlpha");
		biblio.println("cn: " + getDenominazione());
		biblio.println("mail: " + getEmail());
		return biblioSW.toString();
	}

	public String getComune()
	{
		return comune;
	}

	public void setComune(String comune)
	{
		this.comune = comune;
	}

	public String getProvincia()
	{
		return provincia;
	}

	public void setProvincia(String provincia)
	{
		this.provincia = provincia;
	}

	public boolean getDaconteggiare()
	{
		if(daconteggiare.equals("true")) return true;
		else return false;
	}

	public String getCodiceSbnPolo()
	{
		return codiceSbnPolo;
	}
}
