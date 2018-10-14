package com.client.chatwindow;

import com.client.login.LoginController;
import com.messages.Message;
import com.messages.MessageType;
import com.messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;



public class Listener implements Runnable{

    private static final String HASCONNECTED = "has connected";

    private static String picture;
    private Socket socket;
    public String hostname;
    public int port;
    public static String username;
    public ChatController controller;
    public static String portListen;
    public static String ipAddress;
    
    private static ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    Logger logger = LoggerFactory.getLogger(Listener.class);

    public Listener(String hostname, int port, String username, String picture,String portListen,String ipAddress, ChatController controller) {
        this.hostname = hostname;
        this.port = port;
        Listener.username = username;
        Listener.picture = picture;
        this.controller = controller;
        Listener.portListen=portListen;
        Listener.ipAddress=ipAddress;
    }

    public void run() {
        try {
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
                        case USER:
                            controller.addToChat(message);
                            break;
                        case SERVER:
                            controller.addAsServer(message);
                            break;
                        case CONNECTED:
                            controller.setUserList(message);
                            break;
                        case DISCONNECTED:
                            controller.setUserList(message);
                            break;
                        case STATUS:
                            controller.setUserList(message);
                            break;
                        case CONNECT_FRIEND:
                            controller.acceptRequestConnect(message, username);
                            break;
                        case STATUS_CONNECT:
                            controller.notice(message,username);
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
    public static void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        createMessage.setPicture(picture);
        createMessage.setIp(ipAddress);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
        oos.flush();
    }



    /* This method is used for sending a normal Message
 * @param msg - The message which the user generates
 */
    public static void sendStatusUpdate(Status status) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.STATUS);
        createMessage.setStatus(status);
        createMessage.setPicture(picture);
        createMessage.setIp(ipAddress);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* Message first*/
    public static void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.CONNECTED);
        createMessage.setMsg(HASCONNECTED);
        createMessage.setPicture(picture);
        createMessage.setIp(ipAddress);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
    }

    // Send connect request
    public static void sendConnectFriend(String userNameFriend) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.CONNECT_FRIEND);
        createMessage.setMsg(userNameFriend);//user name muon ket noi
        createMessage.setPicture(picture);
        createMessage.setIp(ipAddress);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
    }
    
    public static void sendStatusConnect(String userSendConnect, String statusConnect) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.STATUS_CONNECT);
        createMessage.setMsg(userSendConnect);//thong bao den user A trang thai ket noi
        createMessage.setIp(statusConnect);// Trang thai ket noi la true hay false        
        createMessage.setPicture(picture);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
    }
}
