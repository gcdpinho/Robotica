package another;
import lejos.nxt.LCD;
import lejos.nxt.*;
import lejos.robotics.objectdetection.*;
import java.util.ArrayList;


public class A {
    private static ArrayList<Nodo> nodos = new ArrayList<Nodo>();
    private static ArrayList<Nodo> nodosConhecidos = new ArrayList<Nodo>();
    public static int dL=6, dC=10, direcao = 0, dist=35
    		, frente=1240, frenteR=1140;
    
    public static void main(String[] args) throws Exception{
        
        Nodo nodoI = new Nodo(10, 10, dL, dC);
        nodoI.setVisited(true);
        nodos.add(nodoI);
        nodosConhecidos.add(nodoI);
        Nodo nodoA = nodoI;
        
        /*Nodo nodo1 = new Nodo(11, 10, dL, dC);
        nodos.add(nodo1);
        nodosConhecidos.add(nodo1);
        Nodo nodo2 = new Nodo(11, 9, dL, dC);
        nodos.add(nodo2);
        nodosConhecidos.add(nodo2);
        Nodo nodo3 = new Nodo(11, 11, dL, dC);
        nodos.add(nodo3);
        nodosConhecidos.add(nodo3);
        Nodo nodo4 = new Nodo(11, 12, dL, dC);
        nodos.add(nodo4);
        nodosConhecidos.add(nodo4);
        Nodo nodo5 = new Nodo(10, 12, dL, dC);
        nodos.add(nodo5);
        nodosConhecidos.add(nodo5);
        Nodo nodo6 = new Nodo(9, 12, dL, dC);
        nodos.add(nodo6);
        nodosConhecidos.add(nodo6);
        Nodo nodo7 = new Nodo(8, 12, dL, dC);
        nodos.add(nodo7);
        nodosConhecidos.add(nodo7);
        Nodo nodo8 = new Nodo(9, 11, dL, dC);
        nodos.add(nodo8);
        nodosConhecidos.add(nodo8);
        Nodo nodo9 = new Nodo(8, 11, dL, dC);
        nodos.add(nodo9);
        nodosConhecidos.add(nodo9);
        Nodo nodo10 = new Nodo(8, 10, dL, dC);
        nodos.add(nodo10);
        nodosConhecidos.add(nodo10);*/
        
        while(true){
        	if(nodoA.getColuna() == dC && nodoA.getLinha() == dL){
        		LCD.drawString("Cheguei", 0, 0);
                Thread.sleep(2000);
        		System.exit(0);
        	}else{
        		vasculha(nodoA);
            	Nodo nodoT = escolheProxNodo();
            	//LCD.drawString("Esta "+nodoA.getLinha()+" "+nodoA.getColuna()+"   ", 0, 0);
            	//LCD.drawString("vai "+nodoT.getLinha()+" "+nodoT.getColuna()+"   ", 0, 1);
            	//Thread.sleep(1000);
            	nodoA = irNodo(nodoA, escolheProxNodo());
        	}
        }
        
    }
    
    public static Nodo irNodo(Nodo atual, Nodo destino) throws Exception{
        
    	if(destino.getColuna() == 1000 && destino.getLinha() == 1000){
            LCD.drawString("Impossivel", 0, 0);
            Thread.sleep(2000);
    	}else{
            melhorCaminho(atual, destino);
            ArrayList<Nodo> caminho = new ArrayList<Nodo>();
            Nodo n = destino;
            caminho.add(n);
            while(n.getPred() != null){
                n = n.getPred();
                caminho.add(n);
            }
            for(int i=caminho.size()-1; i > 0; i--){
            	//LCD.drawString("Esta em "+caminho.get(i).getLinha()+" "+caminho.get(i).getColuna(), i, 0);
            	//LCD.drawString("vai para "+caminho.get(i-1).getLinha()+" "+caminho.get(i-1).getColuna(), 0, 1);
                //Thread.sleep(2000);
				if(direcao == 0){//norte ^
		        	if(caminho.get(i).getColuna() == caminho.get(i-1).getColuna()){
		        		if(caminho.get(i).getLinha() < caminho.get(i-1).getLinha()){
		        			Motor.B.rotate(-frente, true);
		        			Motor.C.rotate(-frente);
		        		}else{
		        			Motor.B.rotate(frente, true);
		        			Motor.C.rotate(frente);
		        		}
		        	}else{
		        		if(caminho.get(i).getColuna() < caminho.get(i-1).getColuna()){
		        			//vira direita
		        			direcao = 1;
		        			Motor.B.rotate(450, true);
		        			Motor.C.rotate(-450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}else{
		        			//vira esquerda
		        			direcao = 3;
		        			Motor.B.rotate(-450, true);
		        			Motor.C.rotate(450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}
		        	}
				}else if(direcao == 1){// leste >
					if(caminho.get(i).getColuna() == caminho.get(i-1).getColuna()){
		        		if(caminho.get(i).getLinha() < caminho.get(i-1).getLinha()){
		        			//vira direita
		        			direcao = 2;
		        			Motor.B.rotate(480, true);
		        			Motor.C.rotate(-450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}else{
		        			//vira esquerda
		        			direcao = 0;
		        			Motor.B.rotate(-450, true);
		        			Motor.C.rotate(450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}
		        	}else{
		        		if(caminho.get(i).getColuna() < caminho.get(i-1).getColuna()){
		        			Motor.B.rotate(frente, true);
		        			Motor.C.rotate(frente);
		        		}else{
		        			Motor.B.rotate(-frente, true);
		        			Motor.C.rotate(-frente);
		        		}
		        	}
				}else if(direcao == 2){// sul v
					
					if(caminho.get(i).getColuna() == caminho.get(i-1).getColuna()){
		        		if(caminho.get(i).getLinha() < caminho.get(i-1).getLinha()){
		        			Motor.B.rotate(frente, true);
		        			Motor.C.rotate(frente);
		        		}else{
		        			Motor.B.rotate(-frente, true);
		        			Motor.C.rotate(-frente);
		        		}
		        	}else{
		        		if(caminho.get(i).getColuna() < caminho.get(i-1).getColuna()){
		        			//vira esquerda
		        			direcao = 1;
		        			Motor.B.rotate(-450, true);
		        			Motor.C.rotate(450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}else{
		        			//vira direita
		        			direcao = 3;
		        			Motor.B.rotate(450, true);
		        			Motor.C.rotate(-450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}
		        	}
				}else{// oeste < 
					if(caminho.get(i).getColuna() == caminho.get(i-1).getColuna()){
		        		if(caminho.get(i).getLinha() < caminho.get(i-1).getLinha()){
		        			//vira esquerda
		        			direcao = 2;
		        			Motor.B.rotate(-450, true);
		        			Motor.C.rotate(450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}else{
		        			//vira direita
		        			direcao = 0;
		        			Motor.B.rotate(450, true);
		        			Motor.C.rotate(-450);
		        			Motor.B.rotate(frenteR, true);
		        			Motor.C.rotate(frenteR);
		        		}
		        	}else{
		        		if(caminho.get(i).getColuna() < caminho.get(i-1).getColuna()){
		        			Motor.B.rotate(-frente, true);
		        			Motor.C.rotate(-frente);
		        		}else{
		        			Motor.B.rotate(frente, true);
		        			Motor.C.rotate(frente);
		        		}
		        	}
				}
            }
    	}
    	destino.setVisited(true);
        return destino;
    }
    
    public static void melhorCaminho(Nodo nodoO, Nodo nodoD) throws Exception{
        ArrayList<Nodo> nodosAbertos = new ArrayList<Nodo>();
        for(Nodo nodo: nodosConhecidos){
            nodo.setCusto(16384);
            nodo.setPred(null);
            nodosAbertos.add(nodo);
        }
        int i = 0;
        
        nodoO.setCusto(0);
        
        while(!nodosAbertos.isEmpty()){
            Nodo nodoAberto = nodosAbertos.get(0);
            for(Nodo nodo: nodosAbertos){
                if(nodo.getCusto() < nodoAberto.getCusto()){
                    nodoAberto = nodo;
                }
            }
            
            nodosAbertos.remove(nodoAberto);
            
            for(Nodo nodo: nodosAbertos){
                if(nodo.getColuna() == nodoAberto.getColuna()){
                    if(nodo.getLinha()+1 == nodoAberto.getLinha() || nodo.getLinha()-1 == nodoAberto.getLinha()){
                        if(nodo.getCusto() > nodoAberto.getCusto()+1){
                            nodo.setCusto(nodoAberto.getCusto()+1);
                            nodo.setPred(nodoAberto);
                        }
                    }
                }else if(nodo.getLinha() == nodoAberto.getLinha()){
                    if(nodo.getColuna()+1 == nodoAberto.getColuna() || nodo.getColuna()-1 == nodoAberto.getColuna()){
                        if(nodo.getCusto() > nodoAberto.getCusto()+1){
                            nodo.setCusto(nodoAberto.getCusto()+1);
                            nodo.setPred(nodoAberto);
                        }
                    }
                }
            }
        }
    }
    
    public static Nodo escolheProxNodo() throws Exception{
        Nodo nodoAtual = new Nodo(1000, 1000, dL, dC);
        int i =0;
        for(Nodo nodo: nodos){
        	//LCD.drawString("Nodo "+nodo.getLinha()+" "+nodo.getColuna()+"   ", 0, i++);
        	//Thread.sleep(1000);
            if(nodoAtual.getDist() > nodo.getDist() && !nodo.isVisited()){
                nodoAtual = nodo;
            }
        }
        return nodoAtual;
    }
    
    public static void vasculha(Nodo nodo) throws Exception{
    	UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
    	UltrasonicSensor us2 = new UltrasonicSensor(SensorPort.S3);
    	FeatureDetector fd = new RangeFeatureDetector(us, 10000, 500);
    	FeatureDetector fd2 = new RangeFeatureDetector(us2, 10000, 500);
    	Feature result;
    	Feature result2;
    	switch(direcao){
    		case 0: //norte ^
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()-1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()-1, nodo.getColuna());
    			Motor.A.rotate(-100);
    			Thread.sleep(500);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()+1);
	    			}
    			}else criaNodo(nodo.getLinha(), nodo.getColuna()+1);
    			Motor.A.rotate(190);
    			Thread.sleep(500);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()-1);
	    			}
	    		}else criaNodo(nodo.getLinha(), nodo.getColuna()-1);
    			Motor.A.rotate(-90);
    			Thread.sleep(500);
    			result2 = fd2.scan();
    			if(result2 != null){
	    			if(result2.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()+1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()+1, nodo.getColuna());
    			break;
    		case 1: // leste >
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()+1);
	    			}
	    		}else criaNodo(nodo.getLinha(), nodo.getColuna()+1);
    			Motor.A.rotate(-90);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()+1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()+1, nodo.getColuna());
    			Motor.A.rotate(180);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()-1, nodo.getColuna());
	    			}
	    		}else criaNodo(nodo.getLinha()-1, nodo.getColuna());
    			Motor.A.rotate(-90);
    			result2 = fd2.scan();
    			if(result2 != null){
	    			if(result2.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()-1);
	    			}
    			}else criaNodo(nodo.getLinha(), nodo.getColuna()-1);
    			break;
    		case 2: // sul v
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()+1, nodo.getColuna());
	    			}
	    		}else criaNodo(nodo.getLinha()+1, nodo.getColuna());
    			Motor.A.rotate(-90);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()-1);
	    			}
    			}else criaNodo(nodo.getLinha(), nodo.getColuna()-1);
    			Motor.A.rotate(180);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()+1);
	    			}
	    		}else criaNodo(nodo.getLinha(), nodo.getColuna()+1);
    			Motor.A.rotate(-90);
    			result2 = fd2.scan();
    			if(result2 != null){
	    			if(result2.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()-1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()-1, nodo.getColuna());
    			break;
    		case 3: // oeste <
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()-1);
	    			}
	    		}else criaNodo(nodo.getLinha(), nodo.getColuna()-1);
    			Motor.A.rotate(-90);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()-1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()-1, nodo.getColuna());
    			Motor.A.rotate(180);
    			result = fd.scan();
    			if(result != null){
	    			if(result.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha()+1, nodo.getColuna());
	    			}
    			}else criaNodo(nodo.getLinha()+1, nodo.getColuna());
    			Motor.A.rotate(-90);
    			result2 = fd2.scan();
    			if(result2 != null){
	    			if(result2.getRangeReading().getRange() >= dist){
	    				criaNodo(nodo.getLinha(), nodo.getColuna()+1);
	    			}
    			}else criaNodo(nodo.getLinha(), nodo.getColuna()+1);
    			break;
    	}
    }
    
    public static void criaNodo(int l, int c){
    	int existe = 0;
    	for(Nodo no: nodosConhecidos){
    		if(no.getColuna() == c && no.getLinha() == l){
    			existe = 1;
    		}
    	}
    	if(existe == 0){
	    	Nodo nodo = new Nodo(l, c, dL, dC);
	    	nodos.add(nodo);
	    	nodosConhecidos.add(nodo);
    	}
    }
}
