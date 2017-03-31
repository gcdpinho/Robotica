package aStar;

import kalman.Kalman;
import java.util.ArrayList;
import java.util.Random;
import lejos.nxt.*;


public class AStar {

	private int mapSize, direita, esquerda, motSensor;
	private double dist, length, goalY;
	private Kalman kalman;
	private static final int FREEPASS = 0;
	private static final int OBSTACLE = 1;
	private static final int POSROBOT = 10;
	private String direc, sensor;
	private Node[][] map;
	private Node initial;
	private ArrayList<Node> openNodes, visitNodes;
	
	public AStar(int mapSize, int xInital, int yInitial, /*int xGoal, int yGoal,*/ double length, Kalman kalman, int direita, int esquerda, int motSensor){
		this.mapSize = mapSize; // tamaho do mapa
		this.kalman = kalman; // kalman (andar x centimetros)
		this.map = new Node[this.mapSize][this.mapSize]; // tabuleiro
		this.initial = new Node(xInital, yInitial); // nodo inicial
		//this.goal = new Node(xGoal, yGoal);
		this.length = length; // tamanho do quadrado
		this.direc = "frente"; // direcao da ultima posição
		this.sensor = "frente";
		this.direita = direita; // angulo necessario pra virar a direita
		this.esquerda = esquerda; // angulo necessario pra virar a esquerda
		this.motSensor = motSensor;// angulo que gira o sensor ultrassom

		for (int i=0; i<this.mapSize; i++)
			for (int j=0; j<this.mapSize; j++)
				this.map[i][j] = new Node(i, j);
		
		this.map[xInital][yInitial].setValue(POSROBOT); // posição inicial no tabuleiro
		//this.map[xGoal][yGoal].setIsGoal(true);
		
		this.openNodes = new ArrayList<>();
		this.visitNodes = new ArrayList<>();
	}
	
	public void run(){
		int currentX = this.initial.getX(); 
		int currentY = this.initial.getY();
		Node goal;
		Node current = this.map[currentX][currentY];

		//this.map[currentX][currentY].setPath(this.initial);
		// Medi distancia em linha reta do alvo
		this.dist = this.getDistance(5); // distancia do alvo em linha reta
		while (this.dist == 255.0){
			System.out.println ("Erro ao identificar o osbtaculo.");
			Button.waitForAnyPress();
			this.dist = this.getDistance(5);
		}
		System.out.println("Dist:" + this.dist);

		this.mapGoal();

		goal = this.map[currentX][(int)this.goalY];
		goal.setIsGoal(true); //mapeando a casa do alvo no tabuleiro	
		System.out.println("Goal:" + goal.getX() + " " + goal.getY());
		
		this.visitNodes.add(this.initial);

		Button.waitForAnyPress();
		
		// Gira para todos os lados e ve os obstaculos
		// Adiciona os nodos FREEPASS vizinhos na lista de nodos abertos
		// Calcula a distancia euclidiana em relação ao alvo

		
		while (!current.getIsGoal()){

			this.calcNodes(currentX, currentY, goal);

			this.turnSensor(this.motSensor, 1);
			this.sensor = "esquerda";
			this.calcNodes(currentX, currentY, goal);

			this.turnSensor(this.motSensor, 2);

			this.turnSensor(this.motSensor, 2);
			this.sensor = "direita";
			this.calcNodes(currentX, currentY, goal);

			this.turnSensor(this.motSensor, 1);
			this.sensor = "frente";
			

			removeRepeat();

			// calcula o custo de todos os nodos da lista para a posição atual do robo
			for (int i=0; i<this.openNodes.size(); i++){
				this.openNodes.get(i).setCost(currentX, currentY);
				System.out.println(this.openNodes.get(i).getX() + " " + this.openNodes.get(i).getY() /*+ " " + this.openNodes.get(i).getCost() + " " + this.openNodes.get(i).getDist()*/);
				
			}

			
			// pega o nodo com menor distância	
			int temp = getBestNode();
			System.out.println("best node: " + openNodes.get(temp).getX() + " " + openNodes.get(temp).getY());
			
			//Button.waitForAnyPress();
			
			// calcula o caminho para esse nodo e vai até ele
			this.calculatePath(current, openNodes.get(temp));
			
			// seta a posicao atual do robo como livre
			this.map[currentX][currentY].setValue(FREEPASS);

			// nodo atual passa a ser o destino
			current = openNodes.get(temp);
			currentX = current.getX();
			currentY = current.getY();

			this.visitNodes.add(current);
			// seta a nova posicao atual do robo
			this.map[currentX][currentY].setValue(POSROBOT);			

			// remove esse nodo da lista
			this.openNodes.remove(temp);

		}

		System.out.println("\n\nCheguei!");

		//fim
		
	}
		
	
	

