 /*
 * @(#)Launcher.java		0.3 14/6/18
 * 
 * Copyright 2014, MAGIC Spell Studios, LLC
 */
package com.percipient24.cgc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.util.Locale;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.percipient24.enums.Platform;

/*
 * A Launcher for getting desired screen size from the user
 * 
 * @version 0.3 14/6/18
 * @author J.D. Kelly
 */
public class Launcher implements Runnable {

	private String[] resolutions = {"640 x 480", "720 x 480", "800 x 600", "1024 x 768", "1152 x 720", "1280 x 720", "1280 x 800", "1344 x 840", "1440 x 900", "1920 x 1080", "1920 x 1200"};
	private Color backgroundColor = new Color(.13f, .066f, 0.0f, 1.0f);
	private Color fontColor = new Color(.96f, .64f, .07f, 1.0f);
	private final JFrame myFrame = new JFrame("Chain Gang Chase Launcher");
	
	/*
	 * Preferences In Order:
	 * Whether or not to show the Launcher
	 * The desired width of the screen
	 * The desired height of the screen
	 * Whether or not the game should be in full screens
	 */
	private final String[] preferences = new String[4];
	
	//Whether or not the text in the W and H boxes are valid integers > 0
	private boolean wFine = false;
	private boolean hFine = false;
	
	//The screen's width divided by the screen's height
	//Used for determining if something can be used for full screen
	private double myRes = 0.0f;
	
	//Warnings Labels for when X or Y is invalid
	private final JLabel wWarning = new JLabel("Not an Int", JLabel.CENTER);
	private final JLabel hWarning = new JLabel("Not an Int", JLabel.CENTER);
	
	//Text Fields for inputting the desired width and height values
	private final JTextField wBox = new JTextField();
	private final JTextField hBox = new JTextField();
	
	//The font for the text boxes when everything is normal
	private Font plainBoxFont = new Font("Dialog", Font.PLAIN, 12);
	//The font for the text boxes when something is wrong
	private Font boldBoxFont = new Font("Dialog", Font.BOLD, 12);
	
	//Check box for full screen
	private final JCheckBox fullScreenBox = new JCheckBox("Full Screen");

	//A combo box with all the default resolutions as options
	private final JComboBox resolutionSelect = new JComboBox(resolutions);

