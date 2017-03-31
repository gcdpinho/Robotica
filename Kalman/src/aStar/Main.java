package aStar;
import kalman.Kalman;
import lejos.nxt.*;

public class Main {
   
    public static void main(String[] args) {    
        
        //KALMAN W = V²2/(V²1+V²2)

        /*pos = medicao odometro*w + (1-w)*medicao ultrasom*/
        /*
        double v1 = 0.587644, v2 = 0.536655, w, pos = 0, init, roda = 13.2, distance, value;
        int color, count = 0, anterior = 7;
        
        w = v2/(v1+v2);
        
        UltrasonicSensor ultrasom = new UltrasonicSensor(SensorPort.S4);
         
         init = ultrasom.getDistance();
         
         Motor.B.setSpeed(120);
         Motor.C.setSpeed(120);
         
         do {
                ColorSensor colorSensor = new ColorSensor(SensorPort.S1);
                color = colorSensor.getColorID();
                if (color != 7 && anterior == 7){
                    count ++;
                    value = ultrasom.getDistance();
                    distance = init - value;
                    pos = 13.2 * count * w + (1-w)*distance;
                    System.out.println(pos);
                }
                SensorPort.S1.reset();
                anterior = color;
             
            } while(pos < 13.5 && Motor.B.isMoving());
                
         Motor.B.stop();
         Motor.C.stop();
<<<<<<< HEAD
    	*/
    	
    	// Medicao Odometro
    	/*
    	Odometro odometro = new Odometro(50);
    	odometro.medirOdometro();
    	*/
    	
    	// Medicao Ultrassom
    	/*
    	Ultrasom ultrasom = new Ultrasom(50);
    	ultrasom.medirUltrasom();
    	*/
    	
    	// Kalman
    	
    	Kalman kalman = new Kalman(0.9017, 0.1972, 23);
        // arrumar valores de direita e esquerda (2880, 3050)
    	AStar aStar = new AStar(5, 2, 0, 23, kalman, 2350, 2350);
    	
    	aStar.run();
    	
=======
        */
        
        // Medicao Odometro
        /*
        Odometro odometro = new Odometro(50);
        odometro.medirOdometro();
        */
        
        // Medicao Ultrassom
        /*
        Ultrasom ultrasom = new Ultrasom(50);
        ultrasom.medirUltrasom();
        */
        
        // Kalman
        
        Kalman kalman = new Kalman(0.5512, 0.4488, 26);

        //Button.waitForAnyPress();
        
        //kalman.filtroKalman();

        // arrumar valores de direita e esquerda (2880, 3050)
        AStar aStar = new AStar(10, 3, 5, 26+10, kalman, 510, 510);
        
        aStar.run();
<<<<<<< HEAD
        
>>>>>>> 1ccb7074c1b985d65a243c9bc6eb5ac5c14ce65d
        Button.waitForAnyPress();
=======
        //aStar.teste(20);

         Button.waitForAnyPress();
    
>>>>>>> 2ce1b31e152f16e5d702aea2ca05447e7fdde88b
    }
}