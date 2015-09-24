/** 
 * Keeps track of the area to draw the Cards for a game of Poker Solitaire
 * Includes code to select, drag and drop cards in the panel
 * @author G Ridout and ...
 * @version April 2015
 */
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

//http://www.redblobgames.com/grids/hexagons/

public class Board extends JPanel implements MouseListener, MouseMotionListener
{
	// Constants for the table layout
	static final int WIDTH = 960;
	static final int HEIGHT = 800;
	private static final Color TABLE_COLOUR = Color.BLACK;
	private static final int HEX_WIDTH = 38;

	private static final int NO_PLAYER = 1;
	private static final int PLAYER_ONE = 2;
	private static final int PLAYER_TWO = 3;
	private static final int MAX_DEPTH = 2;

	private int topOffset = 100;
	private int leftOffset = -100;
	private HexSpace[][] board;
	private int currentPlayer;
	private boolean gameOver;
	private ImageIcon penguin;
	private Image foreground;
	private ImageIcon background;
	private ImageIcon help1, help2, help3;
	private Image playerOne;
	private Image playerTwo;
	private File file;
	private Image menu;
	private boolean showMenu;
	private boolean showHelp1;
	private boolean showHelp2;
	private boolean showHelp3;
	private int bestMoveRow;
	private int bestMoveCol;
	private int level;
	private int noOfRows;
	private int noOfCols;
	private boolean scoringBoard;
	private int blueMaxInARow;
	private int redMaxInARow;
	private boolean fromMainMenu;
	private ArrayList<Integer> lastRows;
	private ArrayList<Integer> lastCols;

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
		help1 = new ImageIcon("help1.gif");
		help2 = new ImageIcon("help2.gif");
		help3 = new ImageIcon("help3.gif");
		penguin = new ImageIcon("penguin.gif");
		background = new ImageIcon("background.gif");
		playerOne = new ImageIcon("playerOne.png").getImage();
		playerTwo = new ImageIcon("playerTwo.png").getImage();
		// Add listeners to handle mouse events to select, drag and drop Cards
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		// Create players

		level = 3;

		showMenu = true;
		showHelp1 = false;
		showHelp2 = false;
		showHelp3 = false;
		if (level == 3)
		{
			leftOffset = 100;
			topOffset = 200;
			file = new File("layout3.txt");
		}

		else if (level == 2)
		{
			leftOffset = 30;
			topOffset = 200;
			file = new File("layout2.txt");
		}
		else if (level == 1)
		{
			leftOffset = -100;
			topOffset = 100;
			file = new File("layout1.txt");
		}

		BufferedReader boardFile = new BufferedReader(new FileReader(file));

		String dimensionStr = boardFile.readLine();

		noOfRows = (dimensionStr.charAt(0) - '0') * 10 + dimensionStr.charAt(1)
				- '0';
		noOfCols = (dimensionStr.charAt(3) - '0') * 10 + dimensionStr.charAt(4)
				- '0';

		// Set up the array
		board = new HexSpace[noOfRows][noOfCols];

		// Where the game space is going to start
		int currentX = leftOffset;
		int currentY = topOffset;

		for (int row = 0; row < noOfRows; row++)
		{

			// THIS DOES NOT WORK IF THERE IS AN ODD # OF EMPTY ROWS ABOVE THE
			// BOARD ):

			String rowStr = boardFile.readLine();
			for (int col = 0; col < noOfCols; col++)
			{

				int space = rowStr.charAt(col) - '0';

				if (space > 1)
				{
					board[row][col] = new HexSpace(space, currentX, currentY,
							true);
				}
				else
					board[row][col] = new HexSpace(space, currentX, currentY,
							false);

				// Change the currentX point
				currentX += HEX_WIDTH;
			}

			currentX = leftOffset + HEX_WIDTH / 2 + (row * (HEX_WIDTH / 2));

			// Change the currentY point DUDE IDK THIS JUST WORKS LOL
			currentY += (HEX_WIDTH - 5);

		}
		boardFile.close();

