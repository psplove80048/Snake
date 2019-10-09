import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Yard extends Frame {

	PaintThread paintThread = new PaintThread();
	private boolean gameOver = false;
	
	public static final int ROWS = 30;
	public static final int COLS = 30;
	public static final int BLOCK_SIZE = 15;
	
	private Color color = Color.GREEN;
	
	private Font fontGameOver = new Font(Font.DIALOG, Font.BOLD, 50);
	private int score = 0;
	
	Snake s = new Snake(this);
	Egg e = new Egg();
	Image offScreenImage = null;
	
	public int getScore() {
		return score;
	}
    public void setScore(int score) {
		this.score = score;
	}
	
	public void launch() {
		this.setLocation(200, 200);
		this.setSize(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setVisible(true);
		this.addKeyListener(new KeyMonitor());
		new Thread(paintThread).start();
	}
	
	public static void main(String[] args) {
		new  Yard().launch();
	}
	
	public void stop() {
		gameOver = true;
	}
	
	
	
	
	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE );
		}
		Graphics gOff = offScreenImage.getGraphics();
		Color c = gOff.getColor();
		gOff.setColor(Color.GRAY);
		gOff.fillRect(0, 0, COLS * BLOCK_SIZE, ROWS * BLOCK_SIZE);
		gOff.setColor(Color.DARK_GRAY);
		
		for(int i=1; i<ROWS; i++) {
			gOff.drawLine(0, BLOCK_SIZE * i, COLS * BLOCK_SIZE, BLOCK_SIZE * i);
		}
		for(int i=1; i<COLS; i++) {
			gOff.drawLine(BLOCK_SIZE * i, 0, BLOCK_SIZE * i, BLOCK_SIZE * ROWS);
		}
		
		gOff.setColor(Color.YELLOW);
		gOff.drawString("score:" + score, 10, 60);
		if(gameOver) {
			gOff.setFont(fontGameOver);
			gOff.drawString("GAME OVER", 120, 180);
			
			paintThread.pause();
		}
		gOff.setColor(c);
		s.eat(e);
		
		
		c = gOff.getColor();
		gOff.setColor(color);
		gOff.fillOval(Yard.BLOCK_SIZE * e.col, Yard.BLOCK_SIZE * e.row, e.w, e.h);  
		gOff.setColor(c);
		if(color == Color.GREEN) color = Color.RED;
		else color = Color.GREEN;
		s.draw(gOff);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	private class PaintThread implements Runnable {

		private boolean pause = false;
		private boolean running = true;
		public void run() {
			while(running) {
				if(pause) continue;
				else repaint();
				try {
					Thread.sleep(200);		
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void pause() {
			this.pause = true;
		}
		
		
	}
	
	private class KeyMonitor extends KeyAdapter {
		
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_F2) {
				paintThread.pause = false;
				s = new Snake(Yard.this);
				gameOver = false;
			}
			
			s.keyPressed(e);
		}	
	}
}
