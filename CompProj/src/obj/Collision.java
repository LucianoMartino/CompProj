package obj;

import java.awt.Graphics2D;

public class Collision {
	public int x, y, width, height;

	public Collision(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Collision() {

	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean doesOverlap(Collision other) {
		if (x < other.x + other.width && x + width > other.x) {
			if (y < other.y + other.height && y + height > other.y) {
				return true;
			}
		}
		return false;
	}

	// debug stuff
	public void draw(Graphics2D g) {
		g.drawRect(x, y, width, height);
	}
}
