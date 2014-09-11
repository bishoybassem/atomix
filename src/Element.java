import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Element {
	
	private String name;
	private String[][] bonds;
	private BufferedImage image;
	
	public static final int side;
	private static final HashMap<String, Color> colorMap;
	public static final BufferedImage wall;
	public static final BufferedImage arrowUp;
	public static final BufferedImage arrowDown;
	public static final BufferedImage arrowLeft;
	public static final BufferedImage arrowRight;
	private static final BufferedImage bond1;
	private static final BufferedImage bond2;
	
	static {
		side = (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 150) / 155) * 10;
		
		colorMap = new HashMap<String, Color>();
		colorMap.put("br", new Color(204, 118, 95));
		colorMap.put("c", new Color(92, 155, 209));
		colorMap.put("cl", new Color(151, 151, 151));
		colorMap.put("cr", new Color(92, 155, 209));
		colorMap.put("f", new Color(209, 92, 174));
		colorMap.put("h", new Color(132, 215, 101));
		colorMap.put("n", new Color(217, 212, 84));
		colorMap.put("o", new Color(209, 92, 92));
		colorMap.put("p", new Color(129, 95, 205));
		colorMap.put("s", new Color(209, 166, 92));
		
		bond1 = new BufferedImage(3 * side / 4, side / 5, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) bond1.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, bond1.getHeight() + 1, new Color(141, 130, 130)));
		g2d.fillRect(0, 0, bond1.getWidth(), bond1.getHeight());
		g2d.setColor(new Color(141, 130, 130).brighter());
		g2d.drawLine(0, 0, bond1.getWidth(), 0);
		
		bond2 = new BufferedImage(3 * side / 4, side / 5, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) bond2.getGraphics();
		g2d.rotate(Math.PI, bond2.getWidth() / 2.0, bond2.getHeight() / 2.0);
		g2d.drawImage(bond1, 0, 0, null);
		
		wall = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) wall.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(new GradientPaint(0, 0, new Color(236, 228, 176), side, side, new Color(194, 172, 127)));
		g2d.fillRect(0, 0, side, side);
		g2d.setStroke(new BasicStroke(2f));
		g2d.setColor(new Color(194, 172, 127).darker());
		g2d.draw(new Rectangle2D.Double(0.5, 0.5, side - 1.5, side - 1.5));
		
		arrowUp = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) arrowUp.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Polygon poly = new Polygon(new int[]{1, side - 1, side / 2}, new int[]{1, 1, side / 2}, 3);
		g2d.setPaint(new GradientPaint(0, 1, Color.WHITE, 0, side / 2, Color.YELLOW));
		g2d.fillPolygon(poly);
		g2d.setStroke(new BasicStroke(2f));
		g2d.setPaint(new GradientPaint(0, 1, Color.WHITE, 0, side / 2, Color.YELLOW.darker()));
		g2d.drawPolygon(poly);
		
		arrowDown = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) arrowDown.getGraphics();
		g2d.rotate(Math.PI, side / 2.0, side / 2.0);
		g2d.drawImage(arrowUp, 0, 0, null);
		
		arrowLeft = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) arrowLeft.getGraphics();
		g2d.rotate(Math.PI / 2, side / 2.0, side / 2.0);
		g2d.drawImage(arrowUp, 0, 0, null);
		
		arrowRight = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		g2d = (Graphics2D) arrowRight.getGraphics();
		g2d.rotate(3 * Math.PI / 2, side / 2.0, side / 2.0);
		g2d.drawImage(arrowUp, 0, 0, null);
	}
	
	public Element(String data) {
		String[] s = data.split(" ");
		name = s[0];
		bonds = new String[s.length - 1][2];
		for (int i = 1; i < s.length; i++) {
			bonds[i - 1] = new String[]{s[i].charAt(0) + "", s[i].substring(1)};
		}
		image = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
		drawAllBonds();
		if (!name.equals("b")) {
			drawElement();
		}
	}
	
	private void drawAllBonds() {
		for (int i = 0; i < bonds.length; i++) {
			int n = Integer.parseInt(bonds[i][0]);
			if (bonds[i][1].equals("R")) {
				drawBond(n, 0, false);
			} else if (bonds[i][1].equals("L")) {
				drawBond(n, Math.PI, true);
			} else if (bonds[i][1].equals("D")) {
				drawBond(n, Math.PI / 2, false);
			} else if (bonds[i][1].equals("U")) {
				drawBond(n, - Math.PI / 2, true);
			} else if (bonds[i][1].equals("LU")) {
				drawBond(n, -0.75 * Math.PI, true);
			} else if (bonds[i][1].equals("RD")) {
				drawBond(n, 0.25 * Math.PI, false);
			} else if (bonds[i][1].equals("RU")) {
				drawBond(n, -0.25 * Math.PI, false);
			} else if (bonds[i][1].equals("LD")) {
				drawBond(n, 0.75 * Math.PI, true);
			}
		}
	}
	
	private void drawBond(int n, double angle, boolean flipVertically) {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.rotate(angle, side / 2, side / 2);
		int y = (int)(side - n * bond1.getHeight()) / 2;
		for (int j = 0; j < n; j++) {
			if (flipVertically) {
				g.drawImage(bond2, side / 2, y, null);
			} else {
				g.drawImage(bond1, side / 2, y, null);
			}
			y += bond1.getHeight();
		}
		g.rotate(-angle, side / 2, side / 2);
	}
	
	private void drawElement() {
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		Path2D p = new Path2D.Double();
		p.moveTo(side / 2, 0.1 * side);
		p.lineTo(0.9 * side, side / 2);
		p.lineTo(side / 2, 0.9 * side);
		p.lineTo(0.1 * side, side / 2);
		p.closePath();
		
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.setPaint(new RadialGradientPaint(side / 2, 3 * side / 8, 3 * side / 8, new float[]{0, 1f}, new Color[]{Color.WHITE, colorMap.get(name)}));
		g2d.fill(p);
		g2d.setColor(colorMap.get(name).darker());
		g2d.draw(p);
		
		String captName = name.equals("cr")? "" : name.toUpperCase().charAt(0) + name.substring(1);
		g2d.setFont(new Font("Arial", Font.BOLD, 3 * side / 8));
		FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(captName, g2d);
        float x = (float)(side - r.getWidth()) / 2;
        float y = (float)(side - r.getHeight()) / 2 + fm.getAscent();
        
        g2d.setColor(Color.BLACK);
        g2d.drawString(captName, x, y);
	}
	
	public BufferedImage getImage() {
		return image;
	}
		
}
