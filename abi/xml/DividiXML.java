package it.sbn.iccu.abi.xml;
import it.sbn.iccu.abi.xml.xsd.BibliotecaType;
import it.sbn.iccu.abi.xml.xsd.Biblioteche;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
/**  Description of the Class */


public class DividiXML
{

	/*
	 * Carica un file XML conforme al formato 1.5 e lo divide in file
	 * più piccoli in base al numero di biblioteche
	 */

	private static int maxBib = 100; 
	public static void main(String[] args)
	{
		JAXBContext jc;
		try
		{
			/*
			 * Il JAXBContext è il punto di partenza. La stringa deve riprodurre
			 * esattamente il package contenente tutte le classi generate dal
			 * compilatore XJC
			 */
			jc = JAXBContext.newInstance("it.sbn.iccu.abi.xml.xsd" );

			/* 
			 * unmarshall è l'azione di caricare nelle classi create un file XML
			 * secondo il formato da cui sono state create le classi stesse
			 */
			Unmarshaller u = jc.createUnmarshaller();
			
			/*
			 * marshall è l'azione di trasformare le suddette istanze in un XML
			 * conforme al formato da cui sono state create le classi
			 */
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , true);
			
			// questa banale dichiarazione carica un file nella struttura Java
			Biblioteche biblioteche 
				= (Biblioteche) u.unmarshal(new File(args[0]));
			
			String baseFileName = args[0].substring(0, args[0].lastIndexOf(".xml"));
			
			// bibDiv servirà per accumulare 100 biblioteche alla volta
			Biblioteche bibDiv = new Biblioteche();
			
			// serve la dataExport, altrimenti i file piccoli non sono validi
			XMLGregorianCalendar dataExport = biblioteche.getDataExport();
			bibDiv.setDataExport(dataExport);
			Iterator<BibliotecaType> i = biblioteche.getBiblioteca().iterator();
			// contatori per le biblioteche e per i file prodotti
			int bc = 0;
			int fc = 0;
			while(i.hasNext())
			{
				BibliotecaType bib = (BibliotecaType) i.next();
				if(bc == maxBib)
				{
					try
					{
						m.marshal(bibDiv, new PrintWriter(new File(baseFileName + "-" + fc++ + ".xml")));
					}
					catch(FileNotFoundException e)
					{
						e.printStackTrace();
					}
					bibDiv = new Biblioteche();
					bibDiv.setDataExport(dataExport);
					bc = 0;
				}
				bc++;
				bibDiv.getBiblioteca().add(bib);
				/*
				 * Come prova banale, estrae codici e denominazioni di ogni
				 * biblioteca
				 */
				System.out.print
				(
						bib.getAnagrafica().getCodici().getIccu()
						+ " "
						+ bib.getAnagrafica().getNome().getAttuale()
				);
				System.out.println();					
			}
			// finite le biblioteche, può essere che bc non sia arrivato a 100
			// per cui bisogna buttar fuori quelle accumulate nel frattempo
			try
			{
				m.marshal(bibDiv, new PrintWriter(new File(baseFileName + "-" + fc++ + ".xml")));
			}
			catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}

			/*
			 *  marshal è l'azione di "schierare" in un albero XML le istanze 
			 *  delle classi create a partire dallo schema
			 */
			//Marshaller m = jc.createMarshaller();
			//m.marshal(biblioteche, System.out);
		}
		catch(JAXBException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

