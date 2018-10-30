package tp3;

import java.util.ArrayList;

public class Sommet {
	public int id;
	int degre;
	int coul;
	ArrayList<Sommet> pere;
	ArrayList<Sommet> adj;
	
	public boolean addAdjacence(Sommet s) {
		if(adj.contains(s))
			return false;
		adj.add(s);
		return true;
	}
	public boolean supprAdjacence(Sommet s) {
		return adj.remove(s);
	}
	
}
