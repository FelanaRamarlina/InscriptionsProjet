package inscriptions;
import src.Connect;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Point d'entr�e dans l'application, un seul objet de type Inscription
 * permet de g�rer les comp�titions, candidats (de type equipe ou personne)
 * ainsi que d'inscrire des candidats � des comp�tition.
 */

public class Inscriptions implements Serializable
{
	private static final long serialVersionUID = -3095339436048473524L;
	private static final String FILE_NAME = "Inscriptions.srz";
	private static Inscriptions inscriptions;
	private Connect connect;
	private SortedSet<Competition> competitions = new TreeSet<Competition>();
	private SortedSet<Candidat> candidats = new TreeSet<Candidat>();
	//Doit faire manip avec base
	public static boolean SERIALIZE = true; 
	public static boolean SAVE_OBJECT = true; 
	
	
	private Inscriptions()
	{
	}
	
	/**
	 * Retourne les comp�titions.
	 * @return
	 * @throws SQLException 
	 */
	
	public SortedSet<Competition> getCompetitions() throws SQLException
	{
		if (!SERIALIZE)
			return Collections.unmodifiableSortedSet(competitions);
		return connect.getCompetitions();
	}
	
	/**
	 * Retourne tous les candidats (personnes et �quipes confondues).
	 * @return
	 * @throws SQLException 
	 */
	
	public SortedSet<Candidat> getCandidats() throws SQLException
	{
		if (!SERIALIZE)
			return Collections.unmodifiableSortedSet(candidats);
		return connect.getCandidats();
	}

	/**
	 * Retourne toutes les personnes.
	 * @return
	 * @throws SQLException 
	 */
	
	public SortedSet<Personne> getPersonnes() throws SQLException
	{
		if (!SERIALIZE){
			SortedSet<Personne> personnes = new TreeSet<Personne>();
			for (Candidat c : getCandidats())
				if (c instanceof Personne)
					personnes.add((Personne)c);
			return Collections.unmodifiableSortedSet(personnes);
		}else{
			return (SortedSet<Personne>) connect.getPersonnes();
		}
	}

	/**
	 * Retourne toutes les �quipes.
	 * @return
	 * @throws SQLException 
	 */
	
	public SortedSet<Equipe> getEquipes() throws SQLException
	{
		if (!SERIALIZE){
			SortedSet<Equipe> equipes = new TreeSet<>();
			for (Candidat c : getCandidats())
				if (c instanceof Equipe)
					equipes.add((Equipe)c);
			return Collections.unmodifiableSortedSet(equipes);
		}else{
			return (SortedSet<Equipe>) connect.getEquipes();
		}
	}

	/**
	 * Cr��e une comp�tition. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Competition}.
	 * @param nom
	 * @param dateCloture
	 * @param enEquipe
	 * @return
	 * @throws SQLException 
	 */
	
	public Competition createCompetition(String nom,LocalDate dateCloture, boolean enEquipe) throws SQLException
	{
		Competition competition = new Competition(this, nom, dateCloture, enEquipe);
		if (SAVE_OBJECT)
			connect.add(competition);
		competitions.add(competition);
		return competition;
	}

	/**
	 * Cr��e une Candidat de type Personne. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Personne}.

	 * @param nom
	 * @param prenom
	 * @param mail
	 * @return
	 * @throws SQLException 
	 */
	
	public Personne createPersonne(String nom, String prenom, String mail) throws SQLException
	{
		Personne personne = new Personne(this, nom, prenom, mail);
		if (SAVE_OBJECT)
			connect.add(personne);
		candidats.add(personne);
		return personne;
	}
	
	/**
	 * Cr��e une Candidat de type �quipe. Ceci est le seul moyen, il n'y a pas
	 * de constructeur public dans {@link Equipe}.
	 * @param nom
	 * @param prenom
	 * @param mail
	 * @return
	 * @throws SQLException 
	 */
	
	public Equipe createEquipe(String nom) throws SQLException
	{
		Equipe equipe = new Equipe(this, nom);
		if (SAVE_OBJECT)
			connect.add(equipe);
		candidats.add(equipe);
		return equipe;
	}
	
	public void remove(Competition competition)
	{
		competitions.remove(competition);
		connect.delComp(competition.getIdcompetition());
	}
	
	public void remove(Candidat candidat)
	{
		candidats.remove(candidat);
		connect.delCandidat(candidat.getIdCandidat());
	}
	
	/**
	 * Retourne l'unique instance de cette classe.
	 * Cr�e cet objet s'il n'existe d�j�.
	 * @return l'unique objet de type {@link Inscriptions}.
	 */
	
	public static Inscriptions getInscriptions()
	{
		if (inscriptions == null)
		{
			if (SERIALIZE)
				inscriptions = readObject();
			if (inscriptions == null)
				inscriptions = new Inscriptions();
			if (!SERIALIZE)
			inscriptions.connect = new Connect();
		}
		return inscriptions;
	}

	/**
	 * Retourne un object inscriptions vide. Ne modifie pas les comp�titions
	 * et candidats d�j� existants.
	 */
	
	public Inscriptions reinitialiser()
	{
		inscriptions = new Inscriptions();
		return getInscriptions();
	}

	/**
	 * Efface toutes les modifications sur Inscriptions depuis la derni�re sauvegarde.
	 * Ne modifie pas les comp�titions et candidats d�j� existants.
	 */
	
	public Inscriptions recharger()
	{
		inscriptions = null;
		return getInscriptions();
	}
	
	private static Inscriptions readObject()
	{
		ObjectInputStream ois = null;
		try
		{
			FileInputStream fis = new FileInputStream(FILE_NAME);
			ois = new ObjectInputStream(fis);
			return (Inscriptions)(ois.readObject());
		}
		catch (IOException | ClassNotFoundException e)
		{
			return null;
		}
		finally
		{
				try
				{
					if (ois != null)
						ois.close();
				} 
				catch (IOException e){}
		}	
	}
	
	/**
	 * Sauvegarde le gestionnaire pour qu'il soit ouvert automatiquement 
	 * lors d'une ex�cution ult�rieure du programme.
	 * @throws IOException 
	 */
	
	public void sauvegarder() throws IOException
	{
		ObjectOutputStream oos = null;
		try
		{
			FileOutputStream fis = new FileOutputStream(FILE_NAME);
			oos = new ObjectOutputStream(fis);
			oos.writeObject(this);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (oos != null)
					oos.close();
			} 
			catch (IOException e){}
		}
	}
	
	@Override
	public String toString()
	{
		try {
			return "Candidats : " + getCandidats().toString()
				+ "\nCompetitions  " + getCompetitions().toString();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws SQLException
	{
		LocalDate date = LocalDate.of(2017,Month.APRIL,10);
		Inscriptions inscriptions = Inscriptions.getInscriptions();
		Competition flechettes = inscriptions.createCompetition("Mondial de fl�chettes", date, false);
		Personne tony = inscriptions.createPersonne("Tony", "Dent de plomb", "azerty"), 
				boris = inscriptions.createPersonne("Boris", "le Hachoir", "ytreza");
		flechettes.add(tony);
		Equipe lesManouches = inscriptions.createEquipe("Les Manouches");
		lesManouches.add(boris);
		lesManouches.add(tony);
		System.out.println(inscriptions);
		lesManouches.delete();
		System.out.println(inscriptions);
		try
		{
			inscriptions.sauvegarder();
		} 
		catch (IOException e)
		{
			System.out.println("Sauvegarde impossible." + e);
		}
		
	}
	
	
	public void openConnection()
	{
		connect = new Connect();
	}
	
	public void closeConnection()
	{
		connect.close();
	}
	
}
