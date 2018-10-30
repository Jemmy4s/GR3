package prof;
/* Corrige du TP 2 de GRI 2018, M2 Info Paris Diderot. Copyright Fabien de Montgolfier  */

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays; // sort()
import java.util.LinkedList; // utilise par kcoeur

class Sommet {
    int num ; // nom numéro pour débug seulement
    int degre; // son degré. Veut dire degré sortant si orienté.
    int degreE; // son degré entrant, si orienté
    int coul; // 0=existe pas 1=non visite par ParcoursLargeur 2=dans la file du PL 3=visite par PL
    int []adj; // tableau d'adjacence. une case = un numero de voisin. sa longueur est degré
    int []adjE; // tableau d'adjacence entrante, si orienté. sa longueur est degreE
    int dist; // sa distance à D, utilisé dans le parcours en largeur
    int pere; // son pere dans le parcours en largeur (numero et non Sommet)
    Sommet(int i) { this.num = i; }
} // pas de constructeur on affectera tous les champs plus tard

class Graphe { 
    boolean undirected; // vrai si non-oriente, faux si oriente
    int n;      // nombre de sommets "reels"
    int nmax;   // numero max d'un sommet
    int m;      // nombre d'arcs ou aretes
    int dmax;   // degre max d'un sommet
    int demax;  // degre entrant max d'un sommet (si non orienté)
    int dmaxmax;// si orienté vaut max des deux précédents. Sinon vaut dmax.
    Sommet[] V; // tableau des sommets. De taille nmax+1 normalement
    LinkedList [] lDegree; // sommets par degré sortant : si x a degré entrant d alors il existe j tel que apparait dans lDegree[i]. C'est une liste de Sommets en réalité
}  // pas de constructeur on affectera tous les champs plus tard


