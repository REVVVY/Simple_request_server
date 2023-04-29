package org.example;
import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * Server class for starting a simple http server that waits for incoming connections
 * and writes requested files if found.
 */
public class Server implements Runnable{

    private int port;
    private String serverRoot;
    private Thread serverThread = new Thread(this);
    private ServerSocket serverSocket;
    private ExecutorService executor;

    /***
     * Server contstructor, creates and starts sever on specified port with selected server path.
     * @param port which server runs on
     * @param root the absolute path of the server
     * @throws IOException
     */
    public Server(int port, String root) throws IOException {
        this.port = port;
        this.serverRoot = root;
        executor = Executors.newFixedThreadPool(10);

        serverSocket = new ServerSocket(port);
        serverThread.start();
    }

    /***
     * Waiting for incoming connections and creates a clientHandler and start its thread.
     */
    @Override
    public void run() {
        while (true){
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                executor.execute(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /***
     * ClientHandler class, for every incoming connection a clienthandler is created and handles the request.
     */
    public class ClientHandler extends Thread{
        private Socket socket;
        private OutputStream os;

        /***
         * ClientHandler constructor for creating a clientHandler object.
         * @param socket endpoint of the client
         * @throws IOException
         */
        public ClientHandler(Socket socket) throws IOException{
            this.socket = socket;
        }

        /***
         * Client thread that starts with creating a bufferedreader, reads and sorts out the requested file.
         * Creates a file object if the file exists and writes the file to the client.
         * If requested file does not exist, sends a 404 not found to the client.
         */
        public void run(){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String requestedLine = br.readLine();
                System.out.println(requestedLine);
                String request = requestedLine.substring(5);
                //System.out.println(request);
                String[] strings = request.split(" ");
                String filePath = strings[0];
                //System.out.println(filePath);

                File file = new File(serverRoot + "\\" + filePath);
                os = socket.getOutputStream();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    os.write("HTTP/1.1 200 OK\r\n".getBytes());
                    os.write("\r\n".getBytes());

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1){
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                    System.out.println("Requested file sent");

                    System.out.println("Closing client connection!");
                    br.close();
                    os.close();
                    fis.close();
                    socket.close();
                } catch (FileNotFoundException e){
                    file = new File(serverRoot + "\\" + "404.html");
                    FileInputStream fis = new FileInputStream(file);
                    System.out.println("Requested file not found");
                    os.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                    os.write("\r\n".getBytes());

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1){
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                    br.close();
                    os.close();
                    fis.close();
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
