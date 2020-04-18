package socialdistancing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JLabel;

public class Control {
		String title = "Social Distance Simulation";
		//Model and View
		ArrayList<Person> model; //the community of Person objects	
		Simulator view; //JPanel graphics window
		
		// counters for "this" simulation instance
		public int numInfected = 0;
		public int numDied= 0;
		
		Wall vWall1 = Simulator.vWall1;
		Wall vWall2 = Simulator.vWall2;
		Wall vWall3 = Simulator.vWall3;
		Wall vWall4 = Simulator.vWall4;
		
		Wall hWall1 = Simulator.hWall1;
		Wall hWall2 = Simulator.hWall2;
		Wall hWall3 = Simulator.hWall3;
		Wall hWall4 = Simulator.hWall4;
		
		// simulation control values
		public int  numPeople;			
		public double toRoam;			    
		public double toBeInfected;		
		public double toDie;				
		public int sickTimeLow;			
		public int sickTimeMax;
		//frame extents
		public int frameX;
		public int frameY;
		//position extents, keep objects away from the edges
		public int xExt;
		public int yExt;
		//oval size, represents person in frame
		public int OvalW;	//Height
		public int OvalH;	//Width
		//refresh timer, also used to calculate time/age of infection
		public int timerValue;
	
		/*
		 * Default constructor uses Static/Default simulation values
		 */
		public Control() {
			//This sets defaults in case run with default constructor
			// simulation control starting values
			numPeople = Settings.sNumPeople;			
			toRoam = Settings.sToRoam;			    
			toBeInfected = Settings.sToBeInfected;		
			toDie = Settings.sToDie;				
			sickTimeLow = Settings.sSickTimeLow;			
			sickTimeMax = Settings.sSickTimeMax;
			//frame extents
			frameX = Settings.sFrameX;
			frameY = Settings.sFrameY;
			//position extents, keep objects away from the edges
			xExt = Settings.sXExt;
			yExt = Settings.sYExt;
			//oval size, represents person in frame
			OvalW = Settings.sOvalW;	//Height
			OvalH = Settings.sOvalH;	//Width
			//refresh timer, also used to calculate time/age of infection
			timerValue = Settings.sTimerValue;
		}

		/*
		 * This constructor uses user defined simulation Settings
		 */
		public Control(Settings sets) {
			// health settings
			numPeople = sets.numPeople;
			toRoam = sets.toRoam;
			toBeInfected = sets.toBeInfected;
			toDie = sets.toDie;
			sickTimeLow = sets.sickTimeLow;
			sickTimeMax = sets.sickTimeMax;
			// simulator settings
			frameX = sets.frameX;
			frameY = sets.frameY;
			yExt = sets.yExt;
			xExt = sets.xExt;
			OvalW = sets.OvalW;
			OvalH = sets.OvalH;
			timerValue = sets.timerValue;
		}
		
		/*
		 * Tester method to run simulation
		 */
		public static void main (String[] args) {
			Control c = new Control();
			c.runSimulation();
		}
		
		/* 
		 * This method coordinates MVC for Simulation
		 * - The Simulation is managing People in a Graphics frame to simulate a virus outbreak
		 * - Prerequisite: Control values from constructor are ready
		 */
		public void runSimulation() {
			//Setup to the Simulation Panel/Frame
			Simulator view = new Simulator(this, title);
			
			//Setup the People
			model = new ArrayList<Person>();
			for(int i = 0; i < numPeople; i++) {
				//instantiate Person object and add it to the ArrayList
				model.add(new Person(this));
			}
			
			// Start the Simulation
			view.activate();
		}
		
		/*
		 * Call Back method for View
		 * paints/repaints model of graphic objects repressing person objects in the frame 
		 */
		public void paintPersons(Graphics gDot1) {
			
			//find the Person in the Model!
			int index = 0;
			for(Person pDot1: model) {
				for(Person pDot2: model) {
					//for each unique pair invoke the collision detection code
					pDot1.collisionDetector(pDot2);
				}
				checkWallCollision(pDot1);
				pDot1.healthManager(); //manage health values of the Person
				pDot1.velocityManager(); //manage social distancing and/or roaming values of the Person
				
				//set the color of the for the person oval based on the health status of person object
				switch(pDot1.state) {
					case candidate:
						gDot1.setColor(Color.LIGHT_GRAY);
						break;
					case infected:
						gDot1.setColor(Color.red);
						break;
					case recovered:
						gDot1.setColor(Color.green);
						break;
					case died:
						gDot1.setColor(Color.black);
						
				}
				
				//draw the person oval in the simulation frame
				gDot1.fillOval(pDot1.x, pDot1.y, OvalW, OvalH);
				
				// draw the person oval in meter/bar indicator
				gDot1.fillOval((frameX-(int)(frameX*.02)), (int)(frameY-((numPeople-index)*OvalH)/1.67), OvalW, OvalH);
				index++;
				
			}
			JLabel parkLabel = new JLabel();
			parkLabel.setText("Park");

		}

		public void checkWallCollision(Person p) {
			Wall[] walls = {vWall1, hWall1, vWall2, hWall2, vWall3, hWall3, vWall4, hWall4};
			Rectangle[] r = {vWall1.getBounds(), hWall1.getBounds(), vWall2.getBounds(), hWall2.getBounds(),
					vWall3.getBounds(), hWall3.getBounds(), vWall4.getBounds(), hWall4.getBounds()};
			Rectangle rect1 = new Rectangle(p.x,p.y, p.width, p.height);
			for(int i = 0; i < walls.length;i++)
			{
				if(r[i].intersects(rect1))
					if(walls[i].vertical)
					{
						p.vx *= -1;
					}
					else
						p.vy *= -1;
			}
		}
		
}
