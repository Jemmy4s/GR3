package tpone;

import java.util.*;
import java.lang.*;

public class Graphe implements GrapheInterface {
	public int doublons;
	ArrayList<Sommet> ensemble;
	HashMap<Integer, Integer> hMap;
	int nbArrete;
	public int nbsommet;
	public int boucle;
	public int nbBoucle;
	
	/* (non-Javadoc)
	 * @see GrapheInterface#addArrete(int, int)
	 */
	@Override
	public void addArrete(int id1, int id2){
		Sommet s2;
		Sommet s;
		if(id1 == id2){
			nbBoucle ++;
			return;
		}
		if(!hMap.containsKey(id2)){
			s2 = new Sommet (this, id2);
			this.ensemble.add(s2);
			hMap.put(id2, nbsommet);
			nbsommet++;
		}else {
			s2 = ensemble.get(hMap.get(id2));
		}
		if(!hMap.containsKey(id1)){
			s = new Sommet(this, id1);
			this.ensemble.add(s);
			hMap.put(id1,nbsommet);
			nbsommet ++;
		}else{
			s = ensemble.get(hMap.get(id1));
		}
		if(s.addAjacence(this, s2))
			nbArrete++;
		//else
			//doublons ++;
	}
	/* (non-Javadoc)
	 * @see GrapheInterface#getSommet(int)
	 */
	@Override
	public Sommet getSommet(int id){
		return ensemble.get(hMap.get(id));
	}
	/* (non-Javadoc)
	 * @see GrapheInterface#existNode(int)
	 */
	@Override
	public boolean existNode(int id) {
		return hMap.containsKey(id);
	}
	
    /* (non-Javadoc)
	 * @see GrapheInterface#parcourLargeur(int, int)
	 */
    @Override
	public int parcourLargeur(int id1 , int id2){
        this.getSommet(id1);
        ArrayDeque<Sommet> l = new ArrayDeque<Sommet>();
        HashMap<Integer,Integer> hm = new HashMap<Integer, Integer>();
        hm.put(id1, 0);
        if (id1 == id2)
            return 0;
        Sommet tmp = this.getSommet(id1);
        do{
            for(Integer i : tmp.adjacence){
                if(!hm.containsKey(i)){
                    l.add(getSommet(i));
                    hm.put(i, hm.get(tmp.id) +1);
                }
            }
            tmp = l.poll();
        }while(tmp.id != id2 && !l.isEmpty());
        Integer a = hm.get(id2);
        if(a != null){
        	return (int) hm.get(id2);
        }
        return -1;
    }
    
	/* (non-Javadoc)
	 * @see GrapheInterface#degreMax()
	 */
	@Override
	public int degreMax() {
		int max = 0;
		for(Sommet s : ensemble) {
			max = (s.degre > max )?s.degre:max; 
		}
		return max;
	}
	
   /* (non-Javadoc)
 * @see GrapheInterface#numSommetMax()
 */
@Override
public int numSommetMax(){
        int n = 0;
        for(Sommet s : ensemble){
            n = (s.id >n )?s.id:n;
        }
        return n;
    }
	
	public Graphe(){
		this.nbsommet = 0;
		this.hMap = new HashMap<Integer, Integer>();
		this.doublons = 0;
		this.ensemble = new ArrayList<Sommet>();
	}
	@SuppressWarnings("unchecked")
	public Graphe(HashMap<Integer,Integer> hMap , ArrayList<Sommet> ensemble){
		this.nbsommet = ensemble.size();
		this.hMap = (HashMap<Integer, Integer>) hMap.clone();
		this.ensemble = (ArrayList<Sommet>) ensemble.clone();
		
	}
}
