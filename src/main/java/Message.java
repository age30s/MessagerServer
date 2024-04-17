import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;

    String message;
    HashMap<Integer, String> usersOnClient = new HashMap<>();

    public Message(String user){
        this.clientUser = user;

    }
}