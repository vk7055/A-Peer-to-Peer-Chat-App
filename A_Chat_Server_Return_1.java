import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.sql.*;
import java.sql.SQLException;

public class A_Chat_Server_Return_1 implements Runnable
{
    
    Socket SOCK;
    private Scanner INPUT;
    private PrintWriter OUT;
    String MESSAGE = "";
    
    public A_Chat_Server_Return_1(Socket X)
    {
        this.SOCK=X;
    }
    
    public void CheckConnection() throws IOException
    {
        
    }
    
    public void run()
    {
        
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;
        try
        {
            myConn = DriverManager.getConnection("jdbc:mysql://localhost/shop", "vin", "123");
            myStmt = myConn.createStatement();
        }catch(Exception e){};
            
        try
        {
            try
            {
                INPUT = new Scanner(SOCK.getInputStream());
                OUT = new PrintWriter(SOCK.getOutputStream());
                
                while(true)
                {
                    CheckConnection();
                    
                    if(!INPUT.hasNext())
                    {
                        return;
                    }
                    
                    MESSAGE = INPUT.nextLine();
                    
                    if(MESSAGE.contains("sign_up"))
                    {
                        String TEMP1 = MESSAGE.substring(7);
                        String[] tempString = TEMP1.split(",");
                        
                        ResultSet rs = myStmt.executeQuery("select * from auth where id = " + "\"" + tempString[1] + "\"" + ";");
                        
                        if(!rs.next())
                        {
                            myStmt.executeUpdate("insert into auth values(" + "\"" + tempString[1] + "\"" + "," + "\"" + tempString[2] + "\"" + ");");
                            OUT.println("sign_up_true");
                            OUT.flush();    
                        }
                        else
                        {
                            OUT.println("sign_up_false");
                            OUT.flush();    
                        }  
                    }
                    
                    else if(MESSAGE.contains("sign_in"))
                    {
                        String TEMP1 = MESSAGE.substring(7);
                        String[] tempString = TEMP1.split(",");
                        ResultSet rs = myStmt.executeQuery("select * from auth where id = " + "\"" + tempString[1] + "\"" + " AND " + "\"" + tempString[2] + "\"" + ";");
                        
                        String id = "";
                        String pwd = "";
                        
                        if(!rs.next())
                        {
                            OUT.println("sign_in_false");
                            OUT.flush();
                        }
                        else
                        {
                            while(rs.next())
                            {
                                id = rs.getString("id");
                                pwd = rs.getString("pwd");
                            }
                            OUT.println("sign_in_true," + tempString[1]);
                            OUT.flush();
                            A_Chat_Server_1.CurrentUsers.add(tempString[1]);
                            A_Chat_Server_1.ConnectionArray.add(SOCK);
                        }
                    }
                    
                    else if(MESSAGE.contains("friend_list"))
                    {
                        String[] tempString = MESSAGE.split(",");
                        
                        String friends = "";
                        ResultSet rs_1 = myStmt.executeQuery("select friend from friends where name = " + "\"" + tempString[1] + "\"" + ";");
                        while(rs_1.next())
                        {
                            friends = friends + rs_1.getString("friend") + "," ;
                        }
                        
                        String others = "";
                        ResultSet rs_2 = myStmt.executeQuery("select id from auth;");
                        while(rs_2.next())
                        {
                            others = others + rs_2.getString("id") + ",";
                        }
                        
                        OUT.println("friend_list," + friends + "#" + others);
                        OUT.flush();
                    }
                    
                    else if(MESSAGE.contains("add_friend"))
                    {
                        String[] tempString = MESSAGE.split(",");
                        
                        if(tempString[1].equals(tempString[2]))
                        {
                            OUT.println("add_friend," + "add_friend_fail_self");
                            OUT.flush();
                        }
                        else
                        {
                            boolean already_friend = false;
                            
                            ResultSet rs = myStmt.executeQuery("select friend from friends where name = " + "\"" + tempString[2] + "\"" + ";");

                            while(rs.next())
                            {
                                if(rs.getString("friend").equals(tempString[1]))
                                {
                                    OUT.println("add_friend," + "add_friend_fail_already_friend");
                                    OUT.flush();
                                    already_friend = true;
                                    break;
                                }    
                            }

                            if(already_friend == false)
                            {
                                myStmt.executeUpdate("insert into friends values (" + "\"" + tempString[2] + "\"" + "," + "\"" + tempString[1] + "\"" + ");");
                                OUT.println("add_friend," + "add_friend_success," + tempString[1]);
                                OUT.flush();
                            }
                        }
                    }
                    else if(MESSAGE.contains("start_chat"))
                    {
                        String[] tempString = MESSAGE.split(",");
                        
                        int count = 0;
                        for(int i = 0; i < A_Chat_Server_1.CurrentUsers.size(); i++)
                        {
                            if(A_Chat_Server_1.CurrentUsers.get(i).equals(tempString[1]))
                            {
                                OUT.println("chat_friend_online," + A_Chat_Server_1.CurrentUsers.get(i));
                                OUT.flush();
                                count--;
                                break;
                            }
                            count++;
                        }
                        if(count == A_Chat_Server_1.CurrentUsers.size())
                        {
                            OUT.println("chat_friend_offline");
                            OUT.flush();
                        } 
                    }
                    else if(MESSAGE.contains("send_chat_message"))
                    {
                        String[] tempString = MESSAGE.split(",");
                    
                        for(int i = 0; i < A_Chat_Server_1.CurrentUsers.size(); i++)
                        {
                            if(A_Chat_Server_1.CurrentUsers.get(i).equals(tempString[1]))
                            {
                                Socket TEMP_SOCK = (Socket) A_Chat_Server_1.ConnectionArray.get(i);
                                PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
                                TEMP_OUT.println("send_chat_message," + tempString[1] + "," + tempString[2] + "," + tempString[3]);
                                TEMP_OUT.flush();
                            }
                        }
                    }
                    else if(MESSAGE.contains("has disconnected"))
                    {
                        System.out.println("Client said: " + MESSAGE);
                    }
                }
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
}