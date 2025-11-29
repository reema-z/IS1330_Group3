import java.io.*; //for input and output
import java.net.*;  // to be able to use socket and send/receive over network 
import java.util.*; //for utilities like random methods 

public class RPSGameServer {
    private static final int PORT = 12345;  //server port number for socket usage, with it being static final meaning it wont change and always runs on the same port 
    private static final Random random = new Random(); //create random obj for the server to make random actions

    public static void main(String[] args) {
        System.out.println("Rock Paper Scissors TCP Server running on port " + PORT);  // like a welcome msg ensuring that the server is working 
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {      //inside a try for error handling, creating the server socket with the final port number which will act as the door for receiving requests from clients
            while (true) {      //keep running accepting new clients 
                Socket clientSocket = serverSocket.accept();   //waits for client connection request, if it connected then create a socket specially for that client connection
                String clientIP = clientSocket.getInetAddress().getHostAddress();   //get the client IP address 
                System.out.println("Client connected from IP: " + clientIP); //msg for ensuring that a client is connected sucessfully
                new ClientHandler(clientSocket, clientIP).start(); //create a thread for that client to handle it and implement concurrency allowing other clients to connect simultaneously
            }
        } catch (IOException e) {  //for error handling, ensure no system crash 
            System.out.println(" Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {   //class named clienthandler extended from Thread class for concurrency control 
        // to handle client these attrubites are needed
        private Socket socket;  //client socket            
        private BufferedReader input; //read input
        private PrintWriter output; //write output
        private String clientIP;   // client IP address

        public ClientHandler(Socket socket, String clientIP) {    //constructor 
            this.socket = socket;
            this.clientIP = clientIP;
        }

        @Override
        public void run() {    //override method run from Thread class, this will be excuted when run  function is called
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //to read/recevie client  input
                output = new PrintWriter(socket.getOutputStream(), true); //to send/response to the client. true value indicate that the response are send immediately

                output.println("Welcome to Rock Paper Scissors Game!"); //output to the client, welcome msg
                output.println("Enter your player name:"); //output to the client, prompt the user to enter thier name

                String playerName = input.readLine(); //wait for the user to send thier name
                output.println("Hello " + playerName + " :) Choose: Rock  Paper  Scissors  or Q to quit."); //explaining the game instructions to the user

                String userChoice;   //to store the user action choice
                while ((userChoice = input.readLine()) != null) {  //always read user action until a null which may indicate a connection closing
                    if (userChoice.equalsIgnoreCase("Q")) break;  //stop the loop (the game) if user typed Q (quit the game)

                    int clientMove;  //for saving the user choice for game logic comparision
                    try {
                        clientMove = Integer.parseInt(userChoice); //turn the string into intger
                        if (clientMove < 1 || clientMove > 3) { //is the user input vaild? within the game options of rock (1) paper (2) scissor (3)
                            output.println("Invalid move! Enter 1, 2, or 3."); //clarification msg 
                            continue; //go back again, allowing the user to enter new move (another/different loop iteration)
                        }
                    } catch (NumberFormatException e) { //for error handling, if the user didnt enter a number
                        output.println("Invalid input! Enter 1, 2, or 3.");
                        continue;
                    }

                    int serverMove = random.nextInt(3) + 1; //generate a random move for the server, (random start from 0-> then 0,1,2 will be the options, for the game logic and comparison matters one is added -> so it will be one of three: 1,2,3)
                    String result = getResult(clientMove, serverMove); //function for the game result (won, lost, or tied)
                    output.println("Server chose " + moveToString(serverMove) + ". " + result); //to show the server move and the game result to the client user

                }  //loop (game) ended

                output.println("Goodbye " + playerName + " :("); //declaring that the connection will be closed
                socket.close(); //close socket 

            } catch (IOException e) { // for error handling
                System.out.println(" Connection with client " + clientIP + " lost.");
            }
        }

        private String moveToString(int move) {  // function to retun the number (1,2,3) to a move(rock, paper, scissors)
            return switch (move) {
                case 1 -> "Rock";
                case 2 -> "Paper";
                case 3 -> "Scissors";
                default -> "Unknown";
            };
        }

        private String getResult(int clientMove, int serverMove) { //function for comparing the client and server moves. determinig the game results
            if (clientMove == serverMove) return "It's a tie!";
            if ((clientMove == 1 && serverMove == 3) ||
                (clientMove == 2 && serverMove == 1) ||
                (clientMove == 3 && serverMove == 2))
                return "You win!";
            else
                return "You lose!";
        }
    }
}