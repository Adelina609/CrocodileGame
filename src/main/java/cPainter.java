import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class cPainter implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		JFrame frame = new JFrame("Collaborative Painter (client)");
		JMenuBar menubar = new JMenuBar();
		JMenu options;
		JMenuItem save;
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
		boolean isZoomIn=false,isZoomOut=false,isServer=false;
		int strokeSize,y,x;
		line tempLine,currentLine= new line(new ArrayList<Point>(),Color.black,sizeSlider.getValue(),1.0,1.0);
		Dot dot;
		Color color = new Color(red.getValue(),green.getValue(),blue.getValue());
		double cWidth=1.0,cHeight=1.0;
		int countOfClients = 0;
		
		transient ObjectInputStream OIS;
		transient ObjectOutputStream OOS;
		transient Socket sock;
		
		public cPainter(Socket sock){
			this.sock = sock;
		}
		
		public void createMenu(){
			this.options = new JMenu("option");
			this.save = new JMenuItem("save");
			this.exit = new JMenuItem("exit");
			this.menubar.add(options);
			this.options.add(save);
			this.options.add(exit);
			this.exit.addActionListener(new exitListener());
			this.save.addActionListener(new saveListener());
		}
		
		public void createScrollPane() {
			this.canvasScrollPane = new JScrollPane(this.canvasPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			this.canvasScrollPane.getVerticalScrollBar().addAdjustmentListener(new verticalAdjust());
			this.canvasScrollPane.getHorizontalScrollBar().addAdjustmentListener(new horizontalAdjust());
			this.canvasScrollPane.setPreferredSize(new Dimension(800,600));
		}
		
		
		public void createColorPicker(){
			zoomIn = new JButton("+");
			zoomOut = new JButton("-");
			zoomIn.addActionListener(new zoomInListener());
			zoomOut.addActionListener(new zoomOutListener());
			JPanel redContainer = new JPanel();
			JPanel greenContainer = new JPanel();
			JPanel blueContainer = new JPanel();
			redContainer.setBackground(Color.red);
			greenContainer.setBackground(Color.GREEN);
			blueContainer.setBackground(Color.blue);
			redContainer.add(red);
			greenContainer.add(green);
			blueContainer.add(blue);
			sliders.setLayout(new BoxLayout(sliders,BoxLayout.Y_AXIS));
			sliders.add(redContainer);sliders.add(blueContainer);sliders.add(greenContainer);
			red.addMouseMotionListener(new setColor());green.addMouseMotionListener(new setColor());blue.addMouseMotionListener(new setColor());
			colorBox.setBackground(new Color(red.getValue(),green.getValue(),blue.getValue()));
			colorBox.setPreferredSize(new Dimension(80,80));
			sizeDotPanel.setPreferredSize(new Dimension(80,80));
			sizeDotPanel.setBackground(Color.white);
			dot = new Dot();
			dot.setPreferredSize(new Dimension(20,20));
			sizeDotPanel.add(dot);
			sizeSliderPanel.add(sizeSlider);
			sizeSlider.setPreferredSize(new Dimension(20,80));
			sizeSlider.addMouseMotionListener(new sizeListener());
			ColorPicker.add(colorBox);
			ColorPicker.add(sliders);
			ColorPicker.add(sizeDotPanel);
			ColorPicker.add(sizeSliderPanel);
			ColorPicker.add(zoomIn);
			ColorPicker.add(zoomOut);
		}
		
		
		public void save(File choosenFile) throws FileNotFoundException, IOException {
			FileOutputStream fileOutputStream = new FileOutputStream(choosenFile);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this.lines);
			objectOutputStream.close();
		}
		
		
		public void createCanvas () {
			this.canvasPanel.add(canvas);
			this.canvasPanel.setSize(new Dimension(1200,1200));
			if(countOfClients == 1) {
				this.canvas.addMouseMotionListener(new canvasMouseMotionListener());
				this.canvas.addMouseListener(new canvasMouseListener());
			}
			this.canvas.setBackground(Color.white);
			this.canvas.setPreferredSize(new Dimension(1200,1200));
		}
		
		
		
		public void initialize(){
			this.createMenu();
			System.out.println("AAAAAAAAAAAa");
			setUpNetworking();
			System.out.println(countOfClients + "init");
			if(countOfClients == 1) {
				this.createColorPicker();
			}
			this.createCanvas();
			this.createScrollPane();
			
			Thread readerThread = new Thread(new IncomingReader());
			readerThread.start();
			
			this.frame.getContentPane().add(BorderLayout.SOUTH,ColorPicker);
			this.frame.getContentPane().add(BorderLayout.NORTH,menubar);
			this.frame.getContentPane().add(BorderLayout.CENTER,canvasScrollPane);
			this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			this.frame.setSize(800,600);
			this.frame.setVisible(true);

		}
		
		public void setUpNetworking(){
			try{
			OIS = new ObjectInputStream(sock.getInputStream());
			OOS = new ObjectOutputStream(sock.getOutputStream());
//			if(countOfClients == 0) {

				countOfClients = OIS.readInt();

				//System.out.println(countOfClients + "READINT");
//			}
			} catch (EOFException e){
				System.out.println("KKKKKKKKKEEEEEEEEEEEEEEEEEKKKKKKKKK "+e.getMessage());
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		class IncomingReader implements Runnable {
			line l;
			@Override
			public void run() {
				try {
					while(true){
						int i=0;
						while((l=(line)OIS.readObject()) != null)
						{
							if(i==0){
								lines.clear();
								i++;
							}
							lines.add(l);
						}
						if(l == null && i==0)
							lines.clear();
						canvas.repaint();
					}
				}
				catch (SocketException e){
					JFrame eFrame = new JFrame("Error");
					JPanel eMainPanel = new JPanel();
					JButton ok = new JButton("OK");
					eMainPanel.setLayout(new BoxLayout(eMainPanel,BoxLayout.Y_AXIS));
					eMainPanel.add(new JLabel("Host is gone!"));
					eMainPanel.add(ok);
					ok.addActionListener(new exitListener());
					eFrame.getContentPane().add(eMainPanel);
					frame.setVisible(false);
					eFrame.setSize(300,150);
					eFrame.setVisible(true);
				}
				catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
		}
		
		class zoomInListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				isZoomIn=true;
				cWidth+=0.1; cHeight += 0.1;
				canvasScrollPane.getVerticalScrollBar().setMaximum((int)(1200*cHeight));
				canvasScrollPane.getHorizontalScrollBar().setMaximum((int)(1200*cWidth));
				if(cWidth>1.0){
				canvasPanel.setPreferredSize(new Dimension((int)(1200*cWidth),(int)(1200*cHeight)));
				canvas.setPreferredSize(new Dimension((int)(1200*cWidth),(int)(1200*cHeight)));
				}
				canvasScrollPane.revalidate();
				canvas.repaint();
			}
			
		}
		
		class zoomOutListener implements ActionListener {
			public void actionPerformed(ActionEvent e){
				isZoomOut=true;
				cWidth -=0.1; cHeight -= 0.1;
				canvasScrollPane.getVerticalScrollBar().setMaximum((int)(1200*cHeight));
				canvasScrollPane.getHorizontalScrollBar().setMaximum((int)(1200*cWidth));
				
				canvasScrollPane.revalidate();
				canvas.repaint();
			}
			
		}
		
		class exitListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);	
			}
		}
		
		class canvasMouseMotionListener implements MouseMotionListener {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				point.add(e.getPoint());
				currentLine = new line(point,color,sizeSlider.getValue(),cWidth,cHeight);
				canvas.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
		class canvasMouseListener implements MouseListener {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				point.clear();
				point.add(e.getPoint());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				point.add(e.getPoint());
				tempLine = new line(point,color,sizeSlider.getValue(),cWidth,cHeight);
				lines.add(tempLine);
				System.out.println(countOfClients + "Mouse");
				try {
					if(countOfClients == 1) {
						for (int i = 0; i < lines.size(); i++)
							OOS.writeObject(lines.get(i));
						OOS.writeObject(null);
						OOS.flush();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				canvas.repaint();
			}
		}
		
		
		class setColor implements MouseMotionListener {

			@Override
			public void mouseDragged(MouseEvent arg0) {
				color = new Color(red.getValue(),green.getValue(),blue.getValue());
				colorBox.setBackground(color);
				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		}
		
		class sizeListener implements MouseMotionListener {

			@Override
			public void mouseDragged(MouseEvent e) {
				dot.repaint();
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		}
		
	
		class saveListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
				 int returnVal = chooser.showOpenDialog(null);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
			 try {
				 save(chooser.getSelectedFile());
			 	} catch (IOException e1) {
					e1.printStackTrace();
				}
				    }
			 }
		}
		
		
		
		
		
		class verticalAdjust implements AdjustmentListener {
			public void adjustmentValueChanged(AdjustmentEvent e){
				y=e.getValue();
			}
		}
		
		class horizontalAdjust implements AdjustmentListener {
			public void adjustmentValueChanged(AdjustmentEvent e){
				x=e.getValue();
			}
		}
		
		
		class Canvas extends JPanel {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g){
				Graphics2D g2d = (Graphics2D) g;
				{
					frame.repaint();
					if(isZoomIn){
						g2d.scale(cWidth, cHeight);
						isZoomIn=false;
						}
					else if(isZoomOut){
						g2d.scale(cWidth, cHeight);
						isZoomOut=false;
					}
					else
						g2d.scale(cWidth, cHeight);
				super.paintComponent(g2d);
				for(int j=0;j<lines.size();j++)
				{
					g2d.setStroke(new BasicStroke(lines.get(j).lineSize));
					g2d.setColor(lines.get(j).colors);
				for(int i=0;i<lines.get(j).points.size()-1;i++)
				{
					g2d.drawLine((int)(lines.get(j).points.get(i).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i).y/(lines.get(j).lineScaleY)), (int)(lines.get(j).points.get(i+1).x/(lines.get(j).lineScaleX)), (int)(lines.get(j).points.get(i+1).y/(lines.get(j).lineScaleY)));
				}
				}
				for(int i=0;i<currentLine.points.size()-1;i++){
					g2d.setStroke(new BasicStroke(sizeSlider.getValue()));
					g2d.setColor(color);
					g2d.drawLine((int)(currentLine.points.get(i).x/cWidth), (int)(currentLine.points.get(i).y/cHeight), (int)(currentLine.points.get(i+1).x/cWidth), (int)(currentLine.points.get(i+1).y/cHeight));
				}
				currentLine.points.clear();
			}
			}
		}
		
		class Dot extends JPanel{
			/**
			 * 
			 */
			private static final long serialVersionUID = 2L;

			public void paintComponent(Graphics g){
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.white);
				g2d.fillRect(0, 0, 20, 20);
				g2d.setColor(Color.black);
				g2d.fillOval(0, 0, sizeSlider.getValue(), sizeSlider.getValue());
			}
		}
}
