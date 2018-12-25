import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;


public class sPainter implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		JFrame frame = new JFrame("Collaborative Painter (Server)");
		JMenuBar menubar = new JMenuBar();
		JMenu options;
		JMenuItem clear;
		JMenuItem undo;
		JMenuItem save;
		JMenuItem load;
		JMenuItem exit;
		JSlider red = new JSlider(0,255);
		JSlider green = new JSlider(0,255);
		JSlider blue = new JSlider(0,255);
		JButton zoomIn;
		JButton zoomOut;
		JPanel ColorPicker = new JPanel();
		JPanel sliders = new JPanel();
		JPanel colorBox = new JPanel();
		JPanel sizeDotPanel = new JPanel();
		JPanel sizeSliderPanel = new JPanel();
		JSlider sizeSlider = new JSlider(JSlider.VERTICAL,10,20,10);
		JPanel canvasPanel = new JPanel();
		Canvas canvas = new Canvas();
		JScrollPane canvasScrollPane;
		ArrayList<Point> point = new ArrayList<>();
		ArrayList<line> lines = new ArrayList<>();
		boolean isClear=false,isUndo=false,isZoomIn=false,isZoomOut=false,isServer=false,client=false;
		int strokeSize,y,x;
		line tempLine,currentLine= new line(new ArrayList<Point>(),Color.black,sizeSlider.getValue(),1.0,1.0);
