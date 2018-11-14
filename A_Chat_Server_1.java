import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class A_Chat_Server_1
{
    public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
    public static ArrayList<String> CurrentUsers = new ArrayList<String>();
    
    public static void main(String[] args) throws IOException
    {
        
        try
        {
            final int PORT = 1048;
            ServerSocket SERVER = new ServerSocket(PORT);
            System.out.println("Waiting for clients...");
            
            while(true)
            {
                Socket SOCK = SERVER.accept();
                
                System.out.println("Client connected from: " + SOCK.getLocalAddress().getHostName());
                
                A_Chat_Server_Return_1 CHAT = new A_Chat_Server_Return_1(SOCK);
                Thread X = new Thread(CHAT);
                X.start();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    } 
}