		newGame();

	}

	/**
	 * Starts a new game by creating board
	 * 
	 * @throws IOException
	 */
	public void newGame()
	{
		lastRows = new ArrayList<Integer>();
		lastCols = new ArrayList<Integer>();
		currentPlayer = PLAYER_ONE;
		blueMaxInARow = 0;
		redMaxInARow = 0;
		for (int row = 0; row < noOfRows; row++)
		{
			for (int col = 0; col < noOfCols; col++)
			{
				if (!board[row][col].isEdge())
					board[row][col].changeStatus(NO_PLAYER);
			}

		}

		gameOver = false;
		repaint();
	}

	/**
	 * Calculates the score of the board.
	 * 
	 * @return the score
	 */
	private int calculateScore()
	{
		blueMaxInARow = 0;
		redMaxInARow = 0;
		int score = 0;
		for (int row = 1; row < board.length; row++)
			for (int col = 2; col < board[0].length - 2; col++)
				if (board[row][col].getStatus() == PLAYER_ONE) // If you blue
				{

					if (hitTop(row, col) && hitBot(row, col))
					{
						score = -10000;
					}
					else if (row != 1 && hitTop(row, col))
						score -= 50;
					else if (row != board.length - 2 && hitBot(row, col))
						score -= 50;

					// If spaces 1 diagonal away are our color and the spaces
					// leading to that space are empty then it's good
					if (board[row - 1][col - 1].getStatus() == PLAYER_ONE
							&& (board[row - 1][col].getStatus() == 1 && board[row][col - 1]
									.getStatus() == 1))
						score -= 15;

					if (row - 2 > 0)
						if (board[row - 2][col + 1].getStatus() == PLAYER_ONE
								&& (board[row - 1][col].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1))
							score -= 15;

					if (col + 2 < board[0].length)
						if (board[row - 1][col + 2].getStatus() == PLAYER_ONE
								&& (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1))
							score -= 15;

					if (row < board.length - 1)
					{
						if (board[row + 1][col + 1].getStatus() == PLAYER_ONE
								&& (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == 1))
							score -= 15;

						if (col - 2 > 0)
							if (board[row + 1][col - 2].getStatus() == PLAYER_ONE
									&& (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
											.getStatus() == 1))
								score -= 15;

						if (row + 2 < board.length)
							if (board[row + 2][col - 1].getStatus() == PLAYER_ONE
									&& (board[row + 1][col - 1].getStatus() == 1 && board[row + 1][col]
											.getStatus() == 1))
								score -= 15;
					}

					// Check if spaces directly beside space are empty or the
					// current color
					if (row < board.length - 1)
					{

						if ((board[row + 1][col].getStatus() == 1 && board[row + 1][col - 1]
								.getStatus() == 1)
								|| (board[row - 1][col].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1))
						{
							score -= 10;
						}

						if ((board[row][col - 1].getStatus() == 1 && board[row - 1][col]
								.getStatus() == 1)
								|| (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1)
								|| (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == 1)
								|| (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
										.getStatus() == 1))
						{
							score -= 10;
						}

					}

					// Account for biggest chain
					maxInARow(row, col, 0, PLAYER_ONE);

				}
				else if (board[row][col].getStatus() == PLAYER_TWO) // If red
				{
					if (hitLeft(row, col) && hitRight(row, col))
						score = 10000;
					else if (board[row][col - 2].getStatus() != 0
							&& hitLeft(row, col))
						score += 50;
					else if (board[row][col + 2].getStatus() != 0
							&& hitRight(row, col))
						score += 50;

					// If spaces 1 diagonal away are our color and the spaces
					// leading to that space are empty then it's good
					if (board[row - 1][col - 1].getStatus() == PLAYER_TWO
							&& (board[row - 1][col].getStatus() == 1 && board[row][col - 1]
									.getStatus() == 1))
						score += 15;
					if (board[row + 1][col + 1].getStatus() == PLAYER_TWO
							&& (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
									.getStatus() == 1))
						score += 15;
					if (col - 2 > 0)
						if (board[row + 1][col - 2].getStatus() == PLAYER_TWO
								&& (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
										.getStatus() == 1))
							score += 15;
					if (col + 2 < board[0].length)
						if (board[row - 1][col + 2].getStatus() == PLAYER_TWO
								&& (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1))
							score += 15;
					if (row - 2 > 0)
						if (board[row - 2][col + 1].getStatus() == PLAYER_TWO
								&& (board[row - 1][col].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1))
							score += 15;
					if (row + 2 < board.length)
						if (board[row + 2][col - 1].getStatus() == PLAYER_TWO
								&& (board[row + 1][col - 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == 1))
							score += 15;
					if (row < board.length - 1)
					{

						// left then right
						if ((board[row][col - 1].getStatus() == 1 && board[row - 1][col - 1]
								.getStatus() == 1)
								|| (board[row + 1][col].getStatus() == 1 && board[row][col + 1]
										.getStatus() == 1))
						{
							score += 10;
						}

						if ((board[row][col - 1].getStatus() == 1 && board[row - 1][col]
								.getStatus() == 1)
								|| (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == 1)
								|| (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == 1)
								|| (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
										.getStatus() == 1))
						{
							score += 10;
						}
					}

					// Account for biggest chain
					maxInARow(row, col, 0, PLAYER_TWO);
				}
		score += 2 * redMaxInARow;
		score -= 2 * blueMaxInARow;
		// System.out.println("red:" + redMaxInARow + "blue:" + blueMaxInARow);

		return score;
	}

	/**
	 * 
	 * @param depth
	 * @param color
	 * @return
	 */
	private int findMaxMove(int depth, int color)
	{
		if (depth == 0)
			return calculateScore();

		int bestValue = Integer.MIN_VALUE;
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				if (board[row][col].getStatus() == 1)
				{
					makeMove(row, col);
					int value = findMinMove(depth - 1, -color);
					if (row == 3 && col == 3 && depth == MAX_DEPTH)
					{
						System.out.println(value);
					}

					if (value > bestValue)
					{
						//System.out.println(depth + " row: " + row + " col: "
								//+ col + " value: " + value);

						bestValue = value;
						if (depth == MAX_DEPTH)
						{
							bestMoveRow = row;
							bestMoveCol = col;
						}
					}
					undoMove(row, col);
				}

		return bestValue;

	}

	/**
	 * 
	 * @param depth
	 * @param color
	 * @return
	 */
	private int findMinMove(int depth, int color)
	{
		if (depth == 0)
			return calculateScore();

		int worstValue = Integer.MAX_VALUE;
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				if (board[row][col].getStatus() == 1)
				{
					makeMove(row, col);
					int value = findMaxMove(depth - 1, -color);

					if (value < worstValue)
					{
						//System.out.println(depth + " row: " + row + " col: "
								//+ col + " value: " + value);

						worstValue = value;
					}
					undoMove(row, col);
				}

		return worstValue;

	}

	/**
	 * 
	 * @param row
	 * @param col
	 */
	private void undoMove(int row, int col)
	{
		board[row][col].changeStatus(NO_PLAYER);
	}

	/**
	 * Makes moves and calls checkWin method
	 * 
	 * @param row
	 * @param col
	 */
	private void makeMove(int row, int col)
	{
		board[row][col].changeStatus(currentPlayer);

		if (!scoringBoard)
			rePaintDrawingAreaImmediately();

		if (currentPlayer == PLAYER_ONE && scoringBoard == false)
		{
			if (hitTop(row, col) && hitBot(row, col))
			{
				JOptionPane.showMessageDialog(Board.this, "Blue has won!");
				gameOver = true;
			}
		}
		else if (currentPlayer == PLAYER_TWO && scoringBoard == false)
		{
			if (hitLeft(row, col) && hitRight(row, col))

			{
				JOptionPane.showMessageDialog(Board.this, "Red has won!");
				gameOver = true;
			}
		}
		if (currentPlayer == PLAYER_ONE)
			currentPlayer = PLAYER_TWO;
		else
			currentPlayer = PLAYER_ONE;
	}

	/**
	 * Undoes last move made
	 */
	private void undoLastMove()
	{
		if (lastRows.size() > 0)
		{
			board[lastRows.get(lastRows.size() - 1)][lastCols.get(lastCols
					.size() - 1)].changeStatus(NO_PLAYER);
			lastRows.remove(lastRows.size() - 1);
			lastCols.remove(lastCols.size() - 1);
		}
	}

	/**
	 * 
	 */
	private void makeAIMove()
	{
		if (!gameOver)
		{
			currentPlayer = PLAYER_TWO;
			repaint();
			scoringBoard = true;
			findMaxMove(MAX_DEPTH, 1);
			currentPlayer = PLAYER_TWO;
			scoringBoard = false;
			makeMove(bestMoveRow, bestMoveCol);
			lastRows.add(bestMoveRow);
			lastCols.add(bestMoveCol);
			System.out.println("same");
			currentPlayer = PLAYER_ONE;
		}
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean hitBot(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 2)
			return false;

		if (row == board.length - 2)
			return true;

		board[row][col].changeStatus(42);
		boolean hitBot = hitBot(row + 1, col) || hitBot(row + 1, col - 1)
				|| hitBot(row, col - 1) || hitBot(row, col + 1)
				|| hitBot(row - 1, col) || hitBot(row - 1, col + 1);
		board[row][col].changeStatus(2);
		return hitBot;
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean hitTop(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 2)
			return false;

		if (row == 1)
			return true;

		board[row][col].changeStatus(42);
		boolean hitTop = hitTop(row + 1, col) || hitTop(row + 1, col - 1)
				|| hitTop(row, col - 1) || hitTop(row, col + 1)
				|| hitTop(row - 1, col) || hitTop(row - 1, col + 1);
		board[row][col].changeStatus(2);
		return hitTop;
	}

	private boolean hitLeft(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 3)
			return false;

		if (board[row][col - 2].getStatus() == 0)
		{
			return true;
		}

		board[row][col].changeStatus(42);
		boolean hitLeft = hitLeft(row + 1, col) || hitLeft(row + 1, col - 1)
				|| hitLeft(row, col - 1) || hitLeft(row, col + 1)
				|| hitLeft(row - 1, col) || hitLeft(row - 1, col + 1);
		board[row][col].changeStatus(3);
		return hitLeft;
	}

	private boolean hitRight(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 3)
			return false;

		if (board[row][col + 2].getStatus() == 0)
		{
			return true;
		}

		board[row][col].changeStatus(42);
		boolean hitRight = hitRight(row + 1, col) || hitRight(row + 1, col - 1)
				|| hitRight(row, col + 1) || hitRight(row, col - 1)
				|| hitRight(row - 1, col) || hitRight(row - 1, col + 1);
		board[row][col].changeStatus(3);
		return hitRight;
	}

	private void maxInARow(int row, int col, int inARow, int color)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].isEdge())
			return;

		if (board[row][col].getStatus() == color)
		{
			inARow++;
			if (color == 2)
			{
				if (inARow > blueMaxInARow)
					blueMaxInARow = inARow;
			}
			else if (inARow > redMaxInARow)
				redMaxInARow = inARow;
		}
		else
			return;

		board[row][col].changeStatus(42);
		maxInARow(row + 1, col, inARow, color);
		maxInARow(row + 1, col - 1, inARow, color);
		maxInARow(row, col + 1, inARow, color);
		maxInARow(row, col - 1, inARow, color);
		maxInARow(row - 1, col, inARow, color);
		maxInARow(row - 1, col + 1, inARow, color);

		board[row][col].changeStatus(color);
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
			menu = new ImageIcon("menu.png").getImage();
			g.drawImage(menu, 0, 0, null);

		}
		else if (showHelp1)
		{
			Image image = help1.getImage();
			g.drawImage(image, 0, 0, this);

		}
		else if (showHelp2)
		{
			Image image = help2.getImage();
			g.drawImage(image, 0, 0, this);
		}
		else if (showHelp3)
		{
			Image image = help3.getImage();
			g.drawImage(image, 0, 0, this);
		}
		else if (showHelp1 == false && showHelp2 == false && showMenu == false)
		{
		
			
			// Icon icon = new ImageIcon("background.gif");
			// icon.paintIcon(this, g, 0, 0);

			for (int row = 0; row < board.length; row++)
			{
				for (int col = 0; col < board[0].length; col++)
				{
					board[row][col].drawHexSpace(g, row, col);
				}
			}
			
			foreground = new ImageIcon("foreground.png").getImage();
			g.drawImage(foreground, 0, 0, null);

			if (currentPlayer == PLAYER_TWO)
				g.drawImage(playerTwo, 104, 630, this);
			else
				g.drawImage(playerOne, 110, 640, this);

			Image image = penguin.getImage();
			g.drawImage(image, 740, 120, null);

			repaint();

		}

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
			// Start button is at (130, 560)
			// Size is 230x110

			System.out.println(x + " " + y);

			if (x >= 130 && x <= 360 && y >= 560 && y <= 670)
			{
				showMenu = false;
				gameOver = false;
			}

			if (x >= 380 && x <= 600 && y >= 560 && y <= 670)
			{
				showMenu = false;
				showHelp1 = true;
				fromMainMenu = true;
			}

			repaint();
		}
		else if (showHelp1)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
			{
				showHelp1 = false;
				showHelp2 = true;
			}

			repaint();
		}
		else if (showHelp2)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
			{
				showHelp2 = false;
				showHelp3 = true;
			}

			repaint();
		}
		else if (showHelp3)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
			{
				showHelp3 = false;
				if (fromMainMenu)
					showMenu = true;
			}
			repaint();
		}
		else if (!gameOver && !showHelp1 && !showHelp2)
		{

			if (x >= 20 && x <= 125 && y >= 635 && y <= 705)
			{
				// Doesn't work yet true
				System.out.println("test");
				this.undoLastMove();
			}
			else if (x >= 20 && x <= 125 && y >= 715 && y <= 785)
			{
				if (JOptionPane.showConfirmDialog(Board.this,
						"Do you want to Start Over?", "Start Over?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					newGame();
			}
			else if (x >= 750 && x <= 800 && y >= 20 && y <= 70)
			{
				showHelp1 = true;
			}
			else if (x >= 890 && x <= 940 && y >= 20 && y <= 70)
			{
				if (JOptionPane
						.showConfirmDialog(
								Board.this,
								"Do you want to go to the main menu? This will end your game.",
								"Main Menu?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					newGame();
				gameOver = true;
				showMenu = true;
			}

			repaint();

			// Figure out the selected row and column on the board and what hex
			// space it corresponds to :)

			if (currentPlayer == 2)
			{
				for (int row = 0; row < board.length; row++)
					for (int col = 0; col < board[0].length; col++)
						if (board[row][col].contains(clickPoint)
								&& board[row][col].getStatus() == 1)
						{
							makeMove(row, col);
							lastRows.add(row);
							lastCols.add(col);
							if (!gameOver)
							{
								makeAIMove();
								if (gameOver)
									if (JOptionPane.showConfirmDialog(
											Board.this,
											"Do you want to Play Again?",
											"Game Over",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
										newGame();
							}
							else if (gameOver)
								if (JOptionPane.showConfirmDialog(Board.this,
										"Do you want to Play Again?",
										"Game Over", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
									newGame();
						}
			}

		}
	}

	/**
	 * Handles a mouseReleased event when dropping a Card
	 * 
	 * @param event the event information
	 */
	public void mouseReleased(MouseEvent event)
	{

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

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
	 * Handles a mouseMove to change the cursor when on a Card
	 * 
	 * @param event the event information
	 */
	public void mouseMoved(MouseEvent event)
	{
		// Figure out if we are on a card or not
		boolean onASpace = false;
		Point clickPoint = event.getPoint();
		int x = clickPoint.x;
		int y = clickPoint.y;

		if (showMenu)
		{
			if (x >= 130 && x <= 360 && y >= 560 && y <= 670)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());

			if (x >= 380 && x <= 600 && y >= 560 && y <= 670)
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

			// Show either a hand (on a card) or the default cursor
			if (onASpace)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Handles a mouseDrag when dragging a Card
	 * 
	 * @param event the event information
	 */
	public void mouseDragged(MouseEvent event)
	{
		// repaint();
	}
}
