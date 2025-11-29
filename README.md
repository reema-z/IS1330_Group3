# IS1330_Group3
group3 project part one code with three files: Server, Client and GUI

**Project objective and description:**

Objective 

The objective of this project is to build a fully functional rock paper scissors game, using Java TCP socket programming supporting concurrency and multithreading. The system demonstrates how client (or several clints) can connect to a server and game moves and received results through reliable, ordered TCP connection.
Furthermore, this project shows how (multithreading) enable numerous of players to play at the same time without interrupting each other.

Discerption 

This project is structured around three primary components:
 RPSGameServer.java 
 RPSGameClient.java
 RPSGameGUI.java
When the server starts, it opens a Server Socket on a x port and waits for client connections. Each time a client connects, the server creates a new thread to handle that client separately. This design lets several players join, leave, and play at the same time (supporting concurrency and multithreading).
Each client sends the following to the server:
 • Player name
 • Selected move (rock = 1, paper = 2, scissors = 3)
Inside the thread, the server receives the move, generates its own random move, applies the game logic, and sends the result back to that client only. The server stays running and ready to accept new clients at any time.

The GUI client includes a connection window for the name, host IP, and port. It has buttons for selecting moves, a text area to show communication with the server, and a scoreboard that automatically updates wins, losses, and ties.

**Explanation of server–client design:**

For this report, we used TCP as main protocol and in this section, we will discuss the server-client design in three aspects:
1. TCP Concept Socket Creation in a TCP client:
server architecture, the server first uses socket() to create a socket. Next, it uses bind() to connect the socket to a given IP address and port. The server then enters a passive waiting state and calls listen() to begin waiting for clients. A communication channel is established when the server uses accept() to accept a client's attempt to connect. 
While for client it calls socket() to create the socket and connect() (with the server IP and port number for TCP) to initiate the connection between them
2. Threading (handling multiple Clients):
The server employs multithreading to support multiple clients concurrently and simultaneously by using new ClientHandler(clientSocket, clientIP).start();  . Each client that establishes a connection is managed in a separate thread. As a result, the server can interact with numerous clients at once without interpreting or delaying others. 
3.Message Exchange (Reliable TCP Communication):
The client and server communicate via the same TCP link once the connection has been made. Both parties can send and receive messages with ease because of TCP, which guarantees that all messages are delivered consistently and reliably and in the right order.
input = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //to read/recevie input
output = new PrintWriter(socket.getOutputStream(), true); //to send/response. true value indicate that the response are send immediately


