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
public class HexSpace extends Polygon
{
	private int status; // 0 = invisible, 1 = empty, 2 = player, 3 = computer
	private int x;
	private int y;
	private boolean isEdge;

	/**
	 * Constructs a HexSpace
	 * 
	 * @param status status of space
	 * @param x x coordinate of top left corner
	 * @param y y coordinate of top left corner
	 * @param isEdge if the space is on an edge
	 */
	public HexSpace(int status, int x, int y, boolean isEdge)
	{
		this.status = status;
		this.x = x;
		this.y = y;
		this.isEdge = isEdge;
		for (int i = 0; i < 6; i++)
			this.addPoint((int) (x + 20 * Math.sin(i * 2 * Math.PI / 6)),
					(int) (y + 20 * Math.cos(i * 2 * Math.PI / 6)));
	}

	/**
	 * Draws a HexSpace
	 * 
	 * @param g graphics
	 * @param row the row of the space
	 * @param col the col of the space
	 */
	public void drawHexSpace(Graphics g, int row, int col)
	{
		g.setColor(Color.WHITE);
		if (status == 2)
			g.setColor(new Color(153, 244, 255));
		else if (status == 3)
			g.setColor(new Color(255, 102, 178));
		 
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
		if (this.status != 0)
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
