/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TicTacToe;

/**
 *
 * @author Michael Liondy
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
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Server implements Runnable {
    
    static String ip = "localhost";
    static Scanner sc = new Scanner(System.in);
    static BufferedReader br;
    static DataInputStream dis;
    static DataOutputStream dos;
    static int port = 22222;
    static ServerSocket ss;
    static Socket s;
    
    static String[] spaces = new String[9]; //untuk board nya max 9 kotak (3x3)
    
    static boolean turn = false;
    static boolean circle = true; // pemain pertama pasti X
    static boolean ac = false;
    static boolean unableToConnect = false; //awalnya diasumsiin bisa konek ke lawan
    static boolean menang = false;
    static boolean lawanMenang = false;
    static boolean imbang = false;
    
    static BufferedImage board;
    static BufferedImage redX;
    static BufferedImage blueX;
    static BufferedImage redCircle;
    static BufferedImage blueCircle;
    
    static Font font = new Font("Verdana", Font.BOLD, 32);
    static Font smallerFont = new Font("Verdana", Font.BOLD, 32);
    static Font largerFont = new Font("Verdana", Font.BOLD, 32);
    
    static String msgWon = "KAMU MENANG";
    static String msgLawanWon = "KAMU KALAH";
    static String msgImbang = "GAME SELESAI DENGAN IMBANG";
    static String menunggu ="MENUNGGU LAWAN..";
    static String msgNoOpp ="TIDAK ADA LAWAN YANG MAU MAIN DENGANMU :((";
    
    static final int WIDTH = 506;
    static final int HEIGHT = 527;
    static Thread thread;
    static JFrame frame;
    static Painter painter;
    
    static int ukuran = 160;
    static int error = 0;
    static int titikAwal = -1; //buat plot titik awal garis kalo menang
    static int titikAkhir = -1; //buat plot titik akhir garis kalo menang
    
    static int[][] syaratMenang = new int[][]{
        {0,1,2},
        {3,4,5},
        {6,7,8},
        {1,4,7},
        {2,5,8},
        {0,4,8},
        {2,4,6}
    };
    
    public static void main(String[] args) {
        
        System.out.println("Input IP Address: ");
        ip = sc.nextLine();
        System.out.println("Input Port: ");
        port = sc.nextInt();
        while (port <= 1024 || port > 65535) {
            System.out.println("Port invalid! Input another port: ");
            port = sc.nextInt();
        }
        if(!connect()){
            initialize();
        }
        System.out.println("Conneted from "+s.getInetAddress());

        frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setContentPane(painter);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        thread = new Thread();
        thread.start();
    }
    
    //menjalankan gui
    @Override
    public void run(){
        while(true){
            tick();
            painter.repaint();
            if(!circle && !ac){
                request();
            }
        }
    }
    
    public static void tick(){
        if(error>=10){ //error trus
            unableToConnect = true; //gabisa konek ke lawan main 
        }
        if(!turn && !unableToConnect){ //kalo bisa, main
            try{
                int space = dis.readInt();
                if(circle){
                    spaces[space] = "X";
                }
                else{
                    spaces[space] = "O";
                }
                cekLawanMenang();
                cekImbang();
                turn = true;
            }
            catch(IOException e){ //kalo gabisa, errornya nambah
                e.printStackTrace();
                error++;
            }
        }
    }
    
    public static void cekLawanMenang(){
        for (int i = 0; i < syaratMenang.length; i++) {
            if(circle){
                if (spaces[syaratMenang[i][0]] == "X" && spaces[syaratMenang[i][1]] == "X" && spaces[syaratMenang[i][2]] == "X") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    lawanMenang = true;
                }
            }
            else {
                if (spaces[syaratMenang[i][0]] == "O" && spaces[syaratMenang[i][1]] == "O" && spaces[syaratMenang[i][2]] == "O") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    lawanMenang = true;
                }
            }
        }
    }
    
    public static void cekMenang(){
        for (int i = 0; i < syaratMenang.length; i++) {
            if(circle){
                if (spaces[syaratMenang[i][0]] == "O" && spaces[syaratMenang[i][1]] == "O" && spaces[syaratMenang[i][2]] == "O") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    menang = true;
                }
            }
            else {
                if (spaces[syaratMenang[i][0]] == "X" && spaces[syaratMenang[i][1]] == "X" && spaces[syaratMenang[i][2]] == "X") {
                    titikAwal = syaratMenang[i][0];
                    titikAkhir = syaratMenang[i][2];
                    menang = true;
                }
            }
        }
    }
    
    public static void cekImbang(){
        for (int i = 0; i < spaces.length; i++) {
            if(spaces[i]==null){
                return;
            }
        }
        imbang = true;
    }
    
    public static void request(){
        Socket socket = null;
        try{
            socket = ss.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            ac = true;
            System.out.println("REQUEST CLIENT TERHUBUNG");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void initialize(){
        try{
//            ss = new ServerSocket(port,8,InetAddress.getByName(ip));
              ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        turn = true; // giliran kita
        circle = false; // X
    }
    
    public static boolean connect(){
        try{
            s = new Socket(ip,port);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            ac = true;
        }
        catch(IOException e){
            System.out.println("Error!");
            return false;
        }
        System.out.println("Server connect di port "+port);
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
                        }
                        else {
                            g.drawImage(blueX, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        }
                    } 
                    else if (spaces[i].equals("O")) {
                        if (circle) {
                        g.drawImage(blueCircle, (i % 3) * ukuran + 10 * (i % 3), (int) (i / 3) * ukuran + 10 * (int) (i / 3), null);
                        }
                        else {
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
                    } 
                    else if (lawanMenang) {
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
        } 
        else {
            g.setColor(Color.RED);
            g.setFont(font);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int stringWidth = g2.getFontMetrics().stringWidth(menunggu);
            g.drawString(menunggu, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
       }

    }
    
    private class Painter extends JPanel implements MouseListener{
        public static final long serialVersionUID = 1L;
        
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
                            if (!circle) spaces[position] = "X";
                            else spaces[position] = "O";
                            turn = false;
                            repaint();
                            Toolkit.getDefaultToolkit().sync();

                            try {
                                dos.writeInt(position);
                                dos.flush();
                            } 
                            catch (IOException e1) {
                                error++;
                                e1.printStackTrace();
                            }

                            System.out.println("DATA WAS SENT");
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
