import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private Level level;
    private MineTile[][] board;
    int tilesClicked = 0;
    boolean gameOver = false;
    int mineCount;
    int flagsUsed;
    ArrayList<MineTile> mineList;
    JFrame frame;
    JLabel textLabel;
    JPanel textPanel;
    JPanel boardPanel;
    Random random = new Random();
    
    public Minesweeper() {
        frame = new JFrame("Minesweeper");
        textLabel = new JLabel();
        textPanel = new JPanel();
        boardPanel = new JPanel();
        level = menuChooseLevel(); 
        run(level);
    
    }
    /**
     * Constructor that initializes the Minesweeper game with a specific level.
     * 
     * @param level the level of the game
     */
    public Minesweeper(Level level) {
        this.level = level;
        run(level);
    }

    /**
     * Displays a dialog for the user to choose a level.
     * 
     * @return the chosen level
     */
    public Level menuChooseLevel(){
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(frame, "Choose a level:", "Minesweeper",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> {
                level = Level.EASY;}
      
            case 1 -> {
                level = Level.MEDIUM;
            }
            case 2 -> {
                level = Level.HARD;
            }
            default -> {
                JOptionPane.showMessageDialog(frame, "Invalid choice. Defaulting to Easy level.");
                level = Level.EASY;
            }

           
          
        }
        return level;
    }

    /**
     * Initializes the game board and sets up the GUI.
     * 
     * @param level the level of the game
     */
    void run(Level level) {
        
        mineCount = level.mines;
        flagsUsed = mineCount;
        board = new MineTile[level.rows][level.cols];
        int boardWidth = level.cols * Settings.TILE_SIZE;
        int boardHeight = level.rows * Settings.TILE_SIZE;

        frame.setSize(boardWidth, boardHeight + 50);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText(Integer.toString(mineCount) + " Mines Left " );
        
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(level.rows, level.cols)); 
        // boardPanel.setBackground(Color.green);
        frame.add(boardPanel);

        for (int r = 0; r < level.rows; r++) {
            for (int c = 0; c < level.cols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                if (flagsUsed > 0) {
                                    tile.setText("🚩");
                                    flagsUsed -= 1;
                                    textLabel.setText(flagsUsed + " Flags Left");
                                } else {
                                    JOptionPane.showMessageDialog(frame, "No flags left!");
                                }
                               
                                
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                                flagsUsed += 1;
                                textLabel.setText(flagsUsed + " Flags Left");
                            } else {
                                JOptionPane.showMessageDialog(frame, "You can't flag this tile!");
                            }
                        }
                    }
                });

                boardPanel.add(tile);

            }
        }

        frame.setVisible(true);

        setMines();
    }
    /**
     * Sets the mines on the board randomly.
     * The number of mines is determined by the level.
     */
    public void setMines() {
        
        mineList = new ArrayList<MineTile>();

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(level.rows);
            int c = random.nextInt(level.cols);

            MineTile tile = board[r][c];

            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }
    /**
     * Reveals all mines on the board and ends the game.
     * It sets the text of each mine tile to "💣" and displays a game over message.
     */
    public void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
        restartGame();
    }
    /**
     *  Prompts the user to restart the game.
     */
    public void restartGame() {
        int option = getEndOption();
        if (option == JOptionPane.YES_OPTION) {
            frame.dispose();
            new Minesweeper();
        } else {
            System.exit(0);
        }
    }
    /**
     * Displays a dialog asking the user if they want to play again.
     * 
     * @return an integer representing the user's choice
     */
    public int getEndOption(){
        return JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION);
    }

    /**
     * Checks the tile at (r, c) and reveals it if it is not a mine.
     * If it is a mine, it reveals all mines and ends the game.
     * If it is not a mine, it checks the surrounding tiles recursively.
     * 
     * @param r row index
     * @param c column index
     */
    public void checkMine(int r, int c) {
      if (r < 0 || r >= level.rows || c < 0 || c >= level.cols)
            return; // out of bounds
        

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        // top 3
        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c); // top
        minesFound += countMine(r - 1, c + 1); // top right

        // left and right
        minesFound += countMine(r, c - 1); // left
        minesFound += countMine(r, c + 1); // right

        // bottom 3
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c); // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            // top 3
            checkMine(r - 1, c - 1); // top left
            checkMine(r - 1, c); // top
            checkMine(r - 1, c + 1); // top right

            // left and right
            checkMine(r, c - 1); // left
            checkMine(r, c + 1); // right

            // bottom 3
            checkMine(r + 1, c - 1); // bottom left
            checkMine(r + 1, c); // bottom
            checkMine(r + 1, c + 1); // bottom right
        }

      if (tilesClicked == level.rows * level.cols - mineList.size())
        {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }
    /**
     * Counts the number of mines around the given tile (r, c).
     * 
     * @param r row index
     * @param c column index
     * @return number of mines around the tile
     */
    int countMine(int r, int c) {
       if (r < 0 || r >= level.rows || c < 0 || c >= level.cols){
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
}
