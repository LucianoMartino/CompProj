package game;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.util.*;
import obj.*;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	private volatile boolean running = true;

	private static Frame frame;
	private static GraphicsEnvironment ge;

	public static int WIDTH, HEIGHT;
	private ArrayList<Integer> currentDownKeyCodes = new ArrayList<Integer>();
	private static int mouseX = 0, mouseY = 0;

	private boolean hasFocus = true, escapeStatus = false;
	private static boolean FULLSCREEN = true;
	public boolean menuMode = false;
	private boolean showingFocusMessage = false;
	private int time = 0;

	public static final int MAP_WIDTH = 15, MAP_HEIGHT = 9;
	private static Wall[][] walls;
	public Player player;
	//private Bullet bullet = new Bullet(Game.this, 0, 0);
	//private Bait bait = new Bait(Game.this, 0, 0);
	public static double camX = 0, camY = 0;

	private ArrayList<Projectile> proj = new ArrayList<Projectile>();
	private ArrayList<Projectile> projsToAdd = new ArrayList<Projectile>();

	private BufferedImage backgroundImg;
	// private BufferedImage tintImg;

	public void addObjects() {
		player = new Player(this, (MAP_WIDTH * Wall.WALL_SIZE) / 2, (MAP_HEIGHT * Wall.WALL_SIZE) / 2);
		createMap();

		backgroundImg = Util.loading("/art/sBackground.png");
		// tintImg = Util.loading("/art/sTint.png");
	}

	public void setCamera(double x, double y) {
		camX = x;
		camY = y;
	}

	public static double[] getCamPos() {
		return new double[] { camX, camY };
	}

	public static Wall[][] getNearbyWalls(double xPix, double yPix) {
		final int NWALL_SIZE = 6; 													//A nearby wall is a wall within the radius of NWALL_SIZE/2
		Wall[][] nearby = new Wall[NWALL_SIZE][NWALL_SIZE];

		int sx = (int) (Math.round(xPix / (double) Wall.WALL_SIZE)) - NWALL_SIZE / 2;
		int sy = (int) (Math.round(yPix / (double) Wall.WALL_SIZE)) - NWALL_SIZE / 2;

		for (int x = 0; x < NWALL_SIZE; x++) {
			for (int y = 0; y < NWALL_SIZE; y++) {
				int xWall = x + sx;
				int yWall = y + sy;

				if (xWall < MAP_WIDTH && xWall >= 0 && yWall < MAP_HEIGHT && yWall >= 0) {
					nearby[y][x] = walls[yWall][xWall];
				}
			}
		}
		return nearby;
	}

	public void init() {
		setFocusable(true);
		requestFocusInWindow();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int kc = e.getKeyCode();
				if (!isKeyDown(kc))
					currentDownKeyCodes.add(kc);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				currentDownKeyCodes.remove(new Integer(e.getKeyCode()));
			}
		});

		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				hasFocus = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				currentDownKeyCodes.clear();
				hasFocus = false;
			}
		});
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1)
						createProjectile(1);
					else if (e.getButton() == MouseEvent.BUTTON3)
						createProjectile(2);
					else if(e.getButton() == MouseEvent.BUTTON2)
						FULLSCREEN = !FULLSCREEN;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		addObjects();
	}

	public static int getMouseX() {
		return mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public boolean isKeyDown(int kc) {
		return currentDownKeyCodes.contains(kc);
	}

	public void createProjectile(int x) {
		if(!menuMode){
			synchronized(projsToAdd){
				if(x == 1){
					projsToAdd.add(new Bullet(Game.this,
							(player.playerImg.getWidth() / 2) * Math.cos(player.getRotation()) + player.getMidX() - Bullet.getWidth() / 2,
							(player.playerImg.getHeight() / 2) * Math.sin(player.getRotation()) + player.getMidY() - Bullet.getHeight() / 2));
				}else{
					projsToAdd.add(new Bait(Game.this,
							(player.playerImg.getWidth() / 2) * Math.cos(player.getRotation()) + player.getMidX() - Bullet.getWidth() / 2,
							(player.playerImg.getHeight() / 2) * Math.sin(player.getRotation()) + player.getMidY() - Bullet.getHeight() / 2));
				}
			}
		// ADD KNOCKBACK
		}
	}

	public static void main(String[] args) {
		final Game game = new Game();

		/* GraphicsEnvironment */ ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsCard = ge.getDefaultScreenDevice();
		DisplayMode dm = graphicsCard.getDisplayMode();

		final Thread thread = new Thread(game);
		/* Frame */ frame = new Frame("Comp Proj");
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				game.running = false;
				try {
					thread.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				frame.dispose();
			}
		});
		frame.add(game);
		
		if (!graphicsCard.isFullScreenSupported() || !FULLSCREEN) {
			WIDTH = 800;
			HEIGHT = 600;
			game.setSize(WIDTH, HEIGHT);
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
		} else {
			WIDTH = dm.getWidth();
			HEIGHT = dm.getHeight();
			frame.setUndecorated(true);
			graphicsCard.setFullScreenWindow(frame);
		}
		
		frame.setVisible(true);

		thread.start();
	}

	public void tick() {
		synchronized(projsToAdd){
			for(Projectile p : projsToAdd){
				proj.add(p);
			}
			
			projsToAdd.clear();
		}
		
		time++;
		
		if (this.isKeyDown(KeyEvent.VK_ESCAPE) != escapeStatus && escapeStatus == true)
			menuMode = !menuMode;
		
		escapeStatus = this.isKeyDown(KeyEvent.VK_ESCAPE);
		
		if (this.isKeyDown(KeyEvent.VK_ENTER))
				System.exit(0);
		
		if (time % 50 == 0)
			showingFocusMessage = !showingFocusMessage;

		player.tick();

		ArrayList<Projectile> toRemove = new ArrayList<Projectile>();

		for (Projectile p: proj) {
			if (p != null) {
				if (p.shouldRemove()) {
					toRemove.add(p);////////////////////////////////////////////////////////////////////////////////////////////////////
				}
				p.tick();
			}
		}
		
		proj.removeAll(toRemove);
		toRemove.clear();
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		(g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setColor(Color.CYAN);
		g.fillRect(MAP_WIDTH*Wall.WALL_SIZE-1280, 0, 1280, 1280);
		
		g.setColor(Color.DARK_GRAY);
		g.drawImage(backgroundImg, 0, 0, null);
		

		/* Random rand = new Random(); for(int i = 0; i < 5; i++){ int x =
		 * rand.nextInt(WIDTH-1)+1; int y = rand.nextInt(HEIGHT-1)+1;
		 * g.drawImage(tintImg,x,y,null);
		 * g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		 * 0.5f)); g.setColor(new
		 * Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256),256));
		 * g.fillRect(x, y, 512, 512); }
		 */

		AffineTransform old = g.getTransform();
		g.translate(-camX, -camY);

		player.render(g);

		//Util.debugPrint(""+proj.size());
		
		for (Projectile p : proj) {
			if (p != null) {
				p.render(g);
			}
		}

		for (int y = 0; y < MAP_HEIGHT; y++) {
			for (int x = 0; x < MAP_WIDTH; x++) {
				if (walls[y][x] != null)
					walls[y][x].render(g);
			}
		}
		// affected ^
		g.setTransform(old);
		// not affected v
		drawNoFocus(g);
		drawMenu(g);
		g.dispose();
		bs.show();
	}

	public void drawNoFocus(Graphics g) {
		if (!hasFocus && !menuMode) {
			g.setColor(new Color(0, 0, 0, 120));
			g.fillRect(0, 0, WIDTH, HEIGHT);

			if (showingFocusMessage) {
				g.setFont(new Font("Consolas", Font.BOLD, 48));
				String msg = "Click the screen to resume!";
				FontMetrics fm = g.getFontMetrics();
				int sw = fm.stringWidth(msg);

				int xOff = WIDTH / 2 - sw / 2;

				g.setColor(Color.DARK_GRAY);
				g.fillRect(xOff - 10 + 4, HEIGHT / 2 - 40 + 4, sw + 20, 80);
				g.setColor(Color.GRAY);
				g.fillRect(xOff - 10, HEIGHT / 2 - 40, sw + 20, 80);

				g.setColor(Color.WHITE.darker().darker().darker());
				g.drawString(msg, xOff + 4, HEIGHT / 2 + fm.getAscent() / 2 + 4);
				g.setColor(Color.WHITE);
				g.drawString(msg, xOff, HEIGHT / 2 + fm.getAscent() / 2);
			}
		}
	}
	
	public void drawMenu(Graphics g) {
		if (menuMode) {
			g.setColor(new Color(0, 0, 0, 100));
			
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			g.setFont(new Font("Consolas", Font.BOLD, (int)WIDTH*HEIGHT/15000));
			String[] msgs = {"New Game","Tutorial", (!FULLSCREEN ? "FullScreen?" : "Windowed?"), "Exit"};
			FontMetrics fm = g.getFontMetrics();
			
			for(int i = 0; i < msgs.length; i++){
				int stringWidth = fm.stringWidth(msgs[i]), stringHeight = fm.getAscent();
				
				int xOff = (int)(WIDTH / 2 - stringWidth / 2);

				g.setColor(Color.DARK_GRAY);
				g.fillRect(xOff - 5, HEIGHT / 2 + (i-2) * (stringHeight + 45) - 40, stringWidth + 20, stringHeight + 25);
				g.setColor(Color.GRAY);
				g.fillRect(xOff - 10, HEIGHT / 2 + (i-2) * (stringHeight + 45) - 45, stringWidth + 20, stringHeight + 25);

				g.setColor(Color.WHITE.darker().darker().darker());
				g.drawString(msgs[i], xOff + 4, HEIGHT / 2 + (i-1) * (stringHeight + 45) - 85);
				g.setColor(Color.WHITE);
				g.drawString(msgs[i], xOff, HEIGHT / 2 + (i-1) * (stringHeight + 45) - 90);
			}
		}
	}

	public void createMap() {
		Random wallRand = new Random();
		walls = new Wall[MAP_HEIGHT][MAP_WIDTH];
		for (int y = 0; y < MAP_HEIGHT; y++) {
			for (int x = 0; x < MAP_WIDTH; x++) {
				if (x == MAP_WIDTH / 2 && y == MAP_HEIGHT / 2)
					walls[y][x] = null;
				else {
					if (wallRand.nextInt(100) <= 8)
						walls[y][x] = new Wall(x, y);
					else
						walls[y][x] = null;
				}
			}
		}
	}

	@Override
	public void run() {
		init();
		final double nsPerTick = 1000000000 / 60;
		long lastTime, now, sec;
		lastTime = sec = System.nanoTime();
		double delta = 0;

		int ticks = 0, frames = 0;
		boolean shouldRender;

		while (running) {
			now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			shouldRender = false;

			while (delta >= 1) {
				tick();
				ticks++;
				delta -= 1;
				shouldRender = true;
			}

			if (shouldRender) {
				render();
				frames++;
			}

			if (now - sec >= 1000000000) {
				System.out.printf("%d ticks, %d frames\n", ticks, frames);
				ticks = 0;
				frames = 0;
				sec = now;
			}

		}
	}

}