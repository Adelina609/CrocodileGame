package ui.painter;

import ui.server.MessagessHandler;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class sPainter implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Canvas canvas = new Canvas();
	private ArrayList<line> lines = new ArrayList<>();
	private transient ServerSocket ss;
	private transient ServerSocket msgSocket;
	private transient ArrayList<ObjectOutputStream> clientOutputStream;
	private transient ArrayList<ObjectOutputStream> chatOS;
	private ArrayList<MessagessHandler> clients = new ArrayList<MessagessHandler>();
	private ArrayList<String> words = new ArrayList<>();
	private String chosenWord;


	private void initWords(){
		words.add("Apple");
		words.add("ВКонтакте");
		words.add("YouTube");
		words.add("божья коровка");
		words.add("скульптор");
		words.add("промоутер");
	}

	public String getChosenWord() {
		return chosenWord;
	}

	private String chooseWord(){
		initWords();
		Random random = new Random();
		return words.get(random.nextInt(words.size()));
	}

	public sPainter(ServerSocket ss, ServerSocket msgSocket){
		this.ss = ss;
		this.msgSocket = msgSocket;
		chosenWord = chooseWord();
	}

//		public void createMenu(){
//			this.options = new JMenu("option");
//			this.clear = new JMenuItem("clear");
//			this.undo = new JMenuItem("undo");
//			this.save = new JMenuItem("save");
//			this.load = new JMenuItem("load");
//			this.exit = new JMenuItem("exit");
//			this.menubar.add(options);
//			this.options.add(clear);
//			this.options.add(undo);
//			this.options.add(save);
//			this.options.add(load);
//			this.options.add(exit);
//			this.exit.addActionListener(new exitListener());
//			this.clear.addActionListener(new clearListener());
//			this.undo.addActionListener(new undoListener());
//			this.save.addActionListener(new saveListener());
//			this.load.addActionListener(new loadListener());
//		}
//
//		public void createScrollPane() {
//			this.canvasScrollPane = new JScrollPane(this.canvasPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//			this.canvasScrollPane.getVerticalScrollBar().addAdjustmentListener(new verticalAdjust());
//			this.canvasScrollPane.getHorizontalScrollBar().addAdjustmentListener(new horizontalAdjust());
//			this.canvasScrollPane.setPreferredSize(new Dimension(800,600));
//		}


//		public void createColorPicker(){
//			zoomIn = new JButton("+");
//			zoomOut = new JButton("-");
//			zoomIn.addActionListener(new zoomInListener());
//			zoomOut.addActionListener(new zoomOutListener());
//			JPanel redContainer = new JPanel();
//			JPanel greenContainer = new JPanel();
//			JPanel blueContainer = new JPanel();
//			redContainer.setBackground(Color.red);
//			greenContainer.setBackground(Color.GREEN);
//			blueContainer.setBackground(Color.blue);
//			redContainer.add(red);
//			greenContainer.add(green);
//			blueContainer.add(blue);
//			sliders.setLayout(new BoxLayout(sliders,BoxLayout.Y_AXIS));
//			sliders.add(redContainer);sliders.add(blueContainer);sliders.add(greenContainer);
//			red.addMouseMotionListener(new setColor());green.addMouseMotionListener(new setColor());blue.addMouseMotionListener(new setColor());
//			colorBox.setBackground(new Color(red.getValue(),green.getValue(),blue.getValue()));
//			colorBox.setPreferredSize(new Dimension(80,80));
//			sizeDotPanel.setPreferredSize(new Dimension(80,80));
//			sizeDotPanel.setBackground(Color.white);
//			dot = new Dot();
//			dot.setPreferredSize(new Dimension(20,20));
//			sizeDotPanel.add(dot);
//			sizeSliderPanel.add(sizeSlider);
//			sizeSlider.setPreferredSize(new Dimension(20,80));
//			sizeSlider.addMouseMotionListener(new sizeListener());
//			ColorPicker.add(colorBox);
//			ColorPicker.add(sliders);
//			ColorPicker.add(sizeDotPanel);
//			ColorPicker.add(sizeSliderPanel);
//			ColorPicker.add(zoomIn);
//			ColorPicker.add(zoomOut);
//		}
//
//		public void save(File choosenFile) throws FileNotFoundException, IOException {
//			FileOutputStream fileOutputStream = new FileOutputStream(choosenFile);
//			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
//			objectOutputStream.writeObject(this.lines);
//			objectOutputStream.close();
//		}
//
//		@SuppressWarnings("unchecked")
//		public void load(File choosenFile) throws FileNotFoundException, IOException , ClassNotFoundException{
//			FileInputStream fileis = new FileInputStream(choosenFile);
//			ObjectInputStream objectis = new ObjectInputStream(fileis);
//			this.lines = (ArrayList<line>) objectis.readObject();
//			objectis.close();	}
//
//		public void createCanvas () {
//			this.canvasPanel.add(canvas);
//			this.canvasPanel.setSize(new Dimension(1200,1200));
//			this.canvas.addMouseMotionListener(new canvasMouseMotionListener());
//			this.canvas.addMouseListener(new canvasMouseListener());
//			this.canvas.setBackground(Color.white);
//			this.canvas.setPreferredSize(new Dimension(1200,1200));
//		}

	public void initialize(){
//			this.createMenu();
//			this.createColorPicker();
//			this.createCanvas();
//			this.createScrollPane();
		clientOutputStream = new ArrayList<>();
		chatOS = new ArrayList<>();
		final sPainter sPainter = this;
		final Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					while(true)
					{
						Socket clientSocket = ss.accept();
						Socket msgClient = msgSocket.accept();
						MessagessHandler client = new MessagessHandler(msgClient,sPainter);
						clients.add(client);
						ObjectOutputStream OOS = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectOutputStream msgOOS = new ObjectOutputStream(msgClient.getOutputStream());
						clientOutputStream.add(OOS);
						chatOS.add(msgOOS);
						OOS.writeInt(clientOutputStream.size());

						for(int i=0;i<lines.size();i++)
							OOS.writeObject(lines.get(i));
						OOS.flush();

						Thread t = new Thread(new clientHandler(clientSocket));
						Thread t2 = new Thread(client);
						t.start();
						t2.start();
						System.out.println(clientOutputStream.size());
					}
				}
				catch(IOException | ClassNotFoundException e){
					e.printStackTrace();
				}
			}
		});
		thread.start();

