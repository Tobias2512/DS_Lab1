import java.io.*;
import java.net.*;

class UDPServer {
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server listening on port " + PORT);
            byte[] receiveBuffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(requestPacket);
                String filename = new String(requestPacket.getData(), 0, requestPacket.getLength()).trim();
                System.out.println("Client requested file: " + filename);

                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                File file = new File(filename);
                if (file.exists()) {
                    sendFile(serverSocket, file, clientAddress, clientPort);
                } else {
                    String errorMessage = "NOFILE";
                    DatagramPacket errorPacket = new DatagramPacket(errorMessage.getBytes(), errorMessage.length(), clientAddress, clientPort);
                    serverSocket.send(errorPacket);
                    System.out.println("File not found: " + filename);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(DatagramSocket socket, File file, InetAddress clientAddress, int clientPort) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        }
        System.out.println("File sent successfully.");
    }
}
