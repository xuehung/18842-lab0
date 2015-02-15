package demo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

import datatype.Message;
import datatype.Node;
import datatype.TimeStampedMessage;
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
		
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		scroll.setSize(400, 700);
		
		frame.setSize(400, 800);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(scroll);
		frame.getContentPane().add(input);
		frame.setVisible(true);
		
		
		input.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) {
		        if(e.getKeyCode() == KeyEvent.VK_ENTER){
		        		String cmd = input.getText();
		        		
		        		input.setText("");
					try {
						document.insertBeforeEnd(document.getElement("body"), 
								"<div align='right'>"
								+ "<p>"+cmd+"<br>");
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					output.setCaretPosition(output.getDocument().getLength());
		        		e.consume();
		        		
		        		String[] tokens = cmd.split(" ");
		        		System.out.println(tokens.length);
		        		if (tokens.length > 0) {
		        			String cmdType = tokens[0];
		        			if ("send".equals(cmdType)) {
		        				System.out.println(cmdType);
		        				String dest = null;
		        				String kind = null;
		        				String text = null;
		        				if (tokens.length >= 4) {
		        					boolean needLog = false;
		        					int textPos = 3;
		        					if ("-l".equals(tokens[1]) && tokens.length >= 5) {
		        						dest = tokens[2];
		        						kind = tokens[3];
		        						needLog = false;
		        						textPos = 4;
		        					} else {
		        						dest = tokens[1];
		        						kind = tokens[2];
		        					}
		        					for (text = tokens[textPos++]; textPos < tokens.length ; textPos++) {
		        						text += (" " + tokens[textPos]);
		        					}
		        					TimeStampedMessage message = new TimeStampedMessage(dest, kind, text);
		        					if (needLog) {
		        						message.setRequireLog(true);
		        					}
		        					mp.send(message);
		        					//mp.multicast(dest, message);
		        					
		        				}
		        			} else if ("log".equals(cmdType)) {
		        				if (tokens.length > 1) {
		        					String logText = null;
		        					int textPos = 1;
		        					boolean toLogger = false;
		        					if ("-l".equals(tokens[1]) && tokens.length >= 3) {
		        						textPos = 2;
		        						toLogger = false;
		        					}
		        					for (logText = tokens[textPos++]; textPos < tokens.length ; textPos++) {
		        						logText += (" " + tokens[textPos]);
		        					}
		        					mp.logEvent(logText, toLogger);
		        					
		        				}
		        			} else if ("time".equals(cmdType)) {
		        				String time = mp.showTime();
		        				try {
		    						document.insertBeforeEnd(document.getElement("body"), 
		    								"<div align='right'>"
		    								+ "<p>"+time+"<br>");
		    					} catch (BadLocationException e1) {
		    						e1.printStackTrace();
		    					} catch (IOException e1) {
		    						e1.printStackTrace();
		    					}
		        			}
		        		}		
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
