package elevator;
/*
 * Copyright (C) 2000 by ETHZ/INF/CS
 * All rights reserved
 * 
 * @version $Id$
 * @author Roger Karrer
 */

import java.util.*;

public class Elevator {

    // shared control object
    private Controls controls; 
    private Vector<ButtonPress> events;
    private Lift[] lifts;
    private int numberOfLifts;

    // Initializer for main class, reads the input and initlizes
    // the events Vector with ButtonPress objects
    private Elevator(String file) {

        events = new Vector<ButtonPress>();

        int numFloors = 4;
        int numLifts = 2;

        events.addElement(new ButtonPress(1, 1, 3));
        events.addElement(new ButtonPress(3, 4, 2));

        // Create the shared control object
        controls = new Controls(numFloors);
        numberOfLifts = numLifts;
        lifts = new Lift[numLifts];
        // Create the elevators
        for(int i = 0; i < numLifts; i++)
            lifts[i] = new Lift(numFloors, controls);
    }

    // Press the buttons at the correct time
    private void begin() {
        // Get the thread that this method is executing in
        Thread me = Thread.currentThread();
        // First tick is 1
        int time = 1;
    
        for(int i = 0; i < events.size(); ) {
            ButtonPress bp = events.elementAt(i);
            // if the current tick matches the time of th next event
            // push the correct buttton
            if(time == bp.time) {
                //System.out.println("Elevator::begin - its time to press a button");
                if(bp.onFloor > bp.toFloor)
                    controls.pushDown(bp.onFloor, bp.toFloor);
                else
                    controls.pushUp(bp.onFloor, bp.toFloor);
                i += 1;
            }
            time += 1;
        }
    }
  
    private void waitForLiftsToFinishOperation(){
    	for(int i = 0; i < numberOfLifts; i++){
    		try{
    			lifts[i].join();
    		}
    		catch(InterruptedException e){
    			System.err.println("Error while waiting for lift " + i + " to finish");
    		}
        }
    }

    public static void main(String args[]) {
        Elevator building = new Elevator("misc/elevinput/data-pp");
        building.begin();
        //building.waitForLiftsToFinishOperation();
    }
}