//		Dot dot;
		Color color = new Color(red.getValue(),green.getValue(),blue.getValue());
		double cWidth=1.0,cHeight=1.0;
		transient ServerSocket ss;
		transient ArrayList<ObjectOutputStream> clientOutputStream;
		
		public sPainter(ServerSocket ss){
			this.ss = ss;
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

			Thread thread = new Thread(new pollClients());
			thread.start();
			
//			this.frame.getContentPane().add(BorderLayout.SOUTH,ColorPicker);
//			this.frame.getContentPane().add(BorderLayout.NORTH,menubar);
//			this.frame.getContentPane().add(BorderLayout.CENTER,canvasScrollPane);
//			this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//			this.frame.setSize(800,600);
//			this.frame.setVisible(false);
		}
		
		class pollClients implements Runnable {

			int count = 0;
			public pollClients(){
			clientOutputStream = new ArrayList<>();
			}

			public void run(){
			try{
				while(true)
				{
					Socket clientSocket = ss.accept();
					ObjectOutputStream OOS = new ObjectOutputStream(clientSocket.getOutputStream());
					clientOutputStream.add(OOS);
					OOS.writeInt(clientOutputStream.size());
						for(int i=0;i<lines.size();i++)
						OOS.writeObject(lines.get(i));
//						if(count == 0) {

//							++count;
//						}
						OOS.flush();
					
					Thread t = new Thread(new clientHandler(clientSocket));
					t.start();
					System.out.println(clientOutputStream.size());
				}
			}
			catch(IOException | ClassNotFoundException e){
				e.printStackTrace();
			}
			}
		}

	public ArrayList<ObjectOutputStream> getClientOutputStream() {
		return clientOutputStream;
	}

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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				catch(IOException e){
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		
		
//		class zoomInListener implements ActionListener {
//			public void actionPerformed(ActionEvent e){
//				isZoomIn=true;
//				cWidth+=0.1; cHeight += 0.1;
//				canvasScrollPane.getVerticalScrollBar().setMaximum((int)(1200*cHeight));
//				canvasScrollPane.getHorizontalScrollBar().setMaximum((int)(1200*cWidth));
//				if(cWidth>1.0){
//				canvasPanel.setPreferredSize(new Dimension((int)(1200*cWidth),(int)(1200*cHeight)));
//				canvas.setPreferredSize(new Dimension((int)(1200*cWidth),(int)(1200*cHeight)));
//				}
//				canvasScrollPane.revalidate();
//				canvas.repaint();
//			}
//
//		}
//
//		class zoomOutListener implements ActionListener {
//			public void actionPerformed(ActionEvent e){
//				isZoomOut=true;
//				cWidth -=0.1; cHeight -= 0.1;
//				canvasScrollPane.getVerticalScrollBar().setMaximum((int)(1200*cHeight));
//				canvasScrollPane.getHorizontalScrollBar().setMaximum((int)(1200*cWidth));
//
//				canvasScrollPane.revalidate();
//				canvas.repaint();
//			}
//
//		}
//
//		class exitListener implements ActionListener {
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		}
//
//		class canvasMouseMotionListener implements MouseMotionListener {
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				point.add(e.getPoint());
//				currentLine = new line(point,color,sizeSlider.getValue(),cWidth,cHeight);
//				canvas.repaint();
//
//			}
//
//			@Override
//			public void mouseMoved(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//		}
//
//		class canvasMouseListener implements MouseListener {
//
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void mouseExited(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				point.clear();
//				point.add(e.getPoint());
//
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				point.add(e.getPoint());
//				tempLine = new line(point,color,sizeSlider.getValue(),cWidth,cHeight);
//				lines.add(tempLine);
//
//				sendEveryone();
//
//				canvas.repaint();
//			}
//		}
//
//		class setColor implements MouseMotionListener {
//
//			@Override
//			public void mouseDragged(MouseEvent arg0) {
//				color = new Color(red.getValue(),green.getValue(),blue.getValue());
//				colorBox.setBackground(color);
//
//			}
//
//			@Override
//			public void mouseMoved(MouseEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		}
//
//		class sizeListener implements MouseMotionListener {
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				dot.repaint();
//
//			}
//
//			@Override
//			public void mouseMoved(MouseEvent e) {
//				// TODO Auto-generated method stub
//			}
//		}
//
//		class clearListener implements ActionListener {
//			public void actionPerformed(ActionEvent e){
//				isClear=true;
//				lines.clear();
//				sendEveryone();
//				canvas.repaint();
//			}
//		}
//
//		class undoListener implements ActionListener {
//			public void actionPerformed(ActionEvent e){
//				isUndo=true;
//				lines.remove(lines.size()-1);
//				lines.remove(lines.size()-1);
//				if(lines.size()==0)
//					lines.clear();
//				sendEveryone();
//				canvas.repaint();
//			}
//		}
//
//		class saveListener implements ActionListener {
//			public void actionPerformed(ActionEvent e) {
//				JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
//				 int returnVal = chooser.showOpenDialog(null);
//				    if(returnVal == JFileChooser.APPROVE_OPTION) {
//			 try {
//				 save(chooser.getSelectedFile());
//			 	} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				    }
//			 }
//		}
//		class loadListener implements ActionListener {
//			public void actionPerformed(ActionEvent e) {
//				JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
//				 int returnVal = chooser.showOpenDialog(null);
//				    if(returnVal == JFileChooser.APPROVE_OPTION) {
//				try {
//				 load(chooser.getSelectedFile());
//			 	} catch (ClassNotFoundException | IOException e1) {
//					e1.printStackTrace();
//				}
//				sendEveryone();
//			 	frame.repaint();
//				}
//			}
//		}
//
//
//
//
//		class verticalAdjust implements AdjustmentListener {
//			public void adjustmentValueChanged(AdjustmentEvent e){
//				y=e.getValue();
//			}
//		}
//
//		class horizontalAdjust implements AdjustmentListener {
//			public void adjustmentValueChanged(AdjustmentEvent e){
//				x=e.getValue();
//			}
//		}
//
//
//		class Canvas extends JPanel {
//
//			/**
//			 *
//			 */
//			private static final long serialVersionUID = 1L;
//
//			public void paintComponent(Graphics g){
//				Graphics2D g2d = (Graphics2D) g;
//				if (isClear){
//				g2d.scale(cWidth, cHeight);
//				super.paintComponent(g2d);
//				isClear=false;
//				}
//				else if(isUndo)
//				{
//					g2d.scale(cWidth, cHeight);
//					super.paintComponent(g2d);
//					for(int j=0;j<lines.size();j++)
//					{
//						g2d.setStroke(new BasicStroke(lines.get(j).lineSize));
//						g2d.setColor(lines.get(j).colors);
//					for(int i=0;i<lines.get(j).points.size()-1;i++)
//					{
//						g2d.drawLine((int)(lines.get(j).points.get(i).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i).y/(lines.get(j).lineScaleY)), (int)(lines.get(j).points.get(i+1).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i+1).y/(lines.get(j).lineScaleY)));
//					}
//					}
//					isUndo=false;
//				}
//				else
//				{
//					frame.repaint();
//					if(isZoomIn){
//						g2d.scale(cWidth, cHeight);
//						isZoomIn=false;
//						}
//					else if(isZoomOut){
//						g2d.scale(cWidth, cHeight);
//						isZoomOut=false;
//					}
//					else
//						g2d.scale(cWidth, cHeight);
//				super.paintComponent(g2d);
//				for(int j=0;j<lines.size();j++)
//				{
//					g2d.setStroke(new BasicStroke(lines.get(j).lineSize));
//					g2d.setColor(lines.get(j).colors);
//				for(int i=0;i<lines.get(j).points.size()-1;i++)
//				{
//					g2d.drawLine((int)(lines.get(j).points.get(i).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i).y/(lines.get(j).lineScaleY)), (int)(lines.get(j).points.get(i+1).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i+1).y/(lines.get(j).lineScaleY)));
//				}
//				}
//				for(int i=0;i<currentLine.points.size()-1;i++){
//					g2d.setStroke(new BasicStroke(sizeSlider.getValue()));
//					g2d.setColor(color);
//					g2d.drawLine((int)(currentLine.points.get(i).x/cWidth), (int)(currentLine.points.get(i).y/cHeight), (int)(currentLine.points.get(i+1).x/cWidth), (int)(currentLine.points.get(i+1).y/cHeight));
//				}
//				currentLine.points.clear();
//			}
//			}
//		}
//
//		class Dot extends JPanel{
//			/**
//			 *
//			 */
//			private static final long serialVersionUID = 2L;
//
//			public void paintComponent(Graphics g){
//				Graphics2D g2d = (Graphics2D) g;
//				g2d.setColor(Color.white);
//				g2d.fillRect(0, 0, 20, 20);
//				g2d.setColor(Color.black);
//				g2d.fillOval(0, 0, sizeSlider.getValue(), sizeSlider.getValue());
//			}
//		}
}
