package aStar;

import java.util.ArrayList;

public class Node {
	
	private int value, x, y;
	private double dist, cost;
	private boolean isGoal;
	private ArrayList<Node> path;
	
	public Node(int x, int y){
		this.value = 0;
		this.dist = 0.0;
		this.cost = 0.0;
		this.x = x;
		this.y = y;
		this.isGoal = false;
		this.path = new ArrayList<>();
	}
	
	
	public void setValue(int value){
		this.value = value;
	}
	
	public void setIsGoal(boolean isGoal){
		this.isGoal = isGoal;
	}
	
	public void setDist(Node goal){
		this.dist = Math.sqrt(Math.pow(goal.getX()-this.x, 2) + Math.pow(goal.getY()-this.y, 2));
		
		//return this.dist;
	}
	
	public void setCost(int x, int y){
		this.cost = Math.abs(this.x - x) + Math.abs(this.y - y);
	}
	
	public void setPath(Node node){
		if (!this.path.isEmpty()){
			this.path.addAll(node.getPath());
			this.path.add(node);
		}
	}
	
	public double getCost(){
		return this.cost;
	}
	
	public double getDist(){
		return this.dist;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
		
	public ArrayList<Node> getPath(){
		return this.path;
	}
	
	public boolean getIsGoal(){
		return this.isGoal;
	}
	
	public int getValue(){
		return this.value;
	}
}
