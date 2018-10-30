package tpone;

public interface GrapheInterface {


	void addArrete(int id1, int id2);

	Sommet getSommet(int id);

	boolean existNode(int id);

	int parcourLargeur(int id1, int id2);

	int degreMax();

	int numSommetMax();

}
