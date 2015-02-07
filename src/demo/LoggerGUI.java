package demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

import datatype.LogEvent;
import datatype.Message;
import datatype.Node;
import datatype.TimeStampedMessage;
import lab0.ConfigLoader;
import lab0.MessagePasser;

public class LoggerGUI implements Runnable {//implements ActionListener {
	final private String htmlINIT = "<html>"
			+ "<body id='body' style='height: 400px; font-size: 18px;'>"
			+ "</body>"
			+ "</html>";
	private String localName = null;
	private Map<String, Node> nodeMap = null;
	private List<String> nameList = null;
	private JFrame frame = null;
	private JTextPane output = null;
	private MessagePasser mp = null;
	private PriorityQueue<TimeStampedMessage> pq = null;
	
	

    private HTMLDocument document = null;
	
	public LoggerGUI(String filePath, String localName) throws IOException {
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
		pq = new PriorityQueue<TimeStampedMessage>();
		
		output = new JTextPane();
		output.setContentType("text/html");
		output.setText(htmlINIT);
		document = (HTMLDocument) output.getDocument();
	}
	
	
	@Override
	public void run() {
		
		frame = new JFrame(this.localName);
		
		output.setEditable(false);
		JScrollPane scroll = new JScrollPane(output);
		scroll.setSize(400, 700);
		
		frame.setSize(400, 800);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(scroll);
		frame.setVisible(true);
		
		Thread thread = new Thread() {
			public void run() {
				while (true) {
					Message m = mp.receive();
					if (m instanceof TimeStampedMessage) {
						addNewLog((TimeStampedMessage)m);
						showAllLog();
					}
				}
			}
		};
		thread.start();
		
		frame.setVisible(true);
	}
	
	private void addNewLog(TimeStampedMessage message) {
		pq.add(message);
	}
	
	private void showAllLog() {
		output.setText(htmlINIT);
		document = (HTMLDocument) output.getDocument();
		TimeStampedMessage[] messageArray = pq.toArray(new TimeStampedMessage[pq.size()]);
		for (int i = 0 ; i < messageArray.length ; i++) {
			TimeStampedMessage currentMessage = messageArray[i];
			try {
				if (i >= 1 && messageArray[i - 1].compareTo(currentMessage) == 0) {
					document.insertBeforeEnd(document.getElement("body"), "<hr>");
				}
				if (currentMessage.getData() instanceof LogEvent) {
					LogEvent e = (LogEvent)currentMessage.getData();
					document.insertBeforeEnd(document.getElement("body"), 
							"<p>"
							+ e.getTimestamp()+"<br>"	
							+ e.getText()
							+"</p>");
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
