
import java.net.URL;
//import data.Data;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
// java imports
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.io.*;

/***
 *================================================================================ The chat Server.========================================================================================
 
 */
 
public class Server implements Runnable {

	private final static int INCOMING_CONNECTION_PORT = 12000;
	private final static String SERVER_NAME = "Server";
	private final static String USER_LOGIN_DETAILS = "server_data/user_details.txt";

	//instance of other classes
	private ServerSocket serverSocket;
	private ConcurrentHashMap<String, String> knownClientDetails;
	private ArrayList<ClientInteractionHandler> currentConnections;
	
	
	private ReentrantReadWriteLock currentConnectionsLock;
	/***
	 *  The Constructor of the Server Class.
	 */
	public Server() {
		
		this.knownClientDetails = new ConcurrentHashMap<String, String>();
		
		// Checks if the Hash Map is populated successfully from database.
		if(this.loadKnownClientDetailsFromDatabase())
			System.out.println("******************************************\n"
					+ "System Notice - Concurrent Hash Map Populated Successfully with the Following Keys and Values:\n"
					+ this.knownClientDetails.toString() + "\n******************************************");
		
		this.currentConnections = new ArrayList<ClientInteractionHandler>();
		
		this.currentConnectionsLock = new ReentrantReadWriteLock();
		this.initialiseServer();
		
		
		}

	/***
	 * ServerSocket method to  listen for  client connections
	 * Catches Exception if the ServerSocket cannot be started.
	 */
	private void initialiseServer() {
		
		// start server socket to check for incoming clients
		
		try {
			this.serverSocket = new ServerSocket(Server.INCOMING_CONNECTION_PORT);
			} catch (IOException e) {
				System.out.println(e);
				}
				
		// tell admin server has started and is waiting for incoming client connection
		
		System.out.println("==========================================\n"
				+ "System Notice - Server started successfully and waiting for Connections on Port: " + Server.INCOMING_CONNECTION_PORT +
				".\n=======================================");
		}

	/*
	 * method to accept new clients and start ClientInteractionHandler threads to handle client requests in parallel.
	 */
	
	private void listenForConnections() {
		Socket clientSocket = null;
		
		// while the server is on get new clients
		
		while(true) {
			try {
				
				// accept client connection on the port
				clientSocket = this.serverSocket.accept();
				} catch (IOException e) {
					System.out.println(e);
					}
			
			// create a new thread to handle the client's connection in parallel.
			
			ClientInteractionHandler currentClient = new ClientInteractionHandler(clientSocket);
			
			try {
				
				this.currentConnectionsLock.writeLock().lock();


				this.currentConnections.add(currentClient);
				System.out.println("=======================================\n"
						+ "System Action -> Current Connection to a Client has been "
						+ "added."
						+ "\n====================================================");
				} finally {
					// unlock the lock once the writing has occurred or in the case of an Exception.
					this.currentConnectionsLock.writeLock().unlock();
					}
			// start new Thread to handle connection in parallel.
			System.out.println("------------------------------------------\n"
					+ "System Action -> New Thread started for current Client."
					+ "\n------------------------------------------");
			new Thread(currentClient).start();
			}
		}

	/***
	 * method to Shut Down Server.
	 */
	private void shutdownServer() {
		if(!this.currentConnections.isEmpty()) {
			for(ClientInteractionHandler client: currentConnections) {
				Message shutdownM = new Message(MessageID.CLOSE_CONNECTION, Server.SERVER_NAME,
												client.getClientUsername(), "Shut Down");
				client.sendMessageToClient(shutdownM);

			}
		}
		
		// tell admin -> Server has shut down.
		System.out.println("============================================\n"
				+ "System Notice - Server has Shutdown & is no longer listening for connections."
				+ "\n================================================");

		System.exit(0);  // exit
	}


