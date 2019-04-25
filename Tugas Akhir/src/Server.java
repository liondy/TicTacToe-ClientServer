
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michael Liondy
 */
public class Server {
    static int PORT = 8000;
    
    public static void main(String[] args) {
        while(true){
            try{
                ServerSocket serverSocket = new ServerSocket(PORT);
                System.out.println("Server running di port " + PORT + "...");
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Alamat user : " + connectionSocket.getInetAdress());
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream serverOutput = new DataOutputStream(connectionSocket.getOutputStream());
                ServerGame game = new ServerGame(clientInput, serverOutput);
                try{
                    game.start();
                }
                catch(CloneNotSupportedException e){
                    System.err.println("The gamed failed to start.");
                    System.exit(-1);
                }
                finally{
                    connectionSocket.close();
                }
            }
            catch(IOException e){
                System.err.println(e);
            }
        }
    }
}
