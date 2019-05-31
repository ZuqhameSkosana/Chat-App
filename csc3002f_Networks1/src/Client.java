

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.*;
import java.io.*;

public class Client {


	private final static int INCOMING_CONNECTION_PORT = 12000;  //port
	private final static String SERVER_NAME = "Server";

	private String username;
	private String password;
	private ReentrantReadWriteLock onlineClientNamesLock;
	private ArrayList<String> onlineClientNames;
	private ServerInteractionHandler serverConnectionHandler;
	private Scanner input;
	private volatile boolean isConfirming;
	private volatile boolean enteringInput;


	public Client() {
		this.onlineClientNamesLock = new ReentrantReadWriteLock();
		this.onlineClientNames = new ArrayList<String>();
		this.isConfirming = false;
		this.enteringInput = true;
		String ip_addr = JOptionPane.showInputDialog(null, "Enter IP address");
		//System.out.println("Please enter the IP/DNS address of the Server");
		input = new Scanner(System.in);
		//String serverIP = input.nextLine();
		this.setupConnectToServer(ip_addr);
		inputUserCredentials();

		}

	/***
	 *  method to uniquely identify users
	 */
	public String getUsername() {
		return this.username;
		}


	public ServerInteractionHandler getServerInteractionHandler() {
		return this.serverConnectionHandler;
		}

	/***
	 * method to get the number of names from online clients.

	 */
	public int getOnlineClientNamesSize() {
		int size = -1;
		try{
			
			this.onlineClientNamesLock.readLock().lock();
			size = this.onlineClientNames.size();
		}
		finally {
		
			this.onlineClientNamesLock.readLock().unlock();
		}
		return size;
		}


	public String getOnlineClientNamesToString() {
		String temp = "";
		try {
		
			this.onlineClientNamesLock.readLock().lock();
			for(String s: this.onlineClientNames)
				temp += s + "\n";
		}
		finally {
			
			this.onlineClientNamesLock.readLock().unlock();
		}
		return temp;
		}

	/***
	 * A method used to update the List of Online Clients.

	 */
	public void setOnlineClientNames(ArrayList<String> onlineClientNames) {
		try {
			
			this.onlineClientNamesLock.writeLock().lock();
			this.onlineClientNames = onlineClientNames;
		}
		finally {
			
			this.onlineClientNamesLock.writeLock().unlock();
			}
		}

	//Scanner read from input line
	public Scanner getInput() {
		return this.input;
		}

	/***
	 * A method  to look if a Client with the given user name is online.

	 */
	public boolean containsOnlineClientName(String onlineClientName) {
		boolean contains = false;
		try {
			
			this.onlineClientNamesLock.readLock().lock();
			for(String s: this.onlineClientNames)
				if(s.equals(onlineClientName)){
					contains = true;
					break;
					}
		}
		finally {
		
			this.onlineClientNamesLock.readLock().unlock();
		}
		return contains;
		}

	/***
	 * A method used by the Client to establish a Connections to the Server via a Socket
	 */
	private void setupConnectToServer(String serverIP) {
		try {
			this.serverConnectionHandler = new ServerInteractionHandler(new Socket(serverIP, Client.INCOMING_CONNECTION_PORT));
			System.out.println("=====================================================================\n"
					+ "System Notice - Client has Succesfully connected to the Server"
					+ "\n=====================================================================");
			}
		catch (IOException e) {
			System.out.println(e);
			}

		}




	private void inputUserCredentials() {
		this.username = "";
		this.password = "";

		do {

			username = JOptionPane.showInputDialog(null, "Enter your username");
		
			this.password = JOptionPane.showInputDialog(null,"Enter Password");
			} while(!checkCredentials());

		}

	 
	private boolean checkCredentials() {
		// makes message to send to server
		Message output = new Message(MessageID.REGISTRATION_REQUEST, this.username, Client.SERVER_NAME, this.password);
		serverConnectionHandler.sendMessageToServer(output);
		// awaits for input from server
		Message input = serverConnectionHandler.getMessageFromServer();
		
		if(((boolean)input.getData()))
			return true;
		// else return false
		System.out.println("===================================================================\n"
				+ "System Notice - Login Failed: The Client Details entered were incorrect."
				+ "\n=====================================================================");
		return false;
		}

	
	public void startServerInteractionHandler() {
		new Thread(this.serverConnectionHandler).start();
		}

