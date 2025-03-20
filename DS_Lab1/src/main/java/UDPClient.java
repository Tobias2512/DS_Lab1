import java.io.*;
import java.net.*;

class UDPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 1234;
    private static final int BUFFER_SIZE = 4096;

    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            String filename = "test.txt";
            byte[] sendBuffer = filename.getBytes();

            DatagramPacket requestPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, PORT);
            clientSocket.send(requestPacket);
            System.out.println("Requested file: " + filename);

            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            FileOutputStream fos = new FileOutputStream("downloaded_test.txt");

            while (true) {
                clientSocket.receive(receivePacket);
                String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (receivedData.equals("NOFILE")) {
                    System.out.println("File not found on server.");
                    fos.close();
                    return;
                }

                fos.write(receivePacket.getData(), 0, receivePacket.getLength());

                if (receivePacket.getLength() < BUFFER_SIZE) {
                    break; // End of file
                }
            }

            fos.close();
            System.out.println("File downloaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
