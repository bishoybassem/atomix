import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SolutionPanel extends JPanel {

	private int[][] solution;
	private Element[] elements;

	public SolutionPanel(int[][] solution, Element[] elements) {
		this.solution = solution;
		this.elements = elements;
		setBackground(new Color(255, 255, 255));
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(Element.SIDE * (solution[0].length) + 30, Element.SIDE * (solution.length) + 30);
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < solution.length; i++) {
			for (int j = 0; j < solution[0].length; j++) {
				if (solution[i][j] != 0) {
					g.drawImage((elements[solution[i][j]]).getImage(), j * Element.SIDE + 15, i * Element.SIDE + 15, null);
				}
			}
		}
	}

}