import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

class TCPClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             FileOutputStream fos = new FileOutputStream("downloaded_test.txt")) {

            String filename = "test.txt";
            dos.writeUTF(filename);

            String response = dis.readUTF();
            if (response.startsWith("EXISTS")) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("File downloaded successfully.");
            } else {
                System.out.println("File not found on server.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
