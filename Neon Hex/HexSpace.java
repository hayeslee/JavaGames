import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

public class HexSpace extends Polygon
{

	private Point position;
	private int status; // 0 = invisible, 1 = empty, 2 = player, 3 = computer
	private int x;
	private int y;
	private boolean isEdge;

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

	public void drawHexSpace(Graphics g, int row, int col)
	{

		g.setColor(Color.WHITE);
		if (status == 2)
			g.setColor(new Color(153, 244, 255));
		else if (status == 3)
			g.setColor(new Color(255, 102, 178));

		if (status != 0)
		{
			g.fillPolygon(this);
			g.setColor(Color.WHITE);
			g.drawPolygon(this);
		}
		g.setFont(new Font(Font.SERIF, Font.PLAIN, 10));
		g.setColor(Color.black);
		// g.drawString(row+":"+col, x, y);
	}

	public int getStatus()
	{
		return this.status;
	}

	public void changeStatus(int status)
	{
		if (this.status != 0)
			this.status = status;
	}

	public boolean isEdge()
	{
		return isEdge;
	}

}
