import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class CoPainter {
	JFrame frame;
	JFrame eframe;
	JPanel mainPanel;
	JTextField Host;
	JTextField Port;


	public static void main(String args[]){
		CoPainter CoPaint = new CoPainter();
		CoPaint.go();
	}
	
	public void go(){
		mainPanel = new JPanel();
		frame = new JFrame("Collaborative Painter");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		JPanel hPanel = new JPanel();
		JPanel pPanel = new JPanel();
		JPanel bPanel = new JPanel();
		JButton asHost = new JButton("Start as a host");
		JButton asClient = new JButton("Connect to a host");
		Host = new JTextField(20);
		Port = new JTextField(20);
		hPanel.add(new JLabel("Host:"));
		pPanel.add(new JLabel("Port:"));
		hPanel.add(Host);pPanel.add(Port);
		bPanel.add(asHost);bPanel.add(asClient);
		asHost.addActionListener(new hostButtonListener());
		asClient.addActionListener(new clientButtonListener());
		mainPanel.add(hPanel);mainPanel.add(pPanel);mainPanel.add(bPanel);
		frame.getContentPane().add(mainPanel);
		frame.setSize(300,150);
		frame.setVisible(true);
		}

	
	class hostButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				ServerSocket ss = new ServerSocket(Integer.parseInt(Port.getText()));
				frame.setVisible(false);
				sPainter serverPaint = new sPainter(ss);
				serverPaint.initialize();
			}
			catch(IOException e){
				frame.setVisible(false);
				JButton ok = new JButton("OK");
				eframe = new JFrame("Fail to Start");
				eframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				JPanel eMainPanel = new JPanel();
				eMainPanel.setLayout(new BoxLayout(eMainPanel,BoxLayout.Y_AXIS));
				eMainPanel.add(new JLabel("Unable to Listen to Port " + Port.getText()));
				eMainPanel.add(ok);
				ok.addActionListener(new okListener());
				eframe.add(eMainPanel);
				eframe.setSize(300,150);
				eframe.setVisible(true);
			}
		}
	}

	class clientButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				Socket s = new Socket(Host.getText(), Integer.parseInt(Port.getText()));
				frame.setVisible(false);
				//if(countOfClients == 2) {
					cPainter clientPaint = new cPainter(s);
					clientPaint.initialize();
				//} else {
//					cPainter clientPaint = new cPainter(s, false);
//					clientPaint.initialize();
//					System.out.println(countOfClients + "Im here");
				//}
			}
			catch(IOException e){
				frame.setVisible(false);
				JButton ok = new JButton("OK");
				eframe = new JFrame("Fail to Start");
				eframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				JPanel eMainPanel = new JPanel();
				eMainPanel.setLayout(new BoxLayout(eMainPanel,BoxLayout.Y_AXIS));
				eMainPanel.add(new JLabel("Unable to connect to host!"));
				eMainPanel.add(ok);
				ok.addActionListener(new okListener());
				eframe.add(eMainPanel);
				eframe.setSize(300,150);
				eframe.setVisible(true);
			}
		}
	}
	
	class okListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0){
			eframe.setVisible(false);
			go();
		}
	}
	
}