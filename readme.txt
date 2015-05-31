Ricart and Agrawala’s algorithm
----------------------------------------------------------------------------------------------------
 Files Required
 1.Bootstrap.java
 2.BootStrapServer.java
 3.Node.java
 4.PeerInterface.java

Process :
----------------------------------------------------------------------------------------------------
1) Compile all the classes using javac *.java
2) Run the BootStrapServer.java on a separate system.//Ex glados.cs.rit.edu
3) Now run Node.java on different system(#total number of different systems) and perform the required operations. //Ex yes.cs.rit.edu

Scenario : 
----------------------------------------------------------------------------------------------------
Step 1: 

a) Run BootStrapServer.java on a separate system.//Ex glados.cs.rit.edu
b) It will start the bootstrap server. Copy the ip address of bootstrapserver

Step 2: 
a) Run Node.java on a differenrt server.//Ex. kansas.cs.rit.edu
b) Following User menu will appear on screen
	Please select an option...
	1.Join
	2.Access Critical section
	3.Nodes in Network
c) Enter required option . Eg. 1
d) When 1 is given as a input. Following message will pop on screen.
   a) Please enter BootStrapServer IP.
   b) Now enter the bootstrapserver ip and node will join the network.
   c) User driven menu will be displayed on the screen

Step 3:
 Repeat Step 2 for 4 different servers. //Ex. yes.cs.rit.edu , newyork.cs.rit.edu , delaware.cs.rit.edu , arizona.cs.rit.edu

Step 4:Now select option 2 for the first server.
      It will call requestCS() method. This method will perform the following actions. 
      a) Get the request stamp from the BootStrapServer.
      b) Send the request message to all the peers in the network for Critical section access. When a peer receives request message
         it checks whether it needs access to critical section. If it needs access then it compares the stamp and depending on the result peer
	 will reply or queue the request. 
      c) Once the node receives the required number of replies it enters the critical section present on BootStrapServer.
      d) It prints "Enter CS" and "Leave CS" message on node and Critical Section.
      e) After leaving the Critical Section it sends reply to all the queued peers.
      
Step 5: Simliarly perform step 4 on different systems and depending on the order of request each node will get acces to Critical section.

