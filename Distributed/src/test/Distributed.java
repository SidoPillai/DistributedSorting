package test;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Distributed {

	File rootFile;
	static List<String> listOfAvailableHost;

	public static void main(String[] args) throws UnknownHostException, IOException {
		listOfAvailableHost = checkHosts("129.21.139"); // in our case we have to add the subnet

		// send the source files
		for (String host : listOfAvailableHost) {
			sendFiles(host);
		}
		
		// compile
		for (String host : listOfAvailableHost) {
			
		}

		//  Call the master
		String[] arguments = new String[] { "6000", String.valueOf(listOfAvailableHost.size())}; 
		Master.main(arguments);
		
		// execute
		for (String host : listOfAvailableHost) {
			// Call the Slave
			// execue - start the client
			
		}

	}

	// looks for the host in the network and adds inti the list of online devices 
	public static ArrayList<String> checkHosts(String subnet) throws UnknownHostException, IOException {
		ArrayList<String> onlineHost = new ArrayList<String>();
		int timeout = 1000;

		for (int i = 1;i<254;i++){
			System.out.println(i);
			String host=subnet + "." + i;
			if (InetAddress.getByName(host).isReachable(timeout)){
				//				System.out.println(host + " is reachable");
				onlineHost.add(host);
			}
		}

		return onlineHost;
	}

	// Running this command to send the file  
	public static boolean sendFiles(String hostname) {
		Session session = null;
		ChannelExec channel = null;

		try{
			JSch jsch = new JSch();
			session = jsch.getSession(hostname);
			session.connect();
			channel = (ChannelExec) session.openChannel("exec");                        
			channel.setCommand("scp /home/files ip_server:/Users/siddeshpillai/Documents/workspace/Distributed/src/"); // $> scp file1…fileN IP_OF_HOST:/PATH_TO_YOUR_FOLDER
			channel.connect();
			return true;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return false;
	}

	// remote copy
	public void remoteCopy(String hostname) throws JSchException, IOException, SftpException {
		JSch js = new JSch();
		Session s = js.getSession(hostname);
		s.connect();
		Channel c = s.openChannel("sftp");
		ChannelSftp ce = (ChannelSftp) c;
		ce.connect();
		ce.put("/home/siddeshpillai/test.txt","test.txt");
		ce.disconnect();
		s.disconnect();    
	}
}
