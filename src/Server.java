
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Server extends Thread {

    public static String sourcePath = "C:\\Users\\user\\Desktop\\ZeroCopy\\Rb type Beat - So Cute.mp4";
    SocketChannel scClient;
    int clientNo;
    ServerGUI gui;
    Server(SocketChannel scClient, int clientNo,ServerGUI gui) {
        this.gui = gui;
        this.scClient = scClient;
        this.clientNo = clientNo;
    }

    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(scClient.socket().getInputStream());
            DataOutputStream outputStream = new DataOutputStream(scClient.socket().getOutputStream());
            outputStream.writeUTF("Client "+clientNo+" is Connecting. . .");
            outputStream.flush();

            String clientMessage = "", serverMessage = "";
            while (true) {
                clientMessage = inputStream.readUTF();      
                if(clientMessage.equals("Rb type Beat - So Cute.mp4")){
                    sourcePath = "C:\\Users\\user\\Desktop\\ZeroCopy\\Rb type Beat - So Cute.mp4";
                }
                else if(clientMessage.equals("Chill RB Guitar Type Beat CREAM.mp4")){
                    sourcePath = "C:\\Users\\user\\Desktop\\ZeroCopy\\Chill RB Guitar Type Beat CREAM.mp4";
                }
                    gui.setServerStatus("Client "+clientNo+" select "+clientMessage);
                    serverMessage = "Client " + clientNo + " is Connected !";
                    outputStream.writeUTF(serverMessage);
                    outputStream.flush();
                    RandomAccessFile fileInput = new RandomAccessFile(sourcePath, "rw");
                    ByteBuffer request = ByteBuffer.allocate(8);
                    request.putLong(fileInput.length());
                    gui.setServerStatus("FileSize : "+fileInput.length());
                    request.flip();
                    scClient.write(request);
                    FileChannel fileChannel = fileInput.getChannel();
                    long AllBytesTransferFrom = 0;
                    
                    while (AllBytesTransferFrom < fileInput.length()) {
                        long bytesTransferred = fileChannel.transferTo(AllBytesTransferFrom, fileInput.length() - AllBytesTransferFrom, scClient);
                        AllBytesTransferFrom += bytesTransferred;
                        System.out.println(AllBytesTransferFrom + "/" +fileInput.length());
                    }
                    ByteBuffer response = ByteBuffer.allocate(8);
                    scClient.read(response);
                    response.flip();
                    scClient.finishConnect();
                    outputStream.writeUTF("zerocopy successfully !!");
                    outputStream.flush();
                    gui.setServerStatus("Client :" + clientNo + " Finished");
                
            
            inputStream.close();
            outputStream.close();
            scClient.close();
            }
        } catch (Exception e) {
            System.out.println();
        }
    }
}
