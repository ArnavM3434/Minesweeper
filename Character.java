import java.text.DecimalFormat;
public class Character
{
	private String name;
	private String birth;
	private String gender;
	private String homeworld;
	private String species;

	public Character(String name, String birth, String gender, String homeworld, String species)
	{
		this.name = name;
		this.birth = birth;
		this.gender = gender;
		this.homeworld = homeworld;
		this.species = species;
	}
	public String getName()
	{
		return name;
	}
	public String getBirth()
	{
			return birth;
	}
	public String getGender()
	{
			return gender;
	}
	public String getHomeWorld()
	{
			return homeworld;
	}
	public String getSpecies()
	{
			return species;
	}

	public String toString()
	{
		DecimalFormat f=new DecimalFormat("0.000");

		return String.format("%-40s%-40s%-40s%-40s%-40s", name, birth, gender, homeworld, species);
	}
}