public class Main {
    public static void lecture(Graphe G, String filename) {
	// lecture du graphe, recopiée du TP1 
	

	// passe 1 : compte les lignes
	int compteur = 0;
	try {
	    BufferedReader read = new BufferedReader(new FileReader(filename));
	    while (read.readLine() != null)
		compteur++;
	    read.close(); 
	    System.out.println(filename+ " fait "+compteur+" lignes.");
	    System.out.println("Inits Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");
	}  catch (IOException e) {
	    System.out.println("Erreur entree/sortie sur "+filename);
	    System.exit(1);
	}

	// Passe 2 : lit le fichier et construit un tableau
	int l = 0;   // nombre de lignes d'aretes déjà lues
	int[][] lus = new int[compteur][2];
	try {
	    BufferedReader read = new BufferedReader(new FileReader(filename));
	    System.out.println("Pass 1 Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");
	    
	    while(true) {
		String line = read.readLine();
		if(line==null)
		    break;
		if(line.charAt(0) == '#') {
		    System.out.println(line);
		    continue;
		}
		int a = 0;
		for (int pos = 0; pos < line.length(); pos++)
		    {
			char c = line.charAt(pos);
			if(c==' ' || c == '\t')
			    {
				if(a!=0)
				    lus[l][0]=a;
				a=0;
				continue;
			    }
			if(c < '0' || c > '9')
			    {
				System.out.println("Erreur format ligne "+l+"c = "+c+" valeur "+(int)c);
				System.exit(1);
			    }
			a = 10*a + c - '0';
		    }
		lus[l][1]=a;
		if(G.nmax<lus[l][0]) // au passage calcul du numéro de sommet max
		    G.nmax = lus[l][0];
		if(G.nmax<lus[l][1]) // au passage calcul du numéro de sommet max
		    G.nmax = lus[l][1];
		l++;
	    }
	    read.close();
	}  catch (IOException e) {
	    System.out.println("Erreur entree/sortie sur "+filename);
	    System.exit(1);
	}
	System.out.println("pass 2 Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");

	// deuxième passe : alloue les sommets et calcule leur degre (sans tenir compte des doublons)
	int nbloop = 0;
	G.V = new Sommet[G.nmax+1];
	for(int i=0;i<=G.nmax;i++)
	    G.V[i]=new Sommet(i);

	for(int i = 0; i< l; i++)
	    {
		int x, y; // juste pour la lisibilité
		x = lus[i][0];
		y = lus[i][1];
		if(x==y && G.undirected) { // nous ignorons les boucles
		    nbloop++;
		    continue;
		}
		    
		(G.V[x].degre)++; // si arc x->y augmente le degre de x 
		if(G.undirected)
		    (G.V[y].degre)++; // ...et celui de y si arete x--y
		
		if(G.V[x].coul==0) { // le sommet x existe, sa couleur passe de 0 a 1
		    G.V[x].coul=1;
		    G.n++; // on incremente alors n
		}
		if(G.V[y].coul==0) { // idem pour l'autre cote de l'arc/arete
		    G.V[y].coul=1;
		    G.n++;
		}
	    }
	if(nbloop > 0)
	    System.out.println(nbloop + " boucles ont été ignorées");
	System.out.println("pass 3 Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");
	// troisieme passe : ajoute les arcs. 
	// d'abord allouons les tableaux d'adjacance
	for(int i=0;i<=G.nmax;i++) {
	    if(G.V[i].degre>0)  
		G.V[i].adj = new int[G.V[i].degre];
	    G.V[i].degre=0; // on remet le degre a zero car degre pointe la première place libre où insérer un élément pour la troisième passe
	}

	for(int i = 0; i< l; i++)
	    {
		int x, y; // juste pour la lisibilité
		x = lus[i][0];
		y = lus[i][1];
		if(x==y)
		    continue;
		G.V[x].adj[G.V[x].degre++] = y;
		// si non oriente on fait la meme chose dans l'autre sens
		if(G.undirected) 
		    G.V[y].adj[G.V[y].degre++] = x;
	    }
	
	System.out.println("pass 4 Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");
	// quatrieme passe : deboublonage, calul de m et des degres reels
	int nbdoubl=0;
	for(int i=0;i<=G.nmax;i++) {
	    if(G.V[i].degre>0) { 
		Arrays.sort(G.V[i].adj); 		    // on commence par trier la liste d'adjacance.
		for(int j= G.V[i].degre-2;j>=0;j--)  
		    if(G.V[i].adj[j]==G.V[i].adj[j+1]) {    // du coup les doublons deviennent consécutifs 
			// oh oh un doublon
			nbdoubl++;
			// on echange le doublon avec le dernier element que l'on supprime
			// boucle de droite a gauche pour eviter de deplacer un autre doublon
			G.V[i].adj[j+1]=G.V[i].adj[G.V[i].degre-1];
			G.V[i].degre--;
		    }
	    }
	    // on calcule le degré max maintenant, et le nombre d'arêtes
	    if(G.dmax < G.V[i].degre)
		G.dmaxmax = G.dmax =  G.V[i].degre; //màj dmax et dmaxmax, égaux à ce point 
	    G.m+=G.V[i].degre;
	}
	if(G.undirected == false) // dans ce cas on contruit les degrés et voisins entrants
	    {	// cinquieme passe, si G non orienté : calcule le degré entrant
		for(int i=0;i<=G.nmax;i++) 
		    if(G.V[i].coul != 0 ) 
			for(int j=0; j< G.V[i].degre; j++)
			    G.V[ G.V[i].adj[j] ].degreE++; // arc i->truc incrémente truc.degreE
		
		// sixieme passe, si G non orienté : alloue les adjE
		for(int i=0;i<=G.nmax;i++) 
		    if(G.V[i].degreE > 0)
			{
			    G.V[i].adjE = new int[G.V[i].degreE];
			    if(G.demax< G.V[i].degreE) // au passage calcule du degré entrant max de G
				G.demax = G.V[i].degreE;
			}    

		// septieme  passe, si G non orienté : remplit les adjE
		for(int i=0;i<=G.nmax;i++) 
		    if(G.V[i].coul != 0 )  
			for(int j=0; j< G.V[i].degre; j++)
			    { // abbreviation utiles : arc i->y
				int y =  G.V[i].adj[j];
				G.V[y].adjE[--(G.V[y].degreE)]=i; // on decremente le degré pour savoir où insérer
			    }

		// une huitieme passe recrée les degreE et les listes de degré entrant
		G.lDegree = new LinkedList[G.dmaxmax];
		for(int i=0;i<=G.nmax;i++) 
		    if(G.V[i].adjE != null)
			G.V[i].degreE = G.V[i].adjE.length; // le degreE de i est la longueur de sa liste
		
		if(G.demax > G.dmax) // on met a jour le max des deux degrés
		    G.dmaxmax = G.demax;
		else
		    G.dmaxmax = G.dmax;
		
	    }			
	// une neuvieme et dernier passe crée les listes de degré entrant
	G.lDegree = new LinkedList[G.dmaxmax+1];
	for(int i=0;i<=G.dmaxmax;i++)
	    G.lDegree[i] = new LinkedList<Sommet>();
	for(int i=0;i<=G.nmax;i++) 
	    if( G.V[i].coul !=0)
		G.lDegree[G.V[i].degre].add(G.V[i]); //  et on ajoute i a la liste des sommets de degré sortant i
	    
	if(G.undirected) // on a compté chaque arête deux fois et chaqyue doublon aussi
	    {
		G.m/=2;
		nbdoubl /= 2;
	    }
	if(nbdoubl >0)
	    System.out.println(nbdoubl+" doublons ont ete supprimes");
    }

