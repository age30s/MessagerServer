import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    String outMessage;
    String clientUser;

    HashMap<Integer, String> usersOnClient = new HashMap<>();
    String user;


    public Message(String user){
        this.clientUser = user;

    }
}