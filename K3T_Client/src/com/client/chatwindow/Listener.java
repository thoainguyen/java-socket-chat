package com.client.chatwindow;

import com.client.login.LoginController;
import com.messages.Message;
import com.messages.MessageType;
import com.messages.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.control.ListView;
import java.io.*;
import java.net.Socket;

import static com.messages.MessageType.CONNECTED;
import com.messages.User;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.logging.Level;

public class Listener implements Runnable {

    private final String HASCONNECTED = "has connected";
    private String picture;
    private Socket socket;
    public String hostname;
    public int port;
    public String username;
    public ChatController controller;

    public String portListen;
    public String ipAddress;

    public ServerSocket subserver; // Them boi Thoai ngay 13/10/18 9:17pm
    public HashMap<String, ObjectOutputStream> senders;// Them boi Thoai ngay 13/10/18 9:17pm
    public HashMap<String, ListView> listview;// Them boi Thoai ngay 13/10/18 9:17pm
    ///public HashMap<String, User> users; // Them boi Thoai ngay 13/10/18 9:17pm

    private ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;
    Logger logger = LoggerFactory.getLogger(Listener.class);

    public Listener(String hostname, int port, String username, String picture, String portListen, String ipAddress, ChatController controller) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.picture = picture;
        this.controller = controller;
        this.portListen = portListen;
        this.ipAddress = ipAddress;
    }

    public void run() {
        try {
            subserver = new ServerSocket(Integer.parseInt(this.portListen));
            senders = new HashMap<>();
            listview = new HashMap<>();
            ///users = new HashMap<>();
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
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            controller.logoutScene();
        }
    }

    public boolean isConnected(String userName) {
        return senders.containsKey(userName);
    }

    public void handleConnection(Socket socket, ObjectInputStream input) {
        Thread r;
        r = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()) {
                        Message message = null;
                        message = (Message) input.readObject();

                        if (message != null) {
                            logger.debug("Message recieved:" + message.getMsg() + " MessageType:" + message.getType() + "Name:" + message.getName());
                            ListView lv = listview.get(message.getName());
                            switch (message.getType()) {
                                case INVITE:
                                    acceptConnection(message);
                                    break;
                                case USER:
                                    controller.addToOtherChat(message, lv);
                                    break;
                                case VOICE:
                                    controller.addToOtherChat(message, lv);
                                    break;
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    controller.logoutScene();
                }
            }

        });
        r.start();
    }

    private void acceptConnection(Message message) throws IOException {
        Socket sock = new Socket(message.getIp(), Integer.parseInt(message.getPort()));
        senders.put(message.getName(), new ObjectOutputStream(sock.getOutputStream()));
        listview.put(message.getName(), new ListView());
    }

    public void createConnect(String name) throws IOException {

        /**
         * ********************************************************
         */
        Thread t;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket sock = subserver.accept();
                        ObjectInputStream obj = new ObjectInputStream(sock.getInputStream());
                        Message message = (Message) obj.readObject();
                        if (message.getType().equals("INVITE")
                                && message.getName().equals(name)) {
                            senders.put(name, new ObjectOutputStream(sock.getOutputStream()));
                            listview.put(name, new ListView());
                            handleConnection(sock, obj);
                            controller.setUserNow(name);
                            break;
                        }
                    }
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(Listener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        /**
         * ********************************
         */
        Message message = new Message();
        message.setName(this.username);
        message.setType(MessageType.INVITE);
        message.setMsg(name);
        message.setIp(this.ipAddress);
        message.setPort(this.portListen);
        this.oos.writeObject(message);
        this.oos.flush();
    }

    /* This method is used for sending a normal Message to Server
     * @param msg - The message which the user generates
     */
    public void send(String msg) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        createMessage.setPicture(this.picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a normal Message to concrete user
     * @param msg - The message which the user generates
     */
    public void sendTo(String name, String msg) {
        Message createMessage = new Message();
        createMessage.setName(this.username);
        createMessage.setType(MessageType.USER);
        createMessage.setStatus(Status.AWAY);
        createMessage.setMsg(msg);
        createMessage.setPicture(this.picture);

        controller.addToMyChat(createMessage);

        ObjectOutputStream ossTo = senders.get(name);
        try {
            ossTo.writeObject(createMessage);
            ossTo.flush();
        } catch (IOException ex) {
            logger.error("Can not write to : " + name, ex);
        }

    }

    /* This method is used for sending a voice Message
 * @param msg - The message which the user generates
     */
    public void sendVoiceMessage(byte[] audio) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.VOICE);
        createMessage.setStatus(Status.AWAY);
        createMessage.setVoiceMsg(audio);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used for sending a normal Message
 * @param msg - The message which the user generates
     */
    public void sendStatusUpdate(Status status) throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(MessageType.STATUS);
        createMessage.setStatus(status);
        createMessage.setPicture(picture);
        oos.writeObject(createMessage);
        oos.flush();
    }

    /* This method is used to send a connecting message */
    public void connect() throws IOException {
        Message createMessage = new Message();
        createMessage.setName(username);
        createMessage.setType(CONNECTED);
        createMessage.setMsg(HASCONNECTED);
        createMessage.setPicture(picture);
        createMessage.setIp(ipAddress);
        createMessage.setPort(portListen);
        oos.writeObject(createMessage);
    }

}
