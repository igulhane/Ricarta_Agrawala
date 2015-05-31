/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of BootStrap server.
 */
public class BootStrapServer implements Bootstrap, Serializable {
	String nodeIp;
	int count;
	protected BootStrapServer() throws RemoteException {
		count=0;
	}

	public static void main(String[] args) throws IOException {
	
		try {
			BootStrapServer server = new BootStrapServer();
			Bootstrap bootstrap = (Bootstrap) UnicastRemoteObject.exportObject(server, 8000);
			Registry reg = LocateRegistry.createRegistry(8000);
			reg.rebind("server", server);
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println("BootStrap Server Started");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores the ip address of first node in the network 
	 */
	@Override
	public void BootStrapNode(String ip) throws RemoteException {
		nodeIp = ip;
	}
	
	/**
	 * Removes the stored ip address
	 */
	public void removeBootStrapNode() throws RemoteException{
		nodeIp=null;
	}
	
	/**
	 * Gives the stamp for current request
	 */
	
	public int getStamp() throws RemoteException {
	 return count++;
	 }
	
	/**
	 * @return Ip address of the first node in the network.
	 */
	@Override
	public String getBootStrapNode() throws RemoteException {
		return nodeIp;
	}
	
	/**
	 * Critical section for all the nodes
	 * 
	 */
	
	public void criticalSection(String ip) throws RemoteException {
		System.out.println(ip+" entering critical section...");
		try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(ip+" exiting critical section...");
	}
}