	/***
	 * The main method of the program. Run if the current Computer is to be set up as the Server.
	 */
	public static void main(String args[]) {
		// instantiate a server object
		System.out.println("Server Log:\n" +
		"===============================================================");
		
		Server server = new Server();
		
		// Start server in a new thread
		
		Thread thread = new Thread(server);
		
		thread.start();
		
		
		System.out.println("Enter a Server Command(Exit):");
		
		Scanner input = new Scanner(System.in);
		
		String command = input.nextLine();
		
		while(!command.equals("Exit"))
		
			command = input.nextLine();

		// shut down the server.
		input.close();
		server.shutdownServer();


		}

	
	public synchronized boolean loadKnownClientDetailsFromDatabase() {
		// If you are doing the database, please implement this method (Look at ServerDatabase Class & libs folder)
		loadKnownClientDetailsFromTextFile();
		System.out.println("========================================\n"
				+ "System Action - Loaded All Known Client Details from Database."
				+ "\n=========================================");
		return true;
		}

	/***
	 * @return A boolean indicating whether the loading was successful.
	 */
	public synchronized boolean loadKnownClientDetailsFromTextFile() {
		try {
			@SuppressWarnings("resource")
			Scanner infile = new Scanner(new FileReader(USER_LOGIN_DETAILS)).useDelimiter("\n");
			while(infile.hasNext()) {
				String line = infile.next();
				String username = line.substring(0, line.indexOf('#'));
				String password = line.substring(line.indexOf('#') + 1);
				this.knownClientDetails.put(username, password);
				}
			} catch (FileNotFoundException e) {
				System.out.println(e);
				return false;
				}
		return true;

		}

	/***
	 * A method used to save a new Client's login details to the database.

	 * @return A boolean indicating whether the new Client was added to the Database successfully.
	 */
	public synchronized boolean saveUserDetailsToDatabase(String username, String password) {
		
		// If you are doing the database, please implement this method (Look at ServerDatabase Class & libs folder)
		
		saveUserDetailsToTextFile(username, password);
		System.out.println("============================================\n"
				+ "System Action - " + username + "'s Details Successfully added to Database."
						+ "\n===========================================");
		return true;
		}

	/***
	 * A method used to save a new Client's login details to the text file.
	 * @return A boolean indicating whether the new Client was added to the text file successfully.
	 */
	 
	public synchronized boolean saveUserDetailsToTextFile(String username, String password) {

		try {
			@SuppressWarnings("resource")
			FileWriter outfile = new FileWriter(USER_LOGIN_DETAILS, true);
			outfile.write("\n" + username + "#" + password);
			outfile.flush();
			} catch (IOException e) {
				System.out.println(e);
				return false;
				}
			return true;
		}

	/***
	 * A method used to check if the given Client's login details are correct.

	 */
	public boolean checkUserCredentials(String username, String password) {
		
	
		
		if(!this.knownClientDetails.containsKey(username)) {
			this.saveUserDetailsToDatabase(username, password);
			this.knownClientDetails.put(username, password);
			return true;
			}
			
		// Else check if the User Login Details Given is Correct
		
		if(this.knownClientDetails.get(username).equals(password))
				return true;
		// If they are incorrect return false
		return false;
		}

	/***
	 * A method used to check if the given Client is connected to the server.

	 */
	 
	public boolean checkOnline(String username) {
		boolean isOnline = false;
		try {

			this.currentConnectionsLock.readLock().lock();
			for(ClientInteractionHandler clientConnection: this.currentConnections)
				if(clientConnection.getClientUsername().equals(username))
					isOnline =  true;
			} finally {
				
				this.currentConnectionsLock.readLock().unlock();
				}
		return isOnline;

		}


	public ClientInteractionHandler getOnlineClient(String username) {
		ClientInteractionHandler soughtConnection = null;
		try {
			// locks the ArrayList with a ReadLock
			this.currentConnectionsLock.readLock().lock();
			for(ClientInteractionHandler clientConnection: this.currentConnections)
				if(clientConnection.getClientUsername().equals(username))
					soughtConnection = clientConnection;
			} finally {
				// Releases the lock since reading has occurred.
				this.currentConnectionsLock.readLock().unlock();
				}
		return soughtConnection;
		}

	/***
	 * The method which is called when Server is parsed into a Thread and start is called.

	 */
	@Override
	public void run() {
		this.listenForConnections();
		}
		
