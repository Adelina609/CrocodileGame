package ui.painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class CoPainter {
	private JFrame frame;
	private JFrame eframe;
	private JPanel mainPanel;

	public static void main(String args[]) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		CoPainter CoPaint = new CoPainter();
		CoPaint.go();
	}

	private void go() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		mainPanel = new JPanel();
		frame = new JFrame("Crocodile");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
//		JPanel hPanel = new JPanel();
//		JPanel pPanel = new JPanel();
		JPanel bPanel = new JPanel();
		JButton asHost = new JButton("Start as a host");
		JButton asClient = new JButton("Connect to a host");
//		Host = new JTextField(20);
//		Port = new JTextField(20);
//		hPanel.add(new JLabel("Host:"));
//		pPanel.add(new JLabel("Port:"));
//		hPanel.add(Host);pPanel.add(Port);
		bPanel.add(asHost);
		bPanel.add(asClient);
		asHost.addActionListener(new hostButtonListener());
		asClient.addActionListener(new clientButtonListener());
		//mainPanel.add(hPanel);mainPanel.add(pPanel);
		mainPanel.add(bPanel);
		frame.setMinimumSize(new Dimension(300, 100));
		frame.getContentPane().add(mainPanel);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame.setVisible(true);
	}


	class hostButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				ServerSocket ss = new ServerSocket(2222);
				ServerSocket smsg = new ServerSocket(3443);
				frame.setVisible(false);
				sPainter serverPaint = new sPainter(ss, smsg);
				serverPaint.initialize();
			}
			catch(IOException e){
				frame.setVisible(false);
				JButton ok = new JButton("OK");
				eframe = new JFrame("Fail to Start");
				eframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				JPanel eMainPanel = new JPanel();
				eMainPanel.setLayout(new BoxLayout(eMainPanel,BoxLayout.Y_AXIS));
				eMainPanel.add(new JLabel("Unable to Listen to Port " + 2222));
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
				Socket s = new Socket("127.0.0.1", 2222);
				Socket smsg = new Socket("127.0.0.1", 3443);
				frame.setVisible(false);
				cPainter clientPaint = new cPainter(s, smsg);
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
			try {
				go();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}