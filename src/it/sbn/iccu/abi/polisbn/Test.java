package it.sbn.iccu.abi.polisbn;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void log(String msg)
	{
		System.out.println(msg);
	}
	/**
	 * L'argomento è la directory contenente le regioni.
	 */
	public static void main(String[] args)
	{
		PrintWriter out = null, err = null;
		File italiaDir = new File(args[0]);
		File[] regioniDir = italiaDir.listFiles();
		Vector<Polo> poli = new Vector<Polo>();
		try
		{
			out = new PrintWriter("output.csv");
			err = new PrintWriter("errori");
		}
		catch(java.lang.Exception e)
		{
			e.printStackTrace();
		}

		//System.out.println("SBN;ISIL;denom;indirizzo;loc;cap;comune;provincia;regione;url;email;emailref");
				
		// Iteriamo sulle regioni
				
		for(int i = 0; i < regioniDir.length; i++)
		{
			err.println(regioniDir[i] + "/");
			File[] poliDir = regioniDir[i].listFiles();
			for(int j = 0; j < poliDir.length; j++)
			{
				if(poliDir[j].isDirectory())
				{
					Polo polo = null;
					polo = new Polo(poliDir[j]); 
					poli.add(polo);
				}
			}
		}
		Iterator<Polo> i = poli.iterator();
		while(i.hasNext())
		{
			Polo polo = i.next();
			try
			{
				//out = new PrintWriter(polo.getCodicePolo() + ".csv");
				//out = new PrintWriter("output-" + polo.getCodicePolo() + ".CSV");
			}
			catch(java.lang.Exception e)
			{
				e.printStackTrace();
			}
			out.print(polo.exportISIL());
			//out.print("\r");
		}
		out.close();
	}
}


