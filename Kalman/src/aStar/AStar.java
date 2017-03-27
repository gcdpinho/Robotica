package aStar;

import kalman.Kalman;
import java.util.ArrayList;
import java.util.Random;
import lejos.nxt.*;


public class AStar {

	private int mapSize;
	private double dist, length;
	private Kalman kalman;
	private static final int OBSTACLE = 1;
	private static final int FREEPASS = 0;
	private static final int POSROBOT = 10;
	//private static final int GOAL = 2;
	private Node[][] map;
	private Node initial, goal;
	private ArrayList<Node> openNodes;
	
	
	public AStar(int mapSize, int xInital, int yInitial, /*int xGoal, int yGoal,*/ double length, Kalman kalman){
		this.mapSize = mapSize;
		this.kalman = kalman;
		this.map = new Node[this.mapSize][this.mapSize];
		this.initial = new Node(xInital, yInitial);
		//this.goal = new Node(xGoal, yGoal);
		this.length = length;
		
		for (int i=0; i<this.mapSize; i++)
			for (int j=0; j<this.mapSize; j++)
				this.map[i][j] = new Node(i, j);
		
		this.map[xInital][yInitial].setValue(POSROBOT);
		//this.map[xGoal][yGoal].setIsGoal(true);
		
		this.openNodes = new ArrayList<>();
	}
	
	public void run(){
		int currentX = this.initial.getX(); 
		int currentY = this.initial.getY();
			
		// Medi distancia em linha reta do alvo
		this.dist = this.getDistance();
		this.map[currentX][(int) (this.dist/this.length)].setIsGoal(true);;
		
		Button.waitForAnyPress();
		
		// Gira para todos os lados e ve os obstaculos
		// Adiciona os nodos FREEPASS na lista de nodos abertos
		// Calcula a distancia euclidiana em relaçã ao alvo
		
		//colocar try catch nos else (current + 1 pode ser fora do tabuleiro)
		
		Node current = this.map[currentX][currentY];
		while (!current.getIsGoal()){
			// frente
			if (getDistance() >= this.length && currentY+1 <= this.mapSize){ // tem espaço
				this.map[currentX][currentY+1].setDist(goal);
				this.openNodes.add(this.map[currentX][currentY+1]);
			}
			else
				this.map[currentX][currentY+1].setValue(OBSTACLE);
			
			//esquerda
			this.turn(2200, 2);
			if (getDistance() >= this.length && currentX-1 >= 0){
				this.map[currentX-1][currentY].setDist(goal);
				this.openNodes.add(this.map[currentX-1][currentY]);
			}
			else
				this.map[currentX-1][currentY].setValue(OBSTACLE);
			
			// tras
			this.turn(2200, 2);
			if (getDistance() >= this.length && currentY-1 >= 0){
				this.map[currentX][currentY-1].setDist(goal);
				this.openNodes.add(this.map[currentX][currentY-1]);
			}
			else
				this.map[currentX][currentY-1].setValue(OBSTACLE);
			
			// direita
			this.turn(2200, 2);
			if (getDistance() >= this.length && currentX+1 <= this.mapSize){
				this.map[currentX+1][currentY].setDist(goal);
				this.openNodes.add(this.map[currentX+1][currentY]);
			}
			else
				this.map[currentX+1][currentY].setValue(OBSTACLE);
			
			// vira pra frente novamente
			this.turn(2200, 2);
			
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
		
		if (currentX - goalX == 0 && (currentY - goalY <= 1 && currentY - goalY >= -1))
			this.walk(current, goal);
		else
			if (currentY - goalY == 0 && (currentX - goalX <= 1 && currentX - goalX >= -1))
				this.walk(current, goal);
			else
				calculatePath(current.getPath().get(current.getPath().size()-1), goal);
	}
	
	
	private void walk(Node current, Node goal){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		
		// frente
		if (currentY - goalY > 0)
				this.kalman.filtroKalman();
		// tras
		else
			if (currentY - goalY < 0){
				this.turn(2200 * 2, 2);
				this.kalman.filtroKalman();
			}
			// direita
			else
				if (currentX - goalX > 0){
					this.turn(2200, 1);
					this.kalman.filtroKalman();
				}
				// esquerda
				else 
					if (currentX - goalX < 0){
						this.turn(2200, 2);
						this.kalman.filtroKalman();
					}
		this.map[goalX][goalY].setPath(this.map[currentX][currentY]);
					
	}
	
	private double getDistance(){
		UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S4);
		
		return ultrasom.getDistance();
	}
	
	private void turn(int degrees, int direction){
		
    	Motor.B.setSpeed(200);
    	Motor.C.setSpeed(200);

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
		double result = this.openNodes.get(0).getCost() + this.openNodes.get(0).getDist();
		double aux;
		
		for (int i=1; i<this.openNodes.size(); i++){
			aux = this.openNodes.get(i).getCost() + this.openNodes.get(i).getDist();
			if (result > aux)
				result = aux;				
		}
		
		for (int i=0; i<this.openNodes.size(); i++){
			if (result == this.openNodes.get(i).getCost() + this.openNodes.get(i).getDist())
				resultIndex.add(i);
		}
		
		return resultIndex.get(new Random().nextInt(resultIndex.size()));
	}
	
}
