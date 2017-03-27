package aStar;

import kalman.Kalman;
import java.util.ArrayList;
import java.util.Random;
import lejos.nxt.*;


public class AStar {

	private int mapSize, direita, esquerda;
	private double dist, length;
	private Kalman kalman;
	private static final int OBSTACLE = 1;
	private static final int FREEPASS = 0;
	private static final int POSROBOT = 10;
	private String direc;
	//private static final int GOAL = 2;
	private Node[][] map;
	private Node initial;
	private ArrayList<Node> openNodes;
	
	
	public AStar(int mapSize, int xInital, int yInitial, /*int xGoal, int yGoal,*/ double length, Kalman kalman, int direita, int esquerda){
		this.mapSize = mapSize; // tamaho do mapa
		this.kalman = kalman; // kalman (andar x centimetros)
		this.map = new Node[this.mapSize][this.mapSize]; // tabuleiro
		this.initial = new Node(xInital, yInitial); // 
		//this.goal = new Node(xGoal, yGoal);
		this.length = length;
		this.direc = "frente";
		this.direita = direita;
		this.esquerda = esquerda;

		for (int i=0; i<this.mapSize; i++)
			for (int j=0; j<this.mapSize; j++)
				this.map[i][j] = new Node(i, j);
		
		this.map[xInital][yInitial].setValue(POSROBOT); // posição inicial
		//this.map[xGoal][yGoal].setIsGoal(true);
		
		this.openNodes = new ArrayList<>();
	}
	
	public void run(){
		int currentX = this.initial.getX(); 
		int currentY = this.initial.getY();
			
		// Medi distancia em linha reta do alvo
		this.dist = this.getDistance(); // distancia do alvo em linha reta
		this.map[currentX][(int) (this.dist/this.length)].setIsGoal(true); //mapeando a casa do alvo no tabuleiro
		
		Button.waitForAnyPress();
		
		// Gira para todos os lados e ve os obstaculos
		// Adiciona os nodos FREEPASS na lista de nodos abertos
		// Calcula a distancia euclidiana em relaçã ao alvo
		
		//colocar try catch nos else (current + 1 pode ser fora do tabuleiro) V
		// multiplicar o custo pelo lenght V

		Node current = this.map[currentX][currentY];
		while (!current.getIsGoal()){
			//busca por nodos abertos, verificando se o ultimo movimento (para não retornar)
			// frente
			if (getDistance() >= this.length && currentY+1 <= this.mapSize && !this.direc.equals("tras")){ 
				this.map[currentX][currentY+1].setDist(goal);
				this.openNodes.add(this.map[currentX][currentY+1]);
				//this.direc = "frente";
			}
			else
				try {
					this.map[currentX][currentY+1].setValue(OBSTACLE);
				}
				catch (Exception e){
					System.out.println("Fora do map.");
				}
			//esquerda
			this.turnOn(this.esquerda, 2);
			if (getDistance() >= this.length && currentX-1 >= 0) && !this.direc.equals("direita"){
				this.map[currentX-1][currentY].setDist(goal);
				this.openNodes.add(this.map[currentX-1][currentY]);
				//this.direc = "esquerda";
			}
			else
				try {
					this.map[currentX-1][currentY].setValue(OBSTACLE);
				}
				catch (Exception e){
					System.out.println("Fora do map.");
				}
				
			
			// tras
			this.turnOn(esquerda, 2);
			if (getDistance() >= this.length && currentY-1 >= 0 && !this.direc.equals("frente")){
				this.map[currentX][currentY-1].setDist(goal);
				this.openNodes.add(this.map[currentX][currentY-1]);
				//this.direc = "tras";
			}
			else
				try {
					this.map[currentX][currentY-1].setValue(OBSTACLE);
				}
				catch (Exception e){
					System.out.println("Fora do map.");
				}
				
			
			// direita
			this.turnOn(esquerda, 2);
			if (getDistance() >= this.length && currentX+1 <= this.mapSize !this.direc.equals("esquerda")){
				this.map[currentX+1][currentY].setDist(goal);
				this.openNodes.add(this.map[currentX+1][currentY]);
				//this.direc = "direita";
			}
			else
				try {
					this.map[currentX+1][currentY].setValue(OBSTACLE);
				}
				catch (Exception e){
					System.out.println("Fora do map.");
				}
				
			
			// vira pra frente novamente
			this.turnOn(esquerda, 2);
			
			// calcula o custo de todos os nodos da lista para a posição do atual do robo
			
			for (int i=0; i<this.openNodes.size(); i++)
				this.openNodes.get(i).setCost(currentX, currentY);	
	
			// vai para o nodo com menor distância
			
			int temp = getBestNode();
			this.calculatePath(current, openNodes.get(temp));
			current = openNodes.get(temp);
			
			this.openNodes.remove(temp);
		
		// repete até chegar no alvo
		}
		
	}
	
