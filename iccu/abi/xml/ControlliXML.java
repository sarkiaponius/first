package it.sbn.iccu.abi.xml;
import it.sbn.iccu.abi.sql.DB;
import it.sbn.iccu.abi.xml.xsd.AnagraficaType;
import it.sbn.iccu.abi.xml.xsd.BibliotecaType;
import it.sbn.iccu.abi.xml.xsd.Biblioteche;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
/**  Description of the Class */


public class ControlliXML
{

	/*
	 * Carica un file XML conforme al formato 1.5
	 */

	public Biblioteche carica(File f)
	{
		/*
		 * Il JAXBContext è il punto di partenza. La stringa deve riprodurre
		 * esattamente il package contenente tutte le classi generate dal
		 * compilatore XJC
		 */
		JAXBContext jc = null;
		Biblioteche biblioteche = null;
		Unmarshaller u = null;
		try
		{
			jc = JAXBContext.newInstance("it.sbn.iccu.abi.xml.xsd" );
			/* 
			 * unmarshal è l'azione di caricare nelle classi create un file XML
			 * secondo il formato da cui sono state create le classi stesse
			 */
			u = jc.createUnmarshaller();
			biblioteche	= (Biblioteche) u.unmarshal(f);

		}
		catch(JAXBException e)
		{
			e.printStackTrace();
		}

		return biblioteche;

	}
	
	// Costruttore con nome di file
	public Biblioteche carica(String fn)
	{
		return carica(new File(fn));
	}
	
	public void lista(Biblioteche b)
	{
		/*
		 * Estrae codici e denominazioni di ogni
		 * biblioteca
		 */
		Iterator<BibliotecaType> i = b.getBiblioteca().iterator();
		while(i.hasNext())
		{
			BibliotecaType bib = (BibliotecaType) i.next();
			System.out.print
			(
					bib.getAnagrafica().getCodici().getIccu()
					+ " "
					+ bib.getAnagrafica().getNome().getAttuale()
			);
			Iterator<AnagraficaType.Contatti.Altro> i2 
				= bib.getAnagrafica()
				.getContatti()
				.getAltro()
				.iterator();
			while(i2.hasNext())
			{
				AnagraficaType.Contatti.Altro aca = i2.next();
				if(aca.getTipo().equals("e-mail"))
				{
				System.out.print(" [" + aca.getValore() + "]");
					if(aca.getNote() != null )
					{
						System.out.print(" (" + aca.getNote() + ") ");
					}
				}
			}
			System.out.println();					
		}
	}

	/*
	 * Un ovvio test sulla validità dei codici ISIL
	 */
	public String controllaCodiciISIL(Biblioteche b)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw); 
		MessageFormat wrongLengthMsg = 
			new MessageFormat("[{0}]: *** codice di lunghezza errata ***");
		MessageFormat badFormatMsg = 
			new MessageFormat("[{0}]: *** codice mal formato ***");

		Iterator<BibliotecaType> i = b.getBiblioteca().iterator();
		while(i.hasNext())
		{
			BibliotecaType bib = (BibliotecaType) i.next();
			String isil = bib.getAnagrafica().getCodici().getIccu();
			
			/*
			 *  se manca il prefisso, lo mettiamo, così i controlli seguenti
			 *  si fanno una volta sola; il codice viene anche corretto nei
			 *  dati, così esce nell'XML, se uno decide di estrarlo 
			 */
			if( ! isil.startsWith("IT-"))
			{
				isil = "IT-" + isil;
				bib.getAnagrafica().getCodici().setIccu(isil);
			}
			if(isil.length() != 9)
			{
				pw.println(wrongLengthMsg.format(new Object[] {isil}));
			}
			else	
			{
				if( ! Pattern.matches("IT-[a-zA-Z]{2}[0-9]{4}", isil))
				{
					pw.println(badFormatMsg.format(new Object[] {isil}));
				}
			}
		}
		return sw.toString();
	}
	
	/*
	 * Controlla che le biblioteche da importare abbiano o meno
	 * un codice CEI
	 */
	public String cercaCodiceCEI(Biblioteche b, DB db) 
	{
		PreparedStatement codiceCEI = db.prepare("select cei from biblioteca"
				+ " where not stato='CANCELLATA'"
				+ " and 'IT-' || isil_pr || lpad(isil_nr, 4, '0') = ?");
		ResultSet rs = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw); 
		Iterator<BibliotecaType> i = b.getBiblioteca().iterator();
		MessageFormat mf = 
			new MessageFormat("[{0}]: *** la biblioteca ha un codice CEI ({1}) ***");
		while(i.hasNext())
		{
			BibliotecaType bib = (BibliotecaType) i.next();
			String isil = bib.getAnagrafica().getCodici().getIccu();
			try
			{
				codiceCEI.setString(1, isil);
				rs = codiceCEI.executeQuery();
				if(rs.next())
				{
					if(rs.getString(1) != null)
					{
						if( ! rs.getString(1).toLowerCase().equals("null"))
						{
							if( rs.getString(1).length() != 0)
							{
								pw.println(mf.format(new Object[] {isil, rs.getString(1)}));
							}
						}
					}
				}
			}
			catch(SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sw.toString();
	}
	/*
	 * Controlla che le biblioteche da importare non siano già nel provvisorio
	 */
	public String cercaInProvvisorio(Biblioteche b, DB db)
	{
		PreparedStatement inProvvisorio = db.prepare("select denominazione, data_import"
				+ " from p_biblioteca"
				+ " where not stato='CANCELLATA'"
				+ " and 'IT-' || isil_pr || lpad(isil_nr, 4, '0') = ?");
		ResultSet rs = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw); 
		Iterator<BibliotecaType> i = b.getBiblioteca().iterator();
		MessageFormat mf = 
			new MessageFormat("[{0}]: *** la biblioteca è già provvisorio ***");
		while(i.hasNext())
		{
			BibliotecaType bib = (BibliotecaType) i.next();
			String isil = bib.getAnagrafica().getCodici().getIccu();
			try
			{
				inProvvisorio.setString(1, isil);
				rs = inProvvisorio.executeQuery();
				if(rs.next())
				{
					pw.println(mf.format(new Object[] {isil}));
				}
			}
			catch(SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sw.toString();
	}
}

