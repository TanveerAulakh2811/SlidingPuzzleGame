import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;

public class LevelPuzzle extends JFrame {
    private JButton[][] squares;
    private int emptyRow, emptyCol;
    private List<ImageIcon> tileImages;
    private List<List<ImageIcon>> levels; // List of image lists representing different levels
    private int currentLevelIndex; // Index of the current level

    private int gridSize;

    public LevelPuzzle(int gridSize) {
        this.gridSize = gridSize;
        System.out.println(gridSize);
        // Initialize levels and currentLevelIndex
        levels = new ArrayList<>();
        levels.add(loadLevel("vampire chibi.jpg")); // Add the first level
        levels.add(loadLevel("lazy.jpg"));
        // Add more levels if needed

        currentLevelIndex = 0; // Start with the first level

        // Load the image for the current level
        tileImages = levels.get(currentLevelIndex);
        System.out.println("Constructor: " + "at"+ currentLevelIndex + tileImages);

        BufferedImage[] images = new BufferedImage[2];

        // Load the first image and store it in the array
        try {
            File imageFile1 = new File("C:\\Users\\aulak\\IdeaProjects\\practicejava_1 GUI\\src\\vampire chibi.jpg");
            images[0] = ImageIO.read(imageFile1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load the second image and store it in the array
        try {
            File imageFile2 = new File("C:\\Users\\aulak\\IdeaProjects\\practicejava_1 GUI\\src\\lazy.jpg");
            images[1] = ImageIO.read(imageFile2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("images[0]: " + images[0]);
        System.out.println("images[1]: " + images[1]);



        BufferedImage originalImage1 = images[currentLevelIndex];
        System.out.println("originalImage1: " +originalImage1);


        // Initialize puzzle
        initializePuzzle();

        // Shuffle and display tiles
        shuffleTiles();

        setTitle("Sliding Puzzle");

        int imagewidth= originalImage1.getWidth();
        int imageheight= originalImage1.getHeight();

        System.out.println("imagewidth: " + imagewidth);
        System.out.println("imageheight: " +imageheight);

        // Calculate the size of the JFrame based on the gridSize
        setSize(imagewidth,imageheight); // Set the size of the JFrame to accommodate the entire puzzle grid

        // Create a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current LevelPuzzle window
                dispose();

                // Prompt the user to select mode and grid size again
                promptModeAndGridSize();
            }
        });

        // Create a panel to hold the back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);

        // Add the button panel to the top of the content pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);

        // Initialize puzzle
        initializePuzzle();

        // Set a preferred size for the content pane
        getContentPane().setPreferredSize(new Dimension(imagewidth, imageheight + buttonPanel.getPreferredSize().height));


        // Shuffle and display tiles
        shuffleTiles();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Automatically size the window based on its components
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void promptModeAndGridSize() {
        String[] modes = {"Easy", "Medium", "Hard", "Custom"};
        String mode = (String) JOptionPane.showInputDialog(null, "Select mode:", "Mode Selection", JOptionPane.PLAIN_MESSAGE, null, modes, modes[0]);
        int gridSize;
        switch (mode) {
            case "Easy":
                gridSize = 3;
                break;
            case "Medium":
                gridSize = 4;
                break;
            case "Hard":
                gridSize = 5;
                break;
            case "Custom":
                gridSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter grid size (e.g., 3 for 3x3 grid):"));
                break;
            default:
                gridSize = 3;
                // Default to easy mode
        }

        SwingUtilities.invokeLater(() -> new LevelPuzzle(gridSize));
    }

    // Method to initialize the puzzle grid
    private void initializePuzzle() {
        JPanel puzzlePanel = new JPanel(new GridLayout(gridSize, gridSize)); // Create a panel for the puzzle grid

        ButaneListener listener = new ButaneListener(); // Create an instance of the ButaneListener class to handle button clicks

        squares = new JButton[gridSize][gridSize]; // Initialize the array to hold buttons for each tile
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton btn = new JButton();
                squares[i][j] = btn;
                // Set the icon for each button based on its position in the grid
                squares[i][j].setIcon(tileImages.get(i * gridSize + j));
                squares[i][j].addActionListener(listener); // Add an action listener to handle button clicks
                puzzlePanel.add(squares[i][j]); // Add the button to the puzzle panel
            }
        }

        // Add the puzzle panel to the content pane
        getContentPane().add(puzzlePanel, BorderLayout.CENTER);
    }
    // Load level images from file
    private List<ImageIcon> loadLevel(String filename) {
        List<ImageIcon> levelImages = new ArrayList<>();
        // Load the image for the current level
        File imageFile = new File("C:\\Users\\aulak\\IdeaProjects\\practicejava_1 GUI\\src\\" + filename);
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(imageFile);
            System.out.println("imageFIle:" +imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("loadlevel: " +gridSize);
        System.out.println("loadlevel: " +originalImage.getWidth());
        System.out.println("loadlevel: " +originalImage.getHeight());
        levelImages = splitImage(originalImage, gridSize, gridSize);
        return levelImages;
    }

    // Calculate grid size based on image dimensions


    // Split the original image into tiles
    private List<ImageIcon> splitImage(BufferedImage originalImage, int rows, int cols) {
        List<ImageIcon> tileImages = new ArrayList<>();
        int width = originalImage.getWidth() / cols;
        int height = originalImage.getHeight() / rows;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                BufferedImage tile = originalImage.getSubimage(x * width, y * height, width, height);
                ImageIcon icon = new ImageIcon(tile);
                tileImages.add(icon);
            }
        }
        return tileImages;
    }

    // Shuffle the tiles in the grid
    private void shuffleTiles() {
        int totalTiles = gridSize * gridSize;
        int totalPieces = totalTiles - 1; // Total tiles minus one for the empty space

        // Shuffle positions
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < totalPieces; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);

        // Assign pieces to the grid
        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (index < totalPieces) {
                    int position = positions.get(index++);
                    int row = position / gridSize;
                    int col = position % gridSize;
                    squares[i][j].setIcon(tileImages.get(row * gridSize + col));
                } else {
                    // Set the last tile as empty
                    squares[i][j].setIcon(null);
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }
    }

    // Method to move a tile in the grid
    private void moveTile(int row, int col) {
        if (isValidMove(row, col)) {
            int targetX = emptyCol * squares[0][0].getWidth();
            int targetY = emptyRow * squares[0][0].getHeight();

            Timer timer = new Timer(10, new ActionListener() {
                int deltaX = (targetX - col * squares[0][0].getWidth()) / 10;
                int deltaY = (targetY - row * squares[0][0].getHeight()) / 10;
                int steps = 10;

                @Override
                public void actionPerformed(ActionEvent e) {
                    steps--;
                    squares[row][col].setLocation(squares[row][col].getX() + deltaX, squares[row][col].getY() + deltaY);
                    if (steps <= 0) {
                        ((Timer) e.getSource()).stop();
                        squares[emptyRow][emptyCol].setIcon(squares[row][col].getIcon());
                        squares[row][col].setIcon(null);
                        squares[row][col].setLocation(emptyCol * squares[0][0].getWidth(), emptyRow * squares[0][0].getHeight());
                        emptyRow = row;
                        emptyCol = col;
                        if (isPuzzleSolved()) {
                            JOptionPane.showMessageDialog(LevelPuzzle.this, "Congratulations! You solved the puzzle!");
                            revealHiddenPiece();
                        }
                    }
                }
            });
            timer.start();
        }
    }

    // Method to check if a move is valid
    private boolean isValidMove(int row, int col) {
        return (Math.abs(row - emptyRow) + Math.abs(col - emptyCol) == 1);
    }

    // Method to check if the puzzle is solved
    private boolean isPuzzleSolved() {
        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (squares[i][j].getIcon() != null) {
                    ImageIcon currentIcon = (ImageIcon) squares[i][j].getIcon();
                    ImageIcon expectedIcon = tileImages.get(index);
                    if (currentIcon != expectedIcon) {
                        return false; // Puzzle is not solved if any tile is misplaced
                    }
                } else {
                    if (index != gridSize * gridSize - 1) {
                        return false; // Puzzle is not solved if any non-empty tile is missing
                    }
                }
                index++;
            }
        }
        return true;
    }

    // Method to reveal the hidden piece
    private void revealHiddenPiece() {
        // Reveal the hidden portion of the image by setting the removed piece's icon
        squares[gridSize - 1][gridSize - 1].setIcon(tileImages.get(tileImages.size() - 1));
        // Proceed to the next level if available
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            tileImages = levels.get(currentLevelIndex);
            shuffleTiles();
        }
        else {
            JOptionPane.showMessageDialog(this, "Congratulations! You have completed all levels!");
            // Optionally, you can reset the game or provide other options
        }
    }

    // ActionListener class for handling button clicks
    class ButaneListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (source == squares[i][j]) {
                        moveTile(i, j);
                        return;
                    }
                }
            }
        }
    }

    public static void main(String args[]) {
        // Prompt the user to select the mode and grid size
        String[] modes = {"Easy", "Medium", "Hard", "Custom"};
        String mode = (String) JOptionPane.showInputDialog(null, "Select mode:", "Mode Selection", JOptionPane.PLAIN_MESSAGE, null, modes, modes[0]);
        int gridSize;
        switch (mode) {
            case "Easy":
                gridSize = 3;
                break;
            case "Medium":
                gridSize = 4;
                break;
            case "Hard":
                gridSize = 5;
                break;
            case "Custom":
                 gridSize = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter grid size (e.g., 3 for 3x3 grid):"));
                 break;
            default:
                gridSize = 3;
                // Default to easy mode
        }

        SwingUtilities.invokeLater(() -> new LevelPuzzle(gridSize));
    }
}