	public boolean getIsConfirming() {
		return this.isConfirming;
		}


	public void setIsConfirming(boolean isConfirming) {
		while(enteringInput);

		this.isConfirming = isConfirming;
		}


	public boolean getEnteringInput() {
		return this.enteringInput;
		}

	public void setEnteringInput(boolean enteringInput) {
		while(isConfirming);

		this.enteringInput = enteringInput;
		}
		
		private static ObjectOutputStream out;
		
		
		static FileW fileW=new FileW();
		static FileInputStream in =null;
		 static File f=null;
		
		public static File selectF(){

			  try{
				JFileChooser ch = new JFileChooser();
                 int c = ch.showOpenDialog(null);
                if (c == JFileChooser.APPROVE_OPTION) {
               f = ch.getSelectedFile();
             
                in = new FileInputStream(f);
                byte b[] = new byte[in.available()];
                in.read(b);
     
                String nameOfFile= JOptionPane.showInputDialog(null, "name of file to send: ");
                fileW.data.setName(nameOfFile);
                fileW.data.setFile(b);
                out.writeObject(fileW.data);
                out.flush();
               
				
				}
				
			}catch(Exception e){
				}
				return f;
			
			}
		
		public static String returnPath(){
			 JFileChooser filechoser = new JFileChooser();
             filechoser.showOpenDialog(null);
             
             String something=filechoser.getSelectedFile().toString();
             System.out.println(something);
			
			return something;
						
			}
			
			
			
		

