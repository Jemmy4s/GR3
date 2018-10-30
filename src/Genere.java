
public class Genere {

	public static boolean[][] gnp (int n, double p) {
		boolean [][] tab = new boolean [n][n];
		for(int i = 0; i < tab.length; i++) {
			for(int j = i ; j < tab.length; j++) {
				if(i == j)
					continue;
				boolean b = Math.random()  <= p;
				tab[i][j] = b;
			}
		}
		return tab;
	}

	public static boolean [][] gnm (int n, int m){
		boolean [][] tab = new boolean [n][n];
		
		for(int i = 0; i <  m; i++) {
			int blocage = 0;
			boolean ajouter = false;
			while(!ajouter || blocage > 1000) {
				blocage ++;
				int x =  ( (int)(Math.random()*n) % n);
				int y = ((int)Math.random()*n) % n;
				if(tab[x][y] || tab[y][x])
					continue;
				tab[x][y] = true;
				tab[y][x] = true;
				ajouter = true;
				
			}
		}
		return tab;
	}
}
