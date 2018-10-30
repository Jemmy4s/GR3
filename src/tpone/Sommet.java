package tpone;

import java.util.ArrayList;

public class Sommet {
	int degre;
	int id;
	ArrayList<Integer> adjacence;

	public Sommet(int id){
		this.adjacence = new ArrayList<Integer>();
		this.id = id;
		this.degre = 0;
	}
	public Sommet(GrapheInterface g, int id){
		this(id);
	}
	public boolean addAjacence(Graphe g, Sommet s){
		if(this.adjacence.contains(s.id)){
			g.doublons ++;
			return false;
		}
		this.degre ++;
		this.adjacence.add(s.id);
		s.adjacence.add(this.id);
		s.degre ++;
		return true;
	}
	
}
