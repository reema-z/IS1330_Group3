import java.io.*;  //for input and output
import java.net.*; // to be able to use socket and send/receive over network 
import java.util.Scanner; //read user input 

public class RPSGameClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";  //local host, connect to the same computer
    private static final int SERVER_PORT = 12345; //same port number for the server, this must be identical to send and receive over the TCP connection 

    public static void main(String[] args) { 
        Scanner scanner = new Scanner(System.in); //scanner obj to read user input

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); //client socket connected to the server by its IP address and port number
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream())); //to read/recevie server  input
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true) //to send/response to the server. true value indicate that the response are send immediately
             ) {  //inside a try to free all resources when connection is lost/closed

            System.out.println("Connected to server: " + SERVER_ADDRESS + ":" + SERVER_PORT); //ensure that the connection is vaild
            System.out.println(input.readLine());  // Welcome msg
            System.out.println(input.readLine());  // client IP 
            System.out.print(input.readLine() + " "); // Enter name prompt

            String name = scanner.nextLine(); //read users input (name)
            output.println(name); //send it to the server
            System.out.println(input.readLine()); // prompt msg from server to choose a move
                /* 
                 * implementing the game logic in this while loop
                 * first it prompt the user to choose a move
                 * then using output it send over the move to the server (where it choose randomly its move and compare it to the user move and send back the result)
                 * if statment to break from the loop(stop the game) if the users chose to quit (Q)
                 */
            while (true) {
                System.out.print("choose your move (from the buttons above) "); 
                String move = scanner.nextLine();
                output.println(move);
                if (move.equalsIgnoreCase("Q")) break;
                System.out.println(input.readLine());
            } 

            System.out.println(input.readLine()); //closing (goodbye) msg 

        } catch (IOException e) { //for error handling
            System.out.println("Connection error: " + e.getMessage());
        }
    }
}