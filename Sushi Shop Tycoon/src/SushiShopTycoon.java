import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Sushi Shop Tycoon
 * 
 * @author Hayes Lee and Jeffrey Wang
 * @version June 16, 2014
 */
public class SushiShopTycoon extends JPanel implements MouseListener,
		KeyListener
{

	// Program variables

	// Game Screen Size
	public final Dimension gameBoard_SIZE = new Dimension(1152, 720);

	// Main Menu
	private boolean drawInstructions, drawInstruct1, drawInstruct2,
			drawInstruct3, drawInstruct4, drawInstruct5 = false;
	private boolean drawMainMenu = true;
	private boolean newGame = true;

	// General Variables
	String name;
	private int totalDays;
	private boolean simOver;
	private boolean dayEnd;
	private int soldPerDay;
	private int soldTotal;
	private int[] soldSushi = new int[5];
	private int dailyRevenue;
	private int totalRevenue;

	// Timer event
	private Timer dayTimer;
	private boolean timerOn;
	private int time;
	private int timeAllowed;

	// Chef Variables
	private final int CHEF_STARTING_OUTPUT = 30;
	private int chefLevel;
	private int chefUpgradeCost;
	// Create Chef
	Chef chef1 = new Chef(CHEF_STARTING_OUTPUT); // Create Chef

	// Menu at the top
	private boolean restaurant = true;
	private boolean city, storage = false;
	private boolean drawFoodMenu, drawStaffMenu, drawInventoryMenu,
			drawStatsMenu = false;

	// Item variables (things we're selling)
	private int[] itemPrices = new int[5];

	// Inventory variables
	private final int[] STARTING_SPOIL_TIMES = { 5, 10, 5, 7, 15 };
	private final String[] INVENTORY_ITEMS = { "rice", "seaweed", "fish",
			"vegetables", "sauce" };
	private final double[] INVENTORY_PRICES = { 0.2, 0.4, 0.2, 0.2, 0.4 };
	private double[] inventoryCost = new double[5];
	private int[] buyInventory = new int[5];
	private boolean outOfStock, spoiled = false;
	private int[] spoiledToday = new int[5];

	private final String[] BREAKING_NEWS = {
			"Researchers say eating sushi twice a day boosts immune system.",
			"High amounts of mercury found in recent Canadian salmon haul.",
			"Gordon Ramsay calls sushi the king of seafood dishes.",
			"Dr. Melodie Kim at Harvard College discovers cancerous substance in seaweed.",
			"Justin Bieber throws sushi at his fans from his condo balcony.",
			"World wide shortage of wasabi causes demand of sushi to lower.",
			"New study reveals that sushi can help with weight loss.",
			"Walken Ridout Sushi, a new restaurant in town, attracts customers with catchy name." };

	// Effect on the customer rate
	private final int[] BREAKING_NEWS_EFFECTS = { -5, 5, -10, 10, 5, 5, -5, 5 };

	private int breakingNewsIndex;
	private double totalMoney;

	// Set times customers come in at
	private int customerRate;
	private int nextCustomerTime;

	// Images
	public Image mainMenu, imageBackground, imageBackground2, foodMenu,
			headerMenu, staffMenu, advertisementMenu, statsMenu, checkMark,
			inventoryMenu, outOfStockImage, spoiledImage, imageBackground3,
			closeButton, reminderImage;
	public Image instructions1, instructions2, instructions3, instructions4,
			instructions5, arrow, arrowLeft;
	private Image cashier, chef;

	// Image of map from:
	// http://blog.canacad.ac.jp/wpmu/mypenglish8/2013/09/05/birds-eye-view-map-mrs-lemleys-class/
	private Image map;

	// Animating our customers
	private Image customer, backCustomer, purpleCustomer, backPurple,
			blueCustomer, backBlue, orangeCustomer, backOrange;
	private int xCustomerPos;
	private int xBackCustomerPos;
	private int currentCustomerColour = (int) (Math.random() * 4);
	private int leavingCustomerColour = currentCustomerColour;
	private final int TIME_INTERVAL = 100;
	private int currentCustomerIndex;
	private Customer currentCustomer;
	private int currentItem;
	private boolean firstCustomerOfDay = true;

	// Advertising variables
	private int areasSelected = 0;
	private final int[][] POPULATION_IN_AREA = { { 1000, 2000, 1500, 2500 },
			{ 500, 3000, 2000, 1000 }, { 1000, 2500, 2000, 600 },
			{ 1200, 2300, 500, 200 } };
	private boolean[][] selected = new boolean[4][4];
	private int totalPopulation;
	private int adType;
	private double adCost, totalAdCost;
	private final double[] AD_COST = { 0.5, 1, 0.2 };
	private final double[] AD_EFFECT_PERCENTAGE = { 0.6, 0.8, 0.4 };
	private double customersIncrease;
	private int increaseMargin;

	// Create the 4 customer object to represent all customers
	Customer[] customerArray = new Customer[4];
	{
		customerArray[0] = new Customer();
		customerArray[1] = new Customer();
		customerArray[2] = new Customer();
		customerArray[3] = new Customer();
	}

	// Create Inventory Objects
	Inventory[] inventoryArray = new Inventory[5];
	{
		for (int item = 0; item < INVENTORY_ITEMS.length; item++)
			inventoryArray[item] = new Inventory(INVENTORY_ITEMS[item],
					STARTING_SPOIL_TIMES[item]);
	}

	/**
	 * Constructs a board object (drawing panel)
	 */
	public SushiShopTycoon()
	{
		// Sets up the board area, loads in piece images and starts a new game
		setPreferredSize(gameBoard_SIZE);

		// Draw Images
		// Main Menu and Instructions
		mainMenu = new ImageIcon("mainmenu.png").getImage();
		instructions1 = new ImageIcon("instructions1.png").getImage();
		instructions2 = new ImageIcon("instructions2.png").getImage();
		instructions3 = new ImageIcon("instructions3.png").getImage();
		instructions4 = new ImageIcon("instructions4.png").getImage();
		instructions5 = new ImageIcon("instructions5.png").getImage();
		arrow = new ImageIcon("arrow.png").getImage();
		arrowLeft = new ImageIcon("arrowLeft.png").getImage();

		// Backgrounds
		imageBackground = new ImageIcon("sushi-shop.png").getImage();
		imageBackground2 = new ImageIcon("ad-screen.png").getImage();
		imageBackground3 = new ImageIcon("storage.png").getImage();
		map = new ImageIcon("map.png").getImage();

		// In-game menus
		foodMenu = new ImageIcon("foodmenu.png").getImage();
		headerMenu = new ImageIcon("headermenu.png").getImage();
		staffMenu = new ImageIcon("staffmenu.png").getImage();
		inventoryMenu = new ImageIcon("inventorymenu.png").getImage();
		advertisementMenu = new ImageIcon("ad-menu.png").getImage();
		statsMenu = new ImageIcon("statsMenu.png").getImage();

		// People (customers, chef, cashier)
		cashier = new ImageIcon("cashier.png").getImage();
		chef = new ImageIcon("chef.png").getImage();
		customer = new ImageIcon("person.png").getImage();
		backCustomer = new ImageIcon("backperson.png").getImage();
		backPurple = new ImageIcon("backPerson2.png").getImage();
		purpleCustomer = new ImageIcon("person2.png").getImage();
		backBlue = new ImageIcon("backPerson4.png").getImage();
		blueCustomer = new ImageIcon("person4.png").getImage();
		backOrange = new ImageIcon("backPerson3.png").getImage();
		orangeCustomer = new ImageIcon("person3.png").getImage();

		// Other
		closeButton = new ImageIcon("closebutton.png").getImage();
		checkMark = new ImageIcon("checkmark.png").getImage();
		outOfStockImage = new ImageIcon("outofstock.png").getImage();
		spoiledImage = new ImageIcon("spoiled.png").getImage();
		reminderImage = new ImageIcon("reminder.png").getImage();

		// Add mouse listeners and Key Listeners to the game board
		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
		requestFocusInWindow();

		// Start new game
		newGame();
	}

	/**
	 * Starts a new game and resets all necessary variables
	 */
	public void newGame()
	{
		// Resets game variables
		simOver = false;
		totalDays = 0;
		totalMoney = 30000.0;
		xCustomerPos = -1000;
		xBackCustomerPos = -500;
		customerRate = 40;
		dayEnd = true;
		soldPerDay = 0;
		soldTotal = 0;
		customersIncrease = 0;
		increaseMargin = 0;
		chefLevel = 1;
		chefUpgradeCost = 500;
		totalRevenue = 0;
		drawStaffMenu = false;
		drawFoodMenu = false;
		drawInventoryMenu = false;
		restaurant = true;

		// Reset ad options
		for (int row = 0; row < 4; row++)
		{
			for (int column = 0; column < 4; column++)
			{
				selected[row][column] = false;
			}
		}
		areasSelected = 0;
		totalPopulation = 0;
		totalAdCost = 0;
		adType = 0;
		adCost = AD_COST[adType];

		// Reset customers
		for (int customer = 0; customer < customerArray.length; customer++)
			customerArray[customer].reset();

		// Reset inventory, start with 50
		for (int item = 0; item < INVENTORY_ITEMS.length; item++)
		{
			inventoryArray[item].reset();
			inventoryArray[item].bought(30);
		}

		// Reset prices
		for (int price = 0; price < 5; price++)
		{
			itemPrices[price] = 5;
		}

		// Reset chef
		chef1.reset(CHEF_STARTING_OUTPUT);

		// Main Game Timer
		timerOn = false;
		time = 0;
		timeAllowed = 240;
		dayTimer = new Timer(TIME_INTERVAL, new TimerEventHandler());

		// Set current breaking news and implement its effects
		breakingNewsIndex = (int) (Math.random() * BREAKING_NEWS.length);
		customerRate += BREAKING_NEWS_EFFECTS[breakingNewsIndex];
		customerArray[(int) Math.random() * 4].maxPriceIncrease();

		repaint();
	}

	/**
	 * Run the events that happen in 1 day of game play
	 */
	public void runDay()
	{
		// Generate random time for next customer
		nextCustomerTime = (int) (Math.random() * customerRate);

		// Set sold sushi today to zero
		for (int item = 0; item < 5; item++)
			soldSushi[item] = 0;
		soldPerDay = 0;
		dailyRevenue = 0;

		if (!simOver)
		{
			dayTimer.start();
			breakingNews();
			totalDays++;
			customerPurchase();
			firstCustomerOfDay = true;
			drawFoodMenu = false;
			chef1.newDay();
			soldPerDay = 0;
			for (int item = 0; item < 5; item++)
				soldSushi[currentItem] = 0;
		}
	}

	/**
	 * Chooses a new breaking news and implements its effects; starts in the
	 * second week of simulation
	 */
	public void breakingNews()
	{
		if (totalDays % 7 == 0 && totalDays != 0)
		{
			// Select new breaking news
			breakingNewsIndex = (int) (Math.random() * BREAKING_NEWS.length);

			// Implement its effect on customer rate and customer max price
			customerRate += BREAKING_NEWS_EFFECTS[breakingNewsIndex];
			customerArray[(int) Math.random() * 4].maxPriceIncrease();
		}
	}

	/**
	 * A customer buys and item; chef, inventory, customers and total money are
	 * affected
	 */
	public void customerPurchase()
	{
		// Choose a random customer
		currentCustomerIndex = (int) (Math.random() * 4);

		// The customer will randomly choose 1 of the 5 items
		currentCustomer = customerArray[currentCustomerIndex];
		currentItem = currentCustomer.chooseItem();

		// Check if the customers will buy the item given its price
		if (currentCustomer.tooExpensive(itemPrices[currentItem]) == false)
		{
			// Check if there is enough inventory to make dish
			boolean sufficientInventory = true;
			for (int item = 0; item < INVENTORY_ITEMS.length; item++)
			{
				if (inventoryArray[item].sufficientInventory() == false)
					sufficientInventory = false;
			}

			// Check to see if chef has enough output to make the dish
			if (chef1.makeDish() && sufficientInventory)
			{
				// Add the item price to total money and total revenue
				totalMoney += itemPrices[currentItem];
				totalRevenue += itemPrices[currentItem];
				dailyRevenue += itemPrices[currentItem];

				// Add 1 to the total amount of specific sushi sold
				soldSushi[currentItem]++;
				soldPerDay++;
				soldTotal++;

				// Use 1 of each of the items (all dishes uses 1 of each)
				for (int item = 0; item < INVENTORY_ITEMS.length; item++)
				{
					inventoryArray[item].used();
				}
			}

			// Check is the customer can buy another item
			if (customerArray[currentCustomerIndex].buyAgain())
			{
				// The customer will choose 1 of the 5 items
				currentItem = customerArray[currentCustomerIndex].chooseItem();
				
				// Check if the customers will buy the item given its price
				if (currentCustomer.tooExpensive(itemPrices[currentItem]) == false)
				{

					// Check if there is enough inventory to make dish
					sufficientInventory = true;
					for (int item = 0; item < INVENTORY_ITEMS.length; item++)
						if (inventoryArray[item].sufficientInventory() == false)
							sufficientInventory = false;

					// Check to see if chef has enough output to make the dish
					if (chef1.makeDish() && sufficientInventory)
					{
						// Add the item price to total money and total revenue
						totalMoney += itemPrices[currentItem];
						totalRevenue += itemPrices[currentItem];
						dailyRevenue += itemPrices[currentItem];

						soldSushi[currentItem]++;
						soldPerDay++;
						soldTotal++;

						// Use 1 of each of the items (all dishes uses 1 of
						// each)
						for (int item = 0; item < INVENTORY_ITEMS.length; item++)
						{
							inventoryArray[item].used();
						}
					}
				}
			}
			// Checks to see if any items are out of stock after a customer
			// purchase, if there is, out of stock pop-up displays
			for (int item = 0; item < inventoryArray.length; item++)
			{
				if (inventoryArray[item].outOfStock())
					outOfStock = true;
			}
		}
		else
		{
			JOptionPane.showMessageDialog(SushiShopTycoon.this,
					"Customer: \"Whoa, that's a little too expensive for me!\""

					, "Complaint", JOptionPane.INFORMATION_MESSAGE);
		}
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

			// If player is bankrupt the game is over
			if (totalMoney <= 0)
			{
				JOptionPane.showMessageDialog(SushiShopTycoon.this,
						"You have gone bankrupt and your restauraunt has been forced to close down."

						, "Game Over", JOptionPane.INFORMATION_MESSAGE);
				int response = JOptionPane.showConfirmDialog(
						SushiShopTycoon.this,
						"Would you like to restart the game?", "New Game",
						JOptionPane.YES_NO_OPTION);
				// Get response and start a new game
				if (response == JOptionPane.NO_OPTION
						|| response == JOptionPane.CLOSED_OPTION)
				{
					drawMainMenu = true;
					restaurant = true;
					newGame();
				}
				else if (response == JOptionPane.YES_OPTION)
				{
					restaurant = true;
					newGame();
				}
			}

			// If day is over
			if (time >= timeAllowed && timerOn)
			{
				timerOn = false;
				dayEnd = true;
				dayTimer.stop();

				// Hide customer images
				xCustomerPos = -300;
				xBackCustomerPos = -300;

				// Check to see if any of the items have spoiled
				for (int item = 0; item < inventoryArray.length; item++)
				{
					spoiledToday[item] = inventoryArray[item].spoil();
				}

				// Display message with end of day summary
				JOptionPane.showMessageDialog(SushiShopTycoon.this, "Day "
						+ totalDays + " is over." + "\n\nSushi Sold Today:  "
						+ soldPerDay + "\nEgg: " + String.valueOf(soldSushi[0])
						+ "\nSalmon: " + soldSushi[1] + "\nSquid: "
						+ soldSushi[2] + "\nShrimp: " + soldSushi[3]
						+ "\nTuna: " + soldSushi[4] + "\n\nSpoiled: "
						+ "\nRice: " + spoiledToday[0] + "\nSeaweed: "
						+ spoiledToday[1] + "\nFish: " + spoiledToday[2]
						+ "\nVegetable: " + spoiledToday[3] + "\nSauce: "
						+ spoiledToday[4] + "\n\nToday's Revenue: $"
						+ dailyRevenue + ".00"

				, "End of Day", JOptionPane.INFORMATION_MESSAGE);

				// If one year is over then the game is over
				if (totalDays == 365)
				{
					simOver = true;
					JOptionPane
							.showMessageDialog(
									SushiShopTycoon.this,
									"Congratulations, you have overcome the challenges of owning a restaurant and have finished the year with an amount of: $"
											+ String.valueOf(totalMoney)
											+ "0."
											+ "\n\n You sold "
											+ soldTotal
											+ " pieces of sushi in 1 year!"

									, "Game Over",
									JOptionPane.INFORMATION_MESSAGE);
					int response = JOptionPane.showConfirmDialog(
							SushiShopTycoon.this,
							"Would you like to restart the game?", "New Game",
							JOptionPane.YES_NO_OPTION);
					// Get response and start a new game
					if (response == JOptionPane.NO_OPTION
							|| response == JOptionPane.CLOSED_OPTION)
					{
						drawMainMenu = true;
						restaurant = true;
						newGame();
					}
					else if (response == JOptionPane.YES_OPTION)
					{
						restaurant = true;
						newGame();
					}
				}

				repaint();
			}
			else if ((time < timeAllowed && timerOn))
			{
				// Time increases
				time++;

				// Move the customer forward
				if (xCustomerPos < 400)
					xCustomerPos += 100;

				if (firstCustomerOfDay == false)
					xBackCustomerPos -= 100;

				// Have customer come in and purchase item(s)
				if (time == nextCustomerTime)
				{
					customerPurchase();

					// Generate a random time for the next customer to come in
					nextCustomerTime = (int) (Math.random()
							* (customerRate - 1) + 1)
							+ time;
					// Generate next customer color and time until next
					// customer from 1 to the max customer rate
					leavingCustomerColour = currentCustomerColour;
					currentCustomerColour = (int) (Math.random() * 4);
					xCustomerPos = 0;
					xBackCustomerPos = 400;
					firstCustomerOfDay = false;
				}
				repaint();
			}

		}
	}

	/**
	 * Repaint the drawing panel.
	 * 
	 * @param g The Graphics context.
	 */
	public void paintComponent(Graphics g)
	{

		super.paintComponent(g);

		// Fonts
		Font myFont = new Font("Calibri", Font.BOLD, 30);
		Font myFontSmall = new Font("Calibri", Font.BOLD, 20);
		Font myFontBottom = new Font("Calibri", Font.BOLD, 17);
		Font inventoryFont = new Font("Calibri", Font.BOLD, 25);
		Font adFont = new Font("Calibri", Font.PLAIN, 20);
		Font storageFont = new Font("Calibri", Font.BOLD, 50);
		Color myColour = new Color(0, 250, 154, 100);
		Color myBlue = new Color(64, 224, 208, 100);
		Color myRed = new Color(255, 0, 0, 100);

		// Main Menu
		if (drawMainMenu)
			g.drawImage(mainMenu, 0, 0, this);

		// Instructions
		else if (drawInstructions)
		{
			if (drawInstruct1)
			{
				g.drawImage(instructions1, 0, 0, this);
				g.drawImage(arrow, 1052, 620, this);
			}

			if (drawInstruct2)
			{
				g.drawImage(instructions2, 0, 0, this);
				g.drawImage(arrow, 1052, 620, this);
				g.drawImage(arrowLeft, 0, 620, this);
			}

			if (drawInstruct3)
			{
				g.drawImage(instructions3, 0, 0, this);
				g.drawImage(arrow, 1052, 620, this);
				g.drawImage(arrowLeft, 0, 620, this);
			}

			if (drawInstruct4)
			{
				g.drawImage(instructions4, 0, 0, this);
				g.drawImage(arrow, 1052, 620, this);
				g.drawImage(arrowLeft, 0, 620, this);
			}

			if (drawInstruct5)
			{
				g.drawImage(instructions5, 0, 0, this);
				g.drawImage(arrowLeft, 0, 620, this);
			}

		}

		else
		// Main Game
		{
			// Draw header menu
			g.drawImage(headerMenu, 0, 0, this);

			if (restaurant == true)
			{
				// Draw background
				g.setColor(myColour);
				g.fillRect(0, 0, 150, 36);
				g.drawImage(imageBackground, 0, 36, this);
				g.setFont(adFont);
				g.setColor(Color.WHITE);
				g.drawString("Restart Game", 1300, 500);

				// Print Name
				if (name != null)
				{
					g.setColor(Color.WHITE);
					g.setFont(myFontBottom);
					g.drawString(name + "'s Restaurant", 900, 60);
				}

				// Draw customer leaving counter
				if (leavingCustomerColour == 0)
				{
					if (firstCustomerOfDay == false)
						g.drawImage(backCustomer, xBackCustomerPos, 200, this);
				}
				else if (leavingCustomerColour == 1)
				{
					if (firstCustomerOfDay == false)
						g.drawImage(backPurple, xBackCustomerPos, 200, this);
				}
				else if (leavingCustomerColour == 2)
				{
					if (firstCustomerOfDay == false)
						g.drawImage(backBlue, xBackCustomerPos, 200, this);
				}
				else if (leavingCustomerColour == 3)
				{
					if (firstCustomerOfDay == false)
						g.drawImage(backOrange, xBackCustomerPos, 200, this);
				}

				// Draw customer walking to counter
				if (currentCustomerColour == 0)
				{
					g.drawImage(customer, xCustomerPos, 260, this);
				}
				else if (currentCustomerColour == 1)
				{
					g.drawImage(purpleCustomer, xCustomerPos, 260, this);
				}
				else if (currentCustomerColour == 2)
				{
					g.drawImage(blueCustomer, xCustomerPos, 260, this);
				}
				else if (currentCustomerColour == 3)
				{
					g.drawImage(orangeCustomer, xCustomerPos, 260, this);
				}

				g.drawImage(chef, 650, 100, this);
				g.drawImage(cashier, 750, 240, this);

			}
			else if (city == true)
			{
				g.drawImage(imageBackground2, 0, 36, this);
				g.setColor(myColour);
				g.fillRect(300, 0, 100, 36);
				g.drawImage(map, 100, 50, this);
				// Draw advertisement options
				g.drawImage(advertisementMenu, 720, 50, this);
				g.setFont(adFont);
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(areasSelected), 1015, 210);
				g.drawString("$" + String.valueOf(adCost) + "0", 1015, 410);
				g.drawString(String.valueOf(totalPopulation), 1015, 455);
				g.drawString("$" + String.valueOf(totalAdCost) + "0", 1015, 500);

				// Check marks on selected areas
				for (int row = 0; row < 4; row++)
				{
					for (int column = 0; column < 4; column++)
					{
						if (selected[row][column])
						{
							g.drawImage(checkMark, (column * 150 + 125),
									(row * 150 + 75), this);
						}
					}
				}

				// Transparent box over selected ad type
				g.setColor(myColour);
				if (adType == 0)
				{
					g.fillRect(730, 260, 130, 130);
				}
				if (adType == 1)
				{
					g.fillRect(865, 260, 130, 130);
				}
				if (adType == 2)
				{
					g.fillRect(980, 260, 130, 130);
				}

			}
			else if (storage == true)
			{
				g.drawImage(imageBackground3, 0, 36, this);
				g.setColor(myColour);
				g.fillRect(150, 0, 150, 36);

				// Draw number of each inventory item
				g.setFont(storageFont);
				g.setColor(Color.WHITE);
				g.drawString(String.valueOf(inventoryArray[0].getAmount()), 90,
						640);
				g.drawString(String.valueOf(inventoryArray[1].getAmount()),
						310, 640);
				g.drawString(String.valueOf(inventoryArray[2].getAmount()),
						530, 640);
				g.drawString(String.valueOf(inventoryArray[3].getAmount()),
						770, 640);
				g.drawString(String.valueOf(inventoryArray[4].getAmount()),
						1000, 640);
			}

			g.setFont(myFontBottom);
			g.setColor(Color.BLACK);
			// Draw total money
			g.setColor(Color.BLACK);
			g.drawString("Total Money: $" + String.valueOf(totalMoney) + "0",
					950, 700);
			// Draw breaking news
			g.drawString("Breaking News: ", 140, 710);
			g.drawString(BREAKING_NEWS[breakingNewsIndex], 260, 710);

			// Print Day and Time
			g.drawString("Day:   " + String.valueOf(totalDays), 950, 715);

			// Timer
			g.setColor(Color.RED);
			g.drawString("Time: " + (time / 10) + ":00", 20, 710);

			// Items
			g.drawString("Time: " + (time / 10) + ":00", 20, 710);
			g.drawString("Time: " + (time / 10) + ":00", 20, 710);
			g.drawString("Time: " + (time / 10) + ":00", 20, 710);
			g.drawString("Time: " + (time / 10) + ":00", 20, 710);

			g.setColor(Color.BLACK);

			// Pop-ups
			if (outOfStock)
				g.drawImage(outOfStockImage, 10, 630, this);
			if (spoiled)
				g.drawImage(outOfStockImage, 10, 630, this);

			// Run day hue
			if (timerOn)
			{
				g.setColor(myRed);
				g.fillRect(400, 0, 200, 36);
			}

			// Draw pricing screen
			if (drawFoodMenu == true)
			{
				g.setFont(myFont);
				g.setColor(myBlue);
				g.fillRect(600, 0, 150, 36);
				// Draw the menu
				g.drawImage(foodMenu, 192, 144, this);
				g.drawImage(closeButton, 922, 150, this);

				// Draw the prices of the food
				g.setColor(Color.BLACK);
				g.drawString("$" + String.valueOf(itemPrices[0]) + ".00", 650,
						265);
				g.drawString("$" + String.valueOf(itemPrices[1]) + ".00", 650,
						335);
				g.drawString("$" + String.valueOf(itemPrices[2]) + ".00", 650,
						405);
				g.drawString("$" + String.valueOf(itemPrices[3]) + ".00", 650,
						475);
				g.drawString("$" + String.valueOf(itemPrices[4]) + ".00", 650,
						545);

				// Reminds player that they cannot price during the day
				if (dayEnd == false)
					g.drawImage(reminderImage, 720, 160, this);
			}

			else if (drawInventoryMenu == true)
			{
				g.drawImage(inventoryMenu, 192, 144, this);
				g.drawImage(closeButton, 922, 150, this);
				g.setColor(myBlue);
				g.fillRect(750, 0, 150, 36);

				g.setFont(inventoryFont);
				g.setColor(Color.black);
				// Draw the current stock
				g.drawString(String.valueOf(inventoryArray[0].getAmount()),
						405, 265);
				g.drawString(String.valueOf(inventoryArray[1].getAmount()),
						405, 335);
				g.drawString(String.valueOf(inventoryArray[2].getAmount()),
						405, 405);
				g.drawString(String.valueOf(inventoryArray[3].getAmount()),
						405, 475);
				g.drawString(String.valueOf(inventoryArray[4].getAmount()),
						405, 545);

				// Draw the quantity the person wishes to purchase
				g.drawString(String.valueOf(buyInventory[0]), 530, 265);
				g.drawString(String.valueOf(buyInventory[1]), 530, 335);
				g.drawString(String.valueOf(buyInventory[2]), 530, 405);
				g.drawString(String.valueOf(buyInventory[3]), 530, 475);
				g.drawString(String.valueOf(buyInventory[4]), 530, 545);

				// Draw total price of quantity
				g.drawString("$" + String.valueOf(inventoryCost[0]) + "0", 660,
						265);
				g.drawString("$" + String.valueOf(inventoryCost[1]) + "0", 660,
						335);
				g.drawString("$" + String.valueOf(inventoryCost[2]) + "0", 660,
						415);
				g.drawString("$" + String.valueOf(inventoryCost[3]) + "0", 660,
						485);
				g.drawString("$" + String.valueOf(inventoryCost[4]) + "0", 660,
						555);

				// Reminds player that they cannot buy inventory during the day
				if (dayEnd == false)
					g.drawImage(reminderImage, 720, 160, this);

			}
			else if (drawStaffMenu)
			{
				g.setColor(myBlue);
				g.fillRect(900, 0, 150, 36);

				g.setFont(myFontSmall);
				g.setColor(Color.black);
				g.drawImage(staffMenu, 192, 144, this);
				g.drawImage(closeButton, 922, 150, this);
				g.drawString(String.valueOf(chefLevel), 410, 410);
				g.drawString("$" + String.valueOf(chefUpgradeCost), 650, 355);

			}
			else if (drawStatsMenu)
			{
				g.setColor(myBlue);
				g.fillRect(1050, 0, 100, 36);

				g.drawImage(statsMenu, 192, 144, this);
				g.drawImage(closeButton, 922, 150, this);

				g.setFont(myFont);
				g.setColor(Color.black);

				g.drawString(String.valueOf(totalDays), 400, 265);
				g.drawString(String.valueOf(soldTotal), 480, 320);
				g.drawString("$" + String.valueOf(totalRevenue) + ".00", 550,
						380);
				g.drawString(String.valueOf(chefLevel), 420, 435);

				// Customer rate determines popularity
				if (customerRate >= 30)
					g.drawString("Very unpopular.", 415, 495);
				else if (customerRate >= 25 && customerRate < 30)
					g.drawString("Unpopular.", 415, 495);
				else if (customerRate >= 20 && customerRate < 25)
					g.drawString("Average.", 415, 495);
				else if (customerRate >= 15 && customerRate < 20)
					g.drawString("Good.", 415, 495);
				else
					g.drawString("Excellent!", 415, 495);
			}

		}
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		Point pressed = event.getPoint();

		// If the main menu is opened
		if (drawMainMenu)
		{
			// If start game is pressed
			if (pressed.x >= 432 && pressed.x < 720 && pressed.y >= 420
					&& pressed.y < 492)
			{

				// Checks to see if this is a new game or a continued game
				if (newGame == false)
				{
					int response = JOptionPane.showConfirmDialog(
							SushiShopTycoon.this,
							"Would you like to start a new game?", "New Game",
							JOptionPane.YES_NO_OPTION);

					// Get response and start a new game
					if (response == JOptionPane.NO_OPTION
							|| response == JOptionPane.CLOSED_OPTION)
					{
						restaurant = true;
						drawMainMenu = false;
						JOptionPane.showMessageDialog(SushiShopTycoon.this,
								"Welcome back!", "Message",
								JOptionPane.INFORMATION_MESSAGE);
					}
					else if (response == JOptionPane.YES_OPTION)
						newGame = true;
				}

				// If it is a new game, the name of the player is asked
				if (newGame)
				{
					// Max length is 15 so it won't show off the screen if it is
					// too long
					name = JOptionPane.showInputDialog(SushiShopTycoon.this,
							"Please enter your name.");
					// Deal with cancel button
					if (name != null)
					{
						while (name != null && name.length() > 15)
						{
							JOptionPane.showMessageDialog(SushiShopTycoon.this,
									"Maximum of 15 characters.", "Message",
									JOptionPane.WARNING_MESSAGE);
							name = JOptionPane.showInputDialog(
									SushiShopTycoon.this,
									"Please enter your name.");
						}
					}
					// Deal with cancel button
					if (name != null)
						JOptionPane.showMessageDialog(SushiShopTycoon.this,
								"Welcome " + name + ".", "Message",
								JOptionPane.INFORMATION_MESSAGE);
					else if (name == null)
						JOptionPane.showMessageDialog(SushiShopTycoon.this,
								"Welcome!", "Message",
								JOptionPane.INFORMATION_MESSAGE);

					drawMainMenu = false;
					newGame = false;
				}

			}

			// If instructions is pressed
			if (pressed.x >= 432 && pressed.x < 720 && pressed.y >= 528
					&& pressed.y < 600)
			{
				drawMainMenu = false;
				drawInstructions = true;
				drawInstruct1 = true;
			}
		}

		// Draw instructions
		if (drawInstructions)
		{
			if (drawInstruct1)
			{
				if (pressed.x >= 1052 && pressed.x < 1152 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct2 = true;
					drawInstruct1 = false;
				}
			}
			else if (drawInstruct2)
			{
				if (pressed.x >= 1052 && pressed.x < 1152 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct3 = true;
					drawInstruct2 = false;
				}

				if (pressed.x >= 0 && pressed.x < 100 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct1 = true;
					drawInstruct2 = false;
				}
			}

			else if (drawInstruct3)
			{
				if (pressed.x >= 1052 && pressed.x < 1152 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct4 = true;
					drawInstruct3 = false;
				}

				if (pressed.x >= 0 && pressed.x < 100 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct2 = true;
					drawInstruct3 = false;
				}

			}

			else if (drawInstruct4)
			{
				if (pressed.x >= 1052 && pressed.x < 1152 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct5 = true;
					drawInstruct4 = false;
				}

				if (pressed.x >= 0 && pressed.x < 100 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct3 = true;
					drawInstruct4 = false;
				}
			}
			else if (drawInstruct5)
			{

				if (pressed.x >= 0 && pressed.x < 100 && pressed.y >= 620
						&& pressed.y < 720)
				{
					drawInstruct4 = true;
					drawInstruct5 = false;
				}

				if (pressed.x >= 384 && pressed.x < 996 && pressed.y >= 420
						&& pressed.y < 540)
				{
					drawInstruct5 = false;
					drawInstructions = false;
					drawMainMenu = true;
					repaint();
				}
			}
		}

		if (drawMainMenu == false)
		{

			if (restaurant == true)
			{
				// Home button and Restart Game Button
				if (pressed.x >= 1044 && pressed.x < 1116 && pressed.y >= 236
						&& pressed.y < 308)
				{
					int response = JOptionPane.showConfirmDialog(
							SushiShopTycoon.this,
							"Would you like to go to the main menu? ",
							"Main Menu", JOptionPane.YES_NO_OPTION);

					// Get response to go back to main menu
					if (response == JOptionPane.NO_OPTION
							|| response == JOptionPane.CLOSED_OPTION)
						restaurant = true;
					else if (response == JOptionPane.YES_OPTION)
					{
						drawMainMenu = true;
					}
				}

				if (pressed.x >= 1044 && pressed.x < 1116 && pressed.y >= 344
						&& pressed.y < 416)
				{
					int response = JOptionPane
							.showConfirmDialog(
									SushiShopTycoon.this,
									"Would you like to restart your game? "
											+ "WARNING: THIS WILL ERASE CURRENT PROGRESS.",
									"Restart Game", JOptionPane.YES_NO_OPTION);

					if (response == JOptionPane.NO_OPTION
							|| response == JOptionPane.CLOSED_OPTION)
					{
						restaurant = true;
					}
					else if (response == JOptionPane.YES_OPTION)
					{
						restaurant = true;
						newGame();
					}
				}
			}

			// Start timer for a new day
			if (pressed.x >= 400 && pressed.x < 600 && pressed.y >= 0
					&& pressed.y < 36 && dayEnd == true)
			{
				time = 0;
				dayEnd = false;
				timerOn = true;

				runDay();
			}
			// Pause timer
			else if (pressed.x >= 400 && pressed.x < 600 && pressed.y >= 0
					&& pressed.y < 36 && dayEnd == false && timerOn == true)
			{
				dayTimer.stop();
				timerOn = false;
			}
			// Start timer
			else if (pressed.x >= 400 && pressed.x < 600 && pressed.y >= 0
					&& pressed.y < 36 && dayEnd == false && timerOn == false)
			{
				dayTimer.start();
				timerOn = true;
			}

			repaint();

			// Pressing Restaurant menu at top
			if (pressed.x >= 0 && pressed.x < 150 && pressed.y >= 0
					&& pressed.y < 36)
			{
				restaurant = true;
				city = false;
				storage = false;
				repaint();
			}
			else if (pressed.x >= 300 && pressed.x < 400 && pressed.y >= 0
					&& pressed.y < 36)
			{
				city = true;
				restaurant = false;
				storage = false;
				repaint();
			}
			else if (pressed.x >= 150 && pressed.x < 300 && pressed.y >= 0
					&& pressed.y < 36)
			{
				city = false;
				restaurant = false;
				storage = true;
				repaint();
			}

			// Pressing Pricing menu
			if (pressed.x >= 600 && pressed.x < 750 && pressed.y >= 0
					&& pressed.y < 36 && drawFoodMenu == false)
			{
				drawFoodMenu = true;
				drawStaffMenu = false;
				drawInventoryMenu = false;
				drawStatsMenu = false;
				repaint();
			}
			else if (pressed.x >= 600 && pressed.x < 750 && pressed.y >= 0
					&& pressed.y < 36 && drawFoodMenu == true)
			{
				drawFoodMenu = false;
				repaint();
			}

			// Adjusting Prices
			if (drawFoodMenu == true)
			{
				// Decrease prices
			
				if (pressed.x >= 768 && pressed.x < 822 && pressed.y >= 216
						&& pressed.y < 270 && itemPrices[0] > 0
						&& timerOn == false)
					itemPrices[0]--;

				if (pressed.x >= 768 && pressed.x < 822 && pressed.y >= 288
						&& pressed.y < 342 && itemPrices[1] > 0
						&& timerOn == false)
					itemPrices[1]--;

				if (pressed.x >= 768 && pressed.x < 822 && pressed.y >= 360
						&& pressed.y < 414 && itemPrices[2] > 0
						&& timerOn == false)
					itemPrices[2]--;

				if (pressed.x >= 768 && pressed.x < 822 && pressed.y >= 432
						&& pressed.y < 486 && itemPrices[3] > 0
						&& timerOn == false)
					itemPrices[3]--;

				if (pressed.x >= 768 && pressed.x < 822 && pressed.y >= 504
						&& pressed.y < 558 && itemPrices[4] > 0
						&& timerOn == false)
					itemPrices[4]--;

				// Increase Prices
				if (pressed.x >= 858 && pressed.x < 912 && pressed.y >= 216
						&& pressed.y < 270 && itemPrices[0] < 100
						&& timerOn == false)
					itemPrices[0]++;

				if (pressed.x >= 858 && pressed.x < 912 && pressed.y >= 288
						&& pressed.y < 342 && itemPrices[1] < 100
						&& timerOn == false)
					itemPrices[1]++;

				if (pressed.x >= 858 && pressed.x < 912 && pressed.y >= 360
						&& pressed.y < 414 && itemPrices[2] < 100
						&& timerOn == false)
					itemPrices[2]++;

				if (pressed.x >= 858 && pressed.x < 912 && pressed.y >= 432
						&& pressed.y < 486 && itemPrices[3] < 100
						&& timerOn == false)
					itemPrices[3]++;

				if (pressed.x >= 858 && pressed.x < 912 && pressed.y >= 504
						&& pressed.y < 558 && itemPrices[4] < 100
						&& timerOn == false)
					itemPrices[4]++;

				// Close window
				if (pressed.x >= 922 && pressed.x <= 952 && pressed.y >= 150
						&& pressed.y <= 192)
					drawFoodMenu = false;

				repaint();

			}

			// Draw Inventory Menu
			if (pressed.x >= 750 && pressed.x < 900 && pressed.y >= 0
					&& pressed.y < 36 && drawInventoryMenu == false)
			{
				drawInventoryMenu = true;
				drawFoodMenu = false;
				drawStaffMenu = false;
				drawStatsMenu = false;
				outOfStock = false;
			}
			// Close window
			else if (pressed.x >= 750
					&& pressed.x < 900
					&& pressed.y >= 0
					&& pressed.y < 36
					&& drawInventoryMenu == true
					|| (pressed.x >= 922 && pressed.x <= 952
							&& pressed.y >= 150 && pressed.y <= 192 && drawInventoryMenu == true))
			{
				drawInventoryMenu = false;
				repaint();
			}

			if (drawInventoryMenu == true && dayEnd == true)
			{
				// Decrease prices
				if (pressed.x >= 732 && pressed.x < 786 && pressed.y >= 216
						&& pressed.y < 270 && buyInventory[0] > 0)
					buyInventory[0] -= 5;

				if (pressed.x >= 732 && pressed.x < 786 && pressed.y >= 288
						&& pressed.y < 342 && buyInventory[1] > 0)
					buyInventory[1] -= 5;

				if (pressed.x >= 732 && pressed.x < 786 && pressed.y >= 360
						&& pressed.y < 414 && buyInventory[2] > 0)
					buyInventory[2] -= 5;

				if (pressed.x >= 732 && pressed.x < 786 && pressed.y >= 432
						&& pressed.y < 486 && buyInventory[3] > 0)
					buyInventory[3] -= 5;

				if (pressed.x >= 732 && pressed.x < 786 && pressed.y >= 504
						&& pressed.y < 558 && buyInventory[4] > 0)
					buyInventory[4] -= 5;

				if (pressed.x >= 795 && pressed.x < 849 && pressed.y >= 216
						&& pressed.y < 270)
					buyInventory[0] += 5;

				if (pressed.x >= 795 && pressed.x < 849 && pressed.y >= 288
						&& pressed.y < 342)
					buyInventory[1] += 5;

				if (pressed.x >= 795 && pressed.x < 849 && pressed.y >= 360
						&& pressed.y < 414)
					buyInventory[2] += 5;

				if (pressed.x >= 795 && pressed.x < 849 && pressed.y >= 432
						&& pressed.y < 486)
					buyInventory[3] += 5;

				if (pressed.x >= 822 && pressed.x < 849 && pressed.y >= 504
						&& pressed.y < 558)
					buyInventory[4] += 5;

				inventoryCost[0] = buyInventory[0] * INVENTORY_PRICES[0];
				inventoryCost[1] = buyInventory[1] * INVENTORY_PRICES[1];
				inventoryCost[2] = buyInventory[2] * INVENTORY_PRICES[2];
				inventoryCost[3] = buyInventory[3] * INVENTORY_PRICES[3];
				inventoryCost[4] = buyInventory[4] * INVENTORY_PRICES[4];

				// If they press buy

				if (pressed.x >= 876 && pressed.x < 948 && pressed.y >= 234
						&& pressed.y < 270)
				{
					totalMoney -= inventoryCost[0];
					inventoryArray[0].bought(buyInventory[0]);
					buyInventory[0] = 0;
					inventoryCost[0] = 0;
				}
				if (pressed.x >= 876 && pressed.x < 948 && pressed.y >= 306
						&& pressed.y < 342)
				{
					totalMoney -= inventoryCost[1];
					inventoryArray[1].bought(buyInventory[1]);
					buyInventory[1] = 0;
					inventoryCost[1] = 0;

				}
				if (pressed.x >= 876 && pressed.x < 948 && pressed.y >= 378
						&& pressed.y < 414)
				{
					totalMoney -= inventoryCost[2];
					inventoryArray[2].bought(buyInventory[2]);
					buyInventory[2] = 0;
					inventoryCost[2] = 0;

				}
				if (pressed.x >= 876 && pressed.x < 948 && pressed.y >= 450
						&& pressed.y < 486)
				{
					totalMoney -= inventoryCost[3];
					inventoryArray[3].bought(buyInventory[3]);
					buyInventory[3] = 0;
					inventoryCost[3] = 0;

				}
				if (pressed.x >= 876 && pressed.x < 948 && pressed.y >= 522
						&& pressed.y < 558)
				{
					totalMoney -= inventoryCost[4];
					inventoryArray[4].bought(buyInventory[4]);
					buyInventory[4] = 0;
					inventoryCost[4] = 0;

				}

				// Close window
				if (pressed.x >= 922 && pressed.x <= 952 && pressed.y >= 150
						&& pressed.y <= 192)
					drawInventoryMenu = false;

				repaint();

			}

			// Draw Staffing Menu
			if (pressed.x >= 900 && pressed.x < 1050 && pressed.y >= 0
					&& pressed.y < 36 && drawStaffMenu == false)
			{
				drawStaffMenu = true;
				drawFoodMenu = false;
				drawInventoryMenu = false;
				drawStatsMenu = false;
				repaint();
			}
			else if (pressed.x >= 900 && pressed.x < 1050 && pressed.y >= 0
					&& pressed.y < 36 && drawStaffMenu == true)
			{
				drawStaffMenu = false;
				repaint();
			}

			if (drawStaffMenu == true)
			{
				if (pressed.x >= 552 && pressed.x < 822 && pressed.y >= 261
						&& pressed.y < 324)
				{
					chefLevel++;
					totalMoney -= chefUpgradeCost;
					chefUpgradeCost += 500;
					chef1.upgrade();

					repaint();
				}

				// Close window
				if (pressed.x >= 922 && pressed.x <= 952 && pressed.y >= 150
						&& pressed.y <= 192)
					drawStaffMenu = false;
				repaint();
			}

			// Opening statistics menu
			if (pressed.x >= 1050 && pressed.x < 1152 && pressed.y >= 0
					&& pressed.y < 36 && drawStatsMenu == false)
			{
				drawStatsMenu = true;
				drawFoodMenu = false;
				drawInventoryMenu = false;
				drawStaffMenu = false;
				repaint();
			}
			else if (pressed.x >= 1050 && pressed.x < 1152 && pressed.y >= 0
					&& pressed.y < 36 && drawStatsMenu == true)
			{
				drawStatsMenu = false;
				repaint();
			}

			if (drawStatsMenu)
			{
				// Close window
				if (pressed.x >= 922 && pressed.x <= 952 && pressed.y >= 150
						&& pressed.y <= 192)
					drawStatsMenu = false;

				repaint();
			}

			if (city == true && drawStaffMenu == false && drawFoodMenu == false
					&& drawInventoryMenu == false)
			{
				// Let player select type of advertisement
				if (pressed.x > 740 && pressed.x < 844 && pressed.y > 260
						&& pressed.y < 360)
				{
					adType = 0;
					adCost = AD_COST[adType];
					totalAdCost = adCost * totalPopulation;
				}
				if (pressed.x > 874 && pressed.x < 979 && pressed.y > 260
						&& pressed.y < 360)
				{
					adType = 1;
					adCost = AD_COST[adType];
					totalAdCost = adCost * totalPopulation;
				}
				if (pressed.x > 999 && pressed.x < 1104 && pressed.y > 260
						&& pressed.y < 360)
				{
					adType = 2;
					adCost = AD_COST[adType];
					totalAdCost = adCost * totalPopulation;
				}

				// Sends out advertisements
				if (pressed.x > 835 && pressed.x < 1020 && pressed.y > 530
						&& pressed.y < 615)
				{
					// Deduct money
					totalMoney -= totalAdCost;

					// Increase customer rate and increase margin
					customersIncrease += (totalPopulation * AD_EFFECT_PERCENTAGE[adType]);
					increaseMargin = (int) (customersIncrease / 1000.0);
					customersIncrease -= 1000 * increaseMargin;

					// Make customers come more frequent to a max rate of 5
					if (customerRate > 10)
						customerRate -= increaseMargin;

					// Reset ad options
					for (int row = 0; row < 4; row++)
					{
						for (int column = 0; column < 4; column++)
						{
							if (selected[row][column])
							{
								selected[row][column] = false;
							}
						}
					}
					areasSelected = 0;
					totalPopulation = 0;
					totalAdCost = 0;
				}

				// Select areas on grid

				// Increase area selected, total population and total ad cost
				// First row
				if (pressed.x >= 100 && pressed.x < 250 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][0] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[0][0];
					totalAdCost += POPULATION_IN_AREA[0][0] * adCost;
					selected[0][0] = true;
				}
				else if (pressed.x >= 100 && pressed.x < 250 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][0] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[0][0] * adCost;
					totalPopulation -= POPULATION_IN_AREA[0][0];
					selected[0][0] = false;
				}
				if (pressed.x >= 250 && pressed.x < 400 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][1] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[0][1];
					totalAdCost += POPULATION_IN_AREA[0][1] * adCost;
					selected[0][1] = true;
				}
				else if (pressed.x >= 250 && pressed.x < 400 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][1] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[0][1] * adCost;
					totalPopulation -= POPULATION_IN_AREA[0][1];
					selected[0][1] = false;
				}
				if (pressed.x >= 400 && pressed.x < 550 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][2] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[0][2];
					totalAdCost += POPULATION_IN_AREA[0][2] * adCost;
					selected[0][2] = true;
				}
				else if (pressed.x >= 400 && pressed.x < 550 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][2] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[0][2] * adCost;
					totalPopulation -= POPULATION_IN_AREA[0][2];
					selected[0][2] = false;
				}
				if (pressed.x >= 550 && pressed.x < 700 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][3] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[0][3];
					totalAdCost += POPULATION_IN_AREA[0][3] * adCost;
					selected[0][3] = true;
				}
				else if (pressed.x >= 550 && pressed.x < 700 && pressed.y >= 50
						&& pressed.y < 200 && selected[0][3] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[0][3] * adCost;
					totalPopulation -= POPULATION_IN_AREA[0][3];
					selected[0][3] = false;
				}

				// Second row
				if (pressed.x >= 100 && pressed.x < 250 && pressed.y >= 200
						&& pressed.y < 350 && selected[1][0] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[1][0];
					totalAdCost += POPULATION_IN_AREA[1][0] * adCost;
					selected[1][0] = true;
				}
				else if (pressed.x >= 100 && pressed.x < 250
						&& pressed.y >= 200 && pressed.y < 350
						&& selected[1][0] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[1][0] * adCost;
					totalPopulation -= POPULATION_IN_AREA[1][0];
					selected[1][0] = false;
				}
				if (pressed.x >= 250 && pressed.x < 400 && pressed.y >= 200
						&& pressed.y < 350 && selected[1][1] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[1][1];
					totalAdCost += POPULATION_IN_AREA[1][1] * adCost;
					selected[1][1] = true;
				}
				else if (pressed.x >= 250 && pressed.x < 400
						&& pressed.y >= 200 && pressed.y < 350
						&& selected[1][1] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[1][1] * adCost;
					totalPopulation -= POPULATION_IN_AREA[1][1];
					selected[1][1] = false;
				}
				if (pressed.x >= 400 && pressed.x < 550 && pressed.y >= 200
						&& pressed.y < 350 && selected[1][2] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[1][2];
					totalAdCost += POPULATION_IN_AREA[1][2] * adCost;
					selected[1][2] = true;
				}
				else if (pressed.x >= 400 && pressed.x < 550
						&& pressed.y >= 200 && pressed.y < 350
						&& selected[1][2] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[1][2] * adCost;
					totalPopulation -= POPULATION_IN_AREA[1][2];
					selected[1][2] = false;
				}
				if (pressed.x >= 550 && pressed.x < 700 && pressed.y >= 200
						&& pressed.y < 350 && selected[1][3] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[1][3];
					totalAdCost += POPULATION_IN_AREA[1][3] * adCost;
					selected[1][3] = true;
				}
				else if (pressed.x >= 550 && pressed.x < 700
						&& pressed.y >= 200 && pressed.y < 350
						&& selected[1][3] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[1][3] * adCost;
					totalPopulation -= POPULATION_IN_AREA[1][3];
					selected[1][3] = false;
				}
				// Third row
				if (pressed.x >= 100 && pressed.x < 250 && pressed.y >= 350
						&& pressed.y < 500 && selected[2][0] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[2][0];
					totalAdCost += POPULATION_IN_AREA[2][0] * adCost;
					selected[2][0] = true;
				}
				else if (pressed.x >= 100 && pressed.x < 250
						&& pressed.y >= 350 && pressed.y < 500
						&& selected[2][0] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[2][0] * adCost;
					totalPopulation -= POPULATION_IN_AREA[2][0];
					selected[2][0] = false;
				}
				if (pressed.x >= 250 && pressed.x < 400 && pressed.y >= 350
						&& pressed.y < 500 && selected[2][1] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[2][1];
					totalAdCost += POPULATION_IN_AREA[2][1] * adCost;
					selected[2][1] = true;
				}
				else if (pressed.x >= 250 && pressed.x < 400
						&& pressed.y >= 350 && pressed.y < 500
						&& selected[2][1] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[2][1] * adCost;
					totalPopulation -= POPULATION_IN_AREA[2][1];
					selected[2][1] = false;
				}
				if (pressed.x >= 400 && pressed.x < 550 && pressed.y >= 350
						&& pressed.y < 500 && selected[2][2] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[2][2];
					totalAdCost += POPULATION_IN_AREA[2][2] * adCost;
					selected[2][2] = true;
				}
				else if (pressed.x >= 400 && pressed.x < 550
						&& pressed.y >= 350 && pressed.y < 500
						&& selected[2][2] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[2][2] * adCost;
					totalPopulation -= POPULATION_IN_AREA[2][2];
					selected[2][2] = false;
				}
				if (pressed.x >= 550 && pressed.x < 700 && pressed.y >= 350
						&& pressed.y < 500 && selected[2][3] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[2][3];
					totalAdCost += POPULATION_IN_AREA[2][3] * adCost;
					selected[2][3] = true;
				}
				else if (pressed.x >= 550 && pressed.x < 700
						&& pressed.y >= 350 && pressed.y < 500
						&& selected[2][3] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[2][3] * adCost;
					totalPopulation -= POPULATION_IN_AREA[2][3];
					selected[2][3] = false;
				}

				// Fourth row
				if (pressed.x >= 100 && pressed.x < 250 && pressed.y >= 500
						&& pressed.y < 750 && selected[3][0] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[3][0];
					totalAdCost += POPULATION_IN_AREA[3][0] * adCost;
					selected[3][0] = true;
				}
				else if (pressed.x >= 100 && pressed.x < 250
						&& pressed.y >= 500 && pressed.y < 750
						&& selected[3][0] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[3][0] * adCost;
					totalPopulation -= POPULATION_IN_AREA[3][0];
					selected[3][0] = false;
				}
				if (pressed.x >= 250 && pressed.x < 400 && pressed.y >= 500
						&& pressed.y < 750 && selected[3][1] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[3][1];
					totalAdCost += POPULATION_IN_AREA[3][1] * adCost;
					selected[3][1] = true;
				}
				else if (pressed.x >= 250 && pressed.x < 400
						&& pressed.y >= 500 && pressed.y < 750
						&& selected[3][1] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[3][1] * adCost;
					totalPopulation -= POPULATION_IN_AREA[3][1];
					selected[3][1] = false;
				}
				if (pressed.x >= 400 && pressed.x < 550 && pressed.y >= 500
						&& pressed.y < 750 && selected[3][2] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[3][2];
					totalAdCost += POPULATION_IN_AREA[3][2] * adCost;
					selected[3][2] = true;
				}
				else if (pressed.x >= 400 && pressed.x < 550
						&& pressed.y >= 500 && pressed.y < 750
						&& selected[3][2] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[3][2] * adCost;
					totalPopulation -= POPULATION_IN_AREA[3][2];
					selected[3][2] = false;
				}
				if (pressed.x >= 550 && pressed.x < 700 && pressed.y >= 500
						&& pressed.y < 750 && selected[3][3] == false)
				{
					areasSelected++;
					totalPopulation += POPULATION_IN_AREA[3][3];
					totalAdCost += POPULATION_IN_AREA[3][3] * adCost;
					selected[3][3] = true;
				}
				else if (pressed.x >= 550 && pressed.x < 700
						&& pressed.y >= 500 && pressed.y < 750
						&& selected[3][3] == true)
				{
					areasSelected--;
					totalAdCost -= POPULATION_IN_AREA[3][3] * adCost;
					totalPopulation -= POPULATION_IN_AREA[3][3];
					selected[3][3] = false;
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{

	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}

}