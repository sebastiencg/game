package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends JFrame {
    private static final int SIZE = 4;
    private JButton[][] buttons;
    private String[][] board;
    private boolean[][] revealed;
    private int[] firstCard = {-1, -1};
    private int attempts = 0;

    public MemoryGame() {
        setTitle("Memory Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(SIZE, SIZE));

        initializeGame();
        createButtons();

        setVisible(true);
    }

    private void initializeGame() {
        // Liste des symboles utilisés comme cartes
        ArrayList<String> cards = new ArrayList<>();
        String[] symbols = {"(^^", "(*_*", "(@_@", "(>_<", "(^_^", "(T_T", "\\(^_^)/", "( ^.^)"};
        for (String symbol : symbols) {
            cards.add(symbol);
            cards.add(symbol); // chaque symbole a une paire
        }
        Collections.shuffle(cards);

        board = new String[SIZE][SIZE];
        revealed = new boolean[SIZE][SIZE];
        buttons = new JButton[SIZE][SIZE];

        int index = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = cards.get(index++);
                revealed[i][j] = false;
            }
        }
        attempts = 0; // Réinitialiser le compteur d'essais
    }

    private void createButtons() {
        getContentPane().removeAll(); // Supprimer les anciens boutons pour redémarrer

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = new JButton("*");
                button.setFont(new Font("Arial", Font.BOLD, 24));
                buttons[i][j] = button;
                int row = i;
                int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        handleCardSelection(row, col);
                    }
                });
                add(button);
            }
        }
        revalidate();
        repaint();
    }

    private void handleCardSelection(int row, int col) {
        if (revealed[row][col] || (firstCard[0] == row && firstCard[1] == col)) {
            return; // Ignore les cartes déjà révélées ou la même carte cliquée deux fois
        }

        buttons[row][col].setText(board[row][col]);
        buttons[row][col].setEnabled(false);

        if (firstCard[0] == -1) {
            firstCard[0] = row;
            firstCard[1] = col;
        } else {
            attempts++;
            int firstRow = firstCard[0];
            int firstCol = firstCard[1];
            if (board[row][col].equals(board[firstRow][firstCol])) {
                revealed[row][col] = true;
                revealed[firstRow][firstCol] = true;
                firstCard[0] = -1;
                firstCard[1] = -1;
                if (isGameOver()) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Félicitations ! Vous avez terminé en " + attempts + " essais. Voulez-vous rejouer ?",
                            "Jeu terminé",
                            JOptionPane.YES_NO_OPTION);

                    if (response == JOptionPane.YES_OPTION) {
                        resetGame(); // Relancer le jeu si l'utilisateur le souhaite
                    } else {
                        System.exit(0); // Fermer le jeu si l'utilisateur choisit de ne pas rejouer
                    }
                }
            } else {
                Timer timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        buttons[row][col].setText("*");
                        buttons[row][col].setEnabled(true);
                        buttons[firstRow][firstCol].setText("*");
                        buttons[firstRow][firstCol].setEnabled(true);
                        firstCard[0] = -1;
                        firstCard[1] = -1;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private boolean isGameOver() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!revealed[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        initializeGame();
        createButtons();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MemoryGame();
            }
        });
    }
}