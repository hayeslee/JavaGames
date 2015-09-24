/** 
 * Keeps track of the area to draw the HexSpaces for a game of Neon Hex
 * Includes code to drop pieces in the panel
 * @author Jeffrey and Hayes
 * @version June 2015
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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

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
	private ImageIcon playerOne;
	private ImageIcon playerTwo;
	private File layoutOne;
	private File layoutTwo;
	private File layoutThree;
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
		background = new ImageIcon("background.gif");
		// Add listeners to handle mouse events to select, drag and drop pieces
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		layoutOne = new File("layout3.txt");
		layoutTwo = new File("layout2.txt");
		layoutThree = new File("layout1.txt");

		showMenu = true;
		showHelp1 = false;
		showHelp2 = false;
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
		currentPlayer = PLAYER_ONE;
		int response = -1;
		while (response == -1)
		{
			String[] options = new String[] { "Small", "Medium", "Large" };
			response = JOptionPane.showOptionDialog(null,
					"Please choose a board size.", "Level Select",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, options, options[0]);
		}
		level = response + 1;
		createBoard(level);
		gameOver = false;
		showMenu = false;
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
		leftOffset = 100;
		topOffset = 200;

		if (level == 1)
		{
			leftOffset = 100;
			topOffset = 200;
			boardFile = new BufferedReader(new FileReader(layoutOne));
		}

		else if (level == 2)
		{
			leftOffset = 30;
			topOffset = 200;
			boardFile = new BufferedReader(new FileReader(layoutTwo));
		}
		else
		{
			leftOffset = -100;
			topOffset = 150;
			boardFile = new BufferedReader(new FileReader(layoutThree));
		}

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

			// Change the currentY point
			currentY += (HEX_WIDTH - 5);

		}
		boardFile.close();
		repaint();
	}

	/**
	 * Calculates the score of the board.
	 * 
	 * @return score the score
	 */
	private int calculateScore()
	{
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
					// leading to that space are or color then it's good
					if (board[row - 1][col - 1].getStatus() == PLAYER_ONE
							&& (board[row - 1][col].getStatus() == 1 && board[row][col - 1]
									.getStatus() == PLAYER_ONE))
						score -= 15;

					if (row - 2 > 0)
						if (board[row - 2][col + 1].getStatus() == PLAYER_ONE
								&& (board[row - 1][col].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == PLAYER_ONE))
							score -= 15;

					if (col + 2 < board[0].length)
						if (board[row - 1][col + 2].getStatus() == PLAYER_ONE
								&& (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == PLAYER_ONE))
							score -= 15;

					if (row < board.length - 1)
					{
						if (board[row + 1][col + 1].getStatus() == PLAYER_ONE
								&& (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == PLAYER_ONE))
							score -= 15;

						if (col - 2 > 0)
							if (board[row + 1][col - 2].getStatus() == PLAYER_ONE
									&& (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
											.getStatus() == PLAYER_ONE))
								score -= 15;

						if (row + 2 < board.length)
							if (board[row + 2][col - 1].getStatus() == PLAYER_ONE
									&& (board[row + 1][col - 1].getStatus() == 1 && board[row + 1][col]
											.getStatus() == PLAYER_ONE))
								score -= 15;
					}

					// Check if spaces directly beside space are empty
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
							&& (board[row - 1][col].getStatus() == PLAYER_TWO && board[row][col - 1]
									.getStatus() == PLAYER_TWO))
						score += 15;
					if (board[row + 1][col + 1].getStatus() == PLAYER_TWO
							&& (board[row][col + 1].getStatus() == 1 && board[row + 1][col]
									.getStatus() == PLAYER_TWO))
						score += 30;
					if (col - 2 > 0)
						if (board[row + 1][col - 2].getStatus() == PLAYER_TWO
								&& (board[row][col - 1].getStatus() == 1 && board[row + 1][col - 1]
										.getStatus() == PLAYER_TWO))
							score += 15;
					if (col + 2 < board[0].length)
						if (board[row - 1][col + 2].getStatus() == PLAYER_TWO
								&& (board[row][col + 1].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == PLAYER_TWO))
							score += 15;
					if (row - 2 > 0)
						if (board[row - 2][col + 1].getStatus() == PLAYER_TWO
								&& (board[row - 1][col].getStatus() == 1 && board[row - 1][col + 1]
										.getStatus() == PLAYER_TWO))
							score += 15;
					if (row + 2 < board.length)
						if (board[row + 2][col - 1].getStatus() == PLAYER_TWO
								&& (board[row + 1][col - 1].getStatus() == 1 && board[row + 1][col]
										.getStatus() == PLAYER_TWO))
							score += 15;
					if (row < board.length - 1)
					{
						// Check if spaces directly beside space are empty
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
				}
		return score;
	}

	/**
	 * Finds the best possible move for the AI
	 * 
	 * @param depth how deep to check
	 * @return the best move value
	 */
	private int findMaxMove(int depth)
	{
		if (depth == 0)
			return calculateScore();

		int bestValue = Integer.MIN_VALUE;
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				if (board[row][col].getStatus() == 1)
				{
					makeMove(row, col);
					int value = findMinMove(depth - 1);

					if (value > bestValue)
					{
						bestValue = value;
						if (depth == MAX_DEPTH)
						{
							bestMoveRow = row;
							bestMoveCol = col;
						}
					}
					// Undo last move
					board[row][col].changeStatus(NO_PLAYER);
				}

		return bestValue;

	}

	/**
	 * Finds worst move for AI
	 * 
	 * @param depth how deep to check
	 * @return worst move value
	 */
	private int findMinMove(int depth)
	{
		if (depth == 0)
			return calculateScore();

		int worstValue = Integer.MAX_VALUE;
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				if (board[row][col].getStatus() == 1)
				{
					makeMove(row, col);
					int value = findMaxMove(depth - 1);

					if (value < worstValue)
					{
						worstValue = value;
					}
					// Undo last checking move
					board[row][col].changeStatus(NO_PLAYER);
				}

		return worstValue;

	}

	/**
	 * Makes moves and checks for win
	 * 
	 * @param row row to place piece
	 * @param col column to place piece
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
		repaint();
	}

	/**
	 * Undoes last move made by the player and the computer
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
		if (lastRows.size() > 0)
		{
			board[lastRows.get(lastRows.size() - 1)][lastCols.get(lastCols
					.size() - 1)].changeStatus(NO_PLAYER);
			lastRows.remove(lastRows.size() - 1);
			lastCols.remove(lastCols.size() - 1);
		}
		repaint();
	}

	/**
	 * Makes the best AI move
	 */
	private void makeAIMove()
	{
		currentPlayer = PLAYER_TWO;
		repaint();

		// Score board and make best move
		scoringBoard = true;
		findMaxMove(MAX_DEPTH);
		currentPlayer = PLAYER_TWO;
		scoringBoard = false;
		makeMove(bestMoveRow, bestMoveCol);
		lastRows.add(bestMoveRow);
		lastCols.add(bestMoveCol);
		currentPlayer = PLAYER_ONE;

		repaint();
	}

	/**
	 * Check if a piece connects to the bottom edge
	 * 
	 * @param row row piece is in
	 * @param col column piece is in
	 * @return whether piece connects to bottom edge
	 */
	private boolean hitBot(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 2)
			return false;

		if (row == board.length - 2)
			return true;

		// Placeholder
		board[row][col].changeStatus(42);
		boolean hitBot = hitBot(row + 1, col) || hitBot(row + 1, col - 1)
				|| hitBot(row, col - 1) || hitBot(row, col + 1)
				|| hitBot(row - 1, col) || hitBot(row - 1, col + 1);
		board[row][col].changeStatus(PLAYER_ONE);
		return hitBot;
	}

	/**
	 * Check if a piece connects to the top edge
	 * 
	 * @param row row piece is in
	 * @param col column piece is in
	 * @return whether piece connects to top edge
	 */
	private boolean hitTop(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 2)
			return false;

		if (row == 1)
			return true;

		// Placeholder
		board[row][col].changeStatus(42);
		boolean hitTop = hitTop(row + 1, col) || hitTop(row + 1, col - 1)
				|| hitTop(row, col - 1) || hitTop(row, col + 1)
				|| hitTop(row - 1, col) || hitTop(row - 1, col + 1);
		board[row][col].changeStatus(PLAYER_ONE);
		return hitTop;
	}

	/**
	 * Check if a piece connects to the left edge
	 * 
	 * @param row row piece is in
	 * @param col column piece is in
	 * @return whether piece connects to left edge
	 */
	private boolean hitLeft(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 3)
			return false;

		if (board[row][col - 2].getStatus() == 0)
		{
			return true;
		}

		// Placeholder
		board[row][col].changeStatus(42);
		boolean hitLeft = hitLeft(row + 1, col) || hitLeft(row + 1, col - 1)
				|| hitLeft(row, col - 1) || hitLeft(row, col + 1)
				|| hitLeft(row - 1, col) || hitLeft(row - 1, col + 1);
		board[row][col].changeStatus(PLAYER_TWO);
		return hitLeft;
	}

	/**
	 * Check if a piece connects to the right edge
	 * 
	 * @param row row piece is in
	 * @param col column piece is in
	 * @return whether piece connects to right edge
	 */
	private boolean hitRight(int row, int col)
	{
		if (row <= 0 || row >= board.length || col == -1
				|| col == board[0].length || board[row][col].getStatus() != 3)
			return false;

		if (board[row][col + 2].getStatus() == 0)
		{
			return true;
		}
		// Placeholder
		board[row][col].changeStatus(42);
		boolean hitRight = hitRight(row + 1, col) || hitRight(row + 1, col - 1)
				|| hitRight(row, col + 1) || hitRight(row, col - 1)
				|| hitRight(row - 1, col) || hitRight(row - 1, col + 1);
		board[row][col].changeStatus(PLAYER_TWO);
		return hitRight;
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
			for (int row = 0; row < board.length; row++)
			{
				for (int col = 0; col < board[0].length; col++)
				{
					board[row][col].drawHexSpace(g, row, col);
				}
			}

			foreground = new ImageIcon("foreground.png").getImage();
			g.drawImage(foreground, 0, 0, null);

			if (currentPlayer == PLAYER_ONE)
			{
				playerOne = new ImageIcon("playerOne.png");
				Image image = playerOne.getImage();
				g.drawImage(image, 106, 636, this);
			}
			else
			{
				playerTwo = new ImageIcon("playerTwo.png");
				Image image = playerTwo.getImage();
				g.drawImage(image, 110, 630, this);
			}

			penguin = new ImageIcon("penguin.gif");
			Image image = penguin.getImage();
			g.drawImage(image, 740, 120, null);
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
			if (x >= 130 && x <= 360 && y >= 560 && y <= 670)
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

			if (x >= 380 && x <= 600 && y >= 560 && y <= 670)
			{
				showMenu = false;
				showHelp1 = true;
				fromMainMenu = true;
				repaint();
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
		else if (gameOver == false && showHelp1 == false && showHelp2 == false)
		{

			// Undo move
			if (x >= 20 && x <= 125 && y >= 635 && y <= 705)
			{
				this.undoLastMove();
			}
			else if (x >= 20 && x <= 125 && y >= 715 && y <= 785)
			{
				if (JOptionPane.showConfirmDialog(Board.this,
						"Do you want to Start Over?", "Start Over?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					try
					{
						newGame();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				{
					gameOver = true;
					showMenu = true;
					return;
				}
			}

			repaint();

			// Figure out the selected row and column on the board and what hex
			// space it corresponds to

			if (currentPlayer == PLAYER_ONE)
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
						}
				repaint();
			}

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
			if (x >= 130 && x <= 360 && y >= 560 && y <= 670)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());

			if (x >= 380 && x <= 600 && y >= 560 && y <= 670)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else if (showHelp1)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else if (showHelp2)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
		}
		else if (showHelp3)
		{
			if (x >= 780 && x <= 960 && y >= 700 && y <= 800)
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
					clip.start();
				}
				catch (Exception e)
				{
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}