		/*
			class FilesW
		{
			public FilesW(Object data)
			{
				try
				{       
						JFileChooser ch = new JFileChooser();
						int c = ch.showSaveDialog(null);
						if (c == JFileChooser.APPROVE_OPTION) {
							try {
								FileOutputStream out = new FileOutputStream(ch.getSelectedFile());
								out.write(data.getFile());
								out.close();
							} catch (Exception e) {
								//JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
								System.out.println("Error: "+e);
							}
					   // }
					}
					
				}catch(Exception e)
				{
					System.out.println("Could not play audio: \n" + e.getMessage());
				}
			}

		}
		*/
		
		/*

		

*/
private class ClientInteractionHandler implements Runnable{

	private final static String IMAGE_CONFIRMATION_REQUEST_TEXT = " would like to send you an a file. Would you like to Download the file? (Yes/No)";


	private Socket connectionToClient;
	private String clientUsername;	//instance variablesentUsername;
	private ObjectInputStream oInputStream;
	private ObjectOutputStream oOutputStream;
	private ReentrantReadWriteLock outstandingMessagesLock;
	private ArrayList<Message> outstandingMessages;


	public ClientInteractionHandler(Socket connectionToClient) {
		this.connectionToClient = connectionToClient;
		this.clientUsername = "";
		this.outstandingMessagesLock = new ReentrantReadWriteLock();
		this.outstandingMessages = new ArrayList<Message>();
		try {
			this.oOutputStream = new ObjectOutputStream(new BufferedOutputStream(this.connectionToClient.getOutputStream()));
			this.oOutputStream.flush();
			this.oInputStream = new ObjectInputStream(new BufferedInputStream(this.connectionToClient.getInputStream()));
			}
		catch (IOException e) {
			System.out.println(e);
			}
	}

	/***
	 * A method used to get the user name of the Client who the ClientInteractionHandler is managing.
	 */
	public String getClientUsername() {
		return this.clientUsername;
		}

	/***
	 * A method to set the user name of the Client who the ClientInteractionHandler is managing.
	 */
	 
	public void setClientUsername(String username) {
		this.clientUsername = username;
		}

	/***
	 * A method used to retrieve a message from the Client through a Socket.
	 * If it doesn't work then it catches an IOException
	 */
	 
	public Message getMessageFromClient() {
		Message message = null;
		try {
			MessageID messageID = ((MessageID) this.oInputStream.readUnshared());
			String sourceName = this.oInputStream.readUTF();
			String destinationName = this.oInputStream.readUTF();
			Object data = this.oInputStream.readUnshared();
			message = new Message(messageID, sourceName, destinationName, data);

			} catch (IOException | ClassNotFoundException e) {
				System.out.println(e);
				}
			return message;
		}

	/***
	 * A method used to send a message to the Client through a Socket.
	 */
	 
	public void sendMessageToClient(Message message) {
		try {

			this.oOutputStream.writeUnshared(message.getMessageID());
			this.oOutputStream.writeUTF(message.getSourceName());
			this.oOutputStream.writeUTF(message.getDestinationName());
			this.oOutputStream.writeUnshared(message.getData());
			this.oOutputStream.flush();
			} catch (IOException e) {
				System.out.println(e);
				}
		}

	public void addMessageToOutstandingMessages(Message message) {
		try {
		this.outstandingMessagesLock.writeLock().lock();
		this.outstandingMessages.add(message);
		}
		finally {
		this.outstandingMessagesLock.writeLock().unlock();
		}
		}

	private void deleteMessageFromOutstandingMessages(String sourceName, String destinationName) {
		try {
			this.outstandingMessagesLock.writeLock().lock();
			for(Message m: this.outstandingMessages)
				if(m.getSourceName().equals(sourceName) && m.getDestinationName().equals(destinationName)) {
					this.outstandingMessages.remove(m);
					break;
					}
			}
		finally {
			this.outstandingMessagesLock.writeLock().unlock();
			}
		}