    // calculs de degré (pour premiere commande)
    public static int[] distriDeg( Graphe G) {
	int[] di = new int[G.dmaxmax+1];
	for(int i=0; i<G.V.length; i++)
	    if(G.V[i].coul != 0)
		di[ G.V[i].degre ]++;
	return di;
    }
    
    // distri de degré entrant si G est non-orienté
    public static int[] distriDegEntrant( Graphe G) {
	int[] di = new int[G.dmaxmax+1];
	for(int i=0; i<G.V.length; i++)
	    if(G.V[i].coul != 0)
		di[ G.V[i].degreE ]++;
	return di;
    }

    // calcul de bascule (pour deuxieme commande)
    public static void bascule( Graphe G) {
	int[] degCr = new int[G.n]; // tableau des degrés des sommets
	int j=0;
	for(int i=0; i<G.V.length; i++)
	    if(G.V[i].coul != 0)
		degCr[j++] = G.V[i].degre;
	Arrays.sort(degCr);
	// degCr est maintenant le tableau des degrés trié en ordre croissant
	int sb = 0, b;
	for(b=0;b<degCr.length;b++)
	    {
		// on calcule Sb 
		sb+=degCr[degCr.length - (1 + b)]; // on parcourt le tableau a l'envers
		// on applique la condition du sujet
		if ( (double)(b+1)/(double)G.n > ( 1 - (double)sb / (double)(2 * G.m)))
	    break; // on a trouvé la bascule !
	    }
	System.out.println("valeur d’équirépartition : "+100.0*b/G.n+"%");
	System.out.println("bascule : "+b+" sur "+G.n+" sommets");
	System.out.println("degre de bascule : "+degCr[degCr.length - (1 + b)]);
	
    }

    // commande 3 : le k coeur
  
    public static void kCore(Graphe G) {
	/* calcul de k-core : enleve tous les sommets de degré <k
	   On a besoin des listes des sommets par degre lDegree
	   En pour le k-coeur on initialise une liste aSupprimer avec lDegree
	   chaque sommet supprimé diminue de 1 le degré (sortant) de ses voisins (entrants)
	   On aurait aussi pu utiliser un tas-Min avec une comparaison sur le degré... 
	   Mais la priorityQueue de Java ne permet pas de faire decroitre la valeur d'une clef !
	   Surtout, cela nous aurait donné une compexité O(m log n) alors que là 
	   on est en O(m) car chaque sommet est supprimé une fois en O(son degré)
	   Un sommet peut rentrer plusieurs fois dans les listes lDegree mais ce n'est pas grave */
	int k=0; // le k de k-coeur
	int supressed=0; // nombre de sommets effacés de G
	System.out.println(k+"-coeur restent "+(G.n-supressed)+" sommets");
	while(supressed != G.n)
	    {
		if(G.lDegree[k]==null || G.lDegree[k].isEmpty() )
		    {
			k++; // rien à faire pour ce k là
			System.out.println(k+"-coeur restent "+(G.n-supressed)+" sommets");
			continue;
		    }
		// boucle sur k
		LinkedList aSupprimer = G.lDegree[k];
		/* nb cette liste peut contenir des sommets déjà supprimés car pour optimiser 
		   car, quand le degré de x diminue, on ajoute x a la liste G.lDegreE[son nouveau degré]
		   mais sans le supprimer de la liste G.lDegreE[son ancien degré] */
		   
		while( ! aSupprimer.isEmpty() ) { // tant qu'on peut supprimer
		    Sommet x = (Sommet) aSupprimer.poll();
		    if( x.coul == 0) // x déjà supprimé : rien à faire
			continue;
		    x.coul = 0; // x n'existe plus
		    supressed++; // on a supprimé x
		    int vraideg;
		    if(G.undirected) // si G non oriente, il faut supprimer les voisins de x
			vraideg = x.degre;
		    else
			vraideg = x.degreE;
		    for(int i=0;i<vraideg;i++) { // parcours des voisins
			int y;
			if(G.undirected) // si G non oriente, il faut supprimer les voisins de x
			    y = x.adj[i];
			else
			    y = x.adjE[i];
			if(G.V[y].coul != 0) { // si y pas deja supprimé
			    int indx; // indice de x dans Adj[y]
			    for(indx=0;indx<G.V[y].degre;indx++) 
				if(G.V[y].adj[indx]==x.num) // trouvé !
				    break;
			    // on écrase x en le remplaçant par la fin de liste
			    G.V[y].adj[indx]=G.V[y].adj[G.V[y].degre-1];
			    // et on diminue le degré de y
			    G.V[y].degre--;
			    G.lDegree[G.V[y].degre].add(G.V[y]);
			    if(G.V[y].degre < k)
				aSupprimer.add(G.V[y]);
			}
		    }
		}
		
		k++;
		System.out.println(k+"-coeur restent "+(G.n-supressed)+" sommets");
	    }
    }
    
