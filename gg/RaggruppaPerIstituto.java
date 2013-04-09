
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.xpath.XPath;

public class RaggruppaPerIstituto
{ 
	/* 
	 * seleziona il primo elemento dai risultati di un XPath
	 */
	public static Element select(Element root, String xpath) {
		Element temp = null;
		try {
			temp = (Element) XPath.selectSingleNode(root, xpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public static void main(String[] args)
	{
		File sDir = new File(args[0]);
		File[] sList = sDir.listFiles();
		File tDir = new File(sDir.getParent(), "/complessi-per-istituto");
		tDir.mkdir();
		SAXBuilder builder = new SAXBuilder();
		int cut = 100000;
		int max;
		if(cut < sList.length)
			max = cut;
		else
			max = sList.length;
		int cNum = 0;
		try
		{
			Document doc;
			Element root;
			String rootName;
			String baseName;
			BufferedReader in;
			BufferedWriter out;
			String istituto;
			for(int ii=0; ii < max; ii++)
			{
				try
				{
					doc = builder.build(sList[ii]);
					root = doc.getRootElement();
					rootName = root.getName();
					if(rootName == "c")
					{
						cNum++;
						baseName = sList[ii].getName();
						in = new BufferedReader(new FileReader(sList[ii]), 1024*8);
						istituto = select(root, "//did/note[@type='genealogia']/list/item/unittitle[@label='istituto']").getTextNormalize();
						istituto = istituto.replaceAll(" *\n *", " - ");
						istituto = istituto.replaceAll(" *\r *", " - ");
						istituto = istituto.trim();
						Element unitid = root 
						.getChild("did")
						.getChild("unitid");
						if(! unitid.getAttributeValue("type").equals("nrecord"))
						{
							System.err.println("*** Attenzione, " + baseName + " non ha nrecord ***");
							System.err.println(unitid.getAttributeValue("type"));
						}
						else
						{
							baseName = unitid.getText() + ".xml";  
							File aDir = new File(tDir, istituto);
							aDir.mkdir();
							out = new BufferedWriter(new FileWriter(new File(aDir, baseName)), 1024*8);
							int cc;
							while ((cc = in.read()) != -1)
							    out.write(cc); in.close();
							out.close();
							in.close();
						}						
					}
					else
					{
						baseName = sList[ii].getName();
						in = new BufferedReader(new FileReader(sList[ii]), 1024*2);
						File aDir = new File(tDir, "non c");
						aDir.mkdir();
						out = new BufferedWriter(new FileWriter(new File(aDir, baseName)), 1024*2);
						int cc;
						while ((cc = in.read()) != -1)
						    out.write(cc); in.close();
						out.close();
						in.close();
					}	

				}
				catch(java.lang.Exception e)
				{
					baseName = sList[ii].getName();
					System.err.println("*** " + baseName);
					e.printStackTrace();
					in = new BufferedReader(new FileReader(sList[ii]), 1024*2);
					File aDir = new File(tDir, "non c");
					aDir.mkdir();
					out = new BufferedWriter(new FileWriter(new File(aDir, baseName)), 1024*2);
					int cc;
					while ((cc = in.read()) != -1)
					    out.write(cc); in.close();
					out.close();
					in.close();
				}

			}
			System.out.println("Trovati " 
					+ cNum + " file di tipo c su " 
					+ sList.length + " totali");
		}
		catch(java.lang.Exception e)
		{
			System.err.print(e.toString() + ": ");
		}
	}
}
