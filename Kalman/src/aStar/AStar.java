package aStar;

import kalman.Kalman;
import java.util.ArrayList;
import java.util.Random;
import lejos.nxt.*;


public class AStar {

	private int mapSize, direita, esquerda, goalY;
	private double dist, length;
	private Kalman kalman;
	private static final int FREEPASS = 0;
	private static final int OBSTACLE = 1;
	private static final int POSROBOT = 10;
	private String direc;
	private Node[][] map;
	private Node initial;
	private ArrayList<Node> openNodes;
	
	public AStar(int mapSize, int xInital, int yInitial, /*int xGoal, int yGoal,*/ double length, Kalman kalman, int direita, int esquerda){
		this.mapSize = mapSize; // tamaho do mapa
		this.kalman = kalman; // kalman (andar x centimetros)
		this.map = new Node[this.mapSize][this.mapSize]; // tabuleiro
		this.initial = new Node(xInital, yInitial); // nodo inicial
		//this.goal = new Node(xGoal, yGoal);
		this.length = length; // tamanho do quadrado
		this.direc = ""; // direcao da ultima posição
		this.direita = direita; // angulo necessario pra virar a direita
		this.esquerda = esquerda; // angulo necessario pra virar a esquerda

		for (int i=0; i<this.mapSize; i++)
			for (int j=0; j<this.mapSize; j++)
				this.map[i][j] = new Node(i, j);
		
		this.map[xInital][yInitial].setValue(POSROBOT); // posição inicial no tabuleiro
		//this.map[xGoal][yGoal].setIsGoal(true);
		
		this.openNodes = new ArrayList<>();
	}
	
