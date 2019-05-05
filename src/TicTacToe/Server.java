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

public class Server {
    
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
    
    public static void main(String[] args) throws IOException {
        while(true){
            try{
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
                
            }
            catch(IOException e){
                
            }
        }
    }
    
    //menjalankan gui
    public static void run(){
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
    
    public static void cekImbang(){
        
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
}
