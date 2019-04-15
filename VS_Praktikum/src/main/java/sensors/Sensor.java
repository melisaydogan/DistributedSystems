/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 *
 * @author capcup
 */
public abstract class Sensor {

    protected int SERVERPORT; //= 9997;
    protected String SERVERADDRESS;
    private DatagramSocket datasocket;
    private DatagramPacket datapacket;
    private byte[] buffer = new byte[1024];
    private final String sensorID = UUID.randomUUID().toString();
    protected String sensorType;
    protected String sensorValue;

    public abstract void simulate();

    public void generateMessage() {
        Timestamp nachrichtenZeit = new Timestamp(System.currentTimeMillis());
        String nachricht = sensorID + "#" + sensorType + "#" + sensorValue + "#" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(nachrichtenZeit) + "#";
        buffer = nachricht.getBytes();
    }

    public void send() {
        try {
            datasocket = new DatagramSocket();
            InetAddress serveradress = InetAddress.getByName(SERVERADDRESS);//getByName("localhost");
            datapacket = new DatagramPacket(buffer, buffer.length, serveradress, SERVERPORT);
            datasocket.send(datapacket);
        } catch (SocketException e) {
            System.out.println("Datagramsocket konnte nicht erstellt werden");
        } catch (UnknownHostException e) {
            System.out.println("Serveradresse konnte nicht aufgelöst werden");
        } catch (IOException e) {
            System.out.println("Paket konnte nicht geschickt werden");
        } finally {
            datasocket.close();
        }
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    } 
}
