import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
	Map<String,ClientThread> usersOnServer = new HashMap<>();


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

		String username = "";


		String user;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;

		}
		public void messageEveryone(Message m){
			System.out.println("Message seding everyone: " + m.message + " true or false: " + m.isEveryone);
			for (Map.Entry<String,ClientThread> entry : usersOnServer.entrySet()){
				try{
					ClientThread t = entry.getValue();
					t.out.writeObject(m);
				}
				catch (Exception e){
					e.printStackTrace();
				}

			}
		}

		public void updateClients(Message message) {
//				System.out.println("recieving : " + message.message);
//				System.out.println("ccurrent size " + clients.size());
//				System.out.println("ccurrent size " + usersOnServer.size());

//				synchronized () {

			if(!Objects.equals(message.outMessage, "")){
				try {
					ClientThread sendTo = usersOnServer.get(message.outMessage);
					System.out.println("Clienthread user found " + sendTo.username);
					sendTo.out.writeObject(message);
				}
				catch (Exception e){
					e.printStackTrace();
				}

				return;
			}
			for (int i = 0; i < clients.size(); i++) {
				System.out.println("i:" + i);
				ClientThread t = clients.get(i);
				try {

					Message updatinglist = new Message(t.username);
					for(Map.Entry<String,ClientThread> entry: usersOnServer.entrySet()){
						updatinglist.usersOnClient.add(entry.getKey());
					}

					System.out.println(updatinglist.clientUser + " " + updatinglist.usersOnClient.size());
//							ClientThread sendTo = usersOnServer.get(message.outMessage);
					t.out.writeObject(updatinglist);
					//								message.login = false;
					//								t.out.writeObject(message);

					//							System.out.println("t.message.clientUser: " + t.message.clientUser + " ------- " + "message.outMessage: " + message.outMessage );
					//							if(Objects.equals(t.message.clientUser, message.outMessage)){
					//								System.out.println("Going herer: " + message.outMessage);
					//								// cli kavya , "hi", bantu -> bantu
					//								t.out.writeObject(message);
					//							}

				} catch (Exception e) {
					e.printStackTrace();
				}
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

					Message temp = (Message) in.readObject(); //cli kavya, "hi", bantu
					System.out.println(temp.clientUser + " send to " + temp.outMessage + " the message: " + temp.message);
					System.out.println("temp.isEveryone: " + temp.isEveryone);
					if(temp.isEveryone == true){
						messageEveryone(temp);
					}

					if(Objects.equals(username, "")) {
						username = temp.clientUser;
						System.out.println(" Current clienthread name is " + this.username);
						usersOnServer.put(this.username, this);

						System.out.println( "Current map size is " + usersOnServer.size());
					}

//							message.message = temp.message;
//							message.outMessage = temp.outMessage;
					// probs creating duplicates on the arraylist
//							usersOnServer.put(count, temp.clientUser);
					updateClients(temp);
					callback.accept("client: " + count + " sent: " + temp.outMessage);

				}
				catch(Exception e) {
					//callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
//					    	updateClients(temp);
					clients.remove(this);
					break;
				}
			}
		}//end of run


	}//end of client thread
}