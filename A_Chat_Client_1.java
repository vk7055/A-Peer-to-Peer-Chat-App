import java.net.*;
import java.io.*;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class A_Chat_Client_1 implements Runnable{
    
    Socket SOCK;
    Scanner INPUT;
    Scanner SEND = new Scanner(System.in);
    PrintWriter OUT;
    A_Chat_Client_GUI_1 client_gui;
    
    String chat_friend_name = "";
    
    String username = "";

    public A_Chat_Client_1(Socket X, A_Chat_Client_GUI_1 Y) 
    {
        this.SOCK = X;
        this.client_gui = Y;
    }
    
    public void run()
    {
        try
        {
            try
            {
                INPUT = new Scanner(SOCK.getInputStream());
                OUT = new PrintWriter(SOCK.getOutputStream());
                OUT.flush();
                CheckStream();
            }
            finally
            {
                SOCK.close();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    public void CheckStream()
    {
        while(true)
        {
            RECEIVE();
        }
    }
    
    public void RECEIVE()
    {
        if(INPUT.hasNext())
        {
            String MESSAGE = INPUT.nextLine();
            
            if(MESSAGE.contains("sign_up_true"))
            {
                JOptionPane.showMessageDialog(null, "Account Created Successfully");
                client_gui.sign_up_username_box.setText("");
                client_gui.sign_up_password_box.setText("");
                client_gui.create_account_panel.setVisible(false);
            }
            else if(MESSAGE.contains("sign_up_false"))
            {
                JOptionPane.showMessageDialog(null, "Username Already Exists");
            }
            else if(MESSAGE.contains("sign_in_true"))
            {
                JOptionPane.showMessageDialog(null, "Logged in Successfully");
                client_gui.sign_in_username_box.setText("");
                client_gui.sign_in_password_box.setText("");
                client_gui.username.setText(MESSAGE.substring(13));
                username = MESSAGE.substring(13);
                friend_list();
            }
            else if(MESSAGE.contains("sign_in_false"))
            {
                JOptionPane.showMessageDialog(null, "Invalid Username or Password");
            }
            else if(MESSAGE.contains("friend_list"))
            {
                String TEMP1 = MESSAGE.substring(12);
                String [] TEMP2 = TEMP1.split("#");
                String [] friends = TEMP2[0].split(",");
                String [] others = TEMP2[1].split(",");
                
                client_gui.friend_list_area.setText("");
                client_gui.others_area.setText("");    
                
                for (String temp  : friends) 
                {
                    client_gui.friend_list_area.setText(client_gui.friend_list_area.getText() + "\r\n" + temp);
                }
                for (String temp  : others) 
                {
                    client_gui.others_area.setText(client_gui.others_area.getText() + "\r\n" + temp);
                }
                
                client_gui.friends_panel.setVisible(true);
            }
            else if(MESSAGE.contains("add_friend"))
            {
                String TEMP1 = MESSAGE.substring(11);
                
                if(TEMP1.contains("add_friend_fail_self"))
                    JOptionPane.showMessageDialog(null, "Cannot be a friend to yourself");
                else if(TEMP1.contains("add_friend_fail_already_friend"))
                    JOptionPane.showMessageDialog(null, "Already a friend");
                else if(TEMP1.contains("add_friend_success"))
                {
                    String [] temp = TEMP1.split(",");
                    
                    JOptionPane.showMessageDialog(null, "Friend added succcessfully");
                    client_gui.add_friend_box.setText("");
                    client_gui.friends_panel.setVisible(false);
                    client_gui.friend_list_area.setText(client_gui.friend_list_area.getText() + "\r\n" + temp[1]);
                    client_gui.friends_panel.setVisible(true);
                }
                
            }
            else if(MESSAGE.contains("chat_friend"))
            {
                String [] temp = MESSAGE.split(",");
                
                if(MESSAGE.contains("chat_friend_offline"))
                    JOptionPane.showMessageDialog(null, "Friend Not Logged In");
                else if(MESSAGE.contains("chat_friend_online"))
                {
                    JOptionPane.showMessageDialog(null, "Friend Online");
                    client_gui.chat_panel.setVisible(true);
                    chat_friend_name = temp[1];
                    
                }
            }
            else if(MESSAGE.contains("send_chat_message"))
            {
                String [] temp = MESSAGE.split(",");
                
                client_gui.chat_area.setText(client_gui.chat_area.getText() + "\r\n" + temp[3] + " : " + temp[2]);
            }
        }
    }
    
    public void add_client(String username, String password)
    {
        OUT.println("sign_up," + username + "," + password);
        OUT.flush();
    }
    
    public void login_client(String username, String password)
    {
        OUT.println("sign_in," + username + "," + password);
        OUT.flush();
    }
    
    public void friend_list()
    {
        OUT.println("friend_list," + username);
        OUT.flush();
    }
    
    public void add_friend(String friend_name)
    {
        OUT.println("add_friend," + friend_name + "," + client_gui.username.getText());
        OUT.flush();
    }
    
    public void start_chat(String friend_name)
    {
        OUT.println("start_chat," + friend_name);
        OUT.flush();
    }
    
    public void send_chat_message(String message)
    {
        OUT.println("send_chat_message," + chat_friend_name +  "," + message + "," + client_gui.username.getText());
        OUT.flush();
        client_gui.chat_area.setText(client_gui.chat_area.getText() + "\r\n" + client_gui.username.getText() + " : " + message);
    }
    
    void logout(String username) throws IOException
    {
        OUT.println(A_Chat_Client_GUI_1.username.getText() + " has disconnected.");
        OUT.flush();
        SOCK.close();
        JOptionPane.showMessageDialog(null, "You disconnected");
        System.exit(0);
    }
}