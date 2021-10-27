package com.slash.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Arrays;

public class SnakeMenu extends JFrame {
    private JPanel mainPanel;
    private JComboBox<String> difficultyList;
    private JButton startButton;
    private JCheckBox wallsCheckBox;
    private JButton colorButton;
    private Color snakeColor = Color.GREEN;

    public static void main(String[] args) {
        new SnakeMenu();
    }

    public SnakeMenu() {
        super("Snake Menu");
        add(mainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(650, 350);
        setResizable(false);
        setVisible(true);
        colorButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JColorChooser colorChooser = new JColorChooser();
                colorChooser.setColor(snakeColor);
                setVisible(false);
                Arrays.stream(colorChooser.getChooserPanels()).filter(ch -> !ch.getDisplayName().equalsIgnoreCase("rgb")).forEach(colorChooser::removeChooserPanel);
                JDialog dialog = JColorChooser.createDialog(SnakeMenu.this, "Select snake colour", false, colorChooser,
                        actionEvent -> {
                            snakeColor = colorChooser.getColor();
                            colorButton.setBackground(snakeColor);
                            setVisible(true);
                        },
                        actionEvent -> {
                            snakeColor = colorChooser.getColor();
                            colorButton.setBackground(snakeColor);
                            setVisible(true);
                        });
                dialog.setVisible(true);
                dialog.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        setVisible(true);
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {

                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {

                    }

                    @Override
                    public void windowActivated(WindowEvent e) {

                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {

                    }
                });
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    setVisible(false);
                    new Snake(snakeColor, (String) difficultyList.getSelectedItem(), wallsCheckBox.isSelected(), SnakeMenu.this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(650, 300);
    }
}
