// ------------------------------------------------------------------------
// Author: Salman Taherizadeh - Jozef Stefan Institute (JSI)
// ------------------------------------------------------------------------
// Execution: java TestIfPortIsOpenOnTheHost IP PORT
// For example: java TestIfPortIsOpenOnTheHost "195.82.14.15" "10001"
// ------------------------------------------------------------------------
import java.io.*;
import java.net.*;

class TestIfPortIsOpenOnTheHost{

	public static void main (String args[]) { 
		try (Socket s = new Socket(args[0], Integer.parseInt(args[1]))) {
			System.out.println("Yes. Port is open.");
		} catch (IOException ex) {
			System.out.println("No. Port is blocked.");
		}
	}

}
