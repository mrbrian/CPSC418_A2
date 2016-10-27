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
    private boolean debug;	// toggle debug messages
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args)
    {
		if (args.length < 2 || 
			args.length > 3 || 
			(args.length >= 3 && !args[2].equals("debug"))) 
		{
		    System.out.println ("Usage: java Client hostname port# [debug]");
		    System.out.println ("hostname is a string identifying your server");
		    System.out.println ("port is a positive integer identifying the port to connect to the server");
		    System.out.println ("[optional] specifying debug will enabled protocol debug messages");
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
		    System.out.println ("Usage: java Client hostname port# [debug]");
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
			
		try {
			// Prompt for key
			System.out.println("Enter key: ");
		    String key = stdIn.readLine();	    
		    SecretKeySpec key_spec = CryptoUtilities.key_from_seed(key.getBytes());
		    
		    // Prompt for file to send
			System.out.println("Enter source filename: ");
		    String src_file = stdIn.readLine();
		    	    
		    // Prompt for destination name			
		    System.out.println("Enter destination filename: ");
		    String dest_name = stdIn.readLine();

		    // Read file data			
			FileInputStream fs = new FileInputStream(src_file);
			int filesize = fs.available();
			byte[] data = new byte[filesize];
			fs.read(data);
			fs.close();
			
			// Allocate byte array for outgoing message
			// [(4 bytes)filename size + (x bytes)filename + (4 bytes)data size + (y bytes)data bytes]
			byte[] out_bytes = new byte[4 + dest_name.length() + 4 + data.length];
			
			// Assemble outgoing message 
			// add filename size in bytes
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.putInt(dest_name.length());
			bb.flip();			
			bb.get(out_bytes, 0, 4);
			
			// add destination filename in bytes
			System.arraycopy(dest_name.getBytes(), 0, out_bytes, 4, dest_name.length());
			bb.clear();		
			
			// add data size in bytes
			bb.putInt(data.length);
			bb.flip();
			bb.get(out_bytes, 4 + dest_name.length(), 4);

			// add data bytes
			System.arraycopy(data, 0, out_bytes, 4 + dest_name.length() + 4, data.length);
			
			// send file message to client
			CryptoUtilities.sendEncrypted(out_bytes, key_spec, out_stream, debug);
						
			printDebug("Waiting for confirmation from server...");
			while (in_stream.available() <= 0)	// wait for a message 
			{
			}
			
			String result = in_stream.readUTF();
			printDebug("Recieved result: " + result);
			
			if (out.checkError()) {
			    System.out.println ("Client exiting.");
			    stdIn.close ();		// close sockets and streams
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
