/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class Node implements Serializable, PeerInterface {
	ArrayList<String> neighbours = new ArrayList<String>();
	String BootStrapIp;
	String ip;
	int stamp;
	boolean requestAccess;
	ArrayList<String> queue;
	int totatReplies;
	/**
	 *	Constructor for Node
	 */
	public Node() throws RemoteException {
		
		requestAccess = false;
		queue = new ArrayList<String>();
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return String : ip address of the node.
	 */
	public String getIp() throws RemoteException {
		return ip;
	}
	
	/**
	 * @return int : Returns request stamp.
	 */
	public int getStamp() throws RemoteException {
		return stamp;
	}

	/**
	 * @return  ArrayList: Returns List of neighbors.
	 */
	public ArrayList<String> getNeighbours() throws RemoteException {
		return neighbours;
	}

	/**
	 * addip() : Adds the new ip to the list of neighbors.
	 */
	public void addIp(String address) throws RemoteException {
		boolean present = false;
		for (int i = 0; i < neighbours.size(); i++) {
			if (neighbours.get(i).equals(address)) {
				present = true;
				break;
			}
		}
		if (present == false) {
			neighbours.add(address);

		}
	}
	
	/**
	 * join() : Adds the node to the network.
	 */
	public void join() {
		try {
			System.out.println("Please enter BootStrapServer IP");
			BootStrapIp = new Scanner(System.in).next();
			Registry reg = LocateRegistry.getRegistry(BootStrapIp, 8000);
			Bootstrap obj = (Bootstrap) reg.lookup("server");
			String firstIp = obj.getBootStrapNode();
			if (firstIp == null) {//if it is the first node in network
				obj.BootStrapNode(ip);
				
				//Binding the new node to registry
				PeerInterface bootstrap = (PeerInterface) UnicastRemoteObject.exportObject(this, 8000);
				Registry reg1 = LocateRegistry.createRegistry(8000);
				reg1.rebind("node", this);
			} else {
				Registry registry = LocateRegistry.getRegistry(firstIp, 8000);
				PeerInterface peerInterface = (PeerInterface) registry.lookup("node");
				neighbours.add(firstIp);
				ArrayList<String> n = peerInterface.getNeighbours();
				
				//Updating all the nodes in the network
				for (int i = 0; i < n.size(); i++) {
					neighbours.add(n.get(i));
					Registry registry2 = LocateRegistry.getRegistry(n.get(i),8000);
					PeerInterface peerInterface1 = (PeerInterface) registry2.lookup("node");
					peerInterface1.addIp(ip);
				}
				peerInterface.addIp(ip);
				
				//Binding the new node to registry
				PeerInterface interface1 = (PeerInterface) UnicastRemoteObject.exportObject(this, 8000);
				Registry registry2 = LocateRegistry.createRegistry(8000);
				registry2.rebind("node", this);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * requestCS() : Node request the access to critical section.
	 */
	public void requestCS() throws RemoteException {
		try {
			// Getting the request stamp from the bootstrap server
			Registry reg = LocateRegistry.getRegistry(BootStrapIp, 8000);
			Bootstrap obj = (Bootstrap) reg.lookup("server");
			stamp = obj.getStamp();
			System.out.println("Sending Request to neighbors");
			this.sendRequest();
			System.out.println("Received Required no of replies");
			this.criticalSection(ip,BootStrapIp);
			this.leave();

		} catch (RemoteException | NotBoundException e) {

			e.printStackTrace();
		}

	}
	
	/**
	 * criticalSection() : Node access the critical section  present on bootstrap server.
	 */
	public static synchronized void criticalSection(String ip,
			String BootStrapIp) throws RemoteException {
		try {
			System.out.println("Entering CS...");
			Registry reg = LocateRegistry.getRegistry(BootStrapIp, 8000);
			Bootstrap obj = (Bootstrap) reg.lookup("server");
			obj.criticalSection(ip);
			System.out.println("Exting CS...");
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * view() : Provides the list of all neighbors in network.
	 */
	public void view() {
		for (int i = 0; i < neighbours.size(); i++) {
			System.out.println(neighbours.get(i));
		}
	}


	public static void main(String[] args) throws RemoteException {
		Scanner scanner = new Scanner(System.in);
		boolean join = false;
		boolean conti = true;
		int option;
		Node node = new Node();
		while (conti) {
			System.out.println("Please select an option...");
			System.out.println("1.Join");
			System.out.println("2.Access Critical section");
			System.out.println("3.Nodes in Network");
			option = scanner.nextInt();
			switch (option) {
			case 1:
				if (!join) {
					node.join();
					System.out.println("Node joined");
					join = true;
					break;
				} else {
					System.out.println("Node Already present in network...");
					break;
				}
			case 2:
				if (join) {
					node.requestCS();
				} else {
					System.out.println("Node not in network. Please join the network first.");
				}
				break;
			case 3:
				if (join) {
					node.view();
				} else {
					System.out.println("Node not in network. Please join the network first.");
				}
				break;

			default:
				System.out.println("Please enter correct option...!!!");
				break;
			}

		}

	}

	/**
	 * sendRequest() : Sends access request to all the nodes.
	 */
	public boolean sendRequest() throws RemoteException {
		this.requestAccess = true;
		totatReplies = 0;
		for (int i = 0; i < this.neighbours.size(); i++) {
			Registry registry = LocateRegistry.getRegistry(neighbours.get(i),8000);
			try {
				PeerInterface interface1 = (PeerInterface) registry.lookup("node");
				interface1.receiveRequest(ip,stamp);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
		//Node waits for the desired number of replies to enter the critical section.
		while (totatReplies < this.getNeighbours().size()) {
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * updateReplies() : Updating the replies on the node.
	 */
	public void updateReplies() throws RemoteException {
		this.totatReplies++;
	}

	/**
	 * receiveRequest() : Receives the request access from the node with ipAddress= ip .
	 */
	public void receiveRequest(String ip,int stampid) throws RemoteException {
		System.out.println("Received request from " + ip);
		try {
			boolean hold = false;
			//if the current section has CS or needs CS and has stamp lower than the requesting node
			//then queue the request else send the reply
			hold = this.requestAccess && (stamp < stampid);
			if (hold) {
				this.queue.add(ip);
			} else {
				this.sendReply(ip);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *	sendReply(): Sends the reply to requesting node
	 **/
	public void sendReply(String ip) throws RemoteException {
		try {
			System.out.println("Sending reply to " + ip);
			Registry registry = LocateRegistry.getRegistry(ip, 8000);
			PeerInterface interface1 = (PeerInterface) registry.lookup("node");
			interface1.updateReplies();
			} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 *	leave(): Current Node leaves the critical section  and sends the reply to queued nodes.
	 **/
	public void leave() throws RemoteException{
		if(queue.size()>0){
			System.out.println("Sending Replies back to neighbors");
		}
		for (int i = 0; i < queue.size(); i++) {
			this.sendReply(queue.get(i));
		}
		this.queue=new ArrayList<String>();
		this.requestAccess = false;

	}

}
