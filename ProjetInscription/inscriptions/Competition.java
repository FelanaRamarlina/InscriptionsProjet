package inscriptions;

import src.Connect;

import java.io.Serializable;
import java.util.Collections;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

/**
 * Représente une compétition, c'est-à-dire un ensemble de candidats 
 * inscrits à un événement, les inscriptions sont closes à la date dateCloture.
 *
 */

public class Competition implements Comparable<Competition>, Serializable
{
	private static final long serialVersionUID = -2882150118573759729L;
	private Inscriptions inscriptions;
	private String nom;
	private Set<Candidat> candidats;
	private Set<Candidat> candidatsNotSign;
	private LocalDate dateCloture;
	private boolean enEquipe = false;
	private int idCompetition;
	private Connect connect;

	Competition(Inscriptions inscriptions, String nom, LocalDate dateCloture, boolean enEquipe)
	{
		this.enEquipe = enEquipe;
		this.inscriptions = inscriptions;
		this.nom = nom;
		this.dateCloture = dateCloture;
	}
	
	/**
	 * Retourne l'id de la compétition.
	 * @return
	 */
	public int getIdcompetition(){
		return idCompetition;
	}
	/**
	 * Modifier l'id de la compétition.
	 * @return
	 */
	public void setIdcompetition(int id){
		this.idCompetition = id;
	}
	/**
	 * Retourne le nom de la compétition.
	 * @return
	 */
	
	public String getNom()
	{
		return nom;
	}
	
	/**
	 * Modifie le nom de la compétition.
	 */
	
	public void setCompetition(String nom,LocalDate dateClotureSet)
	{
		LocalDate dateBefore = dateCloture;
		this.dateCloture = dateClotureSet;
		if(dateCloture.isAfter(dateBefore)){
			System.out.println("Erreur ! Il est impossible d'avancer la date de cl�ture.");
			dateCloture = dateBefore;
		}else {
			connect = new Connect();
			this.nom = nom ;
			connect.SetCompetition(this.getIdcompetition(), nom, dateClotureSet);
			connect.close();
		}
	}
	
	/**
	 * Retourne vrai si les inscriptions sont encore ouvertes, 
	 * faux si les inscriptions sont closes.
	 * @return
	 */
	
	public boolean inscriptionsOuvertes()
	{
		// TODO retourner vrai si et seulement si la date système est antérieure à la date de clôture.
		LocalDate dateAujd = LocalDate.now();
		return dateAujd.isBefore(dateCloture);
	}
	
	/**
	 * Retourne la date de cloture des inscriptions.
	 * @return
	 */
	
	public LocalDate getDateCloture()
	{
		return dateCloture;
	}
	
	/**
	 * Est vrai si et seulement si les inscriptions sont réservées aux équipes.
	 * @return
	 */
	
	public boolean estEnEquipe()
	{
		return enEquipe;
	}
	
	/**
	 * Modifie la date de cloture des inscriptions. Il est possible de la reculer 
	 * mais pas de l'avancer.
	 * @param dateCloture
	 */
	
	
	
	/**
	 * Retourne l'ensemble des candidats inscrits.
	 * @return
	 * @throws SQLException 
	 */
	
	public Set<Candidat> getCandidats() throws SQLException
	{
		if (candidats == null){
			connect = new Connect();
			candidats = connect.getCandidatFromComp(
					this.getIdcompetition());
			connect.close();
		}
		return Collections.unmodifiableSet(candidats);
	}
	
	/**
	 * Retourne l'ensemble des candidats non inscrits.
	 * @return
	 * @throws SQLException 
	 */
	
	public Set<Candidat> getCandidatsNotSign() throws SQLException
	{
		if (candidatsNotSign == null){
			connect = new Connect();
			candidatsNotSign = connect.getCandNotSign(this.getIdcompetition(),
					this.estEnEquipe());
			connect.close();
		}
		return Collections.unmodifiableSet(candidatsNotSign);
	}
	/**
	 * Inscrit un candidat de type Personne à la compétition. Provoque une
	 * exception si la compétition est réservée aux équipes ou que les 
	 * inscriptions sont closes.
	 * @param personne
	 * @return
	 * @throws SQLException 
	 */
	
	public boolean add(Personne personne) throws SQLException
	{
		// TODO vérifier que la date de clôture n'est pas passée
		if (enEquipe || this.inscriptionsOuvertes()==false)
			throw new RuntimeException();
		personne.add(this);
		connect = new Connect();
		connect.addCandCompet(personne, this.getIdcompetition());
		candidatsNotSign.remove(personne);
		return candidats.add(personne);
	}

	/**
	 * Inscrit un candidat de type Equipe à la compétition. Provoque une
	 * exception si la compétition est réservée aux personnes ou que 
	 * les inscriptions sont closes.
	 * @param personne
	 * @return
	 * @throws SQLException 
	 */

	public boolean add(Equipe equipe) throws SQLException
	{
		// TODO vérifier que la date de clôture n'est pas passée
		if (!enEquipe || this.inscriptionsOuvertes()==false)
			throw new RuntimeException();
		equipe.add(this);
		connect = new Connect();
		connect.addCandCompet(equipe, this.getIdcompetition());
		candidatsNotSign.remove(equipe);
		return candidats.add(equipe);
	}

	/**
	 * Désinscrit un candidat.
	 * @param candidat
	 * @return
	 * @throws SQLException 
	 */
	
	public boolean remove(Candidat candidat) throws SQLException
	{
		candidat.remove(this);
		connect = new Connect();
		connect.delCandCompet(candidat.getIdCandidat(),this.getIdcompetition());
		connect.close();
		candidatsNotSign.add(candidat);
		return candidats.remove(candidat);
	}
	
	/**
	 * Supprime la compétition de l'application.
	 * @throws SQLException 
	 */
	
	public void delete() throws SQLException
	{
		for (Candidat candidat : candidats)
			remove(candidat);
		inscriptions.remove(this);
	}
	
	@Override
	public int compareTo(Competition o)
	{
		return getNom().compareTo(o.getNom());
	}
	
	@Override
	public String toString()
	{
		return getNom();
	}
}
