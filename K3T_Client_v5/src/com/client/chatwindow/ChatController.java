package com.client.chatwindow;

import com.client.login.MainLauncher;
import com.messages.Message;
import com.messages.MessageType;
import com.messages.Status;
import com.messages.User;
import com.messages.bubble.BubbleSpec;
import com.messages.bubble.BubbledLabel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

public class ChatController implements Initializable {

    @FXML
    private TextArea messageBox;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label onlineCountLabel;
    @FXML
    private ListView userList;
    @FXML
    private ImageView userImageView;
    @FXML
    ListView chatPane;
    @FXML
    ListView statusList;
    @FXML
    BorderPane borderPane;
    @FXML
    ComboBox statusComboBox;

    private double xOffset;
    private double yOffset;
    Logger logger = LoggerFactory.getLogger(ChatController.class);
    private Listener listener;
    String userNow;
    public CellRenderer cellRender;

    public void setListener(Listener ls) {
        this.listener = ls;
    }

    public Listener getListener() {
        return this.listener;
    }

    public void setUsernameLabel(String username) {
        this.usernameLabel.setText(username);
    }

    public void setImageLabel() throws IOException {
        this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/Dominic.png").toString()));
    }

    public void setOnlineLabel(String usercount) {
        Platform.runLater(() -> onlineCountLabel.setText(usercount));
    }

    public void setUserList(Message msg) {
        logger.info("setUserList() method Enter");
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUsers());
            userList.setItems(users);
            cellRender = new CellRenderer(this);
            userList.setCellFactory(cellRender);
            setOnlineLabel(String.valueOf(msg.getUserlist().size()));
        });
        logger.info("setUserList() method Exit");
    }

    public void sendMethod(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            sendButtonAction();
        }
    }

    @FXML
    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setImageLabel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Drag and Drop */
        borderPane.setOnMousePressed(event -> {
            xOffset = MainLauncher.getPrimaryStage().getX() - event.getScreenX();
            yOffset = MainLauncher.getPrimaryStage().getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            MainLauncher.getPrimaryStage().setX(event.getScreenX() + xOffset);
            MainLauncher.getPrimaryStage().setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> {
            borderPane.setCursor(Cursor.DEFAULT);
        });

        statusComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    listener.sendStatusUpdate(Status.valueOf(newValue.toUpperCase()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /* Added to prevent the enter from adding a new line to inputMessageBox */
        messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                try {
                    sendButtonAction();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ke.consume();
            }
        });

    }

    public void setImageLabel(String selectedPicture) {
        switch (selectedPicture) {
            case "Dominic":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/Dominic.png").toString()));
                break;
            case "Sarah":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/sarah.png").toString()));
                break;
            case "Default":
                this.userImageView.setImage(new Image(getClass().getClassLoader().getResource("images/default.png").toString()));
                break;
        }
    }

    public void logoutScene() {
        Platform.runLater(() -> {
            FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
            Parent window = null;
            try {
                window = (Pane) fmxlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = MainLauncher.getPrimaryStage();
            Scene scene = new Scene(window);
            stage.setMaxWidth(364);
            stage.setMaxHeight(550);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.centerOnScreen();
        });
    }

    @FXML
    void connectActionButton(ActionEvent event) throws IOException {
        String userNeedSendConnect = "M";        //Lay user name cua user duoc chon
        if (!listener.isConnected(userNeedSendConnect)) {//neu chua co ket noi tu truoc
            setUserNow(userNeedSendConnect);
            listener.createConnect(userNeedSendConnect);        //gui message toi server
        } else {
            setUserNow(userNeedSendConnect);
        }        //show pane cua user name do    
    }

    //Ham xu ly khi co yeu cau ket noi cua user A den
    //Ham xu ly khi co yeu cau ket noi cua user A den
//    public void notice(Message message, String userName) {
//        String userSendConnect = message.getMsg();
//        String statusConnect = message.getIp();
//        logger.info("User name: "+userSendConnect+" Status Connect: "+statusConnect);
//        if ("true".equals(statusConnect)){
//            showInfo("Notice to "+userName,"Connect to "+userSendConnect +" SUCCESS !");
//            //Show pane
//        }
//        else{
//            showInfo("Notice to "+userName,"Connect to "+userSendConnect +" FAIL !");
//        }
//        
//    }
    public static void showInfo(String message1, String message2) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(message1);
            alert.setHeaderText(null);
            alert.setContentText(message2);
            alert.showAndWait();
        });
    }

    public void setUserNow(String userN) throws IOException {

        if (!listener.isConnected(userN)) {//neu chua co ket noi tu truoc
            listener.createConnect(userN);        //gui message toi server
        }
        if (!userN.equals(this.userNow)) {
            this.userNow = userN;

            chatPane.getItems().clear();
            ArrayList<Message> list = listener.listview.get(userNow);
            if (list == null) {
                return;
            }
            for (Message mgs : list) {
                addToChat(mgs);
            }
        }

    }

    public synchronized void updateMgs(Message mgs) {
        this.addToChat(mgs);
        this.cellRender.setTxt(mgs.getName(), mgs.getMsg());
    }

    public void sendButtonAction() throws IOException {
        String msg = messageBox.getText();
        if (!messageBox.getText().isEmpty()) {
            listener.sendTo(msg, userNow);
            messageBox.clear();
        }
    }

    public synchronized void addToChat(Message msg) {
        Task<HBox> othersMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                //Image image = new Image(getClass().getClassLoader().getResource("images/" + msg.getPicture() + ".png").toString());
                // Cai tren co loi
                Image image = userImageView.getImage();
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();

                bl6.setText(msg.getName() + " : " + msg.getMsg());

                bl6.setBackground(new Background(new BackgroundFill(Color.PINK, null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_LEFT);
                bl6.setBubbleSpec(BubbleSpec.FACE_LEFT_CENTER);
                x.getChildren().addAll(profileImage, bl6);

                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };
        othersMessages.setOnSucceeded(event -> chatPane.getItems().add(othersMessages.getValue()));

        Task<HBox> yourMessages = new Task<HBox>() {
            @Override
            public HBox call() throws Exception {
                Image image = userImageView.getImage();
                ImageView profileImage = new ImageView(image);
                profileImage.setFitHeight(32);
                profileImage.setFitWidth(32);

                BubbledLabel bl6 = new BubbledLabel();

                bl6.setText(msg.getMsg());

                bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN,
                        null, null)));
                HBox x = new HBox();
                x.setMaxWidth(chatPane.getWidth() - 20);
                x.setAlignment(Pos.TOP_RIGHT);
                bl6.setBubbleSpec(BubbleSpec.FACE_RIGHT_CENTER);
                x.getChildren().addAll(bl6, profileImage);

                setOnlineLabel(Integer.toString(msg.getOnlineCount()));
                return x;
            }
        };
        yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

        if (msg.getName().equals(this.usernameLabel.getText())) {
            /*Thread t1 = new Thread(yourMessages);
            t1.setDaemon(true);
            t1.start();*/
            yourMessages.run();
        } else {
            /*Thread t2 = new Thread(othersMessages);
            t2.setDaemon(true);
            t2.start();*/
            othersMessages.run();
        }
    }
}