	public void run(){
		int currentX = this.initial.getX(); 
		int currentY = this.initial.getY();
		Node goal;
		
		//this.map[currentX][currentY].setPath(this.initial);
		// Medi distancia em linha reta do alvo
		this.dist = this.getDistance(); // distancia do alvo em linha reta
		System.out.println("Dist:" + this.dist);

		this.mapGoal();
		goal = this.map[currentX][this.goalY];
		goal.setIsGoal(true); //mapeando a casa do alvo no tabuleiro	
		System.out.println("Goal:" + goal.getX() + " " + goal.getY());
		
		Button.waitForAnyPress();
		
		// Gira para todos os lados e ve os obstaculos
		// Adiciona os nodos FREEPASS vizinhos na lista de nodos abertos
		// Calcula a distancia euclidiana em relação ao alvo

		Node current = this.map[currentX][currentY];
		while (!current.getIsGoal()){
			//System.out.println(this.direc);
			//busca por nodos abertos, verificando o ultimo movimento (para não retornar)

			// frente
			if (getDistance() >= this.length && currentY+1 < this.mapSize && !this.direc.equals("tras")){
				this.map[currentX][currentY+1].setDist(goal);
				//this.map[currentX][currentY+1].setCost(currentX, currentY);
				this.openNodes.add(this.map[currentX][currentY+1]);
				System.out.println("add frente: " + currentX + " " + (currentY+1));
			}
			else
				if (!this.direc.equals("tras"))
					try {
						this.map[currentX][currentY+1].setValue(OBSTACLE);
					}
					catch (Exception e){
						System.out.println("Fora do map.");
					}

			//esquerda
			this.turnOn(this.esquerda, 2);
			if (getDistance() >= this.length && currentX-1 >= 0 && !this.direc.equals("direita")){
				this.map[currentX-1][currentY].setDist(goal);
				//this.map[currentX-1][currentY].setCost(currentX, currentY);
				System.out.println("add esquerda: " + (currentX-1) + " " + currentY);
				this.openNodes.add(this.map[currentX-1][currentY]);
			}
			else
				if (!this.direc.equals("direita"))
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
				//this.map[currentX][currentY-1].setCost(currentX, currentY);
				this.openNodes.add(this.map[currentX][currentY-1]);
				System.out.println("add tras: " + currentX + " " + (currentY-1));
			}
			else
				if (!this.direc.equals("frente"))
					try {
						this.map[currentX][currentY-1].setValue(OBSTACLE);
					}
					catch (Exception e){
						System.out.println("Fora do map.");
					}

			// direita
			this.turnOn(esquerda, 2);
			if (getDistance() >= this.length && currentX+1 < this.mapSize && !this.direc.equals("esquerda")){
				this.map[currentX+1][currentY].setDist(goal);
				//this.map[currentX+1][currentY].setCost(currentX, currentY);
				this.openNodes.add(this.map[currentX+1][currentY]);
				System.out.println("add direita: " + (currentX+1) + " " + currentY);
			}
			else
				if (!this.direc.equals("esquerda"))
					try {
						this.map[currentX+1][currentY].setValue(OBSTACLE);
					}
					catch (Exception e){
						System.out.println("Fora do map.");
					}
			
			// vira pra frente novamente
			this.turnOn(esquerda, 2);			
			
			// calcula o custo de todos os nodos da lista para a posição atual do robo
			for (int i=0; i<this.openNodes.size(); i++){
				this.openNodes.get(i).setCost(currentX, currentY);
				System.out.println(this.openNodes.get(i).getX() + " " + this.openNodes.get(i).getY() + " " + this.openNodes.get(i).getCost() + " " + this.openNodes.get(i).getDist());
				
			}
			
			//Button.waitForAnyPress();
			
			// pega o nodo com menor distância	
			int temp = getBestNode();
			System.out.println("best node: " + openNodes.get(temp).getX() + " " + openNodes.get(temp).getY());
			
			//Button.waitForAnyPress();
			
			// calcula o caminho para esse nodo e vai até ele
			this.calculatePath(current, openNodes.get(temp), "frente");
			
			// seta a posicao atual do robo como livre
			this.map[currentX][currentY].setValue(FREEPASS);

			// nodo atual passa a ser o destino
			current = openNodes.get(temp);
			currentX = current.getX();
			currentY = current.getY();

			// seta a nova posicao atual do robo
			this.map[currentX][currentY].setValue(POSROBOT);			

			// remove esse nodo da lista
			this.openNodes.remove(temp);

			for (int i=0; i<openNodes.size(); i++)
				System.out.println(this.openNodes.get(i).getX() + " " + this.openNodes.get(i).getY());
			
			// vira pra frente de novo
			turnAndGo(this.direc, "frente", false);
		
		// repete até chegar no alvo
		}
		
	}
	
	private void mapGoal(){
		try {
			this.goalY = (int) (this.dist/this.length) + this.initial.getY() + 1;
		}
		catch (Exception e){
			System.out.println("Erro ao mapear o objetivo.");
			Button.waitForAnyPress();
			this.mapGoal();
		}
	}

	// caso o proximo nodo não for vizinho ele retorna o caminho até que seja vizinho de algum nodo (recursivamente).
	private void calculatePath(Node current, Node goal, String currenDirec){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		Node prox;

		if (currentX - goalX == 0 && (currentY - goalY <= 1 && currentY - goalY >= -1))
			this.walk(current, goal, currenDirec);
		else
			if (currentY - goalY == 0 && (currentX - goalX <= 1 && currentX - goalX >= -1))
				this.walk(current, goal, currenDirec);
			else {
				
				System.out.println("back: "+ current.getPath().size());
				//Button.waitForAnyPress();
				prox = current.getPath().get(current.getPath().size()-1);
				//System.out.println("aqui1");
				this.walk(current, prox, currenDirec);
				//System.out.println("aqui2");
				this.calculatePath(prox, goal, this.direc);
			}
	}
	
	
	private void walk(Node current, Node goal, String currentDirec){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		
		// frente
		if (currentY - goalY < 0){
			this.turnAndGo(currentDirec, "frente", true);
			//this.kalman.filtroKalman();
		}
		// tras
		else
			if (currentY - goalY > 0){
				this.turnAndGo(currentDirec, "tras", true);
				//this.kalman.filtroKalman();
			}
			// direita
			else
				if (currentX - goalX < 0){
					this.turnAndGo(currentDirec, "direita", true);
					//this.kalman.filtroKalman();
				}
				// esquerda
				else 
					if (currentX - goalX > 0){
						this.turnAndGo(currentDirec, "esquerda", true);
						//this.kalman.filtroKalman();
					}
		System.out.println("pathCurrent: "+ currentX + " " + currentY + " " + this.map[currentX][currentY].getPath().size());
		if (goalX != this.initial.getX() || goalY != this.initial.getY()){
			System.out.println("entrou");
			this.map[goalX][goalY].setPath(this.map[currentX][currentY]);
		}
		System.out.println("pathGoal: " + goalX + " " + goalY+ " " + this.map[goalX][goalY].getPath().size());			
	}
	
	private double getDistance(){
		try{
			ArrayList<Double> listUltra = new ArrayList<>();
			double min, max;
			int s = 0;
			int iMin = 0;
			int iMax = 0;

			for (int i=0; i<listUltra.size(); i++){
				UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S4);
				listUltra.add((double)ultrasom.getDistance());
			}

			min = listUltra.get(0);
			max = listUltra.get(0);

			for (int i=0; i<listUltra.size(); i++){
				if (min > listUltra.get(i)){
					min = listUltra.get(i);
					iMin = i;
				}
				if (max < listUltra.get(i)){
					max = listUltra.get(i);
					iMax = i;
				}
			}
			listUltra.remove(iMin);
			listUltra.remove(iMax);

			for (int i=0; i<listUltra.size(); i++)
				s += listUltra.get(i);
		
			return s/listUltra.size();
		}
		catch (Exception e){
			System.out.println("Erro ao pegar a distância.");
			Button.waitForAnyPress;
			this.getDistance();
		}
	}
	
	private void turnAndGo(String current, String goal, boolean go){
    	// verifica a direção atual e para qual direção deve ir

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
    	if (go){		
    		this.kalman.filtroKalman();
    		this.direc = goal;
    	}
	}
	
	private void turnOn (int degrees, int direction){
		//Button.waitForAnyPress();
		///*
		Motor.B.setSpeed(150);
    	Motor.C.setSpeed(150);
    	//Motor.A.setSpeed(15);
		
    	//Motor.A.rotate(-2000, true);
		
    	if (direction == 2) { // esquerda
    		Motor.B.rotate(degrees, true);
    		Motor.C.rotate(-degrees, true);
    	}
    	else {
    		Motor.B.rotate( -degrees, true);
    		Motor.C.rotate( degrees, true);
    	}
		
		try {
			Thread.sleep(13000);
			
			Motor.B.stop();
			Motor.C.stop();
			//Motor.A.stop();
		} 
		catch (InterruptedException e) {
			System.out.println("Erro ao dobrar.");
		}
		//*/
	}

	private int getBestNode(){
		ArrayList<Integer> resultIndex = new ArrayList<>();
		double result = this.openNodes.get(0).getCost() + this.openNodes.get(0).getDist();
		double aux;
		
		// pega o nodo com menor custo
		for (int i=1; i<this.openNodes.size(); i++){
			aux = this.openNodes.get(i).getCost() + this.openNodes.get(i).getDist();
			if (result > aux)
				result = aux;				
		}
		
		// se tiver nodos com custos iguais retorna um randomicamente
		for (int i=0; i<this.openNodes.size(); i++){
			if (result == this.openNodes.get(i).getCost() + this.openNodes.get(i).getDist())
				resultIndex.add(i);
		}
		
		return resultIndex.get(new Random().nextInt(resultIndex.size()));
	}
	
}
