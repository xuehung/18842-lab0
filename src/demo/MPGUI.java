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
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

import datatype.Message;
import datatype.Node;
import lab0.ConfigLoader;
import lab0.MessagePasser;

public class MPGUI implements Runnable {//implements ActionListener {
	final private String htmlINIT = "<html>"
			+ "<body id='body' style='height: 400px; font-size: 18px;'>"
			+ "</body>"
			+ "</html>";
	private String localName = null;
	private Map<String, Node> nodeMap = null;
	private List<String> nameList = null;
	private JFrame frame = null;
	private JTextPane output = null;
	private JTextArea input = null;
	private JTextField kindField = null;
	private JComboBox menu = null;
	private MessagePasser mp = null;
	
	

    private HTMLDocument document = null;
	
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
		
		output = new JTextPane();
		output.setContentType("text/html");
		output.setText(htmlINIT);
		document = (HTMLDocument) output.getDocument();
	}
	
	
	@Override
	public void run() {
		
		frame = new JFrame(this.localName);
		input = new JTextArea(10, 10);
		kindField = new JTextField();
		String[] nameStrings = this.nameList.toArray(new String[nameList.size()]);
		menu = new JComboBox(nameStrings);
		
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		scroll.setSize(400, 700);
		
		kindField.setSize(400, 20);
		kindField.setText("defalt_kind");
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
		        		
		        		input.setText("");
					try {
						document.insertBeforeEnd(document.getElement("body"), 
								"<div align='right'>"
								+ "<p>"+localName+":<br>"
								+text+"</p></div><hr>");
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
		        		//document.insertString(arg0, arg1, arg2);.append(String.format("-> %s(%s)\n%s\n\n", dest, kind, text));
		        		System.out.println(output.getDocument().getLength());
		        		
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
		
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					Message m = mp.receive();
					if (m != null) {
						try {
							document.insertBeforeEnd(document.getElement("body"), 
									"<div align='left'>"
									+ String.format("<p>%s:(%s/%d)<br>",m.getSrc(), m.getKind(), m.getSeqNum())
									+m.getData()+"</p></div><hr>");
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						output.setCaretPosition(output.getDocument()
								.getLength());
					}
				}
			}
		};
		thread.start();
		
		
		
		
		
		
		frame.setVisible(true);
	}
}
