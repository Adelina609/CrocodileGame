//import javax.swing.*;
//import java.awt.*;
//
//public class Painter {
//
//    public void createColorPicker(){
//        zoomIn = new JButton("+");
//        zoomOut = new JButton("-");
//        zoomIn.addActionListener(new sPainter.zoomInListener());
//        zoomOut.addActionListener(new sPainter.zoomOutListener());
//        JPanel redContainer = new JPanel();
//        JPanel greenContainer = new JPanel();
//        JPanel blueContainer = new JPanel();
//        redContainer.setBackground(Color.red);
//        greenContainer.setBackground(Color.GREEN);
//        blueContainer.setBackground(Color.blue);
//        redContainer.add(red);
//        greenContainer.add(green);
//        blueContainer.add(blue);
//        sliders.setLayout(new BoxLayout(sliders,BoxLayout.Y_AXIS));
//        sliders.add(redContainer);sliders.add(blueContainer);sliders.add(greenContainer);
//        red.addMouseMotionListener(new sPainter.setColor());green.addMouseMotionListener(new sPainter.setColor());blue.addMouseMotionListener(new sPainter.setColor());
//        colorBox.setBackground(new Color(red.getValue(),green.getValue(),blue.getValue()));
//        colorBox.setPreferredSize(new Dimension(80,80));
//        sizeDotPanel.setPreferredSize(new Dimension(80,80));
//        sizeDotPanel.setBackground(Color.white);
//        dot = new sPainter.Dot();
//        dot.setPreferredSize(new Dimension(20,20));
//        sizeDotPanel.add(dot);
//        sizeSliderPanel.add(sizeSlider);
//        sizeSlider.setPreferredSize(new Dimension(20,80));
//        sizeSlider.addMouseMotionListener(new sPainter.sizeListener());
//        ColorPicker.add(colorBox);
//        ColorPicker.add(sliders);
//        ColorPicker.add(sizeDotPanel);
//        ColorPicker.add(sizeSliderPanel);
//        ColorPicker.add(zoomIn);
//        ColorPicker.add(zoomOut);
//    }
//
//}
