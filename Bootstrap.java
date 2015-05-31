/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface for the BootStrap Server
 */
public interface Bootstrap extends Remote{
	public void BootStrapNode(String ip) throws RemoteException;
	public void removeBootStrapNode() throws RemoteException;
	public String getBootStrapNode() throws RemoteException;
	public int getStamp() throws RemoteException;
	public void criticalSection(String ip) throws RemoteException;
}
