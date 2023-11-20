import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    
    private class Tile {

        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    int boardwidth;
    int boardHeight;
    int tileSize = 25;

    // COBRA
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // COMIDA
    Tile food;
    Random random;

    // LÓGICA DO JOGO
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    int highestScore = 0;

    SnakeGame(int boardwidth, int boardHeight) {
        this.boardwidth = boardwidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardwidth, this.boardHeight));
        setBackground(new Color(88));
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // DESENHAR A GRADE DE LINHA
        for (int i = 0; i < boardwidth/tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardwidth, i * tileSize);
        }

        // DESENHANDO A COMIDA
        g.setColor(Color.red);
        g.fillOval(food.x * tileSize + 4, food.y * tileSize + 4, tileSize - 8, tileSize - 8);

        // DESENHAR A COBRA
        g.setColor(new Color(52224));
        // g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // CORPO DA COBRA
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            // g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);

        }

        // PONTUAÇÃO
        if (highestScore < snakeBody.size()) {
            highestScore = snakeBody.size();
        }

        g.setFont(new Font("Verdana",Font.PLAIN, 14));
        g.setColor(Color.yellow);
        g.drawString("Recorde: " + String.valueOf(highestScore), boardwidth - 100, tileSize - 7);
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Fim de Jogo - Pontuação: " + String.valueOf(snakeBody.size()), tileSize - 20, tileSize - 7);
        } else {
            g.setColor(Color.yellow);
            g.drawString("Pontuação: " + String.valueOf(snakeBody.size()), tileSize - 20, tileSize - 7);
        }
    } 

    public void placeFood() {
        food.x = random.nextInt(boardwidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move(){

        // COMER A COMIDA
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // CORPO DA COBRA
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // CABEÇA DA COBRA
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // CONDIÇÕES DE FIM DE JOGO

        // COLIDIR COM O PRÓPRIO CORPO
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            // COLIDIR CABEÇA COM O CORPO
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                showGameOver();
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize > boardwidth || 
            snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight) {
            gameOver = true;
            showGameOver();
        }
    }

    public void showGameOver() {
        Object[] options = {"Reiniciar"};
        JFrame frame = new JFrame();
        int result = JOptionPane.showOptionDialog(this.getRootPane(), "Fim de Jogo! Deseja Reiniciar?",
                    "Fim de Jogo!", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (result == JOptionPane.YES_OPTION) {
            gameOver = false;
            snakeBody.clear();
            snakeHead = new Tile(5, 5);
            placeFood();
            gameLoop.restart();
        } else {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver == true) {
            gameLoop.stop();
        } 
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }
    }

    // MÉTODOS NÃO USADOS
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}
