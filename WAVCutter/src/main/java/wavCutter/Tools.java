package main.java.wavCutter;

import java.awt.Container;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Tools {
	public static JButton MyButton(int x, int y, int width, int height, String text, ActionListener actL, Container c){
		JButton b = new JButton();
		b.setText(text);
		b.setBounds(x, y, width, height);
		b.addActionListener(actL);
		c.add(b);
		b.setVisible(true);
		return b;
	}
	
	public static JLabel MyLabel(int x, int y, int width, int height, String text, Container c){
		JLabel b = new JLabel();
		b.setText(text);
		b.setBounds(x, y, width, height);
		c.add(b);
		b.setVisible(true);
		return b;
	}
	
	public static JTextField MyTextField(int x, int y, int width, int height, Container c, boolean editable){
		JTextField b = new JTextField();
		b.setBounds(x, y, width, height);
		c.add(b);
		b.setEditable(editable);
		b.setVisible(true);
		return b;
	}
	
	public static JCheckBox MyCheckBox(int x, int y, int width, int height, String text, ActionListener actL, Container c){
		JCheckBox b = new JCheckBox();
		b.setText(text);
		b.setBounds(x, y, width, height);
		b.addActionListener(actL);
		c.add(b);
		b.setVisible(true);
		return b;
	}
}
