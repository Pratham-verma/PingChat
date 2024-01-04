import javax.swing.JScrollBar;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame{

    Socket socket;

    BufferedReader br; // for Reading
    PrintWriter out;  // for writing

    // Declare Components
    private final JLabel heading = new JLabel("Client Area");
    private  JTextArea messageArea = new JTextArea();
    private  JTextField messageInput = new JTextField();
    private  Font font = new Font("Roboto",Font.PLAIN,20);


    // constructor call
     public Client() {

         try {
             System.out.println("Sending request to server");
             socket = new Socket("127.0.0.1", 7777);
             System.out.println("Connection Done");

             br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             out = new PrintWriter(socket.getOutputStream());

               createGUI();
               handleEvents();
             StartReading();
             //StartWriting();

         } catch (Exception e) {
             e.printStackTrace();
         }
     }
             private void createGUI(){
                 //gui code

                 this.setTitle("Client Messenger[END]");
                 this.setSize(500,600);
                 this.setLocationRelativeTo(null);
                 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 this.setBackground(Color.green);

                 // code for components
                 heading.setFont(font);
                 messageArea.setFont(font);
                 messageInput.setFont(font);
                 heading.setHorizontalAlignment(SwingConstants.CENTER);
                 heading.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
                 messageArea.setEditable(false);
                  //frame ka layout set
                 this.setLayout(new BorderLayout());

                 //adding the components to frame
                 this.add(heading,BorderLayout.NORTH);
                 JScrollPane jScrollPane = new JScrollPane(messageArea);
                 this.add(jScrollPane, BorderLayout.CENTER);
                 this.add(messageInput, BorderLayout.SOUTH);



                 this.setVisible(true);
             }

             private void handleEvents()
             {
                 messageInput.addKeyListener(new KeyListener() {
                     @Override
                     public void keyTyped(KeyEvent e) {

                     }

                     @Override
                     public void keyPressed(KeyEvent e) {

                     }

                     @Override
                     public void keyReleased(KeyEvent e) {
                          // System.out.println("key released "+e.getKeyCode());
                         if (e.getKeyCode() == 10)
                         {
                            // System.out.println("Enter Button Pressed");
                             String contentToSend=messageInput.getText();
                             messageArea.append("Me : " + contentToSend +"\n");
                             out.println(contentToSend);
                             out.flush();
                             messageInput.setText("");
                             messageInput.requestFocus();
                         }
                     }
                 });
             }

             public void StartReading()
             {
                 // Creating Thread for continuously Reading;
                 Runnable r1=()-> {
                     System.out.println("Reader Started... ");

                     try {
                         while (true) {

                             String msg = br.readLine();
                             if (msg.equals("exit")) {
                                 System.out.println("Server terminated the chat");
                                 JOptionPane.showMessageDialog(this,"Server Terminated the chat");
                                 messageInput.setEnabled(false);
                                 socket.close();
                                 break;
                             }
                            // System.out.println("Server : " + msg);
                             messageArea.append("Server : " + msg + "\n" );
                         }
                         
                     } catch (Exception e) {
                         //e.printStackTrace();
                         System.out.println("\nConnection Closed\n");
                     }
                 };

                 new Thread(r1).start();

             }
             public void StartWriting()
             {
                 // Creating Thread for Receiving and Sending Data to Client;
                 Runnable r2 = () -> {

                     System.out.println("Writer Started......");
                     System.out.println();

                     try {
                         while (!socket.isClosed()) {

                             BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                             String content = br1.readLine();
                             out.println(content);
                             out.flush();

                             if (content.equals("exit")) {
                                 socket.close();
                                 break;
                             }

                         }

                     } catch (Exception e) {
                         // e.printStackTrace();
                         System.out.println("Connection Closed");
                     }

                 };

                 new Thread(r2).start();
             }

       public static void main(String[] args) {

          new Client();
    }
}
