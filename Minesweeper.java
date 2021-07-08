import java.awt.*; //color, font
import java.awt.event.*; //action listeners, mouse listeners
import javax.swing.*; //frames, panels, buttons, layouts
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Minesweeper extends JFrame implements ActionListener,MouseListener
{

	JToggleButton[][] board;
	JPanel boardPanel;
	boolean firstClick = true;
	int numMines = 10;
	boolean gameOn = true;
	ImageIcon[] numbers; //array of number images
	ImageIcon[] faces;

	//need menu bar, menu, and 3 menu items, all happens in action perform method
	JMenuBar menuBar = new JMenuBar();
	JMenu difficulty;
	JMenuItem[] difficultyItems;
	String[] levels = {"Beginner", "Intermediate", "Expert"};
	int width = 9;
	int height = 9;
	boolean beginner = false, intermediate = false, expert = false; //if i'm already on intermediate and click intermediate, i don't want it to reset

	JButton reset;
	boolean resetpressed = false;

	int timePassed;
	Timer timer;
	JTextField timeField;
	Font timerFont;


	ImageIcon mineIcon, flag;
	GraphicsEnvironment ge; //we'll load in font without installing it
	Font mineFont; //there's gonna be setFont line

	String[] facelist = {"lose0", "smile0", "wait0", "win0"};


	public Minesweeper()
	{


		try //loading in font from hard drive
		{
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			mineFont = Font.createFont(Font.TRUETYPE_FONT, new File("mine-sweeper.ttf"));
			ge.registerFont(mineFont);

			timerFont = Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf"));
			ge.registerFont(timerFont);


		}catch(IOException | FontFormatException e){
						System.out.println(mineFont);

		}

		mineIcon = new ImageIcon("mine.png");
		mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH)); //scales it down to size of buttons



		numbers = new ImageIcon[8];
		for(int x = 0; x < 8; x++)
		{
			numbers[x] = new ImageIcon((x+1)+".png");
			numbers[x] = new ImageIcon(numbers[x].getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH)); //scales it down to size of buttons
		}

		flag = new ImageIcon("flag.png");
		flag = new ImageIcon(flag.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH)); //scales it down to size of buttons

		faces = new ImageIcon[4];
		for(int x = 0; x < 4; x++)
		{
			faces[x] = new ImageIcon(facelist[x]+".png");
			faces[x] = new ImageIcon(faces[x].getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH)); //scales it down to size of buttons
		}


		difficulty = new JMenu("Difficulty");
		difficultyItems = new JMenuItem[levels.length];
		for(int x = 0; x < difficultyItems.length; x++)
		{
			difficultyItems[x] = new JMenuItem(levels[x]);
			difficultyItems[x].addActionListener(this);
			difficulty.add(difficultyItems[x]);

		}
		menuBar.add(difficulty);
		this.add(menuBar, BorderLayout.NORTH);

		reset = new JButton();
		reset.addActionListener(this);
		reset.setIcon(faces[1]);
		menuBar.add(reset);

		timeField = new JTextField(""+timePassed);
		timeField.setFont(timerFont.deriveFont(30f));
		timeField.setBackground(Color.BLUE);
		timeField.setForeground(Color.YELLOW);
		menuBar.add(timeField);

		createBoard(height,width);



		this.setResizable(false); //window can't change size
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true); //want frame to be visible, "this" refers to frame, or window



	}

	public void createBoard(int row, int col)
	{
		if(boardPanel != null)
		this.remove(boardPanel); //remove boardPanel from frame
		boardPanel = new JPanel(); //new panel to put all buttons into
		board = new JToggleButton[row][col];
		boardPanel.setLayout(new GridLayout(row, col));




		for(int r = 0; r < row; r++)
		{
			for(int c = 0; c < col; c++)
			{
				board[r][c] = new JToggleButton();
				board[r][c].putClientProperty("row", r); //kinda lika a map, store property
				board[r][c].putClientProperty("column", c);
				board[r][c].putClientProperty("state", 0); //is it mine? if not, what's its count


				board[r][c].setBorder(BorderFactory.createBevelBorder(0));
				board[r][c].setFocusPainted(false); //keeps all buttons from having focus


			//	board[r][c].setFont(mineFont.deriveFont(16f));


				board[r][c].addMouseListener(this); //are you right clicking or left clicking on it
				boardPanel.add(board[r][c]);
			}
		}
		this.add(boardPanel); //frame, or window, gets this panel of buttons
		this.setSize(col * 40, row * 40); //need to set size of window based on button sizes
		//it's width by height
		this.revalidate(); //??
	}

	public void actionPerformed(ActionEvent e)
	{
		for(int y = 0; y < levels.length; y++)
		{
			if(e.getSource() == difficultyItems[y])
			{
				if(y == 0&& beginner != true)
				{
					beginner = true;
					intermediate = false;
					expert = false;
					width = 9;
					height = 9;
					numMines = 10;
					firstClick = true;
					gameOn = true;
					timePassed = 0;
					timer.cancel();
					createBoard(width, height);
				}
				else if (y ==1 && intermediate != true)
				{
					beginner = false;
					intermediate = true;
					expert = false;
					width = 16;
					height = 16;
					numMines = 40;
					firstClick = true;
					gameOn = true;
					timePassed = 0;
					timer.cancel();
					timeField.setText(""+timePassed);

					createBoard(width, height);
				}
				else if(y== 2 && expert != true)
				{
					beginner = false;
					intermediate = false;
					expert = true;
					width = 16;
					height = 30;
					numMines = 99;
					firstClick = true;
					gameOn = true;
					timePassed = 0;
					timer.cancel();
					timeField.setText(""+timePassed);

					createBoard(height, width);

				}
			}
		}

		if(e.getSource() == reset)
		{
			timePassed = 0;
			timer.cancel();
			timeField.setText(""+timePassed);
			reset.setIcon(faces[1]);
			firstClick = true;
			gameOn = true;
			createBoard(height, width);


		}
	}




	//5 mouse listener methods need override
	public void mouseReleased(MouseEvent e) //after u click and let go
	{
		//which button threw event
		if(gameOn)
		{
			int row = (int)((JToggleButton)e.getComponent()).getClientProperty("row"); //like a map
			int col = (int)((JToggleButton)e.getComponent()).getClientProperty("column");
			if(e.getButton() == MouseEvent.BUTTON1 && board[row][col].isEnabled()) //button1 is left click
			{
				reset.setIcon(faces[1]); //after ur done left clicking it resets to smiley face

				if(firstClick)
				{
					timer = new Timer();
					timer.schedule(new UpdateTimer(), 0, 1000);
					setMinesAndCounts(row, col);
					firstClick = false;
				}
				//we clicked, now have to expand
				int state = (int)(board[row][col].getClientProperty("state"));
				if(state == -1)
				{
					timer.cancel();
					reset.setIcon(faces[0]); //loser face
					gameOn = false;
					board[row][col].setContentAreaFilled(false);
					board[row][col].setOpaque(true);
					board[row][col].setBackground(Color.RED);
					board[row][col].setIcon(mineIcon);

					//if u lose, show where all mines are
					revealMines();
					//JOptionPane.showMessageDialog(null, "You are a loser!");
					//show other mine locations
					//prevent clicking
					//set all of togglebuttons to disabled

					//wanna display mineIcon




				}
				else //now expansion
				{
					expand(row, col);
					checkWin();


				}

		}
		//flagging
		if(e.getButton()==MouseEvent.BUTTON3)//button3 is right clicking, button1 is left clicking
		{
			//can't have been selected yet
			if(!board[row][col].isSelected())
			{
					//can't flag place twice
					if(board[row][col].getIcon() == null)
					{
						board[row][col].setIcon(flag);
						board[row][col].setDisabledIcon(flag);
						//after flagging can't do anything with it later
						board[row][col].setEnabled(false);

					}
					else //unfllagging
					{ //take flag off and reenable
						board[row][col].setIcon(null);
						board[row][col].setEnabled(true);


					}


			}
		}

	}


	}

	public void revealMines()
	{
		for(int r = 0; r <board.length; r++)
		{
			for(int c = 0;  c < board[0].length; c++)
			{
				int state = (int)board[r][c].getClientProperty("state");
				if(state == -1)
				{
					board[r][c].setIcon(mineIcon);
					board[r][c].setDisabledIcon(mineIcon);
				}

				board[r][c].setEnabled(false); //disable all buttons, problematic cuz colors kinda fade

			}
		}

	}

	public void checkWin() //make this better
	{
		int dimR = board.length;
		int dimC = board[0].length;
		int totalSpaces = dimR * dimC; //how many button total
		int count = 0;
		for(int r = 0; r< dimR; r++)
		{
			for(int c = 0; c<dimC; c++)
			{
				int state = (int)board[r][c].getClientProperty("state");
				if(board[r][c].isSelected() && state != -1)
				count++;

			}
		}
		if(numMines == totalSpaces - count) //in order to win have to clear out entire board that's not mines
		{
			timer.cancel();
			reset.setIcon(faces[3]);
			//JOptionPane.showMessageDialog(null, "You are a winner!");
		}




	}


	public void write(int row, int col, int state)
	{
		/*
		switch (state)
		{
			case 1:
			board[row][col].setForeground(Color.BLUE);
			break;
			case 2:
			board[row][col].setForeground(Color.GREEN);
			break;
			case 3:
			board[row][col].setForeground(Color.RED);
			break;
			//case 4:
			//board[row][col].setForeground(Color.new Color(128, 0, 128));
			//
			 5: 128, 0, 0
			6: cyan
			7: black
			8:

			//break;
			//goes till 8
			//case -1: board[row][col].setIcon(mineIcon); break;

		} */

		if(state >0)
		{
			//board[row][col].setText("" + state); , no longer using font

			board[row][col].setIcon(numbers[state - 1]);
			board[row][col].setDisabledIcon(numbers[state -1]);

		}

	}

	public void expand(int row, int col)
	{
			//expands when u hit 0, doesn't expand when it's 1 or -1 or 2 or 3 or...
			//what's value of button we clicked?
			if(!board[row][col].isSelected()) //don't want to get into cycle don't want button to be already selected
			board[row][col].setSelected(true); //we're checking off buttons

			int state = (int)board[row][col].getClientProperty("state");
			if(state > 0)
			// call set text in method
			write(row,col,state);
			//board[row][col].setText("" + state);
			else //now it's 0; go to every box that surrounds it and see what's there
			{

				for(int r33 = row-1; r33 <= row+1; r33++) //3 by 3 window around current position
				{
						for(int c33 = col-1; c33 <= col+1; c33++)
						{
							try
							{
								if(!board[r33][c33].isSelected())
								expand(r33, c33);
							}catch(ArrayIndexOutOfBoundsException e){}

						}
				}



			}


	}


	public void setMinesAndCounts(int currRow, int currCol)
	{
		int tempcount = numMines;
		//can't put it on other mine or  where u just clicked
		int dimR = board.length;
		int dimC = board[0].length;

		while(tempcount > 0) //everytime u put mine in grid count goes down
		{
			//choosing random location
			int randR = (int)(Math.random() * dimR);
			int randC = (int)(Math.random() * dimC);
			int state = (int)((JToggleButton)board[randR][randC]).getClientProperty("state");
			//mine has state -1
			if(state != -1 && (Math.abs(randR - currRow) > 1 || Math.abs(randC - currCol) > 1)) //mine can't already exist here, and can't put where i clicked or within one space
			{
				board[randR][randC].putClientProperty("state", -1);
				tempcount--;
			}
		}

		for(int r = 0; r < dimR; r++) //process of assigning states to all squares
		{
			for(int c = 0; c < dimC; c++)
			{
				int state = (int)((JToggleButton)board[r][c]).getClientProperty("state");
				if(state != -1)
				{
						tempcount = 0;
						for(int r33 = r-1; r33 <= r+1; r33++) //3 by 3 window around current position
						{
							for(int c33 = c-1; c33 <= c+1; c33++)
							{
								try
								{
									state = (int)((JToggleButton)board[r33][c33]).getClientProperty("state");
									if(state == -1)
									tempcount++;
								}catch(ArrayIndexOutOfBoundsException e){}

							}
						}

					System.out.println(tempcount);
					board[r][c].putClientProperty("state", tempcount);
					//board[r][c].setText("" + state);
				}


			}

		}


	}

	public void mousePressed(MouseEvent e){
		if(e.getButton() == MouseEvent.BUTTON1)
		reset.setIcon(faces[2]);

	}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}


	public class UpdateTimer extends TimerTask implements Runnable
	{
		public void run()
		{
			timePassed++;
			timeField.setText("" + timePassed);

		}


	}


	public static void main(String[] args)
	{
		Minesweeper app = new Minesweeper();
	}
}