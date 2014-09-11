import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class BoardPanel extends JPanel {

	private int[][] board;
	private int[][] boardOriginal;
	private int[][] solution;
	private Element[] elements;
	private MovesPanel movesPanel;
	private BufferedImage walls;
	private BufferedImage atoms;

	private int curRow;
	private int curCol;
	private int curX;
	private int curY;
	private int finRow;
	private int finCol;
	private int finX;
	private int finY;
	private int deltaX;
	private int deltaY;
	private int curAlpha;
	private int deltaAlpha;
	private double curAngle;
	private double deltaAngle;
	
	private List<int[]> movements;
	private int moveIndex;
	
	private Timer animator;
	private boolean animating;
	private boolean fade;
	private boolean move;
	private boolean rotate;
	private boolean won;
	private boolean playMoveAC;
	
	private enum Direction {UP, DOWN, LEFT, RIGHT}
	
	private static AudioClip moveAC;
	private static AudioClip undoAC;
	private static AudioClip cheersAC;
	
	static {
		try {
			moveAC = Applet.newAudioClip(BoardPanel.class.getResource("resources/move.au"));
			undoAC = Applet.newAudioClip(BoardPanel.class.getResource("resources/undo.wav"));
			cheersAC = Applet.newAudioClip(BoardPanel.class.getResource("resources/cheers.wav"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public BoardPanel(int[][] board, int[][] solution, Element[] elements) {
		this.boardOriginal = board;
		this.solution = solution;
		this.elements = elements;
		setInputListeners();
		setAnimator();
		reset();
	}
	
	public void reset() {
		board = new int[boardOriginal.length][];
		for(int i = 0; i < board.length; i++) {
			board[i] = boardOriginal[i].clone();
		}
		if (movesPanel == null){
			movesPanel = new MovesPanel();
		} else {
			movesPanel.reset();
		}
		curRow = 0;
		curCol = 0;
		movements = new ArrayList<int[]>();
		moveIndex = -1;
		won = false;
		playMoveAC = true;
		drawWalls();
		drawAtoms();
		repaint();
	}
	
	public void setAnimator() {
		animator = new Timer(8, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if ((finX == curX && finY == curY) || curAlpha == 20) {					
					int x = board[finRow][finCol];
					board[finRow][finCol] = board[curRow][curCol];
					board[curRow][curCol] = x;
					curRow = finRow;
					curCol = finCol;
					curX = curCol * Element.side;
					curY = curRow * Element.side;
					curAngle = 0;
					curAlpha = 0;
					animating = false;
					move = false;
					fade = false;
					rotate = false;
					playMoveAC = true;
					movesPanel.repaint();
					checkWon();
					animator.stop();
				} else {
					if ((finY == curY - 5 || finY == curY + 5 || finX == curX - 5 || finX == curX + 5 || curAlpha == 19) && playMoveAC){
						moveAC.play();
					}							
					curAngle += deltaAngle;
					curAlpha += deltaAlpha;
					curX = (finX > curX)? curX + deltaX : (finX < curX)? curX - deltaX : curX;
					curY = (finY > curY)? curY + deltaY : (finY < curY)? curY - deltaY : curY;
				}
				repaint();
			}
			
		});
	}
	
	private void setInputListeners() {
		setBackground(new Color(255, 255, 255));
		setFocusable(true);
		
		getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		getActionMap().put("up", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				moveAtom(Direction.UP);
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		getActionMap().put("left", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				moveAtom(Direction.LEFT);
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		getActionMap().put("right", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				moveAtom(Direction.RIGHT);
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		getActionMap().put("down", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				moveAtom(Direction.DOWN);
			}
			
		});
		
		getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
		getActionMap().put("exit", new AbstractAction() {

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
			
		});
		
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (animating || won) {
					return;
				}
				int c = e.getX() / Element.side;
				int r = e.getY() / Element.side;
				if (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
					if (board[r][c] > 0) {
						curRow = r;
						curCol = c;
						curX = curCol * Element.side;
						curY = curRow * Element.side;
						finRow = r;
						finCol = c;
						drawAtoms();
						repaint();
					} else if (board[curRow][curCol] > 0 && board[r][c] == 0) {
						if (r == curRow + 1 && c == curCol) {
							moveAtom(Direction.DOWN);
						}
						if (r == curRow - 1 && c == curCol) {
							moveAtom(Direction.UP);
						}
						if (c == curCol + 1 && r == curRow) {
							moveAtom(Direction.RIGHT);
						}
						if (c == curCol - 1 && r == curRow) {
							moveAtom(Direction.LEFT);
						}
					}
				}
			}

		});	
	}
	
	private void checkWon() {
		for (int i = 0; i <= board.length - solution.length; i++) {
			for (int j = 0; j <= board[0].length - solution[0].length; j++) {
				if (areEqual(i, j)) {
					Graphics2D g2d = (Graphics2D)getGraphics();
					
					Rectangle2D rect = new Rectangle2D.Double(1, (getPreferredSize().height - 3 * Element.side) / 2.0, getPreferredSize().width - 2, 3 * Element.side);
			        g2d.setPaint(new GradientPaint(0, 0, new Color(245, 248, 251), getPreferredSize().width, 0, new Color(221, 232, 242)));
			        g2d.fill(rect);
					g2d.setColor(new Color(100, 150, 200));
					g2d.draw(rect);

					g2d.setFont(new Font("Comic sans ms", Font.PLAIN, Element.side));
					FontMetrics fm = g2d.getFontMetrics();
			        Rectangle2D r = fm.getStringBounds("YOU WIN!", g2d);
			        int x = (int) ((getPreferredSize().width - r.getWidth()) / 2);
			        int y = (int) (getPreferredSize().height - r.getHeight()) / 2 + fm.getAscent();
			        g2d.setColor(Color.BLACK);
			        g2d.drawString("YOU WIN!", x, y);

					cheersAC.play();
					try {
						Thread.sleep(2500);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					won = true;
				}
			}
		}
	}

	private boolean areEqual(int r, int c) {
		for (int i = 0; i < solution.length; i++) {
			for (int j = 0; j < solution[0].length; j++) {
				if (solution[i][j] > 0) {
					if (board[i + r][j + c] < 1 || solution[i][j] != board[i + r][j + c])
						return false;
				}
			}
		}
		return true;
	}
	
	private void moveAtom(Direction direction) {
		if (animating || won || board[curRow][curCol] < 1)
			return;

		if (direction == Direction.UP) {
			while (finRow > 0 && board[finRow - 1][finCol] == 0) {
				finRow--;
			}
		} else if (direction == Direction.DOWN) {
			while (finRow < board.length - 1 && board[finRow + 1][finCol] == 0) {
				finRow++;
			}
		} else if (direction == Direction.LEFT) {
			while (finCol > 0 && board[finRow][finCol - 1] == 0) {
				finCol--;
			}
		} else if (direction == Direction.RIGHT) {
			while (finCol < board[0].length - 1 && board[finRow][finCol + 1] == 0) {
				finCol++;
			}
		}
		if (curRow != finRow || curCol != finCol) {
			movesPanel.increment();
			movements = movements.subList(0, moveIndex + 1);
			movements.add(new int[]{curRow, curCol, finRow, finCol});
			moveIndex++;
			setAnimationType();
			animateMovement();
		}
	}
	
	private void setAnimationType() {
		animating = true;
		Random gen = new Random();		
		while (!move && !fade) {
			move = gen.nextBoolean();
			fade = gen.nextBoolean();
		}
		rotate = gen.nextBoolean();
	}
	
	private void animateMovement() {
		finX = finCol * Element.side;
		finY = finRow * Element.side;
		deltaX = move ? 5 : 0;
		deltaY = move ? 5 : 0;
		deltaAngle = (!rotate)? 0 : (fade)? Math.PI / 10 : (10 * Math.PI / ((finY - curY) + (finX - curX)));
		deltaAlpha = (fade)? 1 : 0;
		animator.start();
	}
		
	public void traverseMovements(boolean undo) {
		if (animating || won)
			return;

		try {
			int[] positions;
			if (undo) {
				positions =  movements.get(moveIndex);
				moveIndex--;
				movesPanel.decrement();
				curRow = positions[2];
				curCol = positions[3];
				finRow = positions[0];
				finCol = positions[1];
			} else {
				positions =  movements.get(moveIndex + 1);
				moveIndex++;
				movesPanel.increment();
				curRow = positions[0];
				curCol = positions[1];
				finRow = positions[2];
				finCol = positions[3];
			}
			curX = curCol * Element.side;
			curY = curRow * Element.side;
			drawAtoms();
			animating = true;
			fade = true;
			playMoveAC = false;
			animateMovement();
			undoAC.play();
		} catch (Exception ex) {
			
		}
	}
	
	private void drawWalls() {
		walls = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = walls.getGraphics();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == -1) {
					g.drawImage(Element.wall, j * Element.side, i * Element.side, null);
				}
			}
		}
		g.setColor(new Color(220, 220, 220));
		for (int i = 0; i <= board.length; i++) {
			g.drawLine(0, i * Element.side, getPreferredSize().width, i * Element.side);
		}
		for (int j = 0; j <= board[0].length; j++) {
			g.drawLine(j * Element.side, 0, j * Element.side, getPreferredSize().height);
		}
	}
	
	public void drawAtoms() {
		atoms = new BufferedImage(Element.side * (board[0].length + 1), Element.side * (board.length + 1), BufferedImage.TYPE_INT_ARGB);
		Graphics g = atoms.getGraphics();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] > 0 && !(i == curRow && j == curCol)) {
					g.drawImage(elements[board[i][j]].getImage(), j * Element.side, i * Element.side, null);
				}
			}
		}
	}
	
	public MovesPanel getMovesPanel() {
		return movesPanel;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(Element.side * board[0].length, Element.side * board.length);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.drawImage(walls, 0, 0, null);
		g2d.drawImage(atoms, 0, 0, null);
        
		if (board[curRow][curCol] < 1)
			return;

		Composite c = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - curAlpha * 0.05)));
		
		g2d.rotate(curAngle, curX + Element.side / 2, curY + Element.side / 2);
		g2d.drawImage(elements[board[curRow][curCol]].getImage(), curX, curY, null);
		g2d.rotate(-curAngle, curX + Element.side / 2, curY + Element.side / 2);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (curAlpha * 0.05)));
		
		g2d.rotate(curAngle, finX + Element.side / 2, finY + Element.side / 2);
		g2d.drawImage(elements[board[curRow][curCol]].getImage(), finX, finY,  null);
		g2d.rotate(-curAngle, finX + Element.side / 2, finY + Element.side / 2);
		
		g2d.setComposite(c);
		
		if (!animating && !won) {
			if (board[curRow - 1][curCol] == 0) {
				g2d.drawImage(Element.arrowDown, curCol * Element.side, (curRow - 1) * Element.side, null);
			}
			if (board[curRow + 1][curCol] == 0) {
				g2d.drawImage(Element.arrowUp, curCol * Element.side, (curRow + 1) * Element.side, null);
			}
			if (board[curRow][curCol - 1] == 0) {
				g2d.drawImage(Element.arrowLeft, (curCol - 1) * Element.side, curRow * Element.side, null);
			}
			if (board[curRow][curCol + 1] == 0) {
				g2d.drawImage(Element.arrowRight, (curCol + 1) * Element.side, curRow * Element.side, null);
			}
		}
	}
	
}
