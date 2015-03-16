import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class Concentration extends JFrame{
    //ImageIcon blank = new ImageIcon("blank.gif");
    javax.swing.Timer timer = new javax.swing.Timer(1000, new TimerListener());

    String[] names = { "One", "Two", "Three", "Four", 
                       "Five", "Six", "Seven", "Eight"
    };
    
    int label_count = names.length * 2;

    ArrayList<String> NameList = new ArrayList<String> (label_count);
    HashMap<JButton, String> ButtonMap = new HashMap<JButton, String>(label_count);
    JButton[] gameButtons;
    JButton clickedButton;
    ArrayList<JButton> clickBuffer = new ArrayList<JButton>(2);    	
    String clickedButtonName;

    int currentScore = 0;
    int bestScore = Integer.MAX_VALUE;
    JButton button1, button2;

    // panels
    JPanel gameBoard;
    JPanel scoreBoard;
    JPanel scorePane;
	
    // menu components
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu helpMenu;
    JMenuItem menuNewGame;
    JMenuItem menuExit;
    JMenuItem about;
	
    // textfields
    JTextField currentScoreText;
    JTextField bestScoreText;

	
    public Concentration() {
        buildWindow();
        buildScoreBoard();
        buildGameBoard();
        buildMenu();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
	
    public void buildWindow(){
        setTitle("Concentration");
        setSize(620, 500);

        /* declare JPanels which will make up game window */
        gameBoard = new JPanel();
        scoreBoard = new JPanel();
        scorePane = new JPanel();

        /* set panel background colors */
        gameBoard.setBackground(Color.green);
        scoreBoard.setBackground(Color.orange);
        scorePane.setBackground(Color.orange);	

        /* lay out panels */
        setLayout(new BorderLayout());
        scorePane.setLayout(new GridLayout(8,1,2,2));
        add(gameBoard, BorderLayout.CENTER);
        add(scoreBoard, BorderLayout.EAST);
        gameBoard.setBorder(BorderFactory.createTitledBorder("Game Board"));
        scoreBoard.setBorder(BorderFactory.createTitledBorder("Scoreboard"));
    }

    public void buildScoreBoard() {
        /* instantiate buttons with actionlisteners */
        JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ExitListener());
        JButton exit = new JButton("Exit");
        exit.addActionListener(new ExitListener());		

        scoreBoard.setPreferredSize(new Dimension(120, 500));
        scoreBoard.setLayout(new BorderLayout());	
        scoreBoard.add(newGame, BorderLayout.NORTH);
        scoreBoard.add(exit, BorderLayout.SOUTH);
        scoreBoard.add(scorePane, BorderLayout.CENTER);

        /* make the necessary text fields and add them to the game board */ 
        currentScoreText = new JTextField();
        bestScoreText = new JTextField();
        currentScoreText.setBorder(BorderFactory.createTitledBorder("Score so far"));
        bestScoreText.setBorder(BorderFactory.createTitledBorder("Best score"));
        scorePane.add(currentScoreText);
        currentScoreText.setEditable(false);
        currentScoreText.setText(Integer.toString(currentScore));
        scorePane.add(bestScoreText);
        bestScoreText.setEditable(false);
        if (bestScore == Integer.MAX_VALUE)
            bestScoreText.setText("Infinity");
        else
            bestScoreText.setText(Integer.toString(bestScore));
    }

    public void buildGameBoard() {
        gameButtons = new JButton[16];
        gameBoard.setLayout (new GridLayout(4,4,2,2));

        // put array elements into ArrayList twice each, then shuffle
        for (int i = 0; i < gameButtons.length / 2; i++) {
            for (int j = 0; j < 2; j++) {
                NameList.add(names[i]);
            }
        }		

        // shuffle buttons then add to gameboard
        Collections.shuffle(NameList);
        for (int i = 0; i < gameButtons.length; i++) {
            gameButtons[i] = new JButton();
            gameButtons[i].setBackground(Color.LIGHT_GRAY);
            gameButtons[i].setForeground(Color.blue);
            gameBoard.add(gameButtons[i]);
            ButtonMap.put(gameButtons[i], NameList.get(i));
            gameButtons[i].addActionListener(new GameButtonListener());
        }
    }

    public void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        JMenuItem menuNew = new JMenuItem("New Game");
        JMenuItem menuExit = new JMenuItem("Exit");
        JMenuItem about = new JMenuItem("About");
        fileMenu.add(menuNew);
        menuNew.addActionListener(new ExitListener());
        fileMenu.add(menuExit);
        menuExit.addActionListener(new ExitListener());
        helpMenu.add(about);
        about.addActionListener(new AboutListener());
        setJMenuBar(menuBar);
    }
	
    private class ExitListener implements ActionListener{
        public void actionPerformed(ActionEvent event) {
            String text = event.getActionCommand();
            if (text.equals("Exit"))
                System.exit(0);
            else endGame(true);
        }
    }
		
    private class GameButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            /* todo */
            timer.stop();
            Object eventSource = event.getSource();

            if (eventSource instanceof JButton) {
                clickedButton = (JButton) eventSource;
            }

            if (clickBuffer.size() < 2) {
                if (!(clickBuffer.size() == 1 && clickedButton.equals(clickBuffer.get(0)))) {
                    /* once the first button has been clicked, it cannot be "flipped" back over,
                       and a new button must be chosen to continue. */
                    clickBuffer.add(clickedButton);
                    clickedButtonName = ButtonMap.get(clickedButton);
                    clickedButton.setText(clickedButtonName);
                }

                if (clickBuffer.size() == 2) {
                    /* compare the values the clicked buttons correspond to.
                       if they match, disable both buttons. otherwise, show the player
                       the mismatched values for one second before covering them again. */
                    button1 = clickBuffer.get(0);
                    button2 = clickBuffer.get(1);
                    currentScore++;
                    currentScoreText.setText(Integer.toString(currentScore));

                    if (ButtonMap.get(button1).equals(ButtonMap.get(button2))) {
                        button1.setEnabled(false);
                        button2.setEnabled(false);

                        while (!clickBuffer.isEmpty())
                            clickBuffer.remove(0);            
                    } else {
                        timer.start();
                    }
                } 
            }
            
            /* if all of the buttons are disabled, the game is over */
            if (allButtonsDisabled())
                endGame(false);
        }

        private boolean allButtonsDisabled() {
            for (JButton gb : gameButtons) {
                if (gb.isEnabled()) {
                    return false;
                } 
            } return true;
        }
    }
	
    private class AboutListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JOptionPane.showMessageDialog(
                null, "Concentration by Eli Mullis\nVersion: 0.1.0");
	}
    }
	
    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            /* cover the text on the clicked buttons after a delay,
               so the player can read them. then clear the buffer of
               clicked game buttons. */
            button1.setText("");
            button2.setText("");
            while (!clickBuffer.isEmpty())
                clickBuffer.remove(0);            
        }
    }
	
    public void endGame(boolean premature) {
        /* reshuffle buttons, then add to gameboard */
        Collections.shuffle(NameList);
        for (int i = 0; i < gameButtons.length; i++) {
            gameButtons[i].setEnabled(true);
            gameButtons[i].setText("");
            ButtonMap.put(gameButtons[i], NameList.get(i));
        }
 
        /* clear click buffer */
        while (!clickBuffer.isEmpty()) {
            clickBuffer.remove(0);
        }
		
        /* reset score, and set best score if necessary*/
        if (!premature) {
            if (currentScore < bestScore)
                bestScore = currentScore;
        }
        currentScore = 0;
        if (bestScore == Integer.MAX_VALUE)
            bestScoreText.setText("Infinity");
        else 
            bestScoreText.setText(Integer.toString(bestScore));
        currentScoreText.setText(Integer.toString(currentScore));
    }

    public static void main (String[]args) {
        Concentration window = new Concentration(); 
    }
}