	/***
	 * The main method of the Client Class.
	 * @param args A String Array containing command-line arguments.
	 */
	public static void main(String args[]) {

		// starts a new Client
		Client thisClient = new Client();
		// starts ServerInteractionHandler in new Thread
		thisClient.startServerInteractionHandler();
		System.out.println("===================================================================\n" +
		"System Notice - " + thisClient.getUsername() + ", you have successfully logged in."
				+ "\n===================================================================");

		Scanner input = thisClient.getInput();
		String choice = "";
		while(!choice.equalsIgnoreCase("Exit")) {
			thisClient.setEnteringInput(true);
			
			System.out.println("Please Enter a number or Exit\n" +
					   "1. Send Message\n" +
					   "2. Send Image\n" +
					   "3. Send Broadcast Message\n" +
					   "4. Send Broadcast Image\n" +
						 "5. Send Audio file"+
					   "Type \"Exit\" to Logout");
					   
					  
					   
			choice = input.nextLine();
			switch(choice) {
				// sending  text message to another client (1-to-1)
				case "1": {
					thisClient.setEnteringInput(false);
					thisClient.getServerInteractionHandler().updateOnlineClients();
					while(!thisClient.getServerInteractionHandler().getUpdated());

					// check online clients
					thisClient.getServerInteractionHandler().setUpdated(false);
					thisClient.setEnteringInput(true);
					System.out.println("Online Clients(" + thisClient.getOnlineClientNamesSize() + ") :\n"
							+ "-----------------------------------------\n" +
							thisClient.getOnlineClientNamesToString() +
		 "-----------------------------------------\nEnter a Client's name to Send the Message to.");
					
					String receivingClient = input.nextLine();
					thisClient.setEnteringInput(false);
					// check if they are online after choosing that client
					if(thisClient.containsOnlineClientName(receivingClient)) {
						thisClient.setEnteringInput(true);
						System.out.println("Enter the Message to Send");
						
						String message = input.nextLine();
						thisClient.setEnteringInput(false);
						
						Message output = new Message(MessageID.TEXT_TRANSFER_REQUEST, thisClient.getUsername(),
								receivingClient, message);
						thisClient.getServerInteractionHandler().sendMessageToServer(output);
						}
						
					else {
						System.out.println("=====================================================================\n"
								+ "System Notice - The Client whose name has been entered is not online. Going Back to Main Menu."
								+ "\n=======================================================================");
						}
						
					break;
					}
				// sending image to another client
				case "2": {
					thisClient.setEnteringInput(false);
					thisClient.getServerInteractionHandler().updateOnlineClients();
					while(!thisClient.getServerInteractionHandler().getUpdated());

					thisClient.getServerInteractionHandler().setUpdated(false);
					thisClient.setEnteringInput(true);
					System.out.println("Online Clients(" + thisClient.getOnlineClientNamesSize() + ") :\n"
							+ "--------------------------------------------\n" +
							thisClient.getOnlineClientNamesToString() +
		 "---------------------------------------------\nPlease Enter a Client's name to Send the Image to.");
					// get client user name to send message to
					String receivingClient = input.nextLine();
					thisClient.setEnteringInput(false);
					// check if they are online
					if(thisClient.containsOnlineClientName(receivingClient)) {
						boolean loaded = false;
						ImageIcon image = null;
						String displayM = "";
						
						String m=returnPath();     
                        
						
						// load image into ImageIcon
						while(!loaded) {
							try {
								thisClient.setEnteringInput(true);
								//System.out.println(displayM);
								System.out.println(m);
								//String imageURL = input.nextLine();
								thisClient.setEnteringInput(false);
							//	image = new ImageIcon(ImageIO.read(new File(imageURL)));
							    image = new ImageIcon(ImageIO.read(new File(m)));
								loaded = true;
								
								}
							catch (IOException e) {
								System.out.println("Specified Image failed to load." + e);
								displayM = "Please re-select the Image File to Send";
						}
							}
						// send message to server
						Message output = new Message(MessageID.IMAGE_TRANSFER_REQUEST, thisClient.getUsername(),
								receivingClient, (Object)image);
						thisClient.getServerInteractionHandler().sendMessageToServer(output);
						}
					else {
						System.out.println("*********************************************************************\n"
								+ "System Notice - The Client entered is not online. Going Back to Main Menu."
								+ "\n*********************************************************************");
						}
					break;
					}
				
				case "3": {
					thisClient.setEnteringInput(false);
					thisClient.getServerInteractionHandler().updateOnlineClients();
					while(!thisClient.getServerInteractionHandler().getUpdated());

					thisClient.getServerInteractionHandler().setUpdated(false);
					thisClient.setEnteringInput(true);
					System.out.println("Please enter the Text Message to Send to Everyone");
					
					String message = input.nextLine();
					thisClient.setEnteringInput(false);
					
					Message output = new Message(MessageID.TEXT_SEND_TO_ALL_REQUEST, thisClient.getUsername(),
								"All", message);
					thisClient.getServerInteractionHandler().sendMessageToServer(output);

					break;
					}
				// send image message to all client
				case "4" : {
					thisClient.setEnteringInput(false);
					thisClient.getServerInteractionHandler().updateOnlineClients();
					while(!thisClient.getServerInteractionHandler().getUpdated());
					// update list of online clients
					thisClient.getServerInteractionHandler().setUpdated(false);

					boolean loaded = false;
					ImageIcon image = null;
					
					String displayM = "Please enter the Location of the Image File to Send to Everyone";
					
					
					
					//String m=returnPath(); 
					
					
					String mo=returnPath(); 
					while(!loaded) {
						try {
							thisClient.setEnteringInput(true);
							
							thisClient.setEnteringInput(false);
							image = new ImageIcon(ImageIO.read(new File(mo)));
							loaded = true;
							}
						catch (IOException e) {
							System.out.println("The Specified Image could not be loaded." + e);
							displayM = "Please re-enter the Location of the Image File to Send";
							}
						}
						// send message to server
						Message output = new Message(MessageID.IMAGE_SEND_TO_ALL_REQUEST, thisClient.getUsername(),
								"All", (Object)image);
						thisClient.getServerInteractionHandler().sendMessageToServer(output);

					break;
					}

					//send audio to another client
					
				case "5": {
					thisClient.setEnteringInput(false);
					thisClient.getServerInteractionHandler().updateOnlineClients();
					while(!thisClient.getServerInteractionHandler().getUpdated());

					// update the online client list
					thisClient.getServerInteractionHandler().setUpdated(false);
					thisClient.setEnteringInput(true);
					System.out.println("Online Clients(" + thisClient.getOnlineClientNamesSize() + ") :\n"
							+ "-----------------------------------------\n" +
							thisClient.getOnlineClientNamesToString() +
		 "-------------------------------------------\nPlease Enter a Client's name to Send the File to.");
					// get client user name to send message to
					String receivingClient = input.nextLine();
					thisClient.setEnteringInput(false);
					// check if they are online
					if(thisClient.containsOnlineClientName(receivingClient)) {
						boolean loaded = false;
						//ImageIcon image = null;
						File f2=null;
						String displayM = "";
						File f3=selectF();
						
						//String m=returnPath();     
                        
						
						// load image into ImageIcon
						while(!loaded) {
							try {
								thisClient.setEnteringInput(true);
								
								thisClient.setEnteringInput(false);
						
							    f2=f3;
							
								loaded = true;
								
								}
							catch (Exception e) {
								System.out.println("The Specified Image could not be loaded." + e);
								displayM = "Please re-select the Image File to Send";
						}
							}
						// send message to server
						Message output = new Message(MessageID.AUDIO_TRANSFER_REQUEST, thisClient.getUsername(),
								receivingClient, (Object)f2);
						thisClient.getServerInteractionHandler().sendMessageToServer(output);
						}
					else {
						System.out.println("*********************************************************************\n"
								+ "System Notice - Client entered is not online. Going Back to Main Menu."
								+ "\n*********************************************************************");
						}
					break;
					}
					
					
				// exit
				case "Exit" : {
					thisClient.setEnteringInput(false);
					try {
						// tell the server that the connection is closing
						thisClient.getServerInteractionHandler().sendMessageToServer(new Message(MessageID.CLOSE_CONNECTION,
								thisClient.getUsername(), Client.SERVER_NAME, ""));
					}
					catch (Exception e) {
						System.out.println(e);
						}
					return;
					}
				default : {
					System.out.println("Sorry the input was correct. Please enter your choice again. (1,2,3,Exit)");
					break;
					}
				}



			}
		}



private class ServerInteractionHandler implements Runnable {
	// instance variables
	private Socket connectionToServer;
	private ObjectInputStream oInputStream;
	private ObjectOutputStream oOutputStream;
	private volatile boolean updated;


