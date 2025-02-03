/*
 * Taryn Boonpongmanee
 * Pinball
 * Plays a game of pinball with 3 turns
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Pinball extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddles */
	private static final int PADDLE_WIDTH = 50;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 70;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Number of turns */
	private static final int NTURNS = 3;
	
/** Gap between left and right paddles */
	private static final int PADDLE_SPACE = 18;
	
/** Gap between paddles and bottom boundary */
	private static final int PADDLE_SPACER = 9; 

/** Bumper width and height */
	private static final int BUMPER_SIZE = 40; 
	
/** Spacing between bumpers in the same row */
	private static final int BUMPER_SPACER = 150;
	
/** Number of rows of bumpers in basic setup */
	private static final int NBUMPER_ROWS = 3; 
	
/** Number of bumpers in basic setup */
	private static final int NBUMPERS = 5; 
	
/** Bumper row offset, spacing between each row of bumpers*/
	private static final int BUMPER_OFFSET = ((HEIGHT-PADDLE_Y_OFFSET) - NBUMPER_ROWS*BUMPER_SIZE)/(NBUMPER_ROWS+1); 
	
/** bottom boundary width and height*/
	private static final int BOUNDARY_WIDTH = (WIDTH - (2*PADDLE_WIDTH + PADDLE_SPACE + 2*PADDLE_SPACER))/2; 
	private static final int BOUNDARY_HEIGHT = PADDLE_HEIGHT; 
	
	//declaring global variables
	GRect leftPaddle;
	GRect rightPaddle;
	
	GRect leftBoundary;
	GRect rightBoundary;
	
	boolean keyIsDown = false;
	
	GOval ball;	
	private double vx, vy;
	
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	
	int bumpersLeft = NBUMPERS;
	int numOfTurns = NTURNS;
	
	GRect clearPage;
	
