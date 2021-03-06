package com.client.chatwindow;

import com.client.login.LoginController;
import com.messages.Message;
import com.messages.MessageType;
import com.messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;



public class Listener implements Runnable{

    private static final String HASCONNECTED = "has connected";

    private  String picture;
    private Socket socket;
    public String hostname;
    public int port;
    public  String username;
    public ChatController controller;
    public  String portListen;
    public  String ipAddress;
    
    private static ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    Logger logger = LoggerFactory.getLogger(Listener.class);

    public ServerSocket subserver; // Them boi Thoai ngay 13/10/18 9:17pm
    public HashMap<String, ObjectOutputStream> senders;// Them boi Thoai ngay 13/10/18 9:17pm
    public HashMap<String, ArrayList<Message>> listview;// Them boi Thoai ngay 13/10/18 9:17pm
 
    

    public Listener(String hostname, int port, String username, String picture,String portListen,String ipAddress, ChatController controller) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.picture = picture;
        this.controller = controller;
        this.portListen=portListen;
        this.ipAddress=ipAddress;
    }

    public void run() {
        try {
            subserver = new ServerSocket(Integer.parseInt(this.portListen));
            logger.info("Server "+ username+" (Port: "+portListen+" IpAddress: "+ipAddress + ") is running !!!");
            senders = new HashMap<>();
            listview = new HashMap<>();

            socket = new Socket(hostname, port);
            LoginController.getInstance().showScene();
            outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            is = socket.getInputStream();
            input = new ObjectInputStream(is);
        } catch (IOException e) {
            LoginController.getInstance().showErrorDialog("Could not connect to server");
            logger.error("Could not Connect");
        }
        logger.info("IP " + hostname + ":" +port);
        logger.info("Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            connect();//message first
            logger.info("Sockets in and out ready!");
            while (socket.isConnected()) {
                Message message = null;
                message = (Message) input.readObject();

                if (message != null) {
                    logger.debug("Message recieved:" + message.getMsg() + " MessageType:" + message.getType() + "Name:" + message.getName());
                    switch (message.getType()) {
                        case CONNECTED:
                            controller.setUserList(message);
                            break;
                        case DISCONNECTED:
                            controller.setUserList(message);
                            break;
                        case STATUS:
                            controller.setUserList(message);
                            break;
                        case INVITE:
                            acceptRequestConnect(message);
                            break;
//                      case STATUS_CONNECT:
//                            controller.notice(message,username);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.logoutScene();
        }
    }





    /* This method is used for sending a normal Message
 * @param msg - The message which the user generates
 */
    public  void sendStatusUpdate(Status status) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.STATUS);
        createMessage.setStatus(status);
        createMessage.setPicture(this.picture);
        createMessage.setIp(this.ipAddress);
        createMessage.setPort(this.portListen);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* Message first*/
    public  void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.CONNECTED);
        createMessage.setMsg(HASCONNECTED);
        createMessage.setPicture(this.picture);
        createMessage.setIp(this.ipAddress);
        createMessage.setPort(this.portListen);
        oos.writeObject(createMessage);
    }
    // Send connect request
    
    public  void sendConnectFriend(String userNameFriend) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.INVITE);
        createMessage.setMsg(userNameFriend);//user name muon ket noi
        createMessage.setPicture(this.picture);
        createMessage.setIp(this.ipAddress);
        createMessage.setPort(this.portListen);
        oos.writeObject(createMessage);
    }
    
    public  void sendStatusConnect(String userSendConnect, String statusConnect) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.STATUS_CONNECT);
        createMessage.setMsg(userSendConnect);//thong bao den user A trang thai ket noi
        createMessage.setIp(statusConnect);// Trang thai ket noi la true hay false        
        createMessage.setPicture(this.picture);
        createMessage.setPort(this.portListen);
        oos.writeObject(createMessage);
    }

    public boolean isConnected(String userName) {
        return senders.containsKey(userName);
    }
    
    public void createConnect(String name) throws IOException {

        /**
         * ********************************************************
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket sock = subserver.accept();
                        ObjectInputStream obj = new ObjectInputStream(sock.getInputStream());
                        Message message = (Message) obj.readObject();
                        
                        if (message.getType() == MessageType.INVITE
                                && message.getName().equals(name)) {
                            senders.put(name, new ObjectOutputStream(sock.getOutputStream()));
                            listview.put(name, new ArrayList<Message>());
                            handleConnection(sock, obj);
                            break;
                        }
                    }
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        /**
         * ********************************
         */
        sendConnectFriend(name);
    }      
    
    

    public void sendTo(String msg , String name) {
        
        logger.info("sendTo: "+name+" message: "+msg);

        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        createMessage.setPicture(this.picture);
        
        listview.get(name).add(createMessage);
        
        if(name.equals(controller.userNow))
            controller.addToChat(createMessage);  
        
        ObjectOutputStream ossTo = senders.get(name);
        try {
            ossTo.writeObject(createMessage);
            ossTo.flush();
        } catch (IOException ex) {
            logger.error("Can not write to : " + name, ex);
        }     
    }

   
    public void handleConnection(Socket socket, ObjectInputStream input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        Message message = null;
                        message = (Message) input.readObject();

                        if (message != null) {
                            logger.info("Message recieved: " + message.getMsg() + " MessageType: " + message.getType() + " Name: " + message.getName());
                            switch (message.getType()) {
                                case USER :
                                    //message.setName(username);
                                    listview.get(message.getName()).add(message);
                                    if(message.getName().equals(controller.userNow))
                                        controller.addToChat(message);
                                    break;
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    controller.logoutScene();
                }
            }

        }).start();
    }


    public void acceptRequestConnect(Message message) throws IOException{
        String userSendConnect = message.getMsg();

        Socket sock = new Socket(message.getIp(), Integer.parseInt(message.getPort()));
        ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
        senders.put(userSendConnect, out);
        listview.put(userSendConnect, new ArrayList<Message>());

        message.setType(MessageType.INVITE);
        message.setName(this.username);//Ten cua client gui
        out.writeObject(message);
        handleConnection(sock,new ObjectInputStream(sock.getInputStream()));
        logger.info("User name: "+userSendConnect+" IP: "+message.getIp()+" Port: "+message.getPort());
        ChatController.showInfo("Notice to "+this.username,"User: "+userSendConnect +" want to connect to you !");
        
//        //Tao socket
//        if (true){        //Kiem tra socket da ket noi den server user A chua
//            //Neu co thi phan hoi den user A la da ket noi thanh cong thong qua server center
//            sendStatusConnect(userSendConnect,"true");
//        }
//        else{
//            sendStatusConnect(userSendConnect,"false");
//            ChatController.showInfo(userName,"Connect to user: "+userSendConnect+" fail !!!");
//            
//        }
    }

}
