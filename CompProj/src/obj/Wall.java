package obj;

import java.awt.*;
import java.awt.image.*;
import game.*;

public class Wall {
	public static final int WALL_SIZE = 128;
	private final int x, y;
	private BufferedImage wallImg = null;
	private final Collision wallCollision;

	public Wall(int blockX, int blockY) {

		if (wallImg == null) {
			wallImg = Util.loading("/art/sWall.png");
		}

		x = blockX * WALL_SIZE;
		y = blockY * WALL_SIZE;

		wallCollision = new Collision(x, y, WALL_SIZE, WALL_SIZE);
	}

	public Collision getCollision() {
		return wallCollision;
	}

	public void render(Graphics2D g) {
		g.drawImage(wallImg, x, y, WALL_SIZE, WALL_SIZE, null);
	}
}
