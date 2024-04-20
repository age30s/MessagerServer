import javafx.util.Pair;

import javax.swing.text.StyledEditorKit;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;
    Boolean login = false;
    String message;
    Boolean isEveryone = false;
    String exception;
    Boolean grpMsg = false;

    ArrayList<String> grpList = new ArrayList<>();

    ArrayList<String> usersOnClient = new ArrayList<>();


    public Message(String user){
        this.clientUser = user;

    }

    void setRecipient(String m){
        outMessage = m;
    }

    void setText(String s){
        message = s;

    }
}