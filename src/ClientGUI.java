package os;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGUI extends javax.swing.JFrame {

    public static String sourcePath = "/home/ez/Desktop/Chill RB Guitar Type Beat CREAM.mp4";
    DataInputStream inputStream;
    DataOutputStream outputStream;
    String clientMessage = "", serverMessage = "";

    public ClientGUI() {
        initComponents();
        statusClient.append("Client is running...\n");
        statusClient.append("[File]\n");
        statusClient.append("1 : Chill RB Guitar Type Beat CREAM.mp4\n");
        statusClient.append("2 : Rb type Beat - So Cute.mp4");

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        fileNumber = new javax.swing.JTextField();
        RunButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        statusClient = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(600, 400));

        jLabel1.setText("Input your file number");

        fileNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNumberActionPerformed(evt);
            }
        });

        RunButton.setText("Run");
        RunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunButtonActionPerformed(evt);
            }
        });

        statusClient.setColumns(20);
        statusClient.setRows(5);
        jScrollPane1.setViewportView(statusClient);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addContainerGap(38, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fileNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(RunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(107, 107, 107))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RunButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fileNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileNumberActionPerformed

    private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunButtonActionPerformed
        try {
            System.out.println(fileNumber.getText());
            if (fileNumber.getText().equals("1") || fileNumber.getText().equals("2")) {
                if (fileNumber.getText().equals("1")) {
                    sourcePath = "/home/ez/Desktop/Chill RB Guitar Type Beat CREAM.mp4";
                    statusClient.append("\nSelect Chill RB Guitar Type Beat CREAM");
                    clientMessage = "Chill RB Guitar Type Beat CREAM.mp4";
                } else if (fileNumber.getText().equals("2")) {
                    sourcePath = "/home/ez/Desktop/Rb type Beat - So Cute.mp4";
                    statusClient.append("\nSelect Rb type Beat - So Cute");
                    clientMessage = "Rb type Beat - So Cute.mp4";
                }
                
                SocketChannel scClient = SocketChannel.open();// 
                 scClient.connect(new InetSocketAddress("192.168.56.1", 3000));
                 inputStream = new DataInputStream(scClient.socket().getInputStream());
                 outputStream = new DataOutputStream(scClient.socket().getOutputStream());
                 statusClient.append("\n" + inputStream.readUTF());
                 
                 while (true) {

                outputStream.writeUTF(clientMessage);
                outputStream.flush();
                long startTime = System.currentTimeMillis();
                serverMessage = inputStream.readUTF();
                statusClient.append("\n" + serverMessage);

                //read input file from server;
                ByteBuffer request = ByteBuffer.allocate(16);
                scClient.read(request);
                request.flip();
                long length = request.getLong();
                RandomAccessFile fileOutput = new RandomAccessFile(sourcePath, "rw");
                FileChannel fileChannel = fileOutput.getChannel();
                long bytesTransfer = 0;
                while (bytesTransfer < length) {
                    long countByteTransfer = fileChannel.transferFrom(scClient, bytesTransfer, length - bytesTransfer);
                    if (countByteTransfer <= 0) {
                        break;
                    }
                    bytesTransfer += countByteTransfer;
                }

                //write output file from server
                ByteBuffer response = ByteBuffer.allocate(16);
                response.putLong(bytesTransfer);
                response.flip();
                scClient.write(response);

                long stopTime = System.currentTimeMillis();
                long time = stopTime - startTime; // Time
                statusClient.append("\nTime" + time + " millisecond");
                serverMessage = inputStream.readUTF();
                statusClient.append("\n" + serverMessage);

                outputStream.close();
                scClient.close();
            }
            }

            else {
                statusClient.append("\nplease choose 1 or 2 and run again!!");

            }
           
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_RunButtonActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton RunButton;
    private javax.swing.JTextField fileNumber;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea statusClient;
    // End of variables declaration//GEN-END:variables
}
