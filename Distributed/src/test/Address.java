package test;
import java.net.InetAddress;

public class Address {
	
	private int port;
	private InetAddress address;
	
	Address(int port, InetAddress address) {
		this.port = port;
		this.address = address;
	}
	
	public Address() {
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
}