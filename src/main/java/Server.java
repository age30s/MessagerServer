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
		public void userAlreadyExists(){
			try{

				Message tempMessage = new Message(username);
				tempMessage.exception = "Username already exists. Please select another username.";
				username = "";
				this.out.writeObject(tempMessage);

			}catch (Exception e){
				e.printStackTrace();
			}

		}

		public void messageGroup(Message temp)
		{
			for(String name: temp.grpList){
				try{
					ClientThread t = usersOnServer.get(name);
					t.out.writeObject(temp);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}

		public void messageEveryone(Message m){
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

			if(!Objects.equals(message.outMessage, "")){
				try {
					ClientThread sendTo = usersOnServer.get(message.outMessage);
					for(Map.Entry<String,ClientThread> entry: usersOnServer.entrySet()){
						message.usersOnClient.add(entry.getKey());
					}
					sendTo.out.writeObject(message);
				}

				catch (Exception e){
					e.printStackTrace();
				}
				return;
			}
			for (int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {

					Message updatinglist = new Message(t.username);

					if(message.login){
						updatinglist.login = true;
					}

					if(Objects.equals(message.exception, "closed")){
						updatinglist.exception = "closed";
					}

					for(Map.Entry<String,ClientThread> entry: usersOnServer.entrySet()){
						updatinglist.usersOnClient.add(entry.getKey());
					}

					t.out.writeObject(updatinglist);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

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


			while(true) {
				try {
					Message temp = (Message) in.readObject();

					if(temp.isEveryone == true){
						messageEveryone(temp);
					}

					if(temp.grpMsg == true){
						messageGroup(temp);
					}

					if(Objects.equals(username, "")) {
						username = temp.clientUser;
						if(usersOnServer.containsKey(temp.clientUser)){
							userAlreadyExists();
							continue;
						}else{
							usersOnServer.put(this.username, this);
						}

					}

					updateClients(temp);
					callback.accept("client: " + count + " sent: " + temp.outMessage);

				}
				catch(Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					clients.remove(this);
					usersOnServer.remove(this.username,this);
					Message temp = new Message("default");
					temp.outMessage = "";
					temp.exception = "closed";
					updateClients(temp);
					break;
				}
			}
		}//end of run


	}//end of client thread
}


	
	

	
