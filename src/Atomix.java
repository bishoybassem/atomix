import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class Atomix extends JFrame {

	private LevelPanel currentLevel;
	
	static JPanel levelSelectPanel;
	static MessageDialog aboutDialog;
	static MessageDialog howToPlayDialog;
	
	public Atomix() throws URISyntaxException, IOException {
		super("Atomix");
		
		UIManager.put("Button.font", new Font("Comic sans ms", Font.PLAIN, 16));
		UIManager.put("ComboBox.font", new Font("Comic sans ms", Font.PLAIN, 16));
		UIManager.put("TextPane.font", new Font("Comic sans ms", Font.PLAIN, 16));
		UIManager.put("Panel.font", new Font("Comic sans ms", Font.PLAIN, 16));
		setUndecorated(true);
		
		final String[] levelNames = readTextFile("levels/names.txt").split("\n");
		String[] formatedLevelNames = new String[levelNames.length];
		for (int i = 0; i < levelNames.length; i++) {
			formatedLevelNames[i] = String.format(" Level %d : %s", i + 1, levelNames[i]);
		}
		
		final JComboBox<String> levelsComboBox = new JComboBox<String>(formatedLevelNames);
		levelsComboBox.setFocusable(false);
		levelsComboBox.setBackground(new Color(255, 255, 255));
		levelsComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				setVisible(false);
				LevelPanel levelPanel = new LevelPanel(levelsComboBox.getSelectedIndex() + 1);
				remove(currentLevel);
				add(levelPanel, BorderLayout.CENTER);
				currentLevel = levelPanel;
				pack();
				setLocationRelativeTo(null);
				setVisible(true);
			}

		});
		
		JButton next = new JButton("Next Level");
		next.setFocusable(false);
		next.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int index = levelsComboBox.getSelectedIndex();
				if (index < levelNames.length - 1) {
					levelsComboBox.setSelectedIndex(index + 1);
				}
			}
			
		});
		
		JButton previous = new JButton("Prev Level");
		previous.setFocusable(false);
		previous.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int index = levelsComboBox.getSelectedIndex();
				if (index > 0) {
					levelsComboBox.setSelectedIndex(index - 1);
				}
			}
			
		});
					
		levelSelectPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		levelSelectPanel.setBackground(Color.WHITE);
		levelSelectPanel.setBorder(new LineBorder(new Color(100, 150, 200), 1));
		levelSelectPanel.add(previous);
		levelSelectPanel.add(Box.createRigidArea(new Dimension(1, 0)));
		levelSelectPanel.add(levelsComboBox);
		levelSelectPanel.add(Box.createRigidArea(new Dimension(1, 0)));
		levelSelectPanel.add(next);
		
		currentLevel = new LevelPanel(1);
		aboutDialog = new MessageDialog(this, true);
		howToPlayDialog = new MessageDialog(this, false);
		
		setIconImage(new ImageIcon(getClass().getResource("resources/logo.png")).getImage());
		setResizable(false);
		add(currentLevel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static String readTextFile(String fileName) {
		String text = "";
		Scanner sc = null;
		try {
			sc = new Scanner(Atomix.class.getResourceAsStream(fileName));
			while (sc.hasNext()) {
				text += sc.nextLine() + "\n";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
		return text.trim();
	}
	
	public static void main(String[] args) throws Exception {
		new Atomix().setVisible(true);
	}
	
}