	/*
	 * Creates the Launcher for Chain Gang Chase
	 */
	public void run() {
		
		// Set the background color and size of the frame
		myFrame.getContentPane().setBackground(backgroundColor);
		
		// Sets the behavior for when the window is closed
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Sets the minimum size of the launcher
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		//Use the screen size to calculate resolution
		myRes = (double) screenSize.width / (double) screenSize.height;
		
		//Set the size of the launcher to fit on the smallest support screen
		myFrame.setMinimumSize(new Dimension(800, 600));
		
		//Set the frame in the middle
		myFrame.setLocation(screenSize.width / 2 - 400, screenSize.height / 2 - 300);
	   
		//The Different Layers of Panels - This Panelception goes pretty deep.  Hopefully, we don't go t limbo.
		
		//Level 1 - One Panel to rule them all, One Panel to find them, One Panel to bring them all and in the darkness bind them
		JPanel uberPanel = new JPanel();
	  
		GroupLayout uberLayout = new GroupLayout(uberPanel);
		uberPanel.setLayout(uberLayout);
		
		//Level 2 - Panels made specifically to hold other panels and center them
		GridBagLayout levelTwoLayout = new GridBagLayout();
		
		JPanel superPanel = new JPanel();
		superPanel.setBackground(backgroundColor);
		superPanel.setLayout(levelTwoLayout);
		
		JPanel duperPanel = new JPanel();
		duperPanel.setBackground(backgroundColor);
		duperPanel.setLayout(levelTwoLayout);
		
		//Level 3 - The Panels that actually hold important stuff
		//Upper Panel holds the text
		JPanel upperPanel = new JPanel();
		upperPanel.setBackground(backgroundColor);
		GroupLayout upperLayout = new GroupLayout(upperPanel);
		upperPanel.setLayout(upperLayout);
		
		//Lower Panel holds the stuff that gets user input
		JPanel lowerPanel = new JPanel();
		lowerPanel.setBackground(backgroundColor);
		GroupLayout lowerLayout = new GroupLayout(lowerPanel);
		lowerPanel.setLayout(lowerLayout);
		
		//Creates the font for the labels
		Font labelFont = new Font("Dialog", Font.PLAIN, 30);
	   
		//Creates the two top labels
		JLabel welcomeLabel = new JLabel("Welcome To Chain Gang Chase!");
		welcomeLabel.setMinimumSize(new Dimension(400, 50));
		welcomeLabel.setForeground(fontColor);
		welcomeLabel.setFont(labelFont);
		
		JLabel selectLabel = new JLabel("Please Select Your Resolution");
		selectLabel.setForeground(fontColor);
		selectLabel.setFont(labelFont);
		
		//The Game Logo
		// java.net.URL imageURL = getClass().getResource("cgc_logotype.png");
		// ImageIcon logoIcon = new ImageIcon(imageURL, "Chain Gang Chase logo");
		// JLabel logo = new JLabel(logoIcon);
		// logo.setMinimumSize(new Dimension(400, 325));
		// logo.setMaximumSize(new Dimension(400, 325));
		
		//The appropriate size for the warning
		Dimension warningSize = new Dimension(100, 12);
		
		//Warnings for the W and H warning text boxes
		Font warningFont = new Font("Dialog", Font.BOLD, 12);
		wWarning.setMinimumSize(warningSize);
		wWarning.setFont(warningFont);
		wWarning.setForeground(backgroundColor);
		
		hWarning.setMinimumSize(warningSize);
		hWarning.setFont(warningFont);
		hWarning.setForeground(backgroundColor);
		
		//Size constraints for the text fields
		Dimension maxTF = new Dimension(100, 30);
		Dimension minTF = new Dimension(100, 30);
		
		//Creates the text boxes that determine the size of the game
		wBox.setHorizontalAlignment(JTextField.CENTER);
		wBox.setMinimumSize(minTF);
		wBox.setMaximumSize(maxTF);
		wBox.setToolTipText("The desired width of the game window.");
		
		//Check the text every time it's changed
		wBox.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent arg0) {
				checkBoxes();
			}

			public void insertUpdate(DocumentEvent arg0) {
				checkBoxes();
			}

