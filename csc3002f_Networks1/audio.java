import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.*;   // import JMF classes
import javax.swing.JFileChooser;
 
/**
*
* @author BUDDHIMA
*/
 
public class audio {
 
private Player audio = null;
 
public audio(URL url) {
 
try {
 
//MediaLocator ml=new MediaLocator(url);
 
audio = Manager.createPlayer(url);
 
} catch (Exception ex) {
 
System.out.println(ex);
 
}
 
}
 
public audio(File file) throws MalformedURLException {
 
this(file.toURI().toURL());
 
}
 
public void play() {
 
audio.start(); // start playing
 
}
 
public void stop() {
 
audio.stop();  //stop playing
 
audio.close();
 
}
 
public static void main(String[] args) {
 
try {
 
// TODO code application logic here
 
JFileChooser fc = new JFileChooser();
 
fc.showOpenDialog(null);
 
File file = fc.getSelectedFile();
 
SimpleAduioPlayer sap = new SimpleAduioPlayer(file);
 
sap.play();
 
//sap.stop();
 
} catch (MalformedURLException ex) {
 
System.out.println(ex);
 
}
 
}
 
}
