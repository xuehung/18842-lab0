package demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import datatype.Node;
import lab0.ConfigLoader;

public class Demo implements ActionListener {
	
	private File dir = new File(System.getProperty("user.dir", "."));
	private JFileChooser jfc = new JFileChooser(dir);
	private JFrame frame = new JFrame();
	private JTextField jtfText1 = new JTextField();
	private JButton button = new JButton("Add");
	private Map<String, Node> nodeMap = null;
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String name = jtfText1.getText().trim();
		if (this.nodeMap.containsKey(name)) {
			try {
				Thread thread = new Thread(new MPGUI(jfc.getSelectedFile().getPath(), name));
				thread.start();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frame,
					    "cannot find configuration: "+ jfc.getSelectedFile().getPath(),
					    "error",
					    JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame,
					    "IOException!",
					    "error",
					    JOptionPane.ERROR_MESSAGE);
			}
			
		} else {
			JOptionPane.showMessageDialog(frame,
				    name + " does not exist in configuration",
				    "error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	public void go() {
		File dir = new File(System.getProperty("user.dir", "."));
	    jfc = new JFileChooser(dir);
	    int result = jfc.showOpenDialog(null);
		switch (result) {
		case JFileChooser.CANCEL_OPTION:
			return;
		case JFileChooser.APPROVE_OPTION:
			ConfigLoader config;
			try {
				config = new ConfigLoader(jfc.getSelectedFile().getPath());
				nodeMap = config.getNodeMap();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			break;
		default:
			return;
		}
		
		
		button.addActionListener(this);
		
		frame.setSize(300, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.getContentPane().add(BorderLayout.CENTER, jtfText1);
		frame.getContentPane().add(BorderLayout.SOUTH, button);
		
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] argv) {
		Demo demo = new Demo();
		demo.go();
		
	}
}
