import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;


public class line implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5109719347545330388L;
	ArrayList<Point> points;
	Color colors;
	int lineSize;
	double lineScaleX,lineScaleY;
	public line(ArrayList<Point> point, Color color, int Size, double lineX, double lineY ) {
		this.points=new ArrayList<Point>(point);
		this.colors= color;
		this.lineSize = Size;
		this.lineScaleX = lineX;
		this.lineScaleY = lineY;
	}
}