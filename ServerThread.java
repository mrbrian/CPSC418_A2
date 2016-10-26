import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

import javax.crypto.spec.SecretKeySpec;

/**
 * Thread to deal with clients who connect to Server.  Put what you want the
 * thread to do in it's run() method.
 */

public class ServerThread extends Thread
{
	private boolean debug;
    private Socket sock;  //The socket it communicates with the client on.
    private Server parent;  //Reference to Server object for message passing.
    private int idnum;  //The client's id number.
    private SecretKeySpec keySpec;
    
    public void printDebug(String s)
    {
    	if (!debug)
    		return;
    	System.out.println("[ServerThread]" + s);
    }
    /**
     * Constructor, does the usual stuff.
     * @param s Communication Socket.
     * @param p Reference to parent thread.
     * @param id ID Number.
     * @param key 
     */
    public ServerThread (Socket s, Server p, int id, String key)
    {
    debug = p.getDebug();
	parent = p;
	sock = s;
	idnum = id;
	keySpec = CryptoUtilities.key_from_seed(key.getBytes());
    }
	
    /**
     * Getter for id number.
     * @return ID Number
     */
    public int getID ()
    {
	return idnum;
    }
	
    /**
     * Getter for the socket, this way the parent thread can
     * access the socket and close it, causing the thread to
     * stop blocking on IO operations and see that the server's
     * shutdown flag is true and terminate.
     * @return The Socket.
     */
    public Socket getSocket ()
    {
	return sock;
    }
	
    /**
     * This is what the thread does as it executes.  Listens on the socket
     * for incoming data and then echos it to the screen.  A client can also
     * ask to be disconnected with "exit" or to shutdown the server with "die".
     */
    public void run ()
    {
	/* Allows us to get input from the keyboard. */
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	DataInputStream in_stream = null;
	DataOutputStream out_stream = null;
		
	try {
	    in_stream = new DataInputStream (sock.getInputStream());
	    out_stream = new DataOutputStream (sock.getOutputStream());
	}
	catch (UnknownHostException e) {
	    System.out.println ("Unknown host error.");
	    return;
	}
	catch (IOException e) {
	    System.out.println ("Could not establish communication.");
	    return;
	}
		
	/* Try to read from the socket */
	try {
	    byte[] in_bytes = CryptoUtilities.receiveEncrypted(keySpec, in_stream, debug);

		//byte[] in_bytes = new byte[4 + dest_file.length() + 4 + data.length];
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(in_bytes, 0, 4);
		bb.flip();
		int dest_name_length = bb.getInt();

		String dest_name = new String(in_bytes, 4, dest_name_length);
		
		bb.clear();
		bb.put(in_bytes, 4 + dest_name_length, 4);
		bb.flip();
		int data_length = bb.getInt();
		byte[] data = new byte[data_length];		
		System.arraycopy(in_bytes, 4 + dest_name_length + 4, data, 0, data.length);

	    FileOutputStream out_file = new FileOutputStream(new String(dest_name));
	    out_file.write(data);
	    out_file.close();
	    
	    // where to check if its a failure
	    String result_str = "SUCCESS";	    
	    byte[] result_bytes = new byte[4 + result_str.length()];
	    
	    CryptoUtilities.sendEncrypted(result_bytes, keySpec, out_stream, CryptoUtilities.MSG_RESULT, debug);	    
		
		stdIn.close();
		sock.close();
		in_stream.close();
		out_stream.close();
	}
	catch (Exception e) {
	    CryptoUtilities.sendEncrypted("FAILURE".getBytes(), keySpec, out_stream, CryptoUtilities.MSG_RESULT, debug);
	    if (parent.getFlag())
		{
		    System.out.println ("shutting down.");
		    return;
		}
	    return;
	}	
			
    }
}
