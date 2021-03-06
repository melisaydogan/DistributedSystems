/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static java.lang.Thread.sleep;


/**
 *
 * @author melisa
 */
public abstract class Sensor {

    protected int SERVERPORT; //= 9991; Port des Servers
    protected String SERVERADDRESS; // IP des Servers
    private DatagramSocket datasocket;
    private DatagramPacket datapacket;
    private byte[] buffer = new byte[1024];     //hier kommt Nachricht rein
    private final String sensorID = UUID.randomUUID().toString();   //universally unique identifier (unveränderlicher universell eindeutiger Bezeichner)
    protected String sensorType;
    protected String sensorValue;

    /*
    * generiert zum Sensor passenden Wert
     */
    public abstract void simulate();

    /* 
    * erstellt Nachricht und speichert in Byte-Array
     */
    public void generateMessage() {
        Timestamp messageTime = new Timestamp(System.currentTimeMillis());
        String message = sensorID + "#" + sensorType + "#" + sensorValue + "#" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(messageTime) + "#";
        buffer = message.getBytes();
    }

    /*
    * schickt Nachricht an UDP-Server
     */
    public void send() {

        try {
            datasocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVERADDRESS); //getByName('localhost')
            datapacket = new DatagramPacket(buffer, buffer.length, serverAddress, SERVERPORT);
            datasocket.send(datapacket);
            System.out.println("Socket sent on Port "+SERVERPORT);

        } catch (SocketException ex) {
            //  Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Datagramsocket konnte nicht erstellt werden");

        } catch (UnknownHostException ex) {
           //  Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Serveradresse konnte nicht aufgelöst werden");

        } catch (IOException ex) {
             // Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Paket konnte nicht geschickt werden");

        }  finally {
            datasocket.close();

        } /*catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void getInformation(){
        System.out.println("Send Information "+this.sensorType + "\n" + this.sensorValue + "\n");
    }


    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
}
