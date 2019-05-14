package weatherstation;

import java.net.ServerSocket;
import java.net.Socket;


/**
 * Klasse, die TCP Verbindungen entgegen nimmt
 */
public class Http_Controller extends Thread {
    private final int SERVERPORT;
    private Weatherstation weatherstation;
    private ServerSocket welcomeSocket;
    private Socket connectionSocket;
    private String herstelleradresse;
    private int herstellerport;

    public Http_Controller(Weatherstation weatherstation, String herstelleradresse, int herstellerport){
        this.weatherstation = weatherstation;
        SERVERPORT = this.weatherstation.getPort();

        this.herstelleradresse = herstelleradresse;
        this.herstellerport = herstellerport;
        try{
            welcomeSocket = new ServerSocket(SERVERPORT);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Http_Controller(Weatherstation weatherstation){
        this.weatherstation = weatherstation;
        SERVERPORT = this.weatherstation.getPort();

        this.herstelleradresse = null;
        this.herstellerport = 0;
        try{
            welcomeSocket = new ServerSocket(SERVERPORT);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("HTTP Server wurde gestartet .......");
        while(true){
            try{
                connectionSocket = welcomeSocket.accept();
                new Thread(new HTTP_Server(weatherstation, connectionSocket)).start();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
