

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
/***
 * A class to display Image Messages sent to the Client.
 * @author Pieter Janse van Rensburg(jnspie007@myuct.ac.za)
 * @version 05/04/2017
 * @since 29/03/2017
 *
 */
public class ClientImageDisplayer extends JFrame implements Runnable{

	// static variables
	private static final long serialVersionUID = 1L;
	// instance variables
	private ImageIcon displayImage;
	
	/***
	 * The constructor of the ClientImageDisplayer Class. Creates and shows the JFrame to display the Image.
	 * @param displayImage The ImageIcon to be displayed.
	 */
	public ClientImageDisplayer(ImageIcon displayImage) {
		this.displayImage = displayImage;
		JLabel imageDisplay = new JLabel(this.displayImage);
		imageDisplay.setBounds(0, 0, 500, 500);
		imageDisplay.setVisible(true);
		this.add(imageDisplay);
		
		// set up JFrame
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Image Message");
		this.setSize(500, 500);
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		toFront();
		pack();
		}
	
	/***
	 * The main method used to test if the class is working as intended.
	 * @param args A String Array of command-line arguments.
	 */
	public static void main(String args[]) {
		
		try {
			new ClientImageDisplayer(new ImageIcon(ImageIO.read(new File("sun.jpg"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}


	@Override
	public void run() {
			
	}
}
