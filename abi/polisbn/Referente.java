/**
 * 
 */
package polisbn;

/**
 * @author agiuliano
 *
 */
public class Referente
{
	private String nome;
	private String ruolo;
	
	public void setNome(String str)
	{
		nome = new String(str);
	}
	
	public String getNome()
	{
		return nome;
	}

	public void setRuolo(String str)
	{
		ruolo = new String(str);
	}
	
	public String getRuolo()
	{
		return ruolo;
	}
	
	public Referente()
	{
		nome = "";
		ruolo = "";
	}
	
	public Referente(String nome, String ruolo)
	{
		setNome(nome);
		setRuolo(ruolo);
	}
}
