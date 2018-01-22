package obj;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;

import game.*;

public class Player {
	private Game game;
	public BufferedImage playerImg;

	private static final int CAMERA_PADDING_X = 100;
	private static final int CAMERA_PADDING_Y = 100;

	private double x, y;
	private double midX, midY;
	private double speed = 5, rotation = 0;

	public double getMidX() {
		return midX;
	}

	public double getMidY() {
		return midY;
	}

	public double getX(double x) {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY(double y) {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	private Collision bounds = new Collision(0, 0, 0, 0);

	public Player(Game game, int x, int y) {
		this.game = game;
		playerImg = Util.loading("/art/sPlayer.png");

		game.setCamera(x - Game.WIDTH / 2, y - Game.HEIGHT / 2);

		this.x = x;
		this.y = y;
	}

	private double[] doControlledMovement() {
		double pMoveX = 0, pMoveY = 0;
		double xDir = 0, yDir = 0;
		if(!game.menuMode){
			if (game.isKeyDown(KeyEvent.VK_W)) {
				yDir -= 1;
			}
			if (game.isKeyDown(KeyEvent.VK_S)) {
				yDir += 1;
			}
			if (game.isKeyDown(KeyEvent.VK_A)) {
				xDir -= 1;
			}
			if (game.isKeyDown(KeyEvent.VK_D)) {
				xDir += 1;
			}
		}
		if (xDir != 0 || yDir != 0) {
			double len = Math.sqrt(xDir * xDir + yDir * yDir);
			xDir /= len;
			yDir /= len;
			pMoveX = xDir * speed;
			pMoveY = yDir * speed;
		}
		return new double[] { pMoveX, pMoveY };
	}

	private boolean[] doCollisions(double pMoveX, double pMoveY) {
		boolean shouldAddX = true, shouldAddY = true;
			
		if(x + pMoveX < 0){
			x = 0; 
			shouldAddX = false;
		
		}else if (x + pMoveX > Game.MAP_WIDTH * Wall.WALL_SIZE - playerImg.getWidth() ){
			x = Game.MAP_WIDTH * Wall.WALL_SIZE - playerImg.getWidth();
			shouldAddX = false;
		}
			
		if(y + pMoveY < 0){
			y = 0;
			shouldAddY = false;
		
		}else if ( y + pMoveY > Game.MAP_HEIGHT * Wall.WALL_SIZE - playerImg.getWidth()){
			y = Game.MAP_HEIGHT * Wall.WALL_SIZE;
			shouldAddY = false;
		}
			
		if(shouldAddX && shouldAddY) {
			Wall[][] nearby = Game.getNearbyWalls(midX, midY);
			Collision xTester = new Collision((int) (x + pMoveX), (int) y, playerImg.getWidth(), playerImg.getHeight());
			Collision yTester = new Collision((int) x, (int) (y + pMoveY), playerImg.getWidth(), playerImg.getHeight());
	
			for (Wall[] walls : nearby) {
				for (Wall notWalls : walls) {
					if (notWalls == null)
						continue;
					Collision wallCollision = notWalls.getCollision();
	
					if (xTester.doesOverlap(wallCollision))
						shouldAddX = false;
					if (yTester.doesOverlap(wallCollision))
						shouldAddY = false;
	
				}
			}
		}
		return new boolean[] { shouldAddX, shouldAddY };
	}

	public void tick() {
		double[] pMove = doControlledMovement();
		double pMoveX = pMove[0];
		double pMoveY = pMove[1];

		midX = x + playerImg.getWidth() / 2;
		midY = y + playerImg.getHeight() / 2;

		double[] cam = Game.getCamPos();
		double camX = cam[0];
		double camY = cam[1];

		if (midX > camX + Game.WIDTH - CAMERA_PADDING_X && camX < Game.MAP_WIDTH * Wall.WALL_SIZE - Game.WIDTH)
			camX += Math.abs(pMoveX);
		else if (midX < camX + CAMERA_PADDING_X && camX > 0)
			camX -= Math.abs(pMoveX);

		if (midY > camY + Game.HEIGHT - CAMERA_PADDING_Y && camY < Game.MAP_HEIGHT * Wall.WALL_SIZE - Game.HEIGHT)
			camY += Math.abs(pMoveY);
		else if (midY < camY + CAMERA_PADDING_Y && camY > 0)
			camY -= Math.abs(pMoveY);

		game.setCamera(camX, camY);

		double vecToMouseX = camX + Game.getMouseX() - midX;
		double vecToMouseY = camY + Game.getMouseY() - midY;

		rotation = Util.getAngle(vecToMouseX, vecToMouseY);

		boolean[] shouldAdd = doCollisions(pMoveX, pMoveY);
		boolean shouldAddX = shouldAdd[0];
		boolean shouldAddY = shouldAdd[1];

		if (shouldAddX)
			x += pMoveX;
		if (shouldAddY)
			y += pMoveY;

		bounds.setBounds((int) x + 5, (int) y + 5, playerImg.getWidth() - 10, playerImg.getHeight() - 10);
	}

	public void render(Graphics2D g) {
		AffineTransform beforeRotation = g.getTransform();
		g.rotate(rotation, (int) x + playerImg.getWidth() / 2, (int) y + playerImg.getHeight() / 2);
		g.drawImage(playerImg, (int) x, (int) y, null);
		g.setTransform(beforeRotation);

		double[] cam = Game.getCamPos();
		double camX = cam[0];
		double camY = cam[1];

		int mouseX = Game.getMouseX() + (int) camX;
		int mouseY = Game.getMouseY() + (int) camY;

		g.setColor(Color.YELLOW);
		g.fillRect(mouseX - 2, mouseY - 2, 4, 4);
	}
	
	public Collision getBounds() {

		return bounds;

	}
}
