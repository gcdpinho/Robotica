package aStar;
import kalman.Kalman;
import lejos.nxt.*;

public class Main {
   
    public static void main(String[] args) {    
        
        
        
       // Kalman kalman = new Kalman(0.5512, 0.4488, 26);
		Kalman kalman = new Kalman(0.9017, 0.1972, 35);
 -    	
   		// tamanho do grid, posição inicial (x,y), tamanho do nodo, kalman, graus de dobra pra direita e esquerda; e graus de giro do sensor
        AStar aStar = new AStar(10, 5, 5, 35, kalman, 510, 510, 90);
        
        aStar.run();


         Button.waitForAnyPress();
    

    }
}