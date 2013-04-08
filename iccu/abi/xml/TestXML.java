package it.sbn.iccu.abi.xml;

import it.sbn.iccu.abi.sql.DB;
import it.sbn.iccu.abi.xml.xsd.Biblioteche;

/**  Description of the Class */

public class TestXML
{	
	public static void main(String[] args)
	{
		ControlliXML t = new ControlliXML();
		Biblioteche b = t.carica(args[0]);
		//t.lista(b);
		System.out.println(t.controllaCodiciISIL(b));
		//System.out.println(t.cercaCodiceCEI(b, new DB(DB.urlEsercizio)));
		System.out.println(t.cercaInProvvisorio(b, new DB(DB.urlTest)));
	}
}

