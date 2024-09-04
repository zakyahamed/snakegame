import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 900;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts;
    int applesEaten;
    int appleX;
    int appleY;
    char direction;
    boolean running = false;
    Timer timer;
    Random random;
    int highscore;

    JButton startButton;
    JButton restartButton;
    boolean gameStarted = false;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null); // Use null layout to position buttons manually

        startButton = new JButton("Start Game");
        startButton.setBounds((SCREEN_WIDTH / 2) - 100, SCREEN_HEIGHT / 2, 200, 50);
        startButton.addActionListener(e -> startGame());
        this.add(startButton);

        restartButton = new JButton("Restart Game");
        restartButton.setBounds((SCREEN_WIDTH / 2) - 100, (SCREEN_HEIGHT / 2) + 60, 200, 50);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false);
        this.add(restartButton);

        this.addKeyListener(new MyKeyAdapter());
    }

    public void startGame() {
        // Initialize game state variables
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        
        newApple();
        gameStarted = true;
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

        startButton.setVisible(false);
        restartButton.setVisible(false);
        repaint();
    }

    public void restartGame() {
        timer.stop(); // Stop the current timer
        startGame();  // Restart the game
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else {
            if (gameStarted) {
                gameOver(g);
            }
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            highscore = Math.max(highscore, applesEaten);
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
            restartButton.setVisible(true);
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        if (highscore <= applesEaten && applesEaten > 0) {
            g.drawString("New High Score!", (SCREEN_WIDTH - metrics1.stringWidth("New High Score!")) / 2, SCREEN_HEIGHT / 2);
        } else {
            g.drawString("Try Again!", (SCREEN_WIDTH - metrics1.stringWidth("Try Again!")) / 2, SCREEN_HEIGHT / 2);
        }
        g.setColor(Color.green);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        int yPosition = g.getFont().getSize(); 
        g.drawString("Your Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Your Score: " + applesEaten)) / 2, yPosition);
        yPosition += g.getFont().getSize(); 
        g.drawString("High Score: " + highscore, (SCREEN_WIDTH - metrics2.stringWidth("High Score: " + highscore)) / 2, yPosition);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
