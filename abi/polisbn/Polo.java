/**
 * 
 */
package polisbn;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author agiuliano
 *
 */
public class Polo
{
	private Vector<Biblioteca> duplicate;
	private TreeMap<String, Biblioteca> biblioteche;
	private String codicePolo;

	public TreeMap<String, Biblioteca> getBiblioteche()
	{
		return biblioteche;
	}
	
	public String getCodicePolo()
	{
		return codicePolo;
	}
	public Polo(File poloDir)
	{
		FilenameFilter fnf = new FilenameFilter()
		{
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.startsWith("biblioteca"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		};
		biblioteche = new TreeMap<String, Biblioteca>();
		duplicate = new Vector<Biblioteca>();
		String polo = poloDir.getName();
		if(poloDir.isDirectory() && polo.length() == 3)
		{
			File[] biblioFile = poloDir.listFiles(fnf);
			for(int k = 0; k < biblioFile.length; k++)
			{
				Biblioteca bib = new Biblioteca(biblioFile[k].getAbsolutePath());
				if(bib.getCodiceSbnBiblioteca() != null && bib.getDaconteggiare())
				{
					bib.setCodiceSbnPolo(polo);
					if(biblioteche.get(bib.getCodiceSbnBiblioteca()) == null)
					{
					  biblioteche.put(bib.getCodiceSbnBiblioteca(), bib);
					}
					else
					{
						System.err.println("Biblioteca " + bib.getCodiceSbnBiblioteca()
								+" duplicata nel file: " + biblioFile[k].getAbsolutePath());
						duplicate.add(bib);
					}
				}
				else
				{
					System.err.println("Nessuna biblioteca nel file: " + biblioFile[k].getAbsolutePath());
				}
			}
		}
		else
		{
			System.err.println("Polo: " + polo);
		}
		if(! biblioteche.isEmpty())
		{
			codicePolo = polo;
		}
	}

	public String exportLDIF()
	{
		Iterator<String> i = biblioteche.keySet().iterator();
		Biblioteca bib;
		StringWriter groupSW = new StringWriter();
		StringWriter biblioEntriesSW = new StringWriter();
		PrintWriter group = new PrintWriter(groupSW);
		PrintWriter biblioEntries = new PrintWriter(biblioEntriesSW);
		group.println("dn: cn=Polo " + codicePolo);
		group.println("objectclass: top");
		group.println("objectclass: groupOfNames");
		group.println("cn: Polo " + codicePolo);
		group.println("mozillaNickname: Polo " + codicePolo);
		while(i.hasNext())
		{
			bib = biblioteche.get(i.next());
			if(bib.getEmail() != null)
			{
				group.println("member: cn=" 
						+ bib.getDenominazione() 
						+ ",mail=" + bib.getEmail());
				biblioEntries.println(bib.exportLDIF());
			}
		}
		return biblioEntriesSW.toString() + groupSW.toString();
	}
	
	public String exportOutlook()
	{
		String[] fields = 
		{
				"Società",
				"Nome",
				"Cognome",
				"Nome visualizzato posta elettronica",
				"Indirizzo posta elettronica",
				"Via (uff.)",
				"Città (uff.)",
				"Provincia (uff.)",
				"CAP (uff.)"
		};
		Iterator<String> i = biblioteche.keySet().iterator();
		Biblioteca bib;
		StringWriter biblioEntriesSW = new StringWriter();
		PrintWriter biblioEntries = new PrintWriter(biblioEntriesSW);
		int j;
		for(j = 0; j < fields.length - 1; j++)
		{
			biblioEntries.print("\"" + fields[j] + "\"" + ",");
		}
		biblioEntries.print(fields[j] + "\r\n");
		while(i.hasNext())
		{
			bib = biblioteche.get(i.next());
			if(bib.getEmail() != null)
			{
				biblioEntries.print(bib.exportOutlook(fields) + "\r\n");
			}
		}
		return biblioEntriesSW.toString();
	}
		
	public String exportVCF()
	{		
		StringWriter vcardSW = new StringWriter();
		PrintWriter vcard = new PrintWriter(vcardSW);
		vcard.println(
				"BEGIN:VCARD\n"
				+ "VERSION:3.0"
				);
		Iterator<String> i = biblioteche.keySet().iterator();
		Biblioteca bib;
		while(i.hasNext())
		{
			bib = biblioteche.get(i.next());
			vcard.println("EMAIL;TYPE=INTERNET:" + bib.getEmail()); 
		}
		vcard.println("FN: Polo " + codicePolo);
		vcard.println("END:VCARD");
		return vcardSW.toString();
	}
	
	public String exportCSV()
	{		
		StringWriter csvSW = new StringWriter();
		PrintWriter csv = new PrintWriter(csvSW);
		Biblioteca bib;
		csv.println("SBN;ISIL;denom;indirizzo;loc;cap;comune;provincia;regione;url;email;emailref");
		Iterator<String> i = biblioteche.keySet().iterator();
		while(i.hasNext())
		{
			bib = biblioteche.get(i.next());
			csv.println(bib.exportCSV2()); 
		}
		return csvSW.toString();
	}

/* 
 * Export minimale per confronti
 */
	
	public String exportISIL()
	{		
		StringWriter csvSW = new StringWriter();
		PrintWriter csv = new PrintWriter(csvSW);
		Biblioteca bib;
		csv.println("SBN;ISIL;nome;comune;provincia;regione");
		Iterator<String> i = biblioteche.keySet().iterator();
		while(i.hasNext())
		{
			bib = biblioteche.get(i.next());
			csv.println(bib.exportISIL()); 
		}
		return csvSW.toString();
	}

	public Vector<Biblioteca> getDuplicate()
	{
		return duplicate;
	}

}
