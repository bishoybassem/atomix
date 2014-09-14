
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class MessageDialog extends JDialog {
	
	private static final String aboutText;
	private static final String howToPlayText;
	
	static {
		aboutText = Atomix.readTextFile("resources/about.txt");
		howToPlayText = Atomix.readTextFile("resources/howtoplay.txt");
	}
	
	public MessageDialog(JFrame main, boolean isAbout) {
		super(main, "Atomix", true);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFocusable(false);
		textPane.setOpaque(false);
		textPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
		textPane.setText(isAbout ? aboutText : howToPlayText);
		
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		JButton ok = new JButton("OK");
		ok.setFocusable(false);
		ok.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
			
		});
		
		JPanel p1 = new JPanel();
		p1.setOpaque(false);
		p1.add(ok);
		
		final JLabel icon = new JLabel(new ImageIcon(getClass().getResource("resources/logo.png")));
		icon.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel p2 = new JPanel(new BorderLayout()) {
			
			public void paintComponent(Graphics g) {
			    if (!isOpaque()) {
			        super.paintComponent(g);
			        return;
			    }
			    
			    Graphics2D g2d = (Graphics2D) g;
			    int w = getWidth();
			    int h = getHeight();
			    GradientPaint gp = new GradientPaint(0, icon.getHeight(), new Color(199, 217, 235), 0, h, Color.WHITE);
			    g2d.setPaint(gp);
			    g2d.fillRect(0, 0, w, h);
			 
			    setOpaque(false);
			    super.paintComponent(g);
			    setOpaque(true);
			}
			
		};
		p2.setBorder(new LineBorder(Color.GRAY, 4));
		p2.setBackground(new Color(255, 245, 185));
		p2.add(icon, BorderLayout.NORTH);
		p2.add(textPane);
		p2.add(p1, BorderLayout.SOUTH);
		
		add(p2);
		setResizable(false);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
	}
			
}
