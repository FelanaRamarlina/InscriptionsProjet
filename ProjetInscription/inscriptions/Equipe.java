package inscriptions;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import src.Connect;

/**
 * Représente une Equipe. C'est-à-dire un ensemble de personnes pouvant 
 * s'inscrire à une compétition.
 * 
 */

public class Equipe extends Candidat
{
	private static final long serialVersionUID = 4147819927233466035L;
	private SortedSet<Personne> membres = new TreeSet<>();
	public static boolean SERIALIZE = false;
	private Connect c;

	Equipe(Inscriptions inscriptions, String nom)
	{
		super(inscriptions, nom);
	}

	/**
	 * Retourne l'ensemble des personnes formant l'équipe.
	 */
	
	public SortedSet<Personne> getMembres()
	{
		return Collections.unmodifiableSortedSet(membres);
	}
	
	/**
	 * Ajoute une personne dans l'équipe.
	 * @param membre
	 * @return
	 */

	public boolean add(Personne membre)
	{
		if (!SERIALIZE){
			membre.add(this);
			return membres.add(membre);
		}else {
			return 
		}
	}

	/**
	 * Supprime une personne de l'équipe. 
	 * @param membre
	 * @return
	 */
	
	public boolean remove(Personne membre)
	{
		membre.remove(this);
		return membres.remove(membre);
	}

	@Override
	public void delete()
	{
		if (!SERIALIZE)
			super.delete();
		c.delCandidat(this.getIdCandidat());
	}
	
	@Override
	public String toString()
	{
		return "Equipe " + super.toString();
	}
}
