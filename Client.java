import java.io.*;
import java.net.*;

import javax.crypto.spec.SecretKeySpec;

/**
 * Client program.  Connects to the server and sends text accross.
 */

public class Client 
{
    private Socket sock;  //Socket to communicate with.
	private boolean debug;
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args)
    {
	if (args.length < 2 || 
		args.length > 3 || 
		!args[2].equals("debug")) 
	{
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("hostname is a string identifying your server");
	    System.out.println ("port is a positive integer identifying the port to connect to the server");
	    return;
	}

	try {
	    Client c = new Client (args[0], Integer.parseInt(args[1]));
	    if (args.length == 3 && args[2].equals("debug"))
	    	c.setDebug(true);
	}
	catch (NumberFormatException e) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("Second argument was not a port number");
	    return;
	}
    }
	    
    public void debug(String s)
    {
    	if (debug)
    		return;
    	System.out.println("[Client]" + s);
    }
    
    public void setDebug(boolean d)
    {
    	debug = d;
    }
    /**
     * Constructor, in this case does everything.
     * @param ipaddress The hostname to connect to.
     * @param port The port to connect to.
     */
    public Client (String ipaddress, int port)
    {
	/* Allows us to get input from the keyboard. */
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	String userinput;
	PrintWriter out;
	DataOutputStream out_stream;
	DataInputStream in_stream;
		
	/* Try to connect to the specified host on the specified port. */
	try {
	    sock = new Socket (InetAddress.getByName(ipaddress), port);
	}
	catch (UnknownHostException e) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("First argument is not a valid hostname");
	    return;
	}
	catch (IOException e) {
	    System.out.println ("Could not connect to " + ipaddress + ".");
	    return;
	}
	
	/* Status info */
	System.out.println ("Connected to " + sock.getInetAddress().getHostAddress() + " on port " + port);
		
	try {
	    out = new PrintWriter(sock.getOutputStream());
	    in_stream = new DataInputStream(sock.getInputStream());
	    out_stream = new DataOutputStream(sock.getOutputStream());
	}
	catch (IOException e) {
	    System.out.println ("Could not create output stream.");
	    return;
	}
		
	/* Wait for the user to type stuff. */
	try {
		/* Tricky bit.  Since Java does short circuiting of logical 
		 * expressions, we need to checkerror to be first so it is always 
		 * executes.  Check error flushes the outputstream, which we need
		 * to do every time after the user types something, otherwise, 
		 * Java will wait for the send buffer to fill up before actually 
		 * sending anything.  See PrintWriter.flush().  If checkerror
		 * has reported an error, that means the last packet was not 
		 * delivered and the server has disconnected, probably because 
		 * another client has told it to shutdown.  Then we check to see
		 * if the user has exitted or asked the server to shutdown.  In 
		 * any of these cases we close our streams and exit.
		 */
		debug("Enter key: ");
	    String key = stdIn.readLine();	    
	    SecretKeySpec keySpec = CryptoUtilities.key_from_seed(key.getBytes());
	    
		debug("Enter source filename: ");
	    String src_file = stdIn.readLine();
	    	    
		debug("Enter destination filename: ");
	    String dest_file = stdIn.readLine();
	    	    
		debug("Sending filesize: ");
		FileInputStream fs = new FileInputStream(src_file);

		int filesize = fs.available();
		byte[] data = new byte[filesize];
		fs.read(data);
		
		CryptoUtilities.sendEncrypted(dest_file.getBytes(), keySpec, out_stream);
		CryptoUtilities.sendEncrypted(Integer.toString(filesize).getBytes(), keySpec, out_stream);
		CryptoUtilities.sendEncrypted(data, keySpec, out_stream);
		
		debug("Sending filename: ");
		debug("Waiting for confirmation from server: ");
		byte[] response = CryptoUtilities.receiveEncrypted(keySpec, in_stream);
		debug(new String(response));
		
		//if ((out.checkError()) || (userinput.compareTo("exit") == 0) || (userinput.compareTo("die") == 0)) {			
		if (out.checkError()) {
		    System.out.println ("Client exiting.");
		    stdIn.close ();
		    out.close ();
		    sock.close();
		    return;
		}
	} catch (Exception e) {
	    System.out.println ("Could not read from input.");
	    return;
	}		
    }
}
