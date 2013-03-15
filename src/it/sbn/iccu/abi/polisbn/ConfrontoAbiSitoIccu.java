package it.sbn.iccu.abi.polisbn;

import it.sbn.iccu.abi.sql.DB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

public class ConfrontoAbiSitoIccu
{

	public ConfrontoAbiSitoIccu()
	{
		FileReader fr = null;
		config = new Properties();
		try
		{
			fr = new FileReader(new File("src/it/sbn/iccu/abi/polisbn/config.prop")); 
			config.load(fr);
			fr.close();
			logPW = new PrintWriter(new File(config.getProperty("file-log")));
			statPW = new PrintWriter(new File(config.getProperty("file-statistiche")));
			errorPW = new PrintWriter(new File(config.getProperty("file-errori")));
			nosbnPW = new PrintWriter(new File(config.getProperty("file-nosbn")));
			dupPW = new PrintWriter(new File(config.getProperty("file-duplicate")));
			oldPW = new PrintWriter(new File(config.getProperty("file-obsolete")));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
 
	private Vector<Polo> poli;
	private Properties config;
	private PrintWriter logPW = null;
	private PrintWriter statPW = null;
	private PrintWriter errorPW = null;
	private PrintWriter nosbnPW = null;
	private PrintWriter dupPW = null;
	private PrintWriter oldPW = null;
	private DB db;
	
	public void setLog(String logFileName)
	{
		try
		{
			logPW = new PrintWriter(new File(logFileName));
		}
		catch(FileNotFoundException e)
		{
			System.err.println("File di log "+ logFileName + " non trovato");
		}
	}
	
	public void closeLog()
	{
		if(logPW != null)
		{
			logPW.close();
		}
		if(statPW != null)
		{
			statPW.close();
		}
		if(errorPW != null)
		{
			errorPW.close();
		}
		if(nosbnPW != null)
		{
			nosbnPW.close();
		}
		if(dupPW != null)
		{
			dupPW.close();
		}
		if(oldPW != null)
		{
			oldPW.close();
		}
	}
	
	// logger su out e su un file di log
	public void log(String msg)
	{
		System.out.println(msg);
		if(logPW != null)
		{
			logPW.println(msg);
		}
	}

	// logger su out, su file di log e su un altro file
	public void log(String msg, PrintWriter pw)
	{
		log(msg);
		if(pw != null)
		{
			pw.println(msg);
		}
	}
/* 
 * I file dei poli, una volta caricati, possono servire a varie cose, quindi
 * questo metodo viene isolato per essere chiamato una sola volta. Altri metodi
 * useranno il vettore di poli così prodotto, che è un membro privato della 
 * classe
 */
	public void caricaPoli(String args[])
	{
		File italiaDir = new File(args[0]);

		// array di tutte le regioni, ordinato

		File[] regioniDir = italiaDir.listFiles();
		Arrays.sort(regioniDir);

		// si inizializza qualche variabile importante

		poli = new Vector<Polo>();
		int poliNum = 0;
		int bibNum = 0;
		int dupNum = 0;

		// ciclo sulle regioni
		for(int i = 0; i < regioniDir.length; i++)
		{
			log("Regione " + regioniDir[i].getName().toUpperCase(), statPW);

			// si crea l'array di tutte le directory dei poli di una regione,
			// che vengono ordinati; poi si itera su questo array

			File[] poliDir = regioniDir[i].listFiles();
			Arrays.sort(poliDir);
			String msg = null;
			for(int j = 0; j < poliDir.length; j++)
			{
				if(poliDir[j].isDirectory())
				{
					Polo polo = null;
					polo = new Polo(poliDir[j]);
					if(polo.getCodicePolo() != null)
					{
						poli.add(polo);
						poliNum++;
						TreeMap<String, Biblioteca> biblioteche = polo.getBiblioteche();
						bibNum += biblioteche.size();
						dupNum += polo.getDuplicate().size();
						msg = "Polo " + polo.getCodicePolo() 
						+ ":" + polo.getBiblioteche().size() 
						+ " biblioteche";
						if(polo.getDuplicate().size() != 0)
						{
							msg += " (" + polo.getDuplicate().size() 
							+ " duplicate)"; 
						}
						log(msg, statPW);
						Iterator<String> bibIter = biblioteche.keySet().iterator();
						Biblioteca biblio;							
						while(bibIter.hasNext())
						{
							biblio = biblioteche.get(bibIter.next());
							log(biblio.getISIL()
									+ ";"
									+ polo.getCodicePolo() 
									+ biblio.getCodiceSbnBiblioteca()
									+ ";"
									+ biblio.getDenominazione());
						}
						// iteriamo anche le eventuali duplicate
						Iterator<Biblioteca> dupIter = polo.getDuplicate().iterator();
						while(dupIter.hasNext())
						{
							biblio = dupIter.next();
							log(biblio.getISIL()
									+ ";"
									+ polo.getCodicePolo() 
									+ biblio.getCodiceSbnBiblioteca()
									+ ";"
									+ biblio.getDenominazione(),
									dupPW);
						}
					}
				}
			}
		}
		/*
		 * poli e biblioteche sono stati tutti caricati, possiamo cominciare
		 * a iterare sui poli
		 */
		log("Caricati " + poliNum + " poli e " + bibNum + " biblioteche (più " 
				+ dupNum + " duplicate)", statPW);
		dupPW.flush();
		statPW.flush();		
	}

	public void cercaInAbi() //throws BibliotecaDuplicataException
	{
		ResultSet rs = null;
		PreparedStatement existsBib = db.prepare("select id_bib from biblioteca"
				+ " where not stato='CANCELLATA'"
				+ " and isil_pr || lpad(isil_nr, 4, '0') = ?");
		PreparedStatement bibHasSBN = db.prepare("select count(*)"
			+ " from catal_collettivo"
			+ " where id_ccc = 146"
			+ " and id_bib = ?");
		Iterator<Polo> i = poli.iterator();
		int ii = 0;
		int jj = 0;
		while(i.hasNext())
		{
			Polo polo = i.next();
			String codPolo = polo.getCodicePolo();
			/* 
			 * ora iteriamo sulle biblioteche del polo corrente
			 */
			Iterator<String> j = polo.getBiblioteche().keySet().iterator();
			while(j.hasNext())
			{
				Biblioteca biblio = polo.getBiblioteche().get(j.next());
				String isil = biblio.getISIL().trim();
				String codBiblio = biblio.getCodiceSbnBiblioteca();
				String nome = biblio.getDenominazione();
				String comune = biblio.getComune();
				String provincia = biblio.getProvincia();
				if(isil.length() != 6 
						|| codPolo.length() != 3 
						|| codBiblio.length() != 2
						|| isil.substring(0, 2).equals("EX"))
				{
					log("* codice mal formato: [" + isil
							+ ";" + codPolo 
							+ codBiblio
							+ ";" + nome + "]", errorPW);
				}
				else
				{
	//				String isilPR = isil.substring(0, 2);
		//			String isilNR = isil.substring(2, 6);
					// ricordarsi sempre le cancellate!
					try
					{
						// vediamo prima se c'è la biblioteca
						existsBib.setString(1, isil);
//						existsBib.setInt(2, Integer.parseInt(isilNR));
						rs = existsBib.executeQuery();
						String msg = isil
						+ ";" + codPolo	+ codBiblio
						+ ";" + nome
						+ ";" + comune
						+ ";" + provincia;
						if(rs.last())
						{
							// se arriva qui, esiste la biblioteca, quindi si 
							// può vedere se partecipa a SBN
							++ii;
							int idBib = rs.getInt(1);
							bibHasSBN.setInt(1, idBib);
							rs = bibHasSBN.executeQuery();
							rs.last();
							// se il conteggio è 0, la biblioteca non partecipa 
							// a SBN secondo l'ABI e viene segnalata
							if(rs.getInt(1) == 0)
							{
								log(msg, nosbnPW);
								jj++;
							}
						}
						else
						{
							// se arriva qui, la biblioteca non esiste in ABI,
							// per cui segnala la cosa
							log("* mancante in ABI:" + msg, errorPW);
						}
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		log("Su " + ii + " biblioteche in ABI con codici validi sul sito ICCU " 
				+ jj + " non dichiarano il catalogo SBN", statPW);
	}

/* 
 * Cerchiamo le biblioteche ABI che risultano aderire a SBN sul sito ICCU.
 * Infatti alcune potrebbero essere uscite da SBN e l'ABI non lo sa.
 */
	public void cercaInSitoIccu()
	{
		ResultSet rs = null;
		// elenchiamo le biblioteche ABI che partecipano a SBN
		rs = db.select("select isil_pr, isil_nr, sbn, denominazione"
				+ " from biblioteca b, catal_collettivo c"
				+ " where not b.stato='CANCELLATA'"
				+ " and c.id_ccc = 146"
				+ " and c.id_bib = b.id_bib"
				+ " order by isil_pr, isil_nr");
		// popoliamo una mappa delle biblioteche dal sito ICCU
		// per cercare più facilmente
		TreeMap<String, Biblioteca> biblioteche = new TreeMap<String, Biblioteca>();
		Iterator<Polo> i = poli.iterator();
		while(i.hasNext())
		{
			Polo polo = i.next();
			/* 
			 * ora iteriamo sulle biblioteche del polo corrente
			 */
			Iterator<String> j = polo.getBiblioteche().keySet().iterator();
			while(j.hasNext())
			{
				Biblioteca bib = polo.getBiblioteche().get(j.next());
				String isil = bib.getISIL().trim();
				biblioteche.put(isil, bib);
			}
//			String isilPR = isil.substring(0, 2);
//			String isilNR = isil.substring(2, 6);
		}
		// ora si può ciclare sul resultset e cercare le biblioteche sul sito
		String isilPR, isilNR;
		int ii = 0;
		int jj = 0;
		try
		{
			DecimalFormat df = new DecimalFormat("0000");
			while(rs.next())
			{
				ii++;
				isilPR = rs.getString("isil_pr");
				isilNR = df.format(rs.getInt("isil_nr"));
				if(biblioteche.get(isilPR + isilNR) == null)
				{
					jj++;
					log(isilPR + isilNR 
							+ ";" + rs.getString("sbn")
							+ ";" + rs.getString("denominazione"), oldPW);
				}
			}
			log("Su " + ii + " biblioteche ABI legate al catalogo SBN, " 
					+ jj + " non risultano sul sito ICCU", statPW);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * L'argomento è la directory contenente le regioni.
	 */
	public static void main(String[] args)
	{
		ConfrontoAbiSitoIccu c = new ConfrontoAbiSitoIccu();
		c.db = new DB(DB.urlEsercizio);
		c.caricaPoli(args);
		c.cercaInAbi();
		c.cercaInSitoIccu();
		c.closeLog();
		c.db.free();
	}
}


