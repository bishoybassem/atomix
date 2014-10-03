import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class LevelPanel extends JPanel {

	private static AudioClip restartAC;
	
	static {
		try {
			restartAC = Applet.newAudioClip(BoardPanel.class.getResource("resources/restart.wav"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public LevelPanel(int level) {
		String levelText = Atomix.readTextFile("levels/Level" + level + ".txt");
		String[] s = levelText.split("\n\n");	
		
		int[][] board = toArray(s[0]);
		int[][] solution = toArray(s[1]);
		String[] atoms = s[2].split("\n");
		Element[] elements = new Element[atoms.length + 1];
		for (int i = 0; i < atoms.length; i++) {
			elements[i + 1] = new Element(atoms[i].substring(atoms[i].indexOf(" ") + 1));
		}
		
		final BoardPanel boardPanel = new BoardPanel(board, solution, elements);
		
		SolutionPanel solutionPanel = new SolutionPanel(solution, elements);
		solutionPanel.setBorder(new LineBorder(new Color(100, 150, 200), 1));
				
		JButton undo = new JButton("Undo");
		undo.setFocusable(false);
		undo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boardPanel.traverseMovements(true);
			}
			
		});
		
		JButton redo = new JButton("Redo");
		redo.setFocusable(false);
		redo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boardPanel.traverseMovements(false);
			}
			
		});
		
		JButton reset = new JButton("Reset Level");
		reset.setFocusable(false);
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boardPanel.reset();
				restartAC.play();
			}
			
		});
		
		JButton howToPlay = new JButton("How To Play");
		howToPlay.setFocusable(false);
		howToPlay.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Atomix.howToPlayDialog.setVisible(true);
			}
			
		});
		
		JButton about = new JButton("About");
		about.setFocusable(false);
		about.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Atomix.aboutDialog.setVisible(true);
			}
			
		});
		
		JButton exit = new JButton("Exit");
		exit.setFocusable(false);
		exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p1.add(undo);
		p1.add(boardPanel.getMovesPanel());
		p1.add(redo);
		p1.setOpaque(false);
		
		JPanel p2 = new JPanel(new GridLayout(5, 1, 0, 5));
		p2.add(p1);
		p2.add(reset);
		p2.add(howToPlay);
		p2.add(about);
		p2.add(exit);
		p2.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(100, 150, 200), 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		p2.setBackground(Color.WHITE);
		
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p3.setOpaque(false);
		p3.add(p2);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.add(new JLabel(new ImageIcon(getClass().getResource("resources/atomix.png"))));
		
		JPanel p4 = new JPanel();
		p4.setOpaque(false);
		p4.setLayout(new BoxLayout(p4, BoxLayout.PAGE_AXIS));
		p4.add(solutionPanel);
		p4.add(Box.createRigidArea(new Dimension(0, 5)));
		p4.add(titlePanel);
		p4.add(Box.createRigidArea(new Dimension(0, 5)));
		p4.add(p3);

		JPanel p5 = new JPanel(new GridBagLayout()) {
			
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
				g.setColor(new Color(220, 220, 220));
				for (int i = 20; i <= getPreferredSize().height; i += Element.SIDE) {
					g.drawLine(0, i, getPreferredSize().width, i);
				}
				for (int j = 20; j <= getPreferredSize().width; j += Element.SIDE) {
					g.drawLine(j, 0, j, getPreferredSize().height);
				}
				g.setColor(new Color(100, 150, 200));
				g.drawRect(0, 0, getPreferredSize().width - 1, getPreferredSize().height - 1);
			}
			
			public Dimension getPreferredSize() {
				return new Dimension(boardPanel.getPreferredSize().width + 40, boardPanel.getPreferredSize().height + 40);
			}
			
		};
		p5.add(boardPanel);
		
		JPanel p6 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		p6.setOpaque(false);
		p6.add(p5);
		
		JPanel p7 = new JPanel(new BorderLayout(0, 5));
		p7.setOpaque(false);
		p7.add(Atomix.levelSelectPanel, BorderLayout.NORTH);
		p7.add(p6);
		
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.GRAY));
		add(p7);
		add(p4, BorderLayout.EAST);
	}
	
	private static int[][] toArray(String s) {
		String[] rows = s.split("\n");
		int[][] array = new int[rows.length][rows[0].length()];
		
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (rows[i].charAt(j) == '#') {
					array[i][j] = -1;
				} else if (rows[i].charAt(j) == '.') {
					array[i][j] = 0;
				} else if (Character.isLetter(rows[i].charAt(j))) {
					array[i][j] = 10 + rows[i].charAt(j) - 'a';
				} else {
					array[i][j] = rows[i].charAt(j) - '0';
				}
			}
		}
		return array;
	}
	
	public void paintComponent(Graphics g) {
	    if (!isOpaque()) {
	        super.paintComponent(g);
	        return;
	    }
	    
	    Graphics2D g2d = (Graphics2D) g;
	    int w = getWidth();
	    int h = getHeight();
	    g2d.setPaint(new GradientPaint(0, 0, Color.WHITE, w, h, new Color(199, 217, 235)));
	    g2d.fillRect(0, 0, w, h);
	 
	    setOpaque(false);
	    super.paintComponent(g);
	    setOpaque(true);
	}
		
}
