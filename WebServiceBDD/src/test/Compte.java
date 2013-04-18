package test;

public class Compte {

	private String numCompte;
	private String type;
	private float valeur;
	
	public Compte(String n, String t, float v) {
		numCompte = n;
		type = t;
		valeur = v;
	}
	

	public String getNumCompte() {
		return numCompte;
	}

	public void setNumCompte(String numCompte) {
		this.numCompte = numCompte;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getValeur() {
		return valeur;
	}

	public void setValeur(float valeur) {
		this.valeur = valeur;
	}
}
