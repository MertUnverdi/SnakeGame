package com.slash.snakegame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Snake extends JComponent implements ActionListener, KeyListener {

    public final Random random = new Random();
    public Rectangle[] snakeTails = new Rectangle[4];
    public final List<Point> pointList = new ArrayList<>();
    public SnakeDirection currentDirection;
    public Color snakeColour;
    public boolean walls;
    public boolean running = false;
    public boolean layout = false;
    public boolean speedUp = false;
    public int score = 0;
    public int counter = 0;
    public int lastKey;
    public Image wallImage;
    public Frame snakeFrame;
    private int resumeCounter = 18;

    public Snake(Color snakeColour, String difficulty, boolean walls, SnakeMenu snakeMenu) throws IOException {
        wallImage = ImageIO.read(snakeMenu.getClass().getResourceAsStream("/wall.jpg"));
        this.snakeColour = snakeColour;
        this.walls = walls;
        this.currentDirection = SnakeDirection.values()[random.nextInt(SnakeDirection.values().length)];
        snakeFrame = new JFrame("Snake Game");
        snakeFrame.add(this);
        snakeFrame.pack();
        snakeFrame.setPreferredSize(getPreferredSize());
        snakeFrame.setLocation(getWidth() / 2, getHeight() / 2);
        snakeFrame.setVisible(true);
        int x = Math.max(155, random.nextInt(getWidth()) - 155);
        int y = Math.max(105, random.nextInt(getHeight()) - 105);
        snakeTails[0] = new Rectangle(x - x % 20, y - y % 20, 20, 20);
        snakeTails[0].setNextDirection(SnakeDirection.values()[random.nextInt(SnakeDirection.values().length)]);
        int directionX = 0;
        int directionY = 0;
        if (currentDirection.equals(SnakeDirection.SOUTH)) directionY = -20;
        else if (currentDirection.equals(SnakeDirection.WEST)) directionX = 20;
        else if (currentDirection.equals(SnakeDirection.EAST)) directionX = -20;
        else directionY = 20;
        for (int i = 1; i < snakeTails.length; i++) {
            Rectangle upRectangle = snakeTails[i - 1];
            snakeTails[i] = new Rectangle(upRectangle.getX() + directionX, upRectangle.getY() + directionY, 20, 20);
            snakeTails[i].setNextDirection(snakeTails[i - 1].getNextDirection());
        }
        Timer t = new Timer(difficulty.equalsIgnoreCase("easy") ? 60 : difficulty.equalsIgnoreCase("medium") ? 40 : 20, this);
        t.start();
        snakeFrame.addKeyListener(this);
        snakeFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                t.stop();
                snakeMenu.setVisible(true);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                t.stop();
                snakeMenu.setVisible(true);
            }

            @Override
            public void windowActivated(WindowEvent e) {
                resumeCounter = 18;
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                running = false;
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(960, 540);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (counter++ % (speedUp ? 1 : 2) == 0) repaint();
    }

    public void resumeCounter(Graphics g) {
        int seconds = resumeCounter / 6;
        int scale = resumeCounter % 6 + 10;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, (scale * 10)));
        g.drawString(String.valueOf(seconds), getWidth() / 2 - 50, getHeight() / 2 + 15);
        if (resumeCounter-- <= 1) running = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        Rectangle gameScreen = new Rectangle(getX(), getY(), getWidth(), getHeight());
        g.setColor(new Color(65, 72, 40));
        g.fillRect(gameScreen.getX(), gameScreen.getY(), gameScreen.width, gameScreen.height);
        if (layout) {
            g.setColor(new Color(0x73000000, true));
            for (int x = 0; x < 1920; x += 20) {
                g.drawLine(x, 0, x, 1080);
            }
            for (int y = 0; y < 1080; y += 20) {
                g.drawLine(0, y, 1920, y);
            }
        }
        paintAndStepSnake(g);
        paintAndPopulatePoints(g);
        g.setColor(new Color(0x5E676780, true));
        if (resumeCounter >= 1) resumeCounter(g);
        if (!running && resumeCounter < 1) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 144));
            g.drawString("PAUSED", getWidth() / 2 - 256, getHeight() / 2 - 15);
        }
        if (walls) {
            gameScreen = new Rectangle(getX() + 20, getY() + 20, getWidth() - 60, getHeight() - 60);
            if (!gameScreen.isOnRectangle(snakeTails[0].getX(), snakeTails[0].getY())) endGame();
            for (int x = 0; x < getWidth() - 20; x += 20) {//draw up-down boundaries
                g.drawImage(wallImage, x, 0, null);
                g.drawImage(wallImage, x, getHeight() - 20, null);
            }
            for (int y = 0; y < getHeight(); y += 20) {//draw right-left boundaries
                g.drawImage(wallImage, 0, y, null);
                g.drawImage(wallImage, getWidth() - 20, y, null);
            }
        }
    }

    private void endGame() {
        snakeFrame.setVisible(false);
        snakeFrame.dispose();
    }

    private void paintAndPopulatePoints(Graphics g) {
        int amountOfSize = (getWidth() / 200) + (getHeight() / 150); //How much point can be populated for the current screen size
        if (pointList.size() < amountOfSize && random.nextInt(100) <= 5) {
            while (true) {
                int x = random.nextInt(getWidth() - 100) + 50;
                int y = random.nextInt(getHeight() - 100) + 50;
                x -= x % 10;
                y -= y % 10;
                if (x % 20 == 0) x += 10;//x-axis of point should be 10 or multiples
                if (y % 20 == 0) y += 10;//y-axis of point should be 10 or multiples
                Point point = new Point(x, y, random.nextInt(15) + 15);
                if (Arrays.stream(snakeTails).noneMatch(rectangle -> rectangle.isOnRectangle(point))) { //Check the generated point is above the snake's way
                    pointList.add(point);
                    break;
                }
            }
        }

        pointList.stream().filter(snakeTails[0]::isOnRectangle).findFirst().ifPresent(point -> { //Check the point is inside of rectangle object(snake head)
            pointList.remove(point);
            score += point.size;
        });

        for (Point point : pointList) {
            if (layout) {
                g.setColor(Color.RED);
                g.drawOval(point.x, point.getY(), 3, 3);
            }
            g.setColor(new Color(point.getColor().getRed(), point.getColor().getGreen(), point.getColor().getBlue(), point.alpha));
            point.alpha -= random.nextInt(10);
            if (point.alpha <= 20) point.alpha = 200;
            g.fillOval(point.getX() - (point.getSize() / 2), point.getY() - (point.getSize() / 2), point.getSize(), point.getSize());
        }

        g.setColor(new Color(0xB300FCB9, true));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 44));
        g.drawString("Score: " + score, 25, 50);

    }

    public void paintAndStepSnake(Graphics g) {
        if (running) {
            updateDirection();
            int x = snakeTails[0].getX();
            int y = snakeTails[0].getY();
            if (currentDirection.equals(SnakeDirection.SOUTH)) y += 20;
            else if (currentDirection.equals(SnakeDirection.WEST)) x -= 20;
            else if (currentDirection.equals(SnakeDirection.EAST)) x += 20;
            else y -= 20;
            if (x > getWidth()) x = -20;
            else if (x < 0) x = (getWidth() - getWidth() % 20) - 20;
            else if (y > getHeight()) y = -20;
            else if (y < 0) y = (getHeight() - getHeight() % 20) - 20;
            Rectangle lastTail = new Rectangle(x, y, 20, 20);
            for (int i = 2; i < snakeTails.length; i++) {
                if (snakeTails[i].isOnRectangle(lastTail.getCenter().getX(), lastTail.getCenter().getY())) {
                    endGame();
                }
            }
            for (int i = 0; i < snakeTails.length; i++) {//Step snake tails
                Rectangle currentTail = snakeTails[i];
                snakeTails[i] = lastTail;
                lastTail = currentTail;
            }
            if ((score - 300) / 100 >= snakeTails.length) {
                snakeTails = Arrays.copyOf(snakeTails, snakeTails.length + 1);
                snakeTails[snakeTails.length - 1] = lastTail;
            }
        }
        snakeTails[0].setNextDirection(currentDirection);
        g.setColor(snakeColour);
        for (Rectangle r : snakeTails) { //Draw snake
            g.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                running = false;
                break;
            case KeyEvent.VK_R:
                if (!running && resumeCounter < 1d) resumeCounter = 18;
                break;
            case KeyEvent.VK_L:
                layout = !layout;
                break;
            case KeyEvent.VK_SPACE:
                speedUp = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_D:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_S:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (running) lastKey = e.getKeyCode();

        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) speedUp = false;
    }

    public void updateDirection() {
        if ((lastKey == KeyEvent.VK_A || lastKey == KeyEvent.VK_LEFT) && currentDirection != SnakeDirection.EAST) {
            currentDirection = SnakeDirection.WEST;
        } else if ((lastKey == KeyEvent.VK_D || lastKey == KeyEvent.VK_RIGHT) && currentDirection != SnakeDirection.WEST) {
            currentDirection = SnakeDirection.EAST;
        } else if ((lastKey == KeyEvent.VK_W || lastKey == KeyEvent.VK_UP) && currentDirection != SnakeDirection.SOUTH) {
            currentDirection = SnakeDirection.NORTH;
        } else if ((lastKey == KeyEvent.VK_S || lastKey == KeyEvent.VK_DOWN) && currentDirection != SnakeDirection.NORTH) {
            currentDirection = SnakeDirection.SOUTH;
        }
    }

    static class Point {
        public int x;
        public int y;
        public int size;
        public int alpha = 100;

        public final Color color;

        public Point(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            Random random = new Random();
            color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255), alpha);
        }

        public Point(int x, int y) {
            this(x, y, 2);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSize() {
            return size;
        }

        public Color getColor() {
            return color;
        }

    }

    static class Rectangle {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public SnakeDirection nextDirection;

        public Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean isOnRectangle(int x, int y) {
            return getX() <= x && getY() <= y && getX() + width >= x && getY() + height >= y;
        }

        public boolean isOnRectangle(Point point) {
            return isOnRectangle(point.getX(), point.getY());
        }

        public Point getCenter() {
            return new Point(x + getWidth() / 2, y + getHeight() / 2);
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public SnakeDirection getNextDirection() {
            return nextDirection;
        }

        public void setNextDirection(SnakeDirection nextDirection) {
            this.nextDirection = nextDirection;
        }

    }

    enum SnakeDirection {NORTH, EAST, SOUTH, WEST}
}
