import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

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
			(args.length > 3 && !args[2].equals("debug"))) 
		{
		    System.out.println ("Usage: java Client hostname port#");
		    System.out.println ("hostname is a string identifying your server");
		    System.out.println ("port is a positive integer identifying the port to connect to the server");
		    return;
		}
	
		try {
			Client c;
		    if (args.length == 3 && args[2].equals("debug"))
		    	c = new Client(args[0], Integer.parseInt(args[1]), true);
		    else
		    	c = new Client(args[0], Integer.parseInt(args[1]), false);
		}
		catch (NumberFormatException e) {
		    System.out.println ("Usage: java Client hostname port#");
		    System.out.println ("Second argument was not a port number");
		    return;
		}
    }

    public void printDebug(String s)
    {
    	if (!debug)
    		return;
    	System.out.println("[Client]" + s);
    }
    
    /**
     * Constructor, in this case does everything.
     * @param ipaddress The hostname to connect to.
     * @param port The port to connect to.
     */
    public Client (String ipaddress, int port, boolean d)
    {
    	debug = d;
		/* Allows us to get input from the keyboard. */
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out;
		DataOutputStream out_stream;
		DataInputStream in_stream;
			
		/* Try to connect to the specified host on the specified port. */
		try {
		    sock = new Socket (InetAddress.getByName(ipaddress), port);
		}
		catch (UnknownHostException e) {
		    System.out.println ("Usage: java Client hostname port# [debug]");
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
			System.out.println("Enter key: ");
		    String key = stdIn.readLine();	    
		    SecretKeySpec key_spec = CryptoUtilities.key_from_seed(key.getBytes());
		    
		    System.out.println("Enter source filename: ");
		    String src_file = stdIn.readLine();
		    	    
		    System.out.println("Enter destination filename: ");
		    String dest_name = stdIn.readLine();
		    
			printDebug("Sending destination filename...");
			FileInputStream fs = new FileInputStream(src_file);
			int filesize = fs.available();
			byte[] data = new byte[filesize];
			fs.read(data);
			fs.close();
			
			// [filename size + filename + data size + data bytes]
			byte[] out_bytes = new byte[4 + dest_name.length() + 4 + data.length];
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.putInt(dest_name.length());
			bb.flip();			
			bb.get(out_bytes, 0, 4);
			System.arraycopy(dest_name.getBytes(), 0, out_bytes, 4, dest_name.length());
			bb.clear();
			bb.putInt(data.length);
			bb.flip();
			bb.get(out_bytes, 4 + dest_name.length(), 4);
			System.arraycopy(data, 0, out_bytes, 4 + dest_name.length() + 4, data.length);
			CryptoUtilities.sendEncrypted(out_bytes, key_spec, out_stream, CryptoUtilities.MSG_FILE, debug);
						
			System.out.println("Waiting for confirmation from server...");
			String result = in_stream.readUTF();
			System.out.println("Recieved result: " + result);
			
			if (out.checkError()) {
			    System.out.println ("Client exiting.");
			    stdIn.close ();
			    out.close ();
			    sock.close();
			    return;
			}
		} catch (Exception e) {
		    System.out.println ("Could not read from input.");
		    System.out.println (e.getMessage());
		    return;
		}		
    }
}
