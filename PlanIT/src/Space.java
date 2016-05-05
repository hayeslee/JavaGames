import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

/**
 * Keeps track of HexSpavce variables and data.
 * 
 * @author Jeffrey and Hayes
 * @version June 2015
 */
@SuppressWarnings("serial")
public class Space extends Polygon
{
	private int status; // 0 = invisible, 1 = empty, 2 = yellow, 3 = red, 4 = green, 5 = purple, 6 = blue
	private int x;
	private int y;
	private boolean isEdge;
	private final int PIECE_SIZE = 12;

	/**
	 * Constructs a HexSpace
	 * 
	 * @param status status of space
	 * @param x x coordinate of top left corner
	 * @param y y coordinate of top left corner
	 * @param isEdge if the space is on an edge
	 */
	public Space(int status, int x, int y, boolean isEdge)
	{
		this.status = status;
		this.x = x;
		this.y = y;
		this.isEdge = isEdge;
		this.addPoint(x, y);
		this.addPoint(x, y + PIECE_SIZE);
		this.addPoint(x + PIECE_SIZE, y + PIECE_SIZE);
		this.addPoint(x + PIECE_SIZE, y);
		
	}

	/**
	 * Draws a HexSpace
	 * 
	 * @param g graphics
	 * @param row the row of the space
	 * @param col the col of the space
	 */
	public void drawSpace(Graphics g, int row, int col)
	{
		if (status == 1)
			g.setColor(Color.BLACK);
		else if (status == 2)
			g.setColor(Color.YELLOW);
		else if (status == 3)
			g.setColor(Color.red);
		else if (status == 4)
			g.setColor(Color.green);
		else if (status == 5)
			g.setColor(Color.MAGENTA);
		else if (status == 6)
			g.setColor(Color.blue);
		else if (status == 7)
			g.setColor(Color.white);
		 
		// Fill space and draw it
		if (status != 0)
		{
			g.fillPolygon(this);
			g.setColor(Color.WHITE);
			g.drawPolygon(this);
		}
		g.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
		g.setColor(Color.black);
	}

	/**
	 * Gets status of space
	 * @return status of space
	 */
	public int getStatus()
	{
		return this.status;
	}

	/**
	 * Changes status of space
	 * @param status new status
	 */
	public void changeStatus(int status)
	{
		this.status = status;
	}

	/**
	 * Checks if the space is an edge
	 * @return if the space is an edge
	 */
	public boolean isEdge()
	{
		return isEdge;
	}

}
