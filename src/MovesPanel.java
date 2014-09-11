import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MovesPanel extends JPanel {

	private int moves;

	public MovesPanel() {
		setOpaque(false);
	}
	
	public void increment() {
		moves++;
		repaint();
	}
	
	public void decrement() {
		moves--;
		repaint();
	}
	
	public void reset() {
		moves = 0;
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(114, 40);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2d.setColor(Color.BLACK);
		FontMetrics fm =  g2d.getFontMetrics();
		int x = (114 - fm.stringWidth("Moves  :  " + moves)) / 2;
		int y = (36 + fm.getAscent()) / 2;
		g2d.drawString("Moves  :  " + moves, x, y);
	}
	
}