			public void removeUpdate(DocumentEvent arg0) {
				checkBoxes();
			}
			
		});
		
		hBox.setHorizontalAlignment(JTextField.CENTER);
		hBox.setMinimumSize(minTF);
		hBox.setMaximumSize(maxTF);
		hBox.setToolTipText("The desired height of the game window");
		
		//Check the text every time it's changed
		hBox.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent arg0) {
				checkBoxes();
			}

			public void insertUpdate(DocumentEvent arg0) {
				checkBoxes();
			}

			public void removeUpdate(DocumentEvent arg0) {
				checkBoxes();
			}
		});
		
		//Creates the font for the labels for the W and H boxes
		Font boxFont = new Font("Dialog", Font.PLAIN, 24);
		
		//Creates the labels for the w and h boxes
		JLabel wTag = new JLabel("W:");
		wTag.setFont(boxFont);
		wTag.setForeground(fontColor);
		
		JLabel hTag = new JLabel("H:");
		hTag.setFont(boxFont);
		hTag.setForeground(fontColor);
		
		//Brings the combo box into alignment with the rest of the row
		resolutionSelect.setMaximumSize(new Dimension(135, 31));
		resolutionSelect.setMinimumSize(new Dimension(135, 31));
		resolutionSelect.setSelectedIndex(0);
	  
		resolutionSelect.addItemListener(new ItemListener() {

			//Sets the text boxes' texts to W and H of the resolution
			//when the selected resolution in the combo box is changed
			public void itemStateChanged(ItemEvent arg0) {
				String[] res = ((String)resolutionSelect.getSelectedItem()).split(" x ");
				wBox.setText(res[0]);
				hBox.setText(res[1]);
			}
			
		});
		
		//Creates the check box for Full Screen
		fullScreenBox.setBackground(backgroundColor);
		fullScreenBox.setForeground(fontColor);
		fullScreenBox.setFont(new Font("Dialog", Font.PLAIN, 18));
		fullScreenBox.setToolTipText("If checked, the game will start in full screen.  Otherwise, it will be windowed.");
	   
		//When the box is checked, update the TextFields and make sure their info is valid
		fullScreenBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxes();
			}
			
		});
		
		//Creates the check box for showing the launcher
		final JCheckBox useLauncherBox = new JCheckBox("Don't Ask Again");
		useLauncherBox.setBackground(backgroundColor);
		useLauncherBox.setForeground(fontColor);
		useLauncherBox.setFont(new Font(fullScreenBox.getFont().getName(), Font.PLAIN, 18));
		useLauncherBox.setToolTipText("<html>If this is checked when preferences are saved, the launcher won't be shown again.<br />This can be changed in the Options menu.</html>");
		
		//Whether or not the preferences have been set
		boolean ps = false;
		try {
			//Reads in the file resolutionPreferences.bin
			FileReader fr = new FileReader("resolutionPreferences.bin");
			BufferedReader br = new BufferedReader(fr);
			preferences[0] = br.readLine();
			preferences[1] = br.readLine();
			preferences[2] = br.readLine();
			preferences[3] = br.readLine();
			
			ps = true;
			
			br.close();
			fr.close();
			
			//Use values 1 and 2 for W and H
			wBox.setText(preferences[1]);
			hBox.setText(preferences[2]);
			
			//If a resolution equal to "prefs[1] x prefs[2]" is in the array, 
			//set it to the current selection
			int ind = java.util.Arrays.asList(resolutions).indexOf(preferences[1]
					+ " x " + preferences[2]);
			
			if(ind > -1)
			{
				resolutionSelect.setSelectedIndex(ind);
			}
			
			//use value 3 for full screen
			fullScreenBox.setSelected(Boolean.parseBoolean(preferences[3]));
			
		} catch (FileNotFoundException e) {
			//Sets the text boxes' texts to W and H of the resolution
			String[] res = ((String)resolutionSelect.getSelectedItem()).split(" x ");
			wBox.setText(res[0]);
			hBox.setText(res[1]);
			ps = false;
			
		} catch (IOException e) {
			//If there's no file, the preferences haven't been set
			ps = false;
		}
		final boolean prefsSet = ps;
		
		//Brings the check box into alignment with the rest of the row
		fullScreenBox.setMaximumSize(new Dimension(200, 26));
		fullScreenBox.setMinimumSize(new Dimension(200, 26));
		
		//Creates a button to reset everything
		JButton resetButton = new JButton("Reset");
		resetButton.setToolTipText("Resets the values to their starting values.");
		resetButton.addActionListener(new ActionListener(){
			
			//Resets the selected resolution back to the original and full screen back to false
			public void actionPerformed(ActionEvent arg0) {
				//If there were loaded preferences, set them back to the original
				if(prefsSet)
				{
					wBox.setText(preferences[1]);
					hBox.setText(preferences[2]);
					
					int ind = java.util.Arrays.asList(resolutions).indexOf(preferences[1]
							+ " x " + preferences[2]);
					
					if(ind > -1)
					{
						resolutionSelect.setSelectedIndex(ind);
					}
					
					fullScreenBox.setSelected(Boolean.parseBoolean(preferences[3]));
				}
				//Otherwise, set the selected resolution back to 0 and full screen to false
				else
				{
					if(resolutionSelect.getSelectedIndex() != 0)
					{
						resolutionSelect.setSelectedIndex(0);
					}
					else
					{
						String[] res = ((String)(resolutionSelect.getItemAt(0))).split(" x ");
						wBox.setText(res[0]);
						hBox.setText(res[1]);
					}
					
					fullScreenBox.setSelected(false);
				}
			}
			
		});
		
		//Creates the button for starting the game
		JButton startButton = new JButton("Start");
		startButton.setToolTipText("Starts the game.");
		startButton.addActionListener(new ActionListener(){
			//Starts the game if everything is correct
			public void actionPerformed(ActionEvent evt)
			{
				//If there's something wrong, show the user a message
				if(!wFine || !hFine)
				{
					String message = "Your ";
					
					if(!wFine)
					{
						if(!hFine)
						{
							message += "W and H values were";
						}
						else
						{
							message += "W value was";
						}
					}
					else if(!hFine)
					{
						message += "H value was";
					}
					
					message += " not usable.\nPlease check the values in the boxes and try again.";
					JOptionPane.showMessageDialog(myFrame, message, "Resolution Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//Check for resolution compatibility
				Dimension ss = new Dimension(Integer.parseInt(wBox.getText()), Integer.parseInt(hBox.getText()));
				double curRes = (double)(ss.width) / (double)(ss.height);
			
				if(curRes > myRes && fullScreenBox.isSelected())
				{
					JOptionPane.showMessageDialog(myFrame, "You have chosen a resolution that cannot be used for full screen.\nPlease, either disable full screen or chose a different resolution.", "Resolution Warning", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				//If it passes, create the game using the given values
				startGame(ss, fullScreenBox.isSelected());
				
				//Hide the launcher
				myFrame.setVisible(false);
			}
		});
		
		//Creates the Detect Resolution Button
		JButton detectButton = new JButton("Detect Res");
		detectButton.setToolTipText("Sets the values to the width and height of the screen.");
		
		detectButton.addActionListener(new ActionListener(){

			//Sets W and H to the Width and Height of the Screen and Full Screen to true
			public void actionPerformed(ActionEvent arg0) {
				wBox.setText(Integer.toString(screenSize.width));
				hBox.setText(Integer.toString(screenSize.height));
				
				//If the list of resolutions contains the screen resolution, select the appropriate item
				int ind = java.util.Arrays.asList(resolutions).indexOf(Integer.toString(screenSize.width) 
						+ " x " +Integer.toString(screenSize.height));
				
				if(ind >= 0)
				{
					resolutionSelect.setSelectedIndex(ind);
				}
				
				//And set full screen to true
				fullScreenBox.setSelected(true);
			}
			
		});
		
		//Creates the button for exiting
		JButton exitButton = new JButton("Exit");
		exitButton.setToolTipText("Exits the launcher.  All unsaved changes will be lost.");
		exitButton.addActionListener(new ActionListener(){

			//Quits the game
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		//Creates a button for saving resolution preferences
		JButton saveButton = new JButton("Save Preferences");
		saveButton.setToolTipText("Saves the values of the check boxes and text boxes for quick access later.");
		saveButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				
					//If there's something wrong with the values, display an error message
					if(!wFine || !hFine)
					{
						String message = "Your ";
						
						if(!wFine)
						{
							if(!hFine)
							{
								message += "W and H values were";
							}
							else
							{
								message += "W value was";
							}
						}
						else if(!hFine)
						{
							message += "H value was";
						}
						
						message += " not usable.  Please check the values in the boxes and try again.";
						JOptionPane.showMessageDialog(myFrame, message, "Resolution Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					/*
					 * Otherwise, write the preferences to the file "resolutionPreferences.bin" in this other
					 * Whether or not to use the launcher again - !launcherBox.isSelected()
					 * Width of the game - wBox.getText();
					 * Height of the game - hBox.getText();
					 * Full Screen - fullScreenBox.isSelected();
					 */
					try {
						FileWriter fw;
						fw = new FileWriter("resolutionPreferences.bin");
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(String.valueOf(!useLauncherBox.isSelected()));
						bw.newLine();
						bw.write(wBox.getText());
						bw.newLine();
						bw.write(hBox.getText());
						bw.newLine();
						bw.write(String.valueOf(fullScreenBox.isSelected()));
						bw.flush();
						bw.close();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
			}
			
		});
		
		//Organizes the Lower Panel's components horizontally
		GroupLayout.SequentialGroup ltr = lowerLayout.createSequentialGroup();
		GroupLayout.ParallelGroup vrow = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup vrow2 = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup vrow3 = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup vrow4 = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup vrow5 = lowerLayout.createParallelGroup();
		
		ltr.addComponent(wTag);
		
		vrow.addComponent(wWarning);
		vrow.addComponent(wBox);
		vrow.addGap(5);
		vrow.addComponent(exitButton);
		ltr.addGroup(vrow);
	   
		ltr.addGap(10);
		ltr.addComponent(hTag);
		
		vrow2.addComponent(hWarning);
		vrow2.addComponent(hBox);
		vrow2.addGap(5);
		vrow2.addComponent(detectButton);
		ltr.addGroup(vrow2);
		
		ltr.addGap(10);
		
		vrow3.addComponent(resolutionSelect);
		vrow3.addGap(5);
		vrow3.addComponent(resetButton);
		ltr.addGroup(vrow3);
		
		ltr.addGap(10);
		
		vrow4.addComponent(fullScreenBox);
		vrow4.addGap(5);
		vrow4.addComponent(startButton);
		ltr.addGroup(vrow4);
		
		ltr.addGap(10);
		
		vrow5.addComponent(useLauncherBox);
		vrow5.addGap(5);
		vrow5.addComponent(saveButton);
		ltr.addGroup(vrow5);
		
		//Organizes the Lower Panel's components vertically
		GroupLayout.SequentialGroup ttb = lowerLayout.createSequentialGroup();
		GroupLayout.ParallelGroup hrow = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup hrow2 = lowerLayout.createParallelGroup();
		GroupLayout.ParallelGroup hrow3 = lowerLayout.createParallelGroup();
	  
		hrow.addComponent(wWarning);
		hrow.addComponent(hWarning);
		ttb.addGroup(hrow);
		
		hrow2.addComponent(wTag);
		hrow2.addComponent(wBox);
		hrow2.addGap(10);
		hrow2.addComponent(hTag);
		hrow2.addComponent(hBox);
		hrow2.addGap(10);
		hrow2.addComponent(resolutionSelect);
		hrow2.addGap(10);
		hrow2.addComponent(fullScreenBox);
		hrow2.addGap(10);
		hrow2.addComponent(useLauncherBox);
		ttb.addGroup(hrow2);
		ttb.addGap(5);
		
		hrow3.addComponent(exitButton);
		hrow3.addGap(10);
		hrow3.addComponent(detectButton);
		hrow3.addGap(10);
		hrow3.addComponent(resetButton);
		hrow3.addGap(10);
		hrow3.addComponent(startButton);
		hrow3.addGap(10);
		hrow3.addComponent(saveButton);
		ttb.addGroup(hrow3);
		
		lowerLayout.setHorizontalGroup(ltr);
		lowerLayout.setVerticalGroup(ttb);
		
		superPanel.add(lowerPanel);
		
		//Organize's the Upper Panel's components vertically
		GroupLayout.SequentialGroup uttb = upperLayout.createSequentialGroup();
		uttb.addComponent(welcomeLabel);
		//uttb.addComponent(logo);
		uttb.addComponent(selectLabel);
		
		//Organize's the Upper Panel's components horizontally
		GroupLayout.SequentialGroup ultr = upperLayout.createSequentialGroup();
		GroupLayout.ParallelGroup uvr = upperLayout.createParallelGroup();
		uvr.addComponent(welcomeLabel);
		//uvr.addComponent(logo);
		uvr.addComponent(selectLabel);
		ultr.addGroup(uvr);
		upperLayout.setHorizontalGroup(ultr);
		upperLayout.setVerticalGroup(uttb);
	   
		duperPanel.add(upperPanel);
		
		//Organizes the One Panel's component's vertically
		GroupLayout.SequentialGroup dttb = uberLayout.createSequentialGroup();
		dttb.addComponent(duperPanel);
		dttb.addComponent(superPanel);
		
		//Organizes the One Panel's component's horizontally
		GroupLayout.SequentialGroup dltr = uberLayout.createSequentialGroup();
		GroupLayout.ParallelGroup dvr = uberLayout.createParallelGroup();
		dvr.addComponent(duperPanel);
		dvr.addComponent(superPanel);
		dltr.addGroup(dvr);
		
		uberLayout.setHorizontalGroup(dltr);
		uberLayout.setVerticalGroup(dttb);
		
		myFrame.add(uberPanel);
		
		// Arrange the components inside the window
		myFrame.pack();
   
		// By default, the window is not visible. Make it visible.
		myFrame.setVisible(true);
	}
	
	/*
	 * Starts the game
	 * 
	 * @param d						The desired screen size
	 * @param b						Whether or not it will be full screen
	 */
	public void startGame(Dimension d, boolean b)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Chain Gang Chase";
		cfg.useGL30 = false;
		
		cfg.width = d.width;
		cfg.height = d.height;
		
		cfg.fullscreen = b;
		
				// TODO : make this changeable
				Locale locale = new Locale("pi");
		new LwjglApplication(new ChaseApp(Platform.DESKTOP, locale), cfg)
		{	
			public void exit()
			{
				SoundManager.endGame();
				TimerManager.clear();
				ChaseApp.stats.saveCurrentStats();
				
				postRunnable(new Runnable() 
				{
					public void run() 
					{
						running = false;
					}
				});
			}
		};
		
	}
	
	/*
	 * Checks the boxes for anything wrong.  
	 * If there is, it turns the box red and displays the appropriate error text.
	 */
	public void checkBoxes(){
		
		//First check W and H
		int w = 0;
		int h = 0;

		try{
			//Try to make an int out of w
			wBox.setFont(plainBoxFont);
			w = Integer.parseInt(wBox.getText());
			wBox.setBackground(Color.WHITE);
			wWarning.setForeground(backgroundColor);
			wFine = true;
			
			//If it's less than or equal to zero, 
			//let the user know that it shouldn't be
			if(w <= 0)
			{
				wBox.setFont(boldBoxFont);
				wBox.setBackground(Color.RED);
				wWarning.setForeground(fontColor);
				wWarning.setText("Must Be > 0");
				wFine = false;
			}
		}
		catch(Exception e)
		{
			//If it's not an int, let the user know that it should be one
			wBox.setFont(boldBoxFont);
			wBox.setBackground(Color.RED);
			wWarning.setForeground(fontColor);
			wWarning.setText("Must Be An Int");
			wFine = false;
		}
		
		//Do the same checks with h.  
		//Then if either of them are invalid, donn't bother with the full screen checks.
		try{
			hBox.setFont(plainBoxFont);
			h = Integer.parseInt(hBox.getText());
			hBox.setBackground(Color.WHITE);
			hWarning.setForeground(backgroundColor);
			hFine = true;
			
			if(h <= 0)
			{
				hBox.setFont(boldBoxFont);
				hBox.setBackground(Color.RED);
				hWarning.setForeground(fontColor);
				hWarning.setText("Must Be > 0");
				hFine = false;
				return;
			}
		}
		catch(Exception e)
		{
			hBox.setFont(boldBoxFont);
			hBox.setBackground(Color.RED);
			hWarning.setForeground(fontColor);
			hWarning.setText("Must Be An Int");
			hFine = false;
			return;
		}
		
		if(!wFine)
		{
			return;
		}
		
		//If they both passed, check width / height and check it against the screen's values
		double curRes = (double)(w) / (double)(h);
		
		if(curRes > myRes && fullScreenBox.isSelected())
		{
			wWarning.setForeground(fontColor);
			wWarning.setText("Not Full Screen");
			hWarning.setForeground(fontColor);
			hWarning.setText("Compatible");
			
			wBox.setFont(boldBoxFont);
			hBox.setFont(boldBoxFont);
			wBox.setBackground(Color.red);
			hBox.setBackground(Color.red);
		}
		
	}

}//End Class