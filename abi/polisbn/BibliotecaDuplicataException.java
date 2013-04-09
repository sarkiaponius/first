package polisbn;

public class BibliotecaDuplicataException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Biblioteca biblioteca;
	private String path;
	public BibliotecaDuplicataException(Biblioteca b, String p)
	{
		setPath(p);
		setBiblioteca(b);
	}
	public String getPath()
	{
		return path;
	}
	public void setPath(String path)
	{
		this.path = path;
	}
	public Biblioteca getBiblioteca()
	{
		return biblioteca;
	}
	public void setBiblioteca(Biblioteca biblioteca)
	{
		this.biblioteca = biblioteca;
	}
}
