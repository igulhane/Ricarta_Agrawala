/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface for Node
 */

public interface PeerInterface extends Remote{
	public void addIp(String address) throws RemoteException;
	public ArrayList<String> getNeighbours() throws RemoteException;
	public void receiveRequest(String ip, int stamp) throws RemoteException;
	public void updateReplies() throws RemoteException;
}
