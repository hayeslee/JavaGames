public class Piece {

	public int type;
	public int col;
	public int row;
	public int color;
	public int grid;
	public int width;
	public boolean isDropped = false;
	public boolean fromTop = true;
	
	
	public Piece (int type, int color, int grid)
	{
		this.type = type;
		this.color = color;
		this.grid = grid;
		this.row = 0;
		this.col = 0;		
	}
	
}
