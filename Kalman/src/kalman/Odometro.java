package kalman;
import lejos.nxt.*;

public class Odometro {
	
	private double volta, meta, marc;
	
	public Odometro(double meta){
		this.volta = 13.5;
		this.meta = meta;
		this.marc = this.volta/4;
	}
	
	public void medirOdometro(){
		int anterior = 7, color, distance = 0;		
		
		Motor.B.setSpeed(100);
	    Motor.C.setSpeed(100);
	        
	    Motor.B.rotate(-36000, true);
	    Motor.C.rotate(-36000, true);
	    
	    do {	
    	 		ColorSensor colorSensor = new ColorSensor(SensorPort.S1);
    	 		color = colorSensor.getColorID();
    	 		//System.out.println(color);
    	 		if (color != 7 && anterior == 7){
	    			distance += this.marc;
	    			System.out.println(distance);
	    			anterior = color;
	    		}
    	 		else
    	 			anterior = 7;
    	 		SensorPort.S1.reset();
    		} while (distance < this.meta && Motor.B.isMoving());
	    
	    Motor.B.stop();
	    Motor.C.stop();
	}
	
}
