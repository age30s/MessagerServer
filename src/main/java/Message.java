import javafx.util.Pair;

import javax.swing.text.StyledEditorKit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;
    Boolean login = false;
    String message;

//    Object lock = new Object();

    HashMap<Integer,String> usersOnClient = new HashMap<>();


    public Message(String user){
        this.clientUser = user;

    }
}