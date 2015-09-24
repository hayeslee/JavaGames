import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Sushi Shop Tycoon Main 
 * Draws frame for Sushi Shop Tycoon Game 
 * 
 * @author Hayes Lee and Jeffrey Wang
 * @version June 15, 2014
 */

public class SushiShopTycoonMain extends JFrame implements ActionListener
{

	private SushiShopTycoon gameScreen;
	JMenuItem saveOption, exitOption, aboutOption, rulesOption, loadOption,
			tipsOption;
	String message;

	public SushiShopTycoonMain()
	{
		// Set up the frame
		super("Sushi Shop Tycoon");
		setLocation(0, 0);
		setResizable(false);

		// Add game screen
		gameScreen = new SushiShopTycoon();
		getContentPane().add(gameScreen, BorderLayout.CENTER);
		Container contentPane = getContentPane();
		contentPane.add(gameScreen, BorderLayout.CENTER);

		// Set up the Help Menu
		// Add rules option
		JMenu helpMenu = new JMenu("Help");
		rulesOption = new JMenuItem("Rules");
		rulesOption.addActionListener(this);
		helpMenu.add(rulesOption);

		// Add tips option
		tipsOption = new JMenuItem("Tips");
		tipsOption.addActionListener(this);
		helpMenu.add(tipsOption);

		// add about option
		aboutOption = new JMenuItem("About");
		aboutOption.addActionListener(this);
		helpMenu.add(aboutOption);

		// Add each MenuItem to the Game Menu (with a separator)
		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(helpMenu);
		
		// Set the menu bar for this frame to mainMenu
		setJMenuBar(mainMenu);

	}

	public static void main(String[] args)
	{
		SushiShopTycoonMain sushiShop = new SushiShopTycoonMain();
		sushiShop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sushiShop.pack();
		sushiShop.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == rulesOption) // If 'rules' is selected
		{
			JOptionPane
					.showMessageDialog(
							this,
							"Try to make the most money in 1 year!"
									+ "\n\nYou can price your sushi through the pricing menu."
									+ "\nYou can buy inventory to run your shop."
									+ "\nYou must have all 5 ingredients in order to make sushi."
									+ "\nYou can upgrade your chef to increase your shop's daily output of sushi."
									+ "\nYou can buy advertisements to get more customers."
									+ "\n\nGo to the instructions in the main menu for more detailed help."
									+ "\n\nGood luck and have fun! :)",
							"Rules", JOptionPane.INFORMATION_MESSAGE);
		}

		else if (event.getSource() == tipsOption) // If 'tips' is selected
		{
			JOptionPane
					.showMessageDialog(
							this,
							"Customers may not buy your items if they are at a high price."
									+ "\nCertain areas on the map reach a larger population."
									+ "\nDifferent inventory items spoil at different rates.",
							"Tips", JOptionPane.INFORMATION_MESSAGE);
		}

		else if (event.getSource() == aboutOption) // If 'about' is selected
		{
			JOptionPane.showMessageDialog(this, "by Hayes Lee and Jeffrey Wang"
					+ "\n\u00a9 2014", "About Sushi Shop Tycoon",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

}