    public static int Habib(Graphe G, int r)
    {
	// implémente l'algorithme de Habib
	int a1, b1, m1, a2, b2, dista1b1, dista2b2;
	a1  = PL(G,r);
	b1 = PL(G,a1);
	dista1b1 = G.V[b1].dist;
	/* ruse pour trouver le milieu d'un chemin on fait une boucle
	   avec un indice qui remonte deux fois plus vite que l'autre */
	int somtmp = b1; // remonte jusqu'a racine
	boolean parite = false;
	m1 = b1;
	while(somtmp != -1)
	    {
		if(parite)
		    m1 = G.V[m1].pere;
		parite = !parite;
		somtmp = G.V[somtmp].pere;
	    }
	a2 = PL(G, m1);
	b2 = PL(G,a2);
	dista2b2 = G.V[b2].dist;
	System.out.println("Heuristique de Habib r="+r+" a1="+a1+" b1="+b1+" m1="+m1+" a2="+a2+" b2="+b2+" dist("+a1+","+b1+")="+dista1b1+" dist("+a2+","+b2+")="+dista2b2);
	return dista2b2;
    }
      
    public static int PL(Graphe G, int D) {
	// parcours en largeur du graphe G depuis D
	// dans cette version on retourne le dernier sommet accessible depuis D
	// on rajoute un nettoyage des couleurs des sommets
	for(int i=0;i<G.V.length;i++)
	    if(G.V[i].coul == 3) // les coul 0, inexistant, ne sont pas touchées
		G.V[i].coul = 1;
	// nb inutile de nettoyer dist et pere car PL ecrase les anciennes valeurs
	ArrayDeque<Integer> file = new ArrayDeque<Integer>(); // file du parcours. On aurait pu faire <Sommet> aussi.
	file.add(D); // file = (D)
	G.V[D].dist=0; // D a distance 0 de lui-meme
	G.V[D].pere=-1; // valeur spéciale signalant la racine
	int rep=1; // Nombre d'accessibles. 1 car on a visité D
	int x=-1; // numéro sommet en cours de visite
	while(!file.isEmpty()) { 
	    x = file.poll(); // extraire tete
	    G.V[x].coul=3; // noir 
	    int i;
	    for(i=0;i<G.V[x].degre;i++) { // parcours des voisins
		int y = G.V[x].adj[i];
		if(G.V[y].coul==1) { // si blanc (et existe. coul==0 si inexistant)
		    rep++; // on en a visite un de plus
		    G.V[y].coul=2; // gris
		    file.add(y);
		    G.V[y].dist = 1+G.V[x].dist;
		    G.V[y].pere = x;
		}
	    }
	}
	if(rep!=G.n)
	    System.out.println("Warning : non connexe, on a parcouru seulement "+rep+" sommets sur "+G.n);
	return x; // numéro du dernier sommet visité
    }
 


    public static int tri(Graphe G, int s) {
	// compte les triangles sur un sommet
	int ct=0;
	if (G.V[s].degre<2) return 0; // petit speedup
	// on cherche les triangles s--u--v--s
	int u,v;
	// passe 1 : marque les voisins de s
	for(int j=0; j<G.V[s].degre;j++)
	    {
		u = G.V[s].adj[j];
		G.V[u].coul = 5; // marque
	    }
	
	// passe 2 : compte les triangles
	for(int j=0; j<G.V[s].degre;j++) {
	    u = G.V[s].adj[j]; // u voisin de s
	    for(int k=0; k<G.V[u].degre;k++) {
	        v = G.V[u].adj[k]; // v kieme voisin de u
		if(G.V[v].coul==5) // on a un triangle car v voisin de s
		    ct++;
	    }
	}
	// passe 3 remet les marques a 0 
	for(int j=0; j<G.V[s].degre;j++)
	    {
	        u = G.V[s].adj[j];
		G.V[u].coul = 1; //demarque
	    }

	return ct/2; // attention chaque triangle compte deux fois
    }

