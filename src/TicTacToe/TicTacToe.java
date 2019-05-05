/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TicTacToe;

/**
 *
 * @author Michael Liondy - 2017730007
 * @author Cristine Artanty - 2017730050
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TicTacToe implements Runnable {

    String ip = "localhost";
    Scanner sc = new Scanner(System.in);
    BufferedReader br;
    DataInputStream dis;
    DataOutputStream dos;
    int port = 22222;
    ServerSocket ss;
    Socket s;

    String[] spaces = new String[9]; //untuk board nya max 9 kotak (3x3)

    boolean turn = false;
    boolean circle = true; // pemain pertama pasti X
    boolean ac = false;
    boolean unableToConnect = false; //awalnya diasumsiin bisa konek ke lawan
    boolean menang = false;
    boolean lawanMenang = false;
    boolean imbang = false;

    BufferedImage board;
    BufferedImage redX;
    BufferedImage blueX;
    BufferedImage redCircle;
    BufferedImage blueCircle;

    Font font = new Font("Verdana", Font.BOLD, 32);
    Font smallerFont = new Font("Verdana", Font.BOLD, 20);
    Font largerFont = new Font("Verdana", Font.BOLD, 40);

    String msgWon = "KAMU MENANG";
    String msgLawanWon = "KAMU KALAH";
    String msgImbang = "GAME SELESAI DENGAN IMBANG";
    String menunggu = "MENUNGGU LAWAN..";
    String msgNoOpp = "TIDAK ADA LAWAN YANG MAU MAIN DENGANMU :((";

    final int WIDTH = 506;
    final int HEIGHT = 527;
    Thread thread;
    JFrame frame;
    Painter painter;

    int ukuran = 160;
    int error = 0;
    int titikAwal = -1; //buat plot titik awal garis kalo menang
    int titikAkhir = -1; //buat plot titik akhir garis kalo menang

    int[][] syaratMenang = new int[][]{
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8},
        {0, 4, 8},
        {2, 4, 6}
    };
    
    public static void main(String[] args) {
        TicTacToe t = new TicTacToe();
    }

    public TicTacToe() {

        System.out.println("Input IP Address: ");
        ip = sc.nextLine();
        System.out.println("Input Port: ");
        port = sc.nextInt();
        while (port <= 1024 || port > 65535) {
            System.out.println("Port invalid! Input another port: ");
            port = sc.nextInt();
        }
        
        load();
        
        painter = new Painter();
        painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        if (!connect()) {
            initialize();
        }

        frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setContentPane(painter);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        thread = new Thread(this, "TicTacToe");
        thread.start();
    }
    
    public void load(){
        try{
            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));
            redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
            redCircle = ImageIO.read(getClass().getResourceAsStream("/redCircle.png"));
            blueX = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
            blueCircle = ImageIO.read(getClass().getResourceAsStream("/blueCircle.png"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    //menjalankan gui
    public void run() {
        while (true) {
            tick();
            painter.repaint();
            if (!circle && !ac) {
                request();
            }
        }
    }

    public void tick() {
        if (error >= 10) { //error trus
            unableToConnect = true; //gabisa konek ke lawan main 
        }
        if (!turn && !unableToConnect) { //kalo bisa, main
            try {
                int space = dis.readInt();
                if (circle) {
                    spaces[space] = "X";
                } else {
                    spaces[space] = "O";
                }
                cekLawanMenang();
                cekImbang();
                turn = true;
            } catch (IOException e) { //kalo gabisa, errornya nambah
                e.printStackTrace();
                error++;
            }
        }
    }

    public void cekLawanMenang() {
        for (int i = 0; i < syaratMenang.length; i++) {
            if (circle) {
                if (spaces[syaratMenang[i][0]] == "X" && spaces[syaratMenang[i][1]] == "X" && spaces[syaratMenang[i][2]] == "X") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    lawanMenang = true;
                }
            } else {
                if (spaces[syaratMenang[i][0]] == "O" && spaces[syaratMenang[i][1]] == "O" && spaces[syaratMenang[i][2]] == "O") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    lawanMenang = true;
                }
            }
        }
    }

    public void cekMenang() {
        for (int i = 0; i < syaratMenang.length; i++) {
            if (circle) {
                if (spaces[syaratMenang[i][0]] == "O" && spaces[syaratMenang[i][1]] == "O" && spaces[syaratMenang[i][2]] == "O") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    menang = true;
                }
            } else {
                if (spaces[syaratMenang[i][0]] == "X" && spaces[syaratMenang[i][1]] == "X" && spaces[syaratMenang[i][2]] == "X") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    menang = true;
                }
            }
        }
    }

    public void cekImbang() {
        for (int i = 0; i < spaces.length; i++) {
            if (spaces[i] == null) {
                return;
            }
        }
        imbang = true;
    }

    public void request() {
        Socket socket = null;
        try {
            socket = ss.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            ac = true;
            System.out.println("REQUEST CLIENT TERHUBUNG");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {
            ss = new ServerSocket(port,8,InetAddress.getByName(ip));
        } catch (IOException e) {
            e.printStackTrace();
        }
        turn = true; // giliran kita
        circle = false; // X
    }

    public boolean connect() {
        try {
            s = new Socket(ip, port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            ac = true;
        } catch (IOException e) {
            System.out.println("Error!");
            return false;
        }
        System.out.println("Server connect di port " + port);
        return true;
    }

    private void render(Graphics g) {
        g.drawImage(board, 0, 0, null);
        if (unableToConnect) {
            g.setColor(Color.RED);
            g.setFont(smallerFont);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(msgNoOpp);
            g.drawString(msgNoOpp, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
            return;
        }

        if (ac) {
            for (int i = 0; i < spaces.length; i++) {
                if (spaces[i] != null) {
                    if (spaces[i].equals("X")) {
                        if (circle) {
                            g.drawImage(redX, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        } else {
                            g.drawImage(blueX, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        }
                    } else if (spaces[i].equals("O")) {
                        if (circle) {
                            g.drawImage(blueCircle, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        } else {
                            g.drawImage(redCircle, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        }
                    }
                }
            }
            if (menang || lawanMenang) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(10));
                g.setColor(Color.BLACK);
                g.drawLine(titikAwal % 3 * ukuran + 10 * titikAwal % 3 + ukuran / 2,
                        (int) (titikAwal / 3) * ukuran + 10 * (int) (titikAwal / 3)
                        + ukuran / 2, titikAkhir % 3 * ukuran + 10 * titikAkhir % 3 + ukuran / 2,
                        (int) (titikAkhir / 3) * ukuran + 10 * (int) (titikAkhir / 3) + ukuran / 2);

                g.setColor(Color.RED);
                g.setFont(largerFont);
                if (menang) {
                    int stringWidth = g2.getFontMetrics().stringWidth(msgWon);
                    g.drawString(msgWon, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
                } else if (lawanMenang) {
                    int stringWidth = g2.getFontMetrics().stringWidth(msgLawanWon);
                    g.drawString(msgLawanWon, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
                }
            }

            if (imbang) {
                Graphics2D g2 = (Graphics2D) g;
                g.setColor(Color.BLACK);
                g.setFont(largerFont);
                int stringWidth = g2.getFontMetrics().stringWidth(msgImbang);
                g.drawString(msgImbang, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
            }
        } else {
            g.setColor(Color.RED);
            g.setFont(font);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(menunggu);
            g.drawString(menunggu, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
        }

    }

    private class Painter extends JPanel implements MouseListener {

        public final long serialVersionUID = 1L;

        public Painter() {
            setFocusable(true);
            requestFocus();
            setBackground(Color.WHITE);
            addMouseListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            render(g);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (ac) {
                if (turn && !unableToConnect && !menang && !lawanMenang) {
                    int x = e.getX() / ukuran;
                    int y = e.getY() / ukuran;
                    y *= 3;
                    int position = x + y;

                    if (spaces[position] == null) {
                        if (!circle) {
                            spaces[position] = "X";
                        } else {
                            spaces[position] = "O";
                        }
                        turn = false;
                        repaint();
                        Toolkit.getDefaultToolkit().sync();

                        try {
                            dos.writeInt(position);
                            dos.flush();
                        } catch (IOException e1) {
                            error++;
                            e1.printStackTrace();
                        }

                        System.out.println("Kirim Paket");
                        cekMenang();
                        cekImbang();

                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    }
}
