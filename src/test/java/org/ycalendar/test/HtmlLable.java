package org.ycalendar.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class HtmlLable extends JPanel {
	// 内置了本地系统桌面操作支持
	private Desktop desktop;

	private static final long serialVersionUID = 1L;

	// html内容
	private static final String HTML_TEXT = "<html><font color='red'><a href=\"http://www.helpsoff.com.cn\" target=\"_blank\">支持SOFF，插件无罪</a></font></html>";

	public HtmlLable() {
		super(new BorderLayout());
		final JLabel label0 = new JLabel("普通的JLabel");
		final JLabel label1 = new JLabel(HTML_TEXT);
		final JLabel label2 = new JLabel(HTML_TEXT) {

			private static final long serialVersionUID = 1L;

			// 设定Enabled样式
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				setForeground(b ? (Color) UIManager.get("Label.foreground") : (Color) UIManager.get("Label.disabledForeground"));
			}
		};
		final JLabel label3 = new JLabel(HTML_TEXT) {
			private static final long serialVersionUID = 1L;

			// 阴影
			private BufferedImage shadow;

			// 设定Enabled样式
			public void setEnabled(boolean b) {
				setForeground(b ? (Color) UIManager.get("Label.foreground") : (Color) UIManager.get("Label.disabledForeground"));
				if (!b) {
					BufferedImage source = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = source.createGraphics();
					g2.setPaint(new Color(0, 0, 0, 0));
					g2.fillRect(0, 0, getWidth(), getHeight());
					paint(g2);
					g2.dispose();
					ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
					shadow = colorConvert.filter(source, null);
				}
				super.setEnabled(b);
			}

			public void paintComponent(Graphics g) {
				if (isEnabled()) {
					super.paintComponent(g);
				} else {
					if (shadow != null)
						g.drawImage(shadow, 0, 0, this);
				}
			}
		};

		/**
		 * 设定为html
		 */
		final JEditorPane editor1 = new MyHtmlEdit("text/html", HTML_TEXT);
		editor1.setOpaque(false);
		editor1.setEditable(false);

		final JEditorPane editor2 = new MyHtmlEdit("text/html", HTML_TEXT);
		editor2.setOpaque(false);
		editor2.setEditable(false);

		editor2.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		editor2.setFont((Font) UIManager.get("Label.font"));

		JPanel box = new JPanel(new GridLayout(2, 3));
		box.add(makePanel("JLabel", label0));
		box.add(makePanel("JLabel+Html", label1));
		box.add(makePanel("JLabel+Html+", label2));
		box.add(makePanel("JLabel+Html++", label3));
		box.add(makePanel("JEditorPane", editor1));
		box.add(makePanel("JEditorPane+", editor2));

		JCheckBox check = new JCheckBox("setEnabled", true);
		check.setAction(new AbstractAction("setEnabled") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				boolean flag = ((JCheckBox) e.getSource()).isSelected();
				setVisible(false);
				label0.setEnabled(flag);
				label1.setEnabled(flag);
				label2.setEnabled(flag);
				label3.setEnabled(flag);
				editor1.setEnabled(flag);
				editor2.setEnabled(flag);
				setVisible(true);
			}
		});

		add(check, BorderLayout.NORTH);
		add(box, BorderLayout.CENTER);
		desktop = Desktop.getDesktop();

	}

	class MyHtmlEdit extends JEditorPane implements HyperlinkListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyHtmlEdit(String type, String text) {
			super(type, text);
			addHyperlinkListener(this);

		}

		public void hyperlinkUpdate(HyperlinkEvent e) {

			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				try {
					// 在本地浏览器中打开uri(jdk1.6后由于强化了awt,就不用再自己写本地浏览器调用了)
					desktop.browse(e.getURL().toURI());

				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (URISyntaxException ex) {
					ex.printStackTrace();
				}

			}

		}
	}

	private JPanel makePanel(String title, JComponent label) {
		JPanel p = new JPanel(new GridLayout(1, 1));
		p.setBorder(BorderFactory.createTitledBorder(title));
		p.add(label);
		return p;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	}

	public static void createGUI() {

		JFrame frame = new JFrame("在Swing中使用Html标签");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(450, 200));

		frame.getContentPane().add(new HtmlLable());

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		// jdk1.6新项,置顶
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

}