/* Method: run() */
/** Runs the Pinball program. */

	public void run() {
		setUpGame();
		playGame();
	}
	
	//draws everything to set up the game
	public void setUpGame() {
		createBumpers();
		createPaddles();
		createBoundaries();
		addKeyListeners();
		makeBall();
	}
	
	//plays game and checks conditions to end it
	public void playGame() {
		//keep playing until out of turns or you hit all the bumpers
		while(numOfTurns > 0 && bumpersLeft > 0) {
			if(keyIsDown == false) {
				resetPaddles();
			}
			
			if(ball.getY() + 2*BALL_RADIUS < APPLICATION_HEIGHT) {
				moveBall();
			}
			
			//if the ball hits the bottom, reset
			if(ball.getY() + 2*BALL_RADIUS >= APPLICATION_HEIGHT) {
				resetGame();
			}
		}
		
		//if you run out of turns, you lose
		if(numOfTurns == 0) {
			loseGame();
		} else if(bumpersLeft == 0) {
			winGame();
		}
	}
	
	//draws bumpers
	public void createBumpers() {
		int bumperPositionWidth = APPLICATION_WIDTH - 300;
		int bumperPositionHeight = APPLICATION_HEIGHT - 500;
		
		for(int i = 0; i < 2; i++) {
			GRect blueBumper = new GRect(bumperPositionWidth + i*BUMPER_SPACER, bumperPositionHeight, BUMPER_SIZE, BUMPER_SIZE);
			blueBumper.setFilled(true);
			blueBumper.setColor(Color.blue);
			add(blueBumper);
		}
		
		GRect magentaBumper = new GRect(bumperPositionWidth + BUMPER_SPACER/2, bumperPositionHeight + BUMPER_OFFSET, BUMPER_SIZE, BUMPER_SIZE);
		magentaBumper.setFilled(true);
		magentaBumper.setColor(Color.magenta);
		add(magentaBumper);
		
		for(int i = 0; i < 2; i++) {
			GRect pinkBumper = new GRect(bumperPositionWidth + i*BUMPER_SPACER, bumperPositionHeight + 2*BUMPER_OFFSET, BUMPER_SIZE, BUMPER_SIZE);
			pinkBumper.setFilled(true);
			pinkBumper.setColor(Color.pink);
			add(pinkBumper);
		}
	}
	
	//draws boundaries
	public void createBoundaries() {
		leftBoundary = new GRect(0, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, BOUNDARY_WIDTH, BOUNDARY_HEIGHT);
		leftBoundary.setFilled(true);
		leftBoundary.setColor(Color.black);
		add(leftBoundary);
		
		rightBoundary = new GRect( APPLICATION_WIDTH - BOUNDARY_WIDTH, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, BOUNDARY_WIDTH, BOUNDARY_HEIGHT);
		rightBoundary.setFilled(true);
		rightBoundary.setColor(Color.black);
		add(rightBoundary);
	}
	
	//draws paddles
	public void createPaddles() {
		leftPaddle = new GRect(BOUNDARY_WIDTH + PADDLE_SPACER, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		leftPaddle.setFilled(true);
		leftPaddle.setColor(Color.black);
		add(leftPaddle);
			
		rightPaddle = new GRect(BOUNDARY_WIDTH + PADDLE_SPACER + PADDLE_WIDTH + PADDLE_SPACE, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		rightPaddle.setFilled(true);
		rightPaddle.setColor(Color.black);
		add(rightPaddle);
	}
	
	//moves paddles
	public void keyPressed(KeyEvent e) {
		leftPaddle.setBounds(BOUNDARY_WIDTH + PADDLE_SPACER, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_HEIGHT, PADDLE_WIDTH);
		rightPaddle.setBounds(BOUNDARY_WIDTH + PADDLE_SPACER + 2*PADDLE_WIDTH + PADDLE_SPACE - PADDLE_HEIGHT, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_HEIGHT, PADDLE_WIDTH);
		keyIsDown = true;
	}
	
	//condition to move paddles back
	public void keyReleased(KeyEvent e) {
		keyIsDown = false;
	}
	
	//moves paddles back
	public void resetPaddles() {
		leftPaddle.setBounds(BOUNDARY_WIDTH + PADDLE_SPACER, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		rightPaddle.setBounds(BOUNDARY_WIDTH + PADDLE_SPACER + PADDLE_WIDTH + PADDLE_SPACE, APPLICATION_HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
	}
	
	//creates ball
	public void makeBall() {
		ball = new GOval(40, APPLICATION_HEIGHT - PADDLE_Y_OFFSET - 25, BALL_RADIUS + 10, BALL_RADIUS + 10);
		ball.setFilled(true);
		ball.setColor(Color.cyan);
		add(ball);
		
		vy = -3.0;
		
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;
		}
	
	//moves ball
	public void moveBall() {
		
		double rightOfBall = ball.getX() + 2*BALL_RADIUS;
		double leftOfBall = ball.getX();
		
		double topOfBall = ball.getY();
		double bottomOfBall = ball.getY() + 2* BALL_RADIUS;
			
		if(topOfBall < 0) {
			vy = -vy;
		} else if(bottomOfBall > APPLICATION_HEIGHT) {
			resetGame();
		}
		
		if(rightOfBall > APPLICATION_WIDTH) {
			vx = -vx;
		} else if(leftOfBall < 0) {
			vx = -vx;
		}
		
		ballCollides(rightOfBall, leftOfBall, topOfBall, bottomOfBall);
		
		ball.move(vx ,vy);
		ball.pause(20);
	}
	
	//checks for collisions
	public void ballCollides(double rightOfBall, double leftOfBall, double topOfBall, double bottomOfBall) {
		GObject collider = getCollidingObject(); 
		if(collider != null) {
			if(collider == leftPaddle || collider == rightPaddle || collider == leftBoundary || collider == rightBoundary) {
				vy = -3.0;
			} else {
				if(topOfBall >= collider.getY() + BUMPER_SIZE/2) {
					vy = -vy;
				}
				
				if(rightOfBall <= collider.getX() + BUMPER_SIZE/2) {
					vx = -vx;
				}
				
				if(leftOfBall >= collider.getX() - BUMPER_SIZE/2) {
					vx = -vx;
				}

				if(bottomOfBall - 20 <= collider.getY()) {
					vy = -vy;
				}
				
				if(collider.getColor() == Color.orange) {
					removeBumper(collider, rightOfBall,  leftOfBall, bottomOfBall, topOfBall);
				} else {
					setBumperColor(collider);
				}
			}
		}
	}
	
	public void setBumperColor(GObject collider) {
		if(collider.getColor() == Color.blue) {
			collider.setColor(Color.red);
		} else if(collider.getColor() == Color.red) {
			collider.setColor(Color.magenta);
		} else if (collider.getColor() == Color.magenta) {
			collider.setColor(Color.yellow);
		} else if(collider.getColor() == Color.yellow) {
			collider.setColor(Color.pink);
		} else if(collider.getColor() == Color.pink) {
			collider.setColor(Color.orange);
		}
	}
	
	//removes bumper if it collides
	public void removeBumper(GObject collider, double rightOfBall, double leftOfBall, double bottomOfBall, double topOfBall) {
		if(collider.getColor() == Color.orange) {
			remove(collider);
			bumpersLeft--;
		}
	}

	//if ball collides make it bounce
	private GObject getCollidingObject() {
		GObject topLeft = getElementAt(ball.getX(), ball.getY());
		if(topLeft != null) {
			return topLeft;
		}
		
		GObject topRight = getElementAt(ball.getX()+2*BALL_RADIUS, ball.getY());
		if(topRight != null) {
			return topRight;
		}
		
		GObject bottomLeft = getElementAt(ball.getX(), ball.getY()+ 2*BALL_RADIUS);
		if(bottomLeft != null) {
			return bottomLeft;
		}
		
		GObject bottomRight = getElementAt(ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS);
		if(bottomRight != null) {
			return bottomRight;
		}
		return null;
	}
	
	//win game, set up screen
	public void winGame() {
		clearBoard();
		GLabel winText = new GLabel("You Win! Game Over.", APPLICATION_WIDTH/2 - 50, APPLICATION_HEIGHT/2);
		add(winText);
	}
	
	//lose game, set up screen
	public void loseGame() {
		clearBoard();
		GLabel loseText = new GLabel("You Lost! Game Over. ", APPLICATION_WIDTH/2 - 50, APPLICATION_HEIGHT/2);
		add(loseText);
	}
	
	//reset game
	public void resetGame() {
		clearBoard();
		numOfTurns = numOfTurns -1;
		remove(ball);
		GLabel resetText = new GLabel("Reset Game. Number of Turns Left: " + numOfTurns, APPLICATION_WIDTH/2 - 70, APPLICATION_HEIGHT/2);
		add(resetText);
		pause(5000);
		remove(resetText);
		remove(clearPage);
		setUpGame();
	}
	
	//create a new blank page to reset
	public void clearBoard() {
		clearPage = new GRect(0,0, APPLICATION_WIDTH, APPLICATION_HEIGHT);
		clearPage.setFilled(true);
		clearPage.setColor(Color.white);
		add(clearPage);
	}
}
