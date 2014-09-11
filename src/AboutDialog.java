
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {
	
	private static BufferedImage icon;
	
	static {
		try {
			icon = ImageIO.read(AboutDialog.class.getResource("resources/logo.png"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public AboutDialog(JFrame main) {
		super(main, "Atomix", true);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFocusable(false);
		textPane.setOpaque(false);
		textPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
		textPane.setText("Created By\nBishoy Bassem Morris\ngithub.com/bishoybassem");
		
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
		
		JPanel p1 = new JPanel() {

			public Dimension getPreferredSize() {
				return new Dimension(icon.getWidth(), icon.getHeight());
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(icon, 0, 0, null);
			}
			
		};
		p1.setOpaque(false);
		
		JPanel p2 = new JPanel();
		p2.setOpaque(false);
		p2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		p2.add(p1);
		
		JPanel p3 = new JPanel();
		p3.setOpaque(false);
		p3.add(ok);
		
		JPanel p4 = new JPanel(new BorderLayout()) {
			
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
		p4.setBorder(new LineBorder(Color.GRAY, 4));
		p4.setBackground(new Color(255, 245, 185));
		p4.add(p2, BorderLayout.NORTH);
		p4.add(textPane);
		p4.add(p3, BorderLayout.SOUTH);
		
		add(p4);
		setResizable(false);
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
	}
			
}