	public ServerInteractionHandler(Socket connectionToServer) {
		this.connectionToServer = connectionToServer;
		this.updated = false;
		// initialize input and output streams.
		try {
			this.oOutputStream = new ObjectOutputStream(new BufferedOutputStream(this.connectionToServer.getOutputStream()));
			this.oOutputStream.flush();
			this.oInputStream = new ObjectInputStream(new BufferedInputStream(this.connectionToServer.getInputStream()));
		} catch (IOException e) {
			System.out.println(e);
			}
		}

	/***
	 * A method to set the value of updated.
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
		}


	public boolean getUpdated() {
		return this.updated;
		}

	/***
	 * method used to send a message to the Server.

	 */
	public void sendMessageToServer(Message message) {
		try {
			// write to stream
			this.oOutputStream.writeUnshared(message.getMessageID());
			this.oOutputStream.writeUTF(message.getSourceName());
			this.oOutputStream.writeUTF(message.getDestinationName());
			this.oOutputStream.writeUnshared(message.getData());
			// flush data across socket for input stream
			this.oOutputStream.flush();
			}
		catch (IOException e) {
			System.out.println(e);
			}
		}

	/***
	 * A method used to receive a message from the Server.
	 */
	public Message getMessageFromServer() {
		Message message = null;
		try {
			// read the message variable by variable since Message isn't serializable.
			MessageID messageID = ((MessageID)this.oInputStream.readUnshared());
			String sourceName = this.oInputStream.readUTF();
			String destinationName = this.oInputStream.readUTF();
			Object data = this.oInputStream.readUnshared();
			message = new Message(messageID, sourceName, destinationName, data);

			}
		catch (IOException | ClassNotFoundException e) {
			System.out.println(e);
			}
		return message;
		}


	public void updateOnlineClients() {
		Message output = new Message(MessageID.ONLINE_CLIENTS_REQUEST, getUsername(), Client.SERVER_NAME,
				"update");
		this.sendMessageToServer(output);
		}


	public void closeConnectionToServer() {
		try {
			this.connectionToServer.close();
			}
		catch (IOException e) {
			System.out.println(e);
			}
		}

