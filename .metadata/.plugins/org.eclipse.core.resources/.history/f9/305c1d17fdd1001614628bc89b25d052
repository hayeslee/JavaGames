import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.*;

/**
 * Plays a game of Neon Hex which is meant for 3-5 year olds. This is why the AI
 * is very simple to beat.
 * 
 * @author Jeffrey Wang and Hayes Lee
 * @version February 2016
 */
public class Main extends JFrame implements ActionListener
{
	private Board tableArea;
	private JMenuItem newMenuItem, topScoresOption, quitMenuItem,
			aboutMenuItem;

	/**
	 * * Creates a Simple Plan-it Frame Application * @throws IOException
	 */
	public Main() throws IOException
	{
		super("Plan-It");
		setIconImage(Toolkit.getDefaultToolkit().getImage("images\\s1.png"));
		// Add in a menu
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');
		newMenuItem = new JMenuItem("New Game");
		newMenuItem.addActionListener(this);
		quitMenuItem = new JMenuItem("Exit");
		quitMenuItem.addActionListener(this);
		gameMenu.add(newMenuItem);
		gameMenu.addSeparator();
		gameMenu.add(quitMenuItem);
		menuBar.add(gameMenu);
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		aboutMenuItem = new JMenuItem("About...");
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
		// Set up the layout
		// Centre the frame in the middle (almost) of the screen
		setLayout(new BorderLayout());
		tableArea = new Board();
		add(tableArea, BorderLayout.CENTER);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - Board.WIDTH) / 2,
				(screen.height - Board.HEIGHT) / 2 - 52);
		setResizable(false);
	}

	/**
	 * * Method that deals with the menu options * @param event the event that
	 * triggered this method
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == newMenuItem)
		{
			try
			{
				tableArea.newGame();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (event.getSource() == topScoresOption)
		{
		}
		else if (event.getSource() == quitMenuItem)
		{
			System.exit(0);
		}
		else if (event.getSource() == aboutMenuItem)
		{
			JOptionPane.showMessageDialog(tableArea,
					"By Hayes Lee, Jeffrey Wang, Jessie Won and Tara Yuen \n\u00a9 2016",
					"About Plan-It", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Main game = new Main();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.pack();
		game.setVisible(true);
	}

}