	private void calculatePath(Node current, Node goal){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		int prox;

		if (currentX - goalX == 0 && (currentY - goalY <= 1 && currentY - goalY >= -1))
			this.walk(current, goal);
		else
			if (currentY - goalY == 0 && (currentX - goalX <= 1 && currentX - goalX >= -1))
				this.walk(current, goal);
			else {
				prox = current.getPath().get(current.getPath().size()-1);
				this.wal(current, prox)
				this.calculatePath(prox, goal);
			}
	}
	
	
	private void walk(Node current, Node goal){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		
		// frente
		if (currentY - goalY > 0){
			this.turnAndGo(this.direc, "frente");
			//this.kalman.filtroKalman();
		}
		// tras
		else
			if (currentY - goalY < 0){
				this.turnAndGo(this.direc, "tras");
				//this.kalman.filtroKalman();
			}
			// direita
			else
				if (currentX - goalX > 0){
					this.turnAndGo(this.direc, "direita");
					//this.kalman.filtroKalman();
				}
				// esquerda
				else 
					if (currentX - goalX < 0){
						this.turnAndGo(this.direc, "esquerda");
						//this.kalman.filtroKalman();
					}
		this.map[goalX][goalY].setPath(this.map[currentX][currentY]);
					
	}
	
	private double getDistance(){
		UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S4);
		
		return ultrasom.getDistance();
	}
	
	private void turnAndGo(String current, String goal){

    	Motor.B.setSpeed(200);
    	Motor.C.setSpeed(200);

    	switch(current){
    		case "frente":
    			switch(goal){
    				case "frente":
    					break;
    				case "tras":
    					this.turnOn(this.esquerda*2, 2);
    					break;
    				case "esquerda":
    					this.turnOn(this.esquerda, 2);
    					break;
    				case "direita":
    					this.turnOn(this.direita, 1);
    					break;
    			}
    			break;
    		case "tras":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.direita*2, 1);
    					break;
    				case "tras":	
    					break;
    				case "esquerda":
    					this.turnOn(this.direita, 1);
    					break;
    				case "direita":
    					this.turnOn(this.esquerda, 2);
    					break;
    			}
    			break;
    		case "esquerda":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.direita, 1);
    					break;
    				case "tras":
    					this.turnOn(this.esquerda, 1);	
    					break;
    				case "esquerda":
    					break;
    				case "direita":
    					this.turnOn(this.esquerda*2, 2);
    					break;
    			}
    			break;
    		case "direita":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.esquerda, 2);
    					break;
    				case "tras":
    					this.turnOn(this.direita, 1);	
    					break;
    				case "esquerda":
    					this.turnOn(this.direita*2, 1);
    					break;
    				case "direita":
    					break;
    			}
    			break;
    	}
    	this.direc = goal
    	this.kalman.filtroKalman();
    	
	}
	
	private void turnOn (int degress, int direction){

		if (direction == 1) { // Direita
    		Motor.B.rotate( degrees, true);
    		Motor.C.rotate( -degrees, true);
    	}
    	else {
    		Motor.B.rotate( -degrees, true);
    		Motor.C.rotate( degrees, true);
    	}
	}

	private int getBestNode(){
		ArrayList<Integer> resultIndex = new ArrayList<>();
		double result = this.openNodes.get(0).getCost() * this.length + this.openNodes.get(0).getDist();
		double aux;
		
		for (int i=1; i<this.openNodes.size(); i++){
			aux = this.openNodes.get(i).getCost() * this.length + this.openNodes.get(i).getDist();
			if (result > aux)
				result = aux;				
		}
		
		for (int i=0; i<this.openNodes.size(); i++){
			if (result == this.openNodes.get(i).getCost() * this.length + this.openNodes.get(i).getDist())
				resultIndex.add(i);
		}
		
		return resultIndex.get(new Random().nextInt(resultIndex.size()));
	}
	
}