	@Override
	public void run() {
		Message input;
		// while the connection is open keep checking for input from the server
		while(!this.connectionToServer.isClosed()) {
			input = this.getMessageFromServer();
			switch(input.getMessageID()) {
				// received a text message for this client
				case TEXT_TRANSFER_RECEIPT: {
					// print out the text message
					System.out.println("---------------------------------------------\nText Message from " +
							input.getSourceName() + "(To You): " + input.getData().toString() +
							"\n---------------------------------------------");
					break;
					}
				// received an image message confirmation for this client
				case IMAGE_TRANSFER_CONFIRMATION_REQUEST: {
					String display = input.getData().toString();
					Scanner in = getInput();
					String choice = "";
					boolean retrieveImage = false;
					// asks the user if they want to download the image
					while(!(choice.equals("Yes") || choice.equals("No"))){
						// inform the user we are waiting for System.in to be free
						System.out.println("*********************************************************************\n"
										+ "System Notice : Type 2 and press enter. "
										+ "\n*********************************************************************");
						setIsConfirming(true);
						System.out.println(display);
						choice = in.nextLine();
						switch(choice) {
							// they want to view the image
							case "Yes": {
								setIsConfirming(false);
								retrieveImage = true;
								break;
								}
							// they dont want to view the image
							case "No": {
								setIsConfirming(false);
								retrieveImage = false;
								break;
								}
							// ask them to enter their option again
							default : {
								display = "Invalid Option, Please enter Yes or No";
								break;
								}
						}
					}

					// sends the confirmation to the server
					Message outMessage = new Message(MessageID.IMAGE_TRANSFER_CONFIRMATION_RESPONSE, getUsername(),
													input.getSourceName(), retrieveImage);
					this.sendMessageToServer(outMessage);

					break;
					}

					// received an image message confirmation for this client
					case AUDIO_TRANSFER_CONFIRMATION_REQUEST: {
						String display = input.getData().toString();
						Scanner in = getInput();
						String choice = "";
						boolean retrieveAudio = false;
						// asks the user if they want to download the image
						while(!(choice.equals("Yes") || choice.equals("No"))){
							// inform the user we are waiting for System.in to be free
							System.out.println("*********************************************************************\n"
											+ "System Notice : Waiting for Previous Input to Finish on System.in  -> Please press 2 and enter"
											+ "\n*********************************************************************");
							setIsConfirming(true);
							System.out.println(display);
							choice = in.nextLine();
							switch(choice) {
								// they want to get the audio file
								case "Yes": {
									setIsConfirming(false);
									retrieveAudio = true;
									break;
									}
								// they dont want to get the audio file
								case "No": {
									setIsConfirming(false);
									retrieveAudio = false;
									break;
									}
								// ask them to enter their option again
								default : {
									display = "Invalid, Please enter Yes or No";
									break;
									}
							}
						}

						// sends the confirmation to the server
						Message outMessage = new Message(MessageID.AUDIO_TRANSFER_CONFIRMATION_RESPONSE, getUsername(),
														input.getSourceName(), retrieveAudio);
						this.sendMessageToServer(outMessage);

						break;
						}

				// receive an image message
				case IMAGE_TRANSFER_RECEIPT : {
					System.out.println("*********************************************************************\n" +
										"System Notice : " + input.getSourceName() +
										" sent you an Image Opening in JFrame." +
										"\n*********************************************************************");
					// start a new JFrame in a new Thread to display the Image
					new Thread(new ClientImageDisplayer((ImageIcon)input.getData())).start();
					break;
					}

					//Receive audio file from a client
				
					case AUDIO_TRANSFER_RECEIPT : {
						System.out.println("*********************************************************************\n" +
											"System Notice : " + input.getSourceName() +
											" sent you an audio playing now." +
											"\n*********************************************************************");
						// Play the audio file
						//Media_Player player = (Media_Player)input.getData();
						
						try
						{
							//player.play_audio();
						}catch(Exception error)
						{
							System.out.println("Could not play sound due to :\n"+error);
						}
						break;
						}
						
						
				// receive a text message send to everyone
				case TEXT_SEND_TO_ALL_RECEIPT: {
					System.out.println("---------------------------------------------\nText Message from " +
							input.getSourceName() +" (To Everyone): " + input.getData().toString() +
							"\n---------------------------------------------");
					break;
					}
				// received a response to updating online clients' user names
				case ONLINE_CLIENTS_RESPONSE: {
					// update the array list
					setOnlineClientNames((ArrayList<String>)input.getData());
					// indicate it has been updated
					this.setUpdated(true);
					break;
					}
				// receive a message from the Server instructing the client to close it's socket
				case CLOSE_CONNECTION: {
					this.closeConnectionToServer();
					System.exit(0);
					return;
					}
				// unknown message identifier
				default: {
					System.out.println("*********************************************************************\n"
							+ "System Notice : Warning unknown Message Code"
							+ "\n*********************************************************************");
					break;
					}

				}
			}


		}




}



}