	public Message getMessageFromOutstandingMessages(String sourceName, String destinationName) {
		Message returnM = null;
		try {
			this.outstandingMessagesLock.writeLock().lock();
			for(Message m : this.outstandingMessages)
				if(m.getSourceName().equals(sourceName) && m.getDestinationName().equals(destinationName)) {
					returnM = m;
					this.outstandingMessages.remove(m);
					break;
					}
			}
		finally {
			this.outstandingMessagesLock.writeLock().unlock();
		}
		return returnM;

		}

	/***
	 * A method used to transfer a Message from one Client's connection to another Client's connection.
	 */
	private void transferMessageToConnection(Message message, ClientInteractionHandler clientConnection) {
		clientConnection.sendMessageToClient(message);
		}

	private void storeMessageinConnectionOutStandingMessages(Message message, ClientInteractionHandler clientConnection) {
		clientConnection.addMessageToOutstandingMessages(message);
		}


	/***
	 * A method used to send all online Client's user names to the Client.

	 */
	private ArrayList<String> getAllOnlineClientDetails(String currentUsername) {
		ArrayList<String> onlineClientUsernames = new ArrayList<String>();

		try {
		currentConnectionsLock.readLock().lock();
		for(ClientInteractionHandler c: currentConnections)
			if(!c.getClientUsername().equals(currentUsername))
				onlineClientUsernames.add(c.getClientUsername());

		}
		finally {
			currentConnectionsLock.readLock().unlock();
		}
		return onlineClientUsernames;
		}