	private void calcNodes(int currentX, int currentY, Node goal){
		double tempDist = getDistance(5);
		System.out.println(tempDist);
		int obs;

			switch(this.direc){
				case "frente":
					switch(sensor){
						case "frente":
							if (currentY+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX][currentY+1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY+1])){
										this.map[currentX][currentY+1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY+1]);
										System.out.println("add frente: " + currentX + " " + (currentY+1));
									}
								}
								else
									this.map[currentX][currentY+1].setValue(OBSTACLE);
						break;
						case "esquerda":
							if (currentX-1 >= 0)
								if (tempDist >= this.length && this.map[currentX-1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX-1][currentY])){
										this.map[currentX-1][currentY].setDist(goal);
										System.out.println("add esquerda: " + (currentX-1) + " " + currentY);
										this.openNodes.add(this.map[currentX-1][currentY]);
									}
								}
								else
									this.map[currentX-1][currentY].setValue(OBSTACLE);
						break;
						case "direita":
							if (currentX+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX+1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX+1][currentY])){
										this.map[currentX+1][currentY].setDist(goal);
										this.openNodes.add(this.map[currentX+1][currentY]);
										System.out.println("add direita: " + (currentX+1) + " " + currentY);
									}
								}
								else
									this.map[currentX+1][currentY].setValue(OBSTACLE);
						break;
					}
				break;
				case "esquerda":
					switch(sensor){
						case "frente":
							if (currentX-1 >= 0)
								if (tempDist >= this.length && this.map[currentX-1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX-1][currentY])){
										this.map[currentX-1][currentY].setDist(goal);
										System.out.println("add esquerda: " + (currentX-1) + " " + currentY);
										this.openNodes.add(this.map[currentX-1][currentY]);
									}
								}
								else
									this.map[currentX-1][currentY].setValue(OBSTACLE);
						break;
						case "esquerda":
							if (currentY-1 >= 0)
								if (tempDist >= this.length && this.map[currentX][currentY-1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY-1])){
										this.map[currentX][currentY-1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY-1]);
										System.out.println("add tras: " + currentX + " " + (currentY-1));
									}
								}
								else
									this.map[currentX][currentY-1].setValue(OBSTACLE);
						break;
						case "direita":
							if (currentY+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX][currentY+1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY+1])){
										this.map[currentX][currentY+1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY+1]);
										System.out.println("add frente: " + currentX + " " + (currentY+1));
									}
								}
								else
									this.map[currentX][currentY+1].setValue(OBSTACLE);
						break;
					}
				break;
				case "direita":
					switch(sensor){
						case "frente":
							if (currentX+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX+1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX+1][currentY])){
											this.map[currentX+1][currentY].setDist(goal);
											this.openNodes.add(this.map[currentX+1][currentY]);
											System.out.println("add direita: " + (currentX+1) + " " + currentY);
									}
								}
								else
									this.map[currentX+1][currentY].setValue(OBSTACLE);
						break;
						case "esquerda":
							if (currentY+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX][currentY+1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY+1])){
										this.map[currentX][currentY+1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY+1]);
										System.out.println("add frente: " + currentX + " " + (currentY+1));
									}
								}
								else
									this.map[currentX][currentY+1].setValue(OBSTACLE);
						break;
						case "direita":
							if (currentY-1 >= 0)
								if (tempDist >= this.length && this.map[currentX][currentY-1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY-1])){
										this.map[currentX][currentY-1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY-1]);
										System.out.println("add tras: " + currentX + " " + (currentY-1));
									}
								}
								else
									this.map[currentX][currentY-1].setValue(OBSTACLE);
						break;
					}
				break;
				case "tras":
					switch(sensor){
						case "frente":
							//System.out.println("?" + tempDist);
							if (currentY-1 >= 0)
								if (tempDist >= this.length && this.map[currentX][currentY-1].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX][currentY-1])){
										this.map[currentX][currentY-1].setDist(goal);
										this.openNodes.add(this.map[currentX][currentY-1]);
										System.out.println("add tras: " + currentX + " " + (currentY-1));
									}
								}
								else
									this.map[currentX][currentY-1].setValue(OBSTACLE);
						break;
						case "esquerda":
							if (currentX+1 < this.mapSize)
								if (tempDist >= this.length && this.map[currentX+1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX+1][currentY])){
										this.map[currentX+1][currentY].setDist(goal);
										this.openNodes.add(this.map[currentX+1][currentY]);
										System.out.println("add direita: " + (currentX+1) + " " + currentY);
									}
								}
								else
									this.map[currentX+1][currentY].setValue(OBSTACLE);
						break;
						case "direita":
							if (currentX-1 >= 0)
								if (tempDist >= this.length && this.map[currentX-1][currentY].getValue() != OBSTACLE){
									if (!this.testVisitNodes(this.map[currentX-1][currentY])){		
										this.map[currentX-1][currentY].setDist(goal);
										System.out.println("add esquerda: " + (currentX-1) + " " + currentY);
										this.openNodes.add(this.map[currentX-1][currentY]);
									}
								}
								else
									this.map[currentX-1][currentY].setValue(OBSTACLE);
						break;
					}
				break;
			}
	}

	private boolean testVisitNodes(Node current){
		for (int i=0; i<this.visitNodes.size(); i++)
			if (this.visitNodes.get(i).getX() == current.getX() && this.visitNodes.get(i).getY() == current.getY())
				return true;

		return false;
	}

	private void turnSensor(int degrees, int direction){
		Motor.A.setSpeed(80);

		if (direction == 1)
			Motor.A.rotate(degrees, true);
		else
			Motor.A.rotate(-degrees, true);
			
		try {
			Thread.sleep(2000);

			Motor.A.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Erro ao girar o sensor.");
		}
			
	}

	private void mapGoal(){
		try {
			//this.goalY = 255.0/26;
			this.goalY =  this.dist/(this.length) + this.initial.getY();
		}
		catch (Exception e){
			System.out.println("Erro ao mapear o objetivo.");
			Button.waitForAnyPress();
			this.mapGoal();
		}
	}

	// caso o proximo nodo não for vizinho ele retorna o caminho até que seja vizinho de algum nodo (recursivamente).
	private void calculatePath(Node current, Node goal){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		Node prox;

		if (currentX - goalX == 0 && (currentY - goalY <= 1 && currentY - goalY >= -1))
			this.walk(current, goal);
		else
			if (currentY - goalY == 0 && (currentX - goalX <= 1 && currentX - goalX >= -1))
				this.walk(current, goal);
			else {
				
				System.out.println("back: "+ current.getPath().size());
				//Button.waitForAnyPress();
				prox = current.getPath().get(current.getPath().size()-1);
				//System.out.println("aqui1");
				this.walk(current, prox);
				//System.out.println("aqui2");
				this.calculatePath(prox, goal);
			}
	}
	
	
	private void walk(Node current, Node goal){
		int currentX = current.getX();
		int currentY= current.getY();
		int goalX = goal.getX();
		int goalY = goal.getY();
		String tempDirec = "";
		// frente
		if (currentY - goalY < 0){
			this.turnAndGo(this.direc, "frente");

		}
		// tras
		else
			if (currentY - goalY > 0){
				this.turnAndGo(this.direc, "tras");

			}
			// direita
			else
				if (currentX - goalX < 0){
					this.turnAndGo(this.direc, "direita");

				}
				// esquerda
				else 
					if (currentX - goalX > 0){
						this.turnAndGo(this.direc, "esquerda");
	
						
					}

		if (goalX != this.initial.getX() || goalY != this.initial.getY()){
			this.map[goalX][goalY].setPath(this.map[currentX][currentY]);
		}
				
	}

	private double getDistance(int x){
		/*try{
			ArrayList<Double> listUltra = new ArrayList<>();
			double min, max;
			int s = 0;
			int iMin = 0;
			int iMax = 0;

			for (int i=0; i<x; i++){
			*/	UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S3);
				/*listUltra.add((double)*/return ultrasom.getDistance();
			/*}

			min = listUltra.get(0);
			max = listUltra.get(0);

			for (int i=0; i<listUltra.size(); i++){
				//System.out.println(listUltra.get(i));
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
			Button.waitForAnyPress();
			//this.getDistance(5);
			
			return 255;
		}*/
		
	}
	
	private void turnAndGo(String current, String goal){
    	// verifica a direção atual e para qual direção deve ir

    	switch(current){
    		case "frente":
    			switch(goal){
    				case "frente":
    					break;
    				case "tras":
    					this.turnOn(this.esquerda, 1);
    					this.turnOn(this.esquerda, 1);
    					break;
    				
    				case "esquerda":
    					this.turnOn(this.esquerda, 1);
    					break;
    				case "direita":
    					this.turnOn(this.direita, 2);
    					break;
    			}
    			break;
    		case "tras":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.direita, 2);
    					this.turnOn(this.direita, 2);
    					break;
    				case "tras":	
    					break;
    				case "esquerda":
    					this.turnOn(this.direita, 2);
    					break;
    				case "direita":
    					this.turnOn(this.esquerda, 1);
    					break;
    			}
    			break;
    		case "esquerda":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.direita, 2);
    					break;
    				case "tras":
    					this.turnOn(this.esquerda, 1);	
    					break;
    				case "esquerda":
    					break;
    				case "direita":
    					this.turnOn(this.esquerda, 1);
    					this.turnOn(this.esquerda, 1);
    					break;
    			}
    			break;
    		case "direita":
    			switch(goal){
    				case "frente":
    					this.turnOn(this.esquerda, 1);
    					break;
    				case "tras":
    					this.turnOn(this.direita, 2);	
    					break;
    				case "esquerda":
    					this.turnOn(this.direita, 2);
    					this.turnOn(this.direita, 2);
    					break;
    				case "direita":
    					break;
    			}
    			break;
    	}
   		this.kalman.filtroKalman();
    	this.direc = goal;
    	
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
			Thread.sleep(5000);
			
			Motor.B.stop();
			Motor.C.stop();
			//Motor.A.stop();
		} 
		catch (InterruptedException e) {
			System.out.println("Erro ao dobrar.");
		}
		//*/
	}
	private void removeRepeat() {
		for (int i = 0; i < this.openNodes.size(); i++) {
			int xi = this.openNodes.get(i).getX();
			int yi = this.openNodes.get(i).getY();
			for (int j = i+1; j < this.openNodes.size(); j++) {
				int xj = this.openNodes.get(j).getX();
				int yj = this.openNodes.get(j).getY();
				if (xi == xj && yi == yj) {
					this.openNodes.remove(j);
					j--;
				}
			}
		}
	};
	private int getBestNode(){
		ArrayList<Integer> resultIndex = new ArrayList<>();
		double result = this.openNodes.get(0).getDist();
		double aux;
		
		

		// pega o nodo com menor custo
		for (int i=1; i<this.openNodes.size(); i++){
			aux = this.openNodes.get(i).getDist();
			if (result > aux)
				result = aux;				
		}

		
	
		
		// se tiver nodos com custos iguais retorna um randomicamente
		for (int i=0; i<this.openNodes.size(); i++){
			if (result == this.openNodes.get(i).getDist())
				resultIndex.add(i);
		}
		
		return resultIndex.get(new Random().nextInt(resultIndex.size()));
	}
	
}