    public static int tri(Graphe G) {
	// compte les triangles de G
	int ct=0;
	for (int i=0;i<=G.nmax; i++)
	    if(G.V[i].coul!=0) // le sommet existe
		ct+=tri(G,i);
	return ct/3; // attention chaque triangle compte trois fois
    }

    public static int nv(Graphe G, int s) {
	// compte les V (aussi appelés P3) de G
	if(G.V[s].degre==0) return 0;
	return (((G.V[s].degre)*(G.V[s].degre-1))/2);
    }

    public static long nv(Graphe G) { // il faut un long car n^3 peut etre beaucoup
	// compte les V de G
	long ct=0;
	for (int i=0;i<=G.nmax; i++)
	    if(G.V[i].coul!=0) // le sommet existe
		ct+=nv(G,i);
	return ct;
    }

    public static double cluL(Graphe G, int s) {
	// clustering local d'un sommet
	if(G.V[s].degre==0) return 0.0;
	if(G.V[s].degre==1) return 0.0;
	else return (double)tri(G,s)/(double)nv(G,s);
    }
    
    public static double cluL(Graphe G) {
	// clustering local moyen
	double cl=0.0;
	for (int i=0;i<=G.nmax; i++)
	    if(G.V[i].coul!=0) // le sommet existe
		cl+=cluL(G,i);
	return cl/(double)G.n;
    }

    public static double cluG(Graphe G) {
	// clustering global
	long nvg = nv(G);
	if(nvg==0L) return 0.0; // cas tres particulier de graphe tres peu dense
	else return (3.0*tri(G))/(double)nvg;
    }

    
    public static void main(String[] args) {
	
	if (args.length < 2 || args.length > 3) {
	    System.out.println("Usage : java TP2 nomFichier.txt commende <option>");
	    return;
	}
	
	Graphe G = new Graphe(); // le graphe sur lequel on travaille 
	G.undirected = true;
	if(args.length == 3 && args[2].equals("oriente") && (args[1].equals("distri") ||
					 args[1].equals("coeur")))
	    G.undirected = false;
	else if(args.length == 3 &&  ! args[1].equals("diametre"))
	    {
		System.out.println("option "+args[2]+" invalide ou incompatible avec commande choisie");
		System.exit(0);
	    }
		 
	// 1- lecture
	lecture(G, args[0]);
	System.out.println("nb arcs "+G.m+" -- nb sommets "+G.n+" -- num sommet max "+G.nmax+" -- degre max"+G.dmax);       
 	System.out.println("Mémoire allouée : " +  (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets ");

	// 2 calculs
	switch(args[1]) {
	case "distri":
	    int [] dgr = distriDeg(G);
	    int[] dide = null;
	    if( ! G.undirected) 
		dide = distriDegEntrant(G);
	    for(int i=0; i<=G.dmaxmax; i++) {
		System.out.print(i + " " + dgr[i] + " ");
		if(G.undirected)
		    System.out.println();
		else
		    System.out.println(dide[i]);
	    }
	    break;

	case "bascule":
	    bascule(G);
	    break;

	case "coeur":
	    kCore(G);
	    break;

	case "diametre":
	    int r=1;
	    if(args.length==3)
		try
		    {
			r = Integer.parseInt(args[2]);
		    }
		catch (NumberFormatException nfe)
		    {
			System.out.println(args[2]+" n'est pas numero de sommet");
			System.exit(0);
		    }
	    if(r<0 || r >= G.V.length || G.V[r].coul == 0)
		    {
			System.out.println("Erreur le sommet "+r+" n'existe pas");
			System.exit(0);
		    }
	    Habib(G,r);
	    break;
	case "cluL":
	    System.out.println("clustering local moyen "+cluL(G));
	    break;
	case "cluG":
	    System.out.println("nombre de triangles "+tri(G)+
			       " nombre de V "+nv(G)+
			       "\nclustering global "+cluG(G));
	    break;
	default:
	    System.out.println("commande "+args[1]+" invalide doit etre distri bascule coeur diamtre cluL cluG");
	}
	System.out.println("Mémoire allouée : " + (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) + " octets");
    }
}