	@Override
	public void run() {
		// When connecting for first time user's Login Details Must be Checked
		
		Message input = this.getMessageFromClient();
		Message output;
		
		// If incorrect check untill the correct details are supplied
		
		boolean isCorrect = checkUserCredentials(input.getSourceName(), input.getData().toString());
		while(!isCorrect) {
			
			
			System.out.println("===========================================\n"
					+ "System Notice - Warning: " + input.getSourceName() + " Entered incorrect Client details."
							+ "\n==========================================");
		
			output = new Message(MessageID.REGISTRATION_RESPONSE, Server.SERVER_NAME, input.getSourceName(), isCorrect);
			this.sendMessageToClient(output);


			input = this.getMessageFromClient();
			isCorrect = checkUserCredentials(input.getSourceName(), input.getData().toString());
			}
			
	
		this.setClientUsername(input.getSourceName());

		System.out.println("============================================\n"
				+ "System Notice - " + input.getSourceName() + " Logged In with correct Client Credentials."
						+ "\n============================================");
		output = new Message(MessageID.REGISTRATION_RESPONSE, Server.SERVER_NAME, input.getSourceName(), isCorrect);
		this.sendMessageToClient(output);

		while(!this.connectionToClient.isClosed()) {

			input = this.getMessageFromClient();


			// based on the Message ID different actions have to be performed.
			switch(input.getMessageID()) {

				case ONLINE_CLIENTS_REQUEST: {
					output = new Message(MessageID.ONLINE_CLIENTS_RESPONSE, Server.SERVER_NAME, input.getSourceName(),
							getAllOnlineClientDetails(this.getClientUsername()));
					this.sendMessageToClient(output);
					System.out.println("------------------------------------------\n"
							+ "System Action - Sent Online Client Usernames to " + this.getClientUsername()
							+ "\n------------------------------------------");
					break;
					}
					
				// Text Message sent to the Server:
				
				case TEXT_TRANSFER_REQUEST: {
					if(checkOnline(input.getDestinationName())) {
						
						output = new Message(MessageID.TEXT_TRANSFER_RECEIPT, input.getSourceName(),
											input.getDestinationName(), input.getData());
						
						this.transferMessageToConnection(output, getOnlineClient(input.getDestinationName()));
						}
					break;
					}
					
					
				// Image sent to the Server
				
				case IMAGE_TRANSFER_REQUEST: {
					if(checkOnline(input.getDestinationName())) {
						//make message to ask client if they would like to receive the Image.
						Message imageMessage = new Message(MessageID.IMAGE_TRANSFER_RECEIPT, input.getSourceName(),
								input.getDestinationName(), input.getData());
						this.storeMessageinConnectionOutStandingMessages(imageMessage, getOnlineClient(input.getDestinationName()));

						output = new Message(MessageID.IMAGE_TRANSFER_CONFIRMATION_REQUEST, input.getSourceName(),
											input.getDestinationName(), (input.getSourceName() +
													ClientInteractionHandler.IMAGE_CONFIRMATION_REQUEST_TEXT));
						this.transferMessageToConnection(output, getOnlineClient(input.getDestinationName()));
						}
					break;
					
					}

					case AUDIO_TRANSFER_REQUEST:
					{
						if(checkOnline(input.getDestinationName()))
						{
							//This message will be sent to the receiving client to ask if they would like to receive the file
							Message audioMessage = new Message(MessageID.AUDIO_TRANSFER_RECEIPT, input.getSourceName(),
								input.getDestinationName(), input.getData());
							this.storeMessageinConnectionOutStandingMessages(audioMessage, getOnlineClient(input.getDestinationName()));
							
							
							
							output = new Message(MessageID.AUDIO_TRANSFER_CONFIRMATION_REQUEST, input.getSourceName(),
								input.getDestinationName(), (input.getSourceName() + ClientInteractionHandler.IMAGE_CONFIRMATION_REQUEST_TEXT));
							this.transferMessageToConnection(output, getOnlineClient(input.getDestinationName()));

							
						}
						break;
					}

				case IMAGE_TRANSFER_CONFIRMATION_RESPONSE: {
					if((boolean)input.getData()) {

						output = this.getMessageFromOutstandingMessages(input.getDestinationName(), input.getSourceName());
						this.sendMessageToClient(output);
						}
					else {
						this.deleteMessageFromOutstandingMessages(input.getDestinationName(), input.getSourceName());
					}
					break;
					}

					case AUDIO_TRANSFER_CONFIRMATION_REQUEST:
					{
						
						if((boolean)input.getData())
						{
							
							output = this.getMessageFromOutstandingMessages(input.getDestinationName(), input.getSourceName());
							this.sendMessageToClient(output);
						}else
						{
							this.deleteMessageFromOutstandingMessages(input.getDestinationName(), input.getSourceName());
						}
						break;
					}

				case TEXT_SEND_TO_ALL_REQUEST: {
					for(ClientInteractionHandler client: currentConnections) {
						if(!client.getClientUsername().equals(input.getSourceName())) {
							output = new Message(MessageID.TEXT_SEND_TO_ALL_RECEIPT, input.getSourceName(),
												client.getClientUsername(), input.getData());
							this.transferMessageToConnection(output, client);
							}
						}
					break;
					}
				case IMAGE_SEND_TO_ALL_REQUEST: {
					for(ClientInteractionHandler client: currentConnections) {
						if(!client.getClientUsername().equals(input.getSourceName())) {
							Message imageMessage = new Message(MessageID.IMAGE_TRANSFER_RECEIPT, input.getSourceName(),
									client.getClientUsername(), input.getData());
							this.storeMessageinConnectionOutStandingMessages(imageMessage, client);

							output = new Message(MessageID.IMAGE_TRANSFER_CONFIRMATION_REQUEST, input.getSourceName(),
													client.getClientUsername(), (input.getSourceName() +
													ClientInteractionHandler.IMAGE_CONFIRMATION_REQUEST_TEXT));
							this.transferMessageToConnection(output, client);
							}
						}
					break;
					}
				case CLOSE_CONNECTION : {

					output = new Message(MessageID.CLOSE_CONNECTION,
							Server.SERVER_NAME, this.clientUsername, "");
					this.sendMessageToClient(output);
					System.out.println("=============================================\n"
										+ "System Notice - " + this.clientUsername + " closed connection"
									+ "\n============================================");
					return;
					}
				// Other Message Code i.e. the Message is not Meant for the Server
				default : {
					System.out.println("=============================================\n"
							+ "System Notice - Warning Message: Unknown Message Received"
							+ "\n===========================================");
					break;
					}
				}
			}

		}






}





}
