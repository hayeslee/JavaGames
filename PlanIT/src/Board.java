
/** 
 * Keeps track of the area to draw the HexSpaces for a game of Neon Hex
 * Includes code to drop pieces in the panel
 * @author Jeffrey and Hayes
 * @version February 2016
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.applet.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class Board extends JPanel implements MouseListener, MouseMotionListener
{
	// Constants for the table layout
	static final int WIDTH = 256;
	static final int HEIGHT = 1152;
	private static final int HEX_WIDTH = 12;
	private final int TIME_INTERVAL = 1000;

	private int topOffset = 100;
	private int leftOffset = -100;
	private final int ROW_LENGTH = 15;
	private Space[][] board;
	private Space[][] currentBoard;
	private Space[][] board90;
	private Space[][] board180;
	private Space[][] board270;
	private Space[][] boardTemp;
	private Space[][] topDrop = new Space[2][ROW_LENGTH];
	private Space[][] botDrop = new Space[2][ROW_LENGTH];
	private boolean gameOver;
	private ImageIcon penguin;
	private Image foreground;
	private ImageIcon background;
	private ImageIcon rules, comingSoon, help3;
	private File layoutThree;
	private Image menu;
	private boolean showMenu;
	private boolean showrules;
	private boolean showComingSoon;
	private boolean showHelp3;
	private int level, score = 000;
	private int noOfRows;
	private int noOfCols;
	private boolean fromMainMenu;
	private ArrayList<Integer> lastRows;
	private ArrayList<Integer> lastCols;
	private boolean topMoving = true;
	private boolean botMoving = true;
	private int botDirection = 0;
	private int topDirection = 0;
	public Piece topPiece;
	public Piece botPiece;
	public Piece currentPiece = null;
	boolean wasDoubleClick;
	public int rotate = 0;
	public boolean invalidPlacement = false;

	// Timer event
	private Timer timer;
	private boolean timerOn;
	private int time;
	private int timeAllowed;

	public enum Colors {
		WHITE(1), YELLOW(2), RED(3), GREEN(4), PURPLE(5), BLUE(6);

		private final int value;

		private Colors(int value)
		{
			this.value = value;
		}
	}
	private final int SINGLE = 1;
	private final int DOUBLE = 2;
	private final int L = 3;
	private final int T = 4;
	private final int QUAD = 5;

	/**
	 * @throws IOException
	 * 
	 * 
	 */
	public Board() throws IOException
	{

		// Sets up the size and colour and font for this Panel
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFont(new Font("Arial", Font.PLAIN, 18));
		this.setBackground(Color.BLACK);
		rules = new ImageIcon("GDCRulesSmall.gif");
		comingSoon = new ImageIcon("GDCFutureSmall.png");
		help3 = new ImageIcon("help3.gif");
		background = new ImageIcon("GameSmall.gif");
		// Add listeners to handle mouse events to select, drag and drop pieces
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		layoutThree = new File("layout1.txt");

		showMenu = true;
		showrules = false;
		showComingSoon = false;
		showHelp3 = false;

		playSound();
		repaint();
	}

	/**
	 * Starts a new game by creating board
	 * 
	 * @throws IOException
	 */
	public void newGame() throws IOException
	{
		lastRows = new ArrayList<Integer>();
		lastCols = new ArrayList<Integer>();
		level = 0;
		createBoard(level);
		gameOver = false;
		showMenu = false;
		currentPiece = topPiece;
		score = 0;
		
		// Main Game Timer
		timerOn = true;
		time = 0;
		timeAllowed = 1800;
		timer = new Timer(TIME_INTERVAL, new TimerEventHandler());
		timer.start();
		repaint();
	}

	/**
	 * Creates the specified board
	 * 
	 * @param level the level of the board
	 * @throws IOException
	 */
	private void createBoard(int level) throws IOException
	{
		BufferedReader boardFile;

		leftOffset = 40;
		topOffset = 540;
		boardFile = new BufferedReader(new FileReader(layoutThree));

		String dimensionStr = boardFile.readLine();

		noOfRows = (dimensionStr.charAt(0) - '0') * 10 + dimensionStr.charAt(1)
				- '0';
		noOfCols = (dimensionStr.charAt(3) - '0') * 10 + dimensionStr.charAt(4)
				- '0';

		// Set up the array
		board = new Space[noOfRows][noOfCols];
		boardTemp = new Space[noOfRows][noOfCols];
		board90 = new Space[noOfRows][noOfCols];
		board180 = new Space[noOfRows][noOfCols];
		board270 = new Space[noOfRows][noOfCols];

		// Where the game space is going to start
		int currentX = leftOffset;
		int currentY = topOffset;

		for (int row = 0; row < noOfRows; row++)
		{
			String rowStr = boardFile.readLine();
			for (int col = 0; col < noOfCols; col++)
			{

				int space = rowStr.charAt(col) - '0';

				if (space > 1)
				{
					board[row][col] = new Space(space, currentX, currentY,
							true);
					board90[row][col] = new Space(space, currentX, currentY,
							true);
					board180[row][col] = new Space(space, currentX, currentY,
							true);
					board270[row][col] = new Space(space, currentX, currentY,
							true);
					boardTemp[row][col] = new Space(space, currentX, currentY,
							true);
				}
				else
				{
					board[row][col] = new Space(space, currentX, currentY,
							false);
					board90[row][col] = new Space(space, currentX, currentY,
							false);
					board180[row][col] = new Space(space, currentX, currentY,
							false);
					board270[row][col] = new Space(space, currentX, currentY,
							false);
					boardTemp[row][col] = new Space(space, currentX, currentY,
							false);
				}

				// Change the currentX point
				currentX += HEX_WIDTH;
			}

			currentX = leftOffset;

			// Change the currentY point
			currentY += (HEX_WIDTH);
		}
		boardFile.close();

		currentX = 40;
		currentY = 420;

		for (int i = 0; i < topDrop.length; i++)
		{
			for (int col = 0; col < topDrop[0].length; col++)
			{
				topDrop[i][col] = new Space(0, currentX, currentY, true);
				currentX += HEX_WIDTH;
			}
			currentX = leftOffset;
			currentY += (HEX_WIDTH);
		}

		currentX = 40;
		currentY = 800;

		for (int i = 0; i < botDrop.length; i++)
		{
			for (int col = 0; col < botDrop[0].length; col++)
			{
				botDrop[i][col] = new Space(0, currentX, currentY, true);
				currentX += HEX_WIDTH;
			}
			currentX = leftOffset;
			currentY += (HEX_WIDTH);
		}

		addTopPiece();
		addBotPiece();

		currentBoard = board;

		repaint();
	}

	private void addTopPiece()
	{
		topPiece = new Piece((int) (Math.random() * 5 + 1),
				(int) (Math.random() * 5 + 2), 0);
		drawPiece(topPiece);
	}

	private void addBotPiece()
	{
		botPiece = new Piece((int) (Math.random() * 5 + 1),
				(int) (Math.random() * 5 + 2), 2);
		botPiece.fromTop = false;
		drawPiece(botPiece);
	}

	// Rotates current board by 90 degrees
	private void rotateBoard()
	{
		for (int i = 0; i < 15; i++)
		{
			for (int j = 0; j < 15; j++)
			{
				int status = currentBoard[i][j].getStatus();
				boardTemp[j][i].changeStatus(status);
			}
		}
		// Reverse each row
		for (int i = 0; i < 15; i++)
		{
			for (int j = 0; j < 15; j++)
			{
				int status = boardTemp[i][j].getStatus();
				if (currentBoard == board)
					board90[i][14 - j].changeStatus(status);
				else if (currentBoard == board90)
					board180[i][14 - j].changeStatus(status);
				else if (currentBoard == board180)
					board270[i][14 - j].changeStatus(status);
				else if (currentBoard == board270)
					board[i][14 - j].changeStatus(status);
			}
		}

		if (currentBoard == board)
			currentBoard = board90;
		else if (currentBoard == board90)
			currentBoard = board180;
		else if (currentBoard == board180)
			currentBoard = board270;
		else if (currentBoard == board270)
			currentBoard = board;

		// for (int i = 0; i < board.length; i++)
		// {
		// for (int j = 0; j < board[0].length; j++)
		// {
		// System.out.print(currentBoard[i][j].getStatus());
		// }
		// System.out.println();
		// }

		repaint();
	}

	/**
	 * Handles the events caused by the timer
	 * 
	 */
	private class TimerEventHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// If game is over
			if (time >= timeAllowed && timerOn)
			{
				timerOn = false;
				gameOver = true;
				timer.stop();
				JOptionPane.showMessageDialog(Board.this, "You scored: " + score + "!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
				int response = JOptionPane.showConfirmDialog(Board.this,
						"Would you like to restart the game?", "New Game",
						JOptionPane.YES_NO_OPTION);
				// Get response and start a new game
				if (response == JOptionPane.NO_OPTION
						|| response == JOptionPane.CLOSED_OPTION)
				{
					showMenu = true;
				}
				else if (response == JOptionPane.YES_OPTION)
				{
					try
					{
						newGame();
					}
					catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
				repaint();
			}
			else if ((time < timeAllowed && timerOn))
			{
				// Time increases
				time += 8;

				boolean firstInRow = true;
				// Move pieces over
				if (topMoving)
				{
					if (topDirection == 0)
					{
						for (int i = 0; i < topDrop.length; i++)
						{
							int count = 0;
							for (int k = 0; k < topDrop[0].length; k++)
							{
								if (topDrop[i][k].getStatus() != 0)
									count++;
							}
							int count2 = 0;
							for (int j = 0; j < topDrop[0].length; j++)
							{
								if (topDrop[i][topDrop[0].length - 1]
										.getStatus() != 0)
								{
									topDirection = 1;
									break;
								}

								if (count2 == count)
									break;
								if (topDrop[i][j].getStatus() != 0
										&& j < topDrop[0].length - 1)
								{
									topDrop[i][j + 1].changeStatus(
											topDrop[i][j].getStatus());
									if (firstInRow)
									{
										topPiece.col = j + 1;
										topDrop[i][j].changeStatus(0);
										firstInRow = false;
									}
									count2++;
								}
							}
							firstInRow = true;
						}
					}
					else if (topDirection == 1)
					{
						for (int i = topDrop.length - 1; i > -1; i--)
						{
							int count = 0;
							for (int k = 0; k < topDrop[0].length; k++)
							{
								if (topDrop[i][k].getStatus() != 0)
									count++;
							}
							int count2 = 0;
							for (int j = topDrop[0].length - 1; j > -1; j--)
							{
								if (topDrop[i][0].getStatus() != 0)
								{
									topDirection = 0;
									break;
								}
								if (count2 == count)
									break;
								if (topDrop[i][j].getStatus() != 0 && j > 0)
								{
									topDrop[i][j - 1].changeStatus(
											topDrop[i][j].getStatus());
									if (firstInRow)
									{
										topPiece.col = j - 1;
										topDrop[i][j].changeStatus(0);
										firstInRow = false;
									}
									count2++;
								}
							}
							firstInRow = true;
						}
					}
				}
				firstInRow = true;
				if (botMoving)
				{
					if (botDirection == 0)
					{
						for (int i = 0; i < botDrop.length; i++)
						{
							int count = 0;
							for (int k = 0; k < botDrop[0].length; k++)
							{
								if (botDrop[i][k].getStatus() != 0)
									count++;
							}
							int count2 = 0;
							for (int j = 0; j < botDrop[0].length; j++)
							{
								if (botDrop[i][botDrop[0].length - 1]
										.getStatus() != 0)
								{
									botDirection = 1;
									break;
								}
								if (count2 == count)
									break;
								if (botDrop[i][j].getStatus() != 0
										&& j < botDrop[0].length - 1)
								{
									botDrop[i][j + 1].changeStatus(
											botDrop[i][j].getStatus());
									if (firstInRow)
									{
										botPiece.col = j + 1;
										botDrop[i][j].changeStatus(0);
										firstInRow = false;
									}
									count2++;
								}
							}
							firstInRow = true;
						}
					}
					else if (botDirection == 1)
					{
						for (int i = botDrop.length - 1; i > -1; i--)
						{
							int count = 0;
							for (int k = 0; k < botDrop[0].length; k++)
							{
								if (botDrop[i][k].getStatus() != 0)
									count++;
							}
							int count2 = 0;
							for (int j = botDrop[0].length - 1; j > -1; j--)
							{
								if (botDrop[i][0].getStatus() != 0)
								{
									botDirection = 0;
									break;
								}
								if (count2 == count)
									break;
								if (botDrop[i][j].getStatus() != 0 && j > 0)
								{
									botDrop[i][j - 1].changeStatus(
											botDrop[i][j].getStatus());
									if (firstInRow)
									{
										botPiece.col = j - 1;
										botDrop[i][j].changeStatus(0);
										firstInRow = false;
									}
									count2++;
								}
							}
							firstInRow = true;
						}
					}
				}
				if (rotate != 2)
				{
					rotate++;
				}
				else
				{
					rotateBoard();
					rotate = 0;
				}
			}
			repaint();
		}
	}

	/**
	 * 
	 */
	private void drawPiece(Piece piece)
	{
		int grid = piece.grid;
		int row = piece.row;
		int col = piece.col;
		int type = piece.type;
		int colour = piece.color;
		if (grid == 0)
		{
			switch (type)
			{
			case SINGLE:
				topDrop[row][col].changeStatus(colour);
				break;
			case DOUBLE:
				topDrop[row][col].changeStatus(colour);
				topDrop[row][col + 1].changeStatus(colour);
				break;
			case L:
				topDrop[row][col].changeStatus(colour);
				topDrop[row][col + 1].changeStatus(colour);
				topDrop[row + 1][col + 1].changeStatus(colour);
				break;
			case T:
				topDrop[row + 1][col].changeStatus(colour);
				topDrop[row + 1][col + 1].changeStatus(colour);
				topDrop[row + 1][col + 2].changeStatus(colour);
				topDrop[row][col + 1].changeStatus(colour);
				repaint();
				break;
			case QUAD:
				topDrop[row][col].changeStatus(colour);
				topDrop[row][col + 1].changeStatus(colour);
				topDrop[row + 1][col + 1].changeStatus(colour);
				topDrop[row + 1][col].changeStatus(colour);
				break;
			}
		}

		else if (grid == 2)
		{
			switch (type)
			{
			case SINGLE:
				botDrop[row][col].changeStatus(colour);
				break;
			case DOUBLE:
				botDrop[row][col].changeStatus(colour);
				botDrop[row][col + 1].changeStatus(colour);
				break;
			case L:
				botDrop[row + 1][col].changeStatus(colour);
				botDrop[row][col + 1].changeStatus(colour);
				botDrop[row + 1][col + 1].changeStatus(colour);
				break;
			case T:
				botDrop[row][col].changeStatus(colour);
				botDrop[row][col + 1].changeStatus(colour);
				botDrop[row][col + 2].changeStatus(colour);
				botDrop[row + 1][col + 1].changeStatus(colour);
				break;
			case QUAD:
				botDrop[row][col].changeStatus(colour);
				botDrop[row][col + 1].changeStatus(colour);
				botDrop[row + 1][col + 1].changeStatus(colour);
				botDrop[row + 1][col].changeStatus(colour);
				break;
			}
		}
		else if (grid == 1)
		{
			if (piece.fromTop)
			{
				switch(type)
				{
					case SINGLE:  
						currentBoard[row][col].changeStatus(colour);
						break;
					case DOUBLE:  
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						break;
					case L: 
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						currentBoard[row+1][col+1].changeStatus(colour);
						break;
					case T:  
						currentBoard[row + 1][col].changeStatus(colour);
						currentBoard[row + 1][col+1].changeStatus(colour);
						currentBoard[row + 1][col+2].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						break;
					case QUAD:  
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						currentBoard[row+1][col+1].changeStatus(colour);
						currentBoard[row+1][col].changeStatus(colour);
						break;
				}	
			}
			else if (!piece.fromTop)
			{
				switch(type)
				{
					case SINGLE:  
						currentBoard[row][col].changeStatus(colour);
						break;
					case DOUBLE:  
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						break;
					case L: 
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row-1][col+1].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						break;
					case T:  
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						currentBoard[row][col+2].changeStatus(colour);
						currentBoard[row+1][col+1].changeStatus(colour);
						break;
					case QUAD:  
						currentBoard[row][col].changeStatus(colour);
						currentBoard[row][col+1].changeStatus(colour);
						currentBoard[row+1][col+1].changeStatus(colour);
						currentBoard[row+1][col].changeStatus(colour);
						break;
				}	
			}
		}
		repaint();
	}

	/**
	 * Calculates the score of the board.
	 * 
	 * @return score the score
	 */
	private int scoreBoard()
	{
		return 0;
	}

	/**
	 * 
	 * @param piece
	 */
	private void dropPiece(Piece piece)
	{
		if (piece.grid == 0)
		{
			piece.grid = 1;
			// Check board at specific column(s)

			for (int i = 0; i < board.length; i++)
			{
				if (currentBoard[i][piece.col].getStatus() != 0)
				{
					if (piece.type == 1)
					{
						// TAKE ROW ABOVE
						piece.row = (i - 1);
					}
					else if (piece.type == 2)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i - 1][piece.col + 1]
								.getStatus() != 0)
						{
							i--;
						}
						piece.row = i - 1;
					}
					else if (piece.type == 3)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i][piece.col + 1].getStatus() != 0)
						{
							i--;
						}
						piece.row = i - 1;
					}
					else if (piece.type == 4)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i][piece.col + 1].getStatus() != 0)
						{
							i--;
						}
						while (currentBoard[i][piece.col + 2].getStatus() != 0)
						{
							i--;
						}
						piece.row = i - 1;
					}
					else if (piece.type == 5)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i][piece.col + 1].getStatus() != 0)
						{
							i--;
						}
						piece.row = i - 1;
					}

					break;
				}
			}

			if ((piece.row == 0) || (piece.row == -1))
				invalidPlacement = true;

			for (int i = 0; i < topDrop.length; i++)
			{
				for (int j = 0; j < topDrop[0].length; j++)
					topDrop[i][j].changeStatus(0);
			}
			addTopPiece();

		}

		else if (piece.grid == 2)
		{
			piece.grid = 1;
			// Check board at specific column(s)

			for (int i = currentBoard[0].length; i > 0; i++)
			{
				if (currentBoard[i][piece.col].getStatus() != 0)
				{
					if (piece.type == 1)
					{
						// TAKE ROW BELOW
						piece.row = (i + 1);
					}
					else if (piece.type == 2)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i + 1][piece.col + 1]
								.getStatus() != 0)
						{
							i++;
						}
						piece.row = i + 1;
					}
					else if (piece.type == 3)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i][piece.col + 1].getStatus() != 0)
						{
							i++;
						}
						piece.row = i + 1;
					}
					else if (piece.type == 4)
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i + 1][piece.col + 1]
								.getStatus() != 0)
						{
							i++;
						}
						while (currentBoard[i + 1][piece.col + 2]
								.getStatus() != 0)
						{
							i++;
						}
						piece.row = i + 1;
					}
					else
					{
						// While piece beside desired spot is not empty
						// Continue checking upwards
						while (currentBoard[i][piece.col + 1].getStatus() != 0)
						{
							i++;
						}
						piece.row = i + 1;
					}
					break;
				}
			}

			for (int i = 0; i < botDrop.length; i++)
			{
				for (int j = 0; j < botDrop[0].length; j++)
					botDrop[i][j].changeStatus(0);
			}
			addBotPiece();

		}

		if (!invalidPlacement)
		{
			drawPiece(piece);
			score += (piece.type * 10);
		}
		else
			invalidPlacement = false;
	}

	/**
	 * Paints the drawing area
	 * 
	 * @param g the graphics context to paint
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		if (showMenu)
		{
			menu = new ImageIcon("GDCHomeSmallFinal.png").getImage();
			g.drawImage(menu, 0, 0, null);

		}
		else if (showrules)
		{
			Image image = rules.getImage();
			g.drawImage(image, 0, 0, this);

		}
		else if (showComingSoon)
		{
			Image image = comingSoon.getImage();
			g.drawImage(image, 0, 0, this);
		}
		else if (showHelp3)
		{
			Image image = help3.getImage();
			g.drawImage(image, 0, 0, this);
		}
		else if (showrules == false && showComingSoon == false
				&& showMenu == false)
		{
			g.drawImage(background.getImage(), 0, 0, null);

			for (int row = 0; row < board.length; row++)
			{
				for (int col = 0; col < board[0].length; col++)
				{
					currentBoard[row][col].drawSpace(g, row, col);
				}
			}

			for (int row = 0; row < topDrop.length; row++)
			{
				for (int col = 0; col < topDrop[0].length; col++)
				{
					topDrop[row][col].drawSpace(g, row, col);
					botDrop[row][col].drawSpace(g, row, col);
				}
			}

			penguin = new ImageIcon("penguin.gif");
			Image image = penguin.getImage();
			g.drawImage(image, 740, 120, null);

			// Timer
			g.setColor(Color.WHITE);
			g.setFont(new Font("Sans Serif", Font.BOLD, 30));
			g.drawString("    " + (time / 10) + ":00", 80, 1041  );
			// Score
			g.drawString("      " + score, 100, 1108);
		}
		repaint();
	}

	/**
	 * Refresh the drawing area immediately Immediate refresh is needed to show
	 * the animation
	 */
	private void rePaintDrawingAreaImmediately()
	{
		paintImmediately(new Rectangle(0, 0, getWidth(), getHeight()));
	}

	/**
	 * Handles a mousePress when selecting a spot to place a piece or picking up
	 * a piece
	 * 
	 * @param event the event information
	 */
	public void mousePressed(MouseEvent event)
	{
		Point clickPoint = event.getPoint();
		int x = clickPoint.x;
		int y = clickPoint.y;

		if (showMenu)
		{
			if (x >= 55 && x <= 202 && y >= 701 && y <= 766)
			{
				try
				{
					newGame();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				showMenu = false;
				repaint();
			}

			// MAIN: Play2 Button --> Future (Coming Soon) Page
			if (x >= 55 && x <= 202 && y >= 3212 / 4 && y <= 3472 / 4)
			{
				showMenu = false;
				showComingSoon = true;
				fromMainMenu = true;
				repaint();
			}

			// MAIN: Rules Button --> Rules Page
			if (x >= 220 / 4 && x <= 808 / 4 && y >= 3620 / 4 && y <= 3880 / 4)
			{
				showMenu = false;
				showrules = true;
				fromMainMenu = true;
				repaint();
			}

			repaint();
		}
		else if (showrules)
		{
			if (x >= 400 / 4 && x <= 688 / 4 && y >= 660 / 4 && y <= 770 / 4)
			{
				showMenu = true;
				showrules = false;
			}
			repaint();
		}
		else if (showComingSoon)
		{
			if (x >= 400 / 4 && x <= 688 / 4 && y >= 660 / 4 && y <= 770 / 4)
			{
				showMenu = true;
				showComingSoon = false;
			}

			repaint();
		}
		else if (gameOver == false && showrules == false
				&& showComingSoon == false)
		{
			if (x >= 400 / 4 && x <= 688 / 4 && y >= 660 / 4 && y <= 770 / 4)
			{
				if (JOptionPane.showConfirmDialog(Board.this,
						"Do you want to go to the main menu? This will end your game.",
						"Main Menu?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					gameOver = true;
					showMenu = true;
					return;
				}
			}

			repaint();

			// Figure out the selected row and column on the board and what hex
			// space it corresponds to

			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board[0].length; col++)
					if (board[row][col].contains(clickPoint)
							&& board[row][col].getStatus() == 1)
					{
						lastRows.add(row);
						lastCols.add(col);
						if (!gameOver)
						{
							if (gameOver && showMenu == false && showrules == false && showComingSoon == false)
								if (JOptionPane.showConfirmDialog(Board.this,
										"Do you want to Play Again?",
										"Game Over",
										JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
								{
									try
									{
										newGame();
										return;
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}
								}
								else
									showMenu = true;
						}
						else if (JOptionPane.showConfirmDialog(Board.this,
								"Do you want to Play Again?", "Game Over",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
						{
							try
							{
								newGame();
								return;
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
						else
							showMenu = true;

						repaint();
					}
//			if (showMenu == false && gameOver == false && showrules == false
//					&& showComingSoon == false)
//			{
//				if (event.getClickCount() == 1)
//				{
//					dropPiece(topPiece);
//				}
//				else if (event.getClickCount() == 2)
//				{
//					dropPiece(botPiece);
//				}
//			}
		}
	}

	/**
	 * Handles a mouseReleased event when dropping a hex piece
	 * 
	 * @param event the event information
	 */
	public void mouseReleased(MouseEvent event)
	{

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getX() > 0)
		{
			dropPiece(topPiece);
		}
		// if (e.getClickCount() == 2)
		// {
		// dropPiece(currentPiece);
		// wasDoubleClick = true;
		// }
		// else
		// {
		// Integer timerinterval = (Integer) Toolkit.getDefaultToolkit()
		// .getDesktopProperty("awt.multiClickInterval");
		// Timer timer1 = new Timer(timerinterval, new ActionListener() {
		// public void actionPerformed(ActionEvent evt)
		// {
		// if (wasDoubleClick)
		// {
		// wasDoubleClick = false; // reset flag
		// }
		// else
		// {
		// if (currentPiece == topPiece)
		// currentPiece = botPiece;
		// else if (currentPiece == botPiece)
		// currentPiece = topPiece;
		// }
		// }
		// });
		// timer1.setRepeats(false);
		// timer1.start();
		// }
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Not used but required since TablePanel implements MouseListener
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// Not used but required since TablePanel implements MouseListener
	}

	/**
	 * Handles a mouseMove to change the cursor
	 * 
	 * @param event the event information
	 */
	public void mouseMoved(MouseEvent event)
	{
		// Figure out if we are on a hexspace or not
		boolean onASpace = false;
		Point clickPoint = event.getPoint();
		int x = clickPoint.x;
		int y = clickPoint.y;

		if (showMenu)
		{
			if (x >= 110 && x <= 202 && y >= 701 && y <= 766)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());

			// MAIN: Play2 Button --> Future (Coming Soon) Page
			if (x >= 110 && x <= 202 && y >= 3212 / 4 && y <= 3472 / 4)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());

			// MAIN: Rules Button --> Rules Page
			if (x >= 220 / 4 && x <= 808 / 4 && y >= 3620 / 4 && y <= 3880 / 4)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else if (showrules)
		{
			if (x >= 324 / 4 && x <= 696 / 4 && y >= 632 / 4 && y <= 788 / 4)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else if (showComingSoon)
		{
			if (x >= 780 / 4 && x <= 960 / 4 && y >= 700 / 4 && y <= 800 / 4)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else
		{
			for (int row = 0; row < board.length; row++)
				for (int col = 0; col < board[0].length; col++)
					if (board[row][col].contains(clickPoint)
							&& board[row][col].getStatus() != 0)
						onASpace = true;

			// Show either a hand or the default cursor
			if (onASpace)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Handles a mouseDrag
	 * 
	 * @param event the event information
	 */
	public void mouseDragged(MouseEvent event)
	{
		// repaint();
	}

	/**
	 * Plays music by Martin Garrix and Avicii, remixed by Jeffrey Wang
	 */
	public synchronized void playSound()
	{
		new Thread(new Runnable() {
			// The wrapper thread is unnecessary, unless it blocks on the
			// Clip finishing; see comments.
			public void run()
			{
				try
				{
					File file = new File("NeonHexSoundtrack.wav");
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(file);
					clip.open(inputStream);
					clip.loop(999);;
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}