//			this.frame.getContentPane().add(BorderLayout.SOUTH,ColorPicker);
//			this.frame.getContentPane().add(BorderLayout.NORTH,menubar);
//			this.frame.getContentPane().add(BorderLayout.CENTER,canvasScrollPane);
//			this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//			this.frame.setSize(800,600);
//			this.frame.setVisible(false);
	}

//	class pollClients implements Runnable {
//
//		int count = 0;
//		public pollClients(){
//			clientOutputStream = new ArrayList<>();
//		}
//
//		public void run(){
//
//		}
//	}
//
//
//	class messageHandler implements Runnable{
//		Socket msgSocketHandler;
//		ObjectInputStream msgOIS;
//		Message message;
//
//		public messageHandler(Socket msgSock){
//			try {
//				msgSocketHandler = msgSock;
//				msgOIS = new ObjectInputStream(msgSocketHandler.getInputStream());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//
//		@Override
//		public void run() {
//			try {
//				while(true)
//				{
//					int i=0;
//					while((message=(Message) msgOIS.readObject()) != null)
//					{
//						messages.add(message);
//						System.out.println(message.toString());
//					}
//					sendEveryone();
//				}
//			}
//			catch(SocketException e){
//				System.out.println("Socket terminated");
//				try {
//					messages.remove(msgSocketHandler.getOutputStream());
//					messages.remove(msgSocketHandler.getOutputStream());
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			catch(IOException e){
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		public void sendEveryone(){
//			Iterator<ObjectOutputStream> it = chatOS.iterator();
//			while(it.hasNext()){
//				ObjectOutputStream OOS = it.next();
//				try {
//					for(int i=0;i<messages.size();i++){
//						OOS.writeObject(messages.get(i));
//					}
//					OOS.writeObject(null);
//					OOS.flush();
//				}
//				catch(SocketException E){
//					System.out.println("Socket is terminated!");
//					continue;
//				}
//				catch(IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//	}

	class clientHandler implements Runnable {
		ObjectInputStream OIS;
		Socket sock;
		line l;

		public clientHandler(Socket clientSocket) throws ClassNotFoundException{
			try {
				sock = clientSocket;
				OIS = new ObjectInputStream(sock.getInputStream());
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while(true)
				{
					int i=0;
					while((l=(line)OIS.readObject()) != null)
					{
						if(i==0){
							lines.clear();
							i++;
						}
						lines.add(l);
					}
					sendEveryone();
					canvas.repaint();
				}
			}
			catch(SocketException e){
				System.out.println("Socket terminated");
				try {
					clientOutputStream.remove(sock.getOutputStream());
					clientOutputStream.remove(sock.getOutputStream());
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
			}
			catch(IOException e){
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
	}


	public void sendEveryone(){
		Iterator<ObjectOutputStream> it = clientOutputStream.iterator();
		while(it.hasNext()){
			ObjectOutputStream OOS = it.next();
			try {
				for(int i=0;i<lines.size();i++){
					OOS.writeObject(lines.get(i));
				}
				OOS.writeObject(null);
				OOS.flush();
			}
			catch(SocketException E){
				System.out.println("Socket is terminated");
				continue;
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void sendMessageToAllClients(String msg) {
		for (MessagessHandler o : clients) {
			o.sendMsg(msg);
		}

	}

	public void removeClient(MessagessHandler client) {
		clients.remove(client);
	}
}