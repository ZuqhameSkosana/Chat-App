import java.io.*;
//import sun.audio.*;
import java.net.MalformedURLException;
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

public class FileW implements Serializable{
    private String filePath;
    private DefaultListModel mod = new DefaultListModel();
    static Data data=new Data();
    //static Message msg=new Message();
    

   public FileW(/*String filePath*/) //throws IOException
   {
     
     //this.filePath = filePath;
    
   }
   
   public static void save() {
       // Data data = (Data) mod.getElementAt(list.getSelectedIndex());
        
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

    }
}
