/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weatherstation;

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
 * @author melisa
 */
public class Weatherstation extends Thread {

    private final int SERVERPORT;
    private DatagramSocket datasocket;
    private DatagramPacket datapacket;
    private final byte[] buffer = new byte[1024];
    Map<String, List<String>> sensorData = new HashMap<>();
    private Timestamp messageTime;
    private final String stationID;// = UUID.randomUUID().toString();
    private String actualWeather = " / Keine Wetterdaten verfuegbar";

    public Weatherstation() {
        SERVERPORT = 9997;
        stationID = UUID.randomUUID().toString();
    }

    /**
     *
     * empfängt Datagram Paket
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
     * verarbeitet Nachricht und speichert in Map
     *
     */
    public String[] processMessage() {
        System.out.println("ProcessMessage!!!!!");
        String message = new String(datapacket.getData());
        String browserdata;
        List<String> tmpList = new ArrayList<>();

        String[] messageArr = message.split("#");

        if (messageArr.length != 5) {
            return null;
        }

        browserdata = "Station: " + stationID + " Sensortype: " + messageArr[1] + "(" + messageArr[0] + ") " + ": " + messageArr[2] + " @: " + messageArr[3];

        message = stationID + "#" + message; //+ stationID;

        if (sensorData.containsKey(messageArr[1])) {
            sensorData.get(messageArr[1]).add(browserdata);
        } else {
            tmpList.add(browserdata);
            sensorData.put(messageArr[1], tmpList);
        }

        return messageArr;
    }
    
    /**
     *  Ausgabe der Nachricht auf der Konsole
     */
    public void printMessage(String[] messageArr) {

        if (messageArr == null) {
            System.out.println("\n\n_______________________ ERROR _______________________");
            System.out.println("---------------- SENSOR Unknown ----------------");
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

    public String getWeather(){
        return actualWeather;
    }
    public String getStationid(){
        return stationID;
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
