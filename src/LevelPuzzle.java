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

public class LevelPuzzle extends JFrame {
    private JButton[][] squares;
    private int emptyRow, emptyCol;
    private List<ImageIcon> tileImages;
    private List<List<ImageIcon>> levels;
    private int currentLevelIndex;
    private int gridSize;
    private JPanel internalPanel;
    private int tileWidth;
    private int tileHeight;

    public LevelPuzzle(int gridSize) {
        this.gridSize = gridSize;

        ImageIcon icon = new ImageIcon("src/nezukoicon.jpg");
        setIconImage(icon.getImage());

        levels = new ArrayList<>();
        levels.add(loadLevel("src/vampire chibi.jpg"));
        levels.add(loadLevel("src/dragon.jpg"));
        levels.add(loadLevel("src/eye.jpg"));
        levels.add(loadLevel("src/blackfox.jpg"));
        levels.add(loadLevel("src/yinandyang.jpg"));
        levels.add(loadLevel("src/moon.jpg"));
        levels.add(loadLevel("src/wallpaper1.jpg"));

        currentLevelIndex = 0;
        tileImages = levels.get(currentLevelIndex);

        initializePuzzle();
        shuffleTiles();

        setTitle("Sliding Puzzle");
        setSize(600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            promptModeAndGridSize();
        });

        JButton showImageButton = new JButton("Show Original Image");
        showImageButton.addActionListener(e -> {
            showOriginalImage();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(showImageButton);

        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(internalPanel, BorderLayout.CENTER);

        pack();
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
        }

        SwingUtilities.invokeLater(() -> new LevelPuzzle(gridSize));
    }

    private void initializePuzzle() {
        if (!tileImages.isEmpty()) {
            tileWidth = tileImages.get(0).getIconWidth();
            tileHeight = tileImages.get(0).getIconHeight();
        }
        internalPanel = new JPanel(new GridLayout(gridSize, gridSize));
        internalPanel.setPreferredSize(new Dimension(tileWidth * gridSize, tileHeight * gridSize));
        internalPanel.setBackground(Color.WHITE); // Set background colour
        internalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Set border

        ButaneListener listener = new ButaneListener();
        squares = new JButton[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(tileWidth, tileHeight));
                btn.setMinimumSize(new Dimension(tileWidth, tileHeight));
                btn.setMaximumSize(new Dimension(tileWidth, tileHeight));
                squares[i][j] = btn;
                squares[i][j].setIcon(tileImages.get(i * gridSize + j));
                squares[i][j].addActionListener(listener);
                // Customising the button
                btn.setBackground(Color.LIGHT_GRAY); // Set background colour
                btn.setFont(new Font("Arial", Font.PLAIN, 12)); // Set font
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Set border
                internalPanel.add(squares[i][j]);
            }
        }
    }

    private List<ImageIcon> loadLevel(String filename) {
        List<ImageIcon> levelImages = new ArrayList<>();
        File imageFile = new File(filename);
        if (!imageFile.exists()) {
            System.err.println("Image file not found: " + filename);
            return levelImages;
        }
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage == null) {
                System.err.println("Failed to load image: " + filename);
                return levelImages;
            }

            System.out.println("Loaded image: " + filename);
            System.out.println("Image dimensions: " + originalImage.getWidth() + "x" + originalImage.getHeight());
            levelImages = splitImage(originalImage, gridSize, gridSize);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
        return levelImages;
    }

    private List<ImageIcon> splitImage(BufferedImage originalImage, int rows, int cols) {
        List<ImageIcon> tileImages = new ArrayList<>();
        int width = originalImage.getWidth() / cols;
        int height = originalImage.getHeight() / rows;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                BufferedImage tile = originalImage.getSubimage(x * width, y * height, width, height);
                tileImages.add(new ImageIcon(tile));
            }
        }
        return tileImages;
    }

    private void shuffleTiles() {
        int totalTiles = gridSize * gridSize;
        int totalPieces = totalTiles - 1;
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < totalPieces; i++) {
            positions.add(i);
        }
        Collections.shuffle(positions);

        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (index < totalPieces) {
                    int position = positions.get(index++);
                    int row = position / gridSize;
                    int col = position % gridSize;
                    squares[i][j].setIcon(tileImages.get(row * gridSize + col));
                } else {
                    squares[i][j].setIcon(null);
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }
    }

    private void moveTile(int row, int col) {
        if (isValidMove(row, col)) {
            squares[emptyRow][emptyCol].setIcon(squares[row][col].getIcon());
            squares[row][col].setIcon(null);
            emptyRow = row;
            emptyCol = col;
            if (isPuzzleSolved()) {
                JOptionPane.showMessageDialog(LevelPuzzle.this, "Congratulations! You solved the puzzle!");
                revealHiddenPiece();
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        return (Math.abs(row - emptyRow) + Math.abs(col - emptyCol) == 1);
    }

    private boolean isPuzzleSolved() {
        int index = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (squares[i][j].getIcon() != null) {
                    ImageIcon currentIcon = (ImageIcon) squares[i][j].getIcon();
                    ImageIcon expectedIcon = tileImages.get(index);
                    if (currentIcon != expectedIcon) {
                        return false;
                    }
                } else {
                    if (index != gridSize * gridSize - 1) {
                        return false;
                    }
                }
                index++;
            }
        }
        return true;
    }

    private void revealHiddenPiece() {
        squares[emptyRow][emptyCol].setIcon(tileImages.get(tileImages.size() - 1));
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            tileImages = levels.get(currentLevelIndex);
            shuffleTiles();
        } else {
            JOptionPane.showMessageDialog(this, "Congratulations! You have completed all levels!");
        }
    }

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

    private void showOriginalImage() {
        List<ImageIcon> originalImage = levels.get(currentLevelIndex);
        BufferedImage img = new BufferedImage(tileWidth * gridSize, tileHeight * gridSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                ImageIcon icon = originalImage.get(i * gridSize + j);
                Image image = icon.getImage();
                g2d.drawImage(image, j * tileWidth, i * tileHeight, tileWidth, tileHeight, null);
            }
        }
        g2d.dispose();

        JFrame frame = new JFrame("Original Image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(img));
        frame.add(new JScrollPane(label), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            // Set the Nimbus look-and-feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
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
            }
            new LevelPuzzle(gridSize);
        });
    }
}
