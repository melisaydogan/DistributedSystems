/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author capcup
 */
public class Central extends Thread {

    private final int SERVERPORT;
    private DatagramSocket datasocket;
    private DatagramPacket datapacket;
    private final byte[] buffer = new byte[1024];
    Map<String, List<String>> sensorData = new HashMap<>();
    private Timestamp messageTime;
    private final String centralID;// = UUID.randomUUID().toString();
    private String actualWeather = " / Keine Wetterdaten verfuegbar";

    public Central() {
        SERVERPORT = 9997;
        centralID = UUID.randomUUID().toString();
    }

    /**
     * receives DatagramPacket
     */
    public boolean receiveDP() {
        try {
            datasocket = new DatagramSocket(SERVERPORT);
            datapacket = new DatagramPacket(buffer, buffer.length);

            datasocket.receive(datapacket);
            messageTime = new Timestamp(System.currentTimeMillis());
            return true;
        } catch (SocketException e) {
            System.out.println("Problem mit DatagramSocket");
        } catch (IOException e) {
            System.out.println("DatagramPacket konnte nicht empfangen werden");
        } finally {
            datasocket.close();
        }
        return false;
    }

    /**
     * Processes message and saves data in Map
     *
     * @return
     */
    public String[] processMessage() {
        String message = new String(datapacket.getData());
        String browserdaten;
        List<String> tmpList = new ArrayList<>();

        String[] messageArr = message.split("#");

        if (messageArr.length != 5) {
            return null;
        }

        browserdaten = "Zentrale: " + centralID + " Sensortyp: " + messageArr[1] + "(" + messageArr[0] + ") " + ": " + messageArr[2] + " am: " + messageArr[3];

        message = centralID + "#" + message; //+ zentraleID;

        if (sensorData.containsKey(messageArr[1])) {
            sensorData.get(messageArr[1]).add(browserdaten);
        } else {
            tmpList.add(browserdaten);
            sensorData.put(messageArr[1], tmpList);
        }

        return messageArr;
    }

    /**
     * Output of the message on the console
     *
     * @param messageArr
     */
    public void printMessage(String[] messageArr) {

        if (messageArr == null) {
            System.out.println("\n\n_______________________ ERROR _______________________");
            System.out.println("---------------- SENSOR NOT KNOWN ----------------");
            System.out.println("______________________________________________________\n\n");
        } else {
            System.out.println("#######################################");
            System.out.println("Receiving from: " + datapacket.getAddress() + " Port: " + datapacket.getPort());
            System.out.println("Sensortype: " + messageArr[1] + " / SensorID: " + messageArr[0]);
            System.out.println("Value: " + messageArr[2]);
            System.out.println("Time: " + messageArr[3]);
            System.out.println("#######################################\n");
        }
    }
    
    public void setDataPacket(DatagramPacket datapacket){
        this.datapacket = datapacket;
    }
    
    public DatagramPacket getDataPacket(){
        return datapacket;
    }
    
    public Map<String, List<String>> getSensorDatenMap(){
        return sensorData;
    }
    
    public Timestamp getZeitpunkt(){
        return messageTime;
    }
    
    public int getPort(){
        return SERVERPORT;
    }
    
    @Override
    public void run(){
        System.out.println("UDP-Server wurde gestartet.......");
              
        
        try{
            while(true){
                receiveDP();
                printMessage(processMessage());

                Arrays.fill(buffer, (byte) 0);      
            }
        }
        catch(Exception e){
            
        }
        finally{
            
        }
    }

}
