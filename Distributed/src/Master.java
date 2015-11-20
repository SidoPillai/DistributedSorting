import java.io.IOException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

public class Master {

//	private void sendCommand(Channel channel, String command) {
//	    try {
//	        //
//	        this.channelExec = (ChannelExec) channel;
//	        this.channelExec.setCommand(command);
//	        //channel.setInputStream(null);
//	        channel.setOutputStream(System.out);
//	        this.is = channel.getInputStream();
//	        channel.connect();
//	        byte[] buffer = new byte[1024];
//	        while (channel.getExitStatus() == -1) {
//	            while (is.available() > 0) {
//	                int i = is.read(buffer, 0, 1024);
//	               // System.out.println("i= " + i);
//	                if (i < 0) {
//	                   // System.out.println("breaking");
//	                    break;
//	                }
//	                String string = new String(buffer, 0, i);                    
//	                output = output.concat(string);
//	                //System.out.println("String= " + string);
//
//	            }
//
//	            if (channel.isClosed()) {
//	                //System.out.println("exit-status: " + channel.getExitStatus());
//	                break;
//	            }
//
//	        }
//	        is.close();            
//	        channel.disconnect();
//	        this.session.disconnect();
//	        System.out.println("Done");
//
//	    } catch (IOException ex) {
//	        System.out.println("ERROR: " + ex);
//	        Logger.getLogger(SSH.class.getName()).log(Level.SEVERE, null, ex);
//	    } catch (JSchException ex) {
//	        System.out.println("ERROR: " + ex);
//	        Logger.getLogger(SSH.class.getName()).log(Level.SEVERE, null, ex);
//	    }
//
//	}
	
	public static void main(String[] args) {
		
	}
}
