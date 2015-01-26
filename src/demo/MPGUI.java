package demo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import datatype.Node;
import lab0.ConfigLoader;
import lab0.Message;
import lab0.MessagePasser;

public class MPGUI implements Runnable {//implements ActionListener {
	private String localName = null;
	private Map<String, Node> nodeMap = null;
	private List<String> nameList = null;
	private JFrame frame = null;
	private JTextArea output = null;
	private JTextArea input = null;
	private JTextField kindField = null;
	private JComboBox menu = null;
	private MessagePasser mp = null;
	
	public MPGUI(String filePath, String localName) throws IOException {
		this.localName = localName;
		ConfigLoader configLoader = new ConfigLoader(filePath);
		this.nodeMap = configLoader.getNodeMap();
		this.nameList = new ArrayList<String>();
		for (String name : this.nodeMap.keySet()) {
			if (!name.equals(localName)) {
				this.nameList.add(name);
			}
		}
		mp = new MessagePasser(filePath, localName);
	}
	@Override
	public void run() {
		
		frame = new JFrame(this.localName);
		output = new JTextArea(40, 10);
		input = new JTextArea();
		kindField = new JTextField();
		String[] nameStrings = this.nameList.toArray(new String[nameList.size()]);
		menu = new JComboBox(nameStrings);
		
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		
		input.setSize(400, 100);
		kindField.setSize(400, 20);
		frame.setSize(400, 800);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(scroll);
		frame.getContentPane().add(menu);
		frame.getContentPane().add(kindField);
		frame.getContentPane().add(input);
		frame.setVisible(true);
		
		
		input.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) {
		        if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        		String text = input.getText();
		        		String kind = kindField.getText();
		        		String dest = (String)menu.getSelectedItem();
		        		System.out.println(text);
		        		System.out.println(kind);
		        		System.out.println(dest);
		        		input.setText("");
		        		output.append(String.format("-> %s(%s)\n%s\n\n", dest, kind, text));
		        		output.setCaretPosition(output.getDocument().getLength());
		        		e.consume();
		        		Message m = new Message(dest, kind, text);
		        		mp.send(m);
		        }
		    }

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Thread thread = new Thread(){
		    public void run(){
		      while (true) {
		    	  	Message m = mp.receive();
		    	  	if (m != null) {
		    	  		output.append(String.format("<- %s(%s)\n%s\n\n", m.getSrc(), m.getKind(), m.getData()));
		    	  		output.setCaretPosition(output.getDocument().getLength());
		    	  	}
		      }
		    }
		};
		thread.start();
		
		
		
		
		
		
		frame.setVisible(true);
	}
}
