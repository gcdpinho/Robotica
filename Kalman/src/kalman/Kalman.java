package kalman;

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Kalman {
	
	private double w, pos, meta, volta, marc;
	
	public Kalman(double v1, double v2, double meta){
		this.w = v2/(v1+v2);
		this.pos = 0;
		this.meta = meta;
		this.volta = 31;
		this.marc = this.volta/18;
	}
	
	public void filtroKalman(){
		double medUltrassom, medOdometro = 0, init, value;
		int anterior = 7, color;
		
		Motor.B.setSpeed(100);
	    Motor.C.setSpeed(100);
	    
	    UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S3);
        init = ultrasom.getDistance();
	    
	    Motor.B.rotate(36000, true);
	    Motor.C.rotate(36000, true);
	    
	    do {
	    		ColorSensor colorSensor = new ColorSensor(SensorPort.S1);
	    		color = colorSensor.getColorID();
	    		//System.out.println(color);
	    		if (color != 7){
	    			medOdometro += this.marc;
	    			anterior = color;
	    		}
	    		//System.out.println("Odometro:"+medOdometro);
    	 		SensorPort.S1.reset();
    	 		
    	 		value = ultrasom.getDistance();
	    		medUltrassom = init - value;
	    		//System.out.println("Ultrassom:"+medUltrassom);
	    		this.pos = medOdometro * this.w + (1-this.w) * medUltrassom;
				//System.out.println(this.pos);
			} while (this.pos < this.meta && Motor.B.isMoving());

	    Motor.B.stop();
	    Motor.C.stop();
	    
	    //System.out.println("Odometro:"+medOdometro);
	    //System.out.println("Ultrassom:"+medUltrassom);
	}
	
}
