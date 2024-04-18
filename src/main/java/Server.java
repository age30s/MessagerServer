import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.util.Pair;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	HashMap<Integer,String> usersOnServer = new HashMap<>();


	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;

			Message message;


			String user;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;

			}
			
			public void updateClients(Message message) {
//				System.out.println("recieving : " + message.message);
//				System.out.println("ccurrent size " + clients.size());
//				System.out.println("ccurrent size " + usersOnServer.size());

//				synchronized () {
					for (int i = 0; i < clients.size(); i++) {
						System.out.println("i:" + i);
						ClientThread t = clients.get(i);
						try {
							if(message.login == true){
								System.out.println(message.message + "-------------------");
								System.out.println(t.message.clientUser);
								message.usersOnClient.clear();
								message.usersOnClient.putAll(usersOnServer);
								System.out.println(message.clientUser + " " + message.usersOnClient.size());
//								message.login = false;
								System.out.println("Going to the if: ");
								t.out.writeObject(message);
							}
							System.out.println("t.message.clientUser: " + t.message.clientUser + " ------- " + "message.outMessage: " + message.outMessage );
							if(Objects.equals(t.message.clientUser, message.outMessage)){
								System.out.println("Going herer: " + message.outMessage);
								t.out.writeObject(message);
							}

						} catch (Exception e) {
						}
					//}
				}
					System.out.println("hellpppppp");
					message.login = false;

			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
//				updateClients("new client on server: client #"+count);
					
				 while(true) {
					    try {
							message = (Message) in.readObject();
							callback.accept("client: " + count + " sent: " + message.outMessage);
							// probs creating duplicates on the arraylist
							usersOnServer.put(count, message.clientUser);
							updateClients(message);
						}
					    catch(Exception e) {
					    	//callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients(message);
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
