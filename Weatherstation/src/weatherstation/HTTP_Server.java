package weatherstation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTP_Server extends Thread {
    private Socket connectionSocket;
    private String[] reqMsgs = new String[100];
    private final Weatherstation weatherstation;
    private List<String> angefragteDaten;
    private String headline;
    private int anzahlSensorDaten;
    private final String HERSTELLER_ADRESSE;
    private final int HERSTELLER_PORT;

    public HTTP_Server(){
        weatherstation = new Weatherstation();
        HERSTELLER_ADRESSE = null;
        HERSTELLER_PORT = 0;
    }

    /**
     * Konstruktor bekommt Zentrale(Wetterstation) übergeben, um die generierten Sensordaten benutzen zu können
     * bindet den Serverport
     */
    public HTTP_Server(Weatherstation zentrale, Socket connectionSocket, String herstelleradresse, int herstellerport){
        this.weatherstation = zentrale;
        this.connectionSocket = connectionSocket;
        HERSTELLER_ADRESSE = herstelleradresse;
        HERSTELLER_PORT = herstellerport;
    }

    public HTTP_Server(Socket connectionSocket){
        this.weatherstation = null;
        HERSTELLER_ADRESSE = null;
        HERSTELLER_PORT = 0;
        this.connectionSocket = connectionSocket;
    }

    public HTTP_Server(Weatherstation zentrale, Socket connectionSocket){
        this.weatherstation = zentrale;
        HERSTELLER_ADRESSE = null;
        HERSTELLER_PORT = 0;
        this.connectionSocket = connectionSocket;
    }


    /**
     * Nimmt Request Message vom Client entgegen und speichert die komplette Nachricht in einem Array
     * @param inFromClient
     */
    public void getReq(BufferedReader inFromClient){
        int pos = 0;
        try{
            while((reqMsgs[pos]=inFromClient.readLine()) != null && reqMsgs[pos].length() != 0){
                pos++;
            }

            // Print incoming request
            for(int i =0;i<reqMsgs.length;i++){
                if(reqMsgs[i]!=null && !reqMsgs[i].isEmpty())
                    System.out.println(reqMsgs[i]);
            }
            System.out.println("------------------------");
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     *
     * Verarbeitet den Request und bereitet angepassten Datensatz für prepareMessage vor
     */
    public void editReq(){

        String[] tmp = reqMsgs[0].split(" ");
        String[] splittedMsg = tmp[1].split("&");
        anzahlSensorDaten=-1;
        Map<String, List<String>> sensorDaten = weatherstation.getSensorDataShortMap();
        angefragteDaten = new ArrayList<>();
        String sensorTyp="";
        headline="ANGEFORDERTER DATENSATZ: ";

        //Parameter aus Adresse bekommen // Fehler werden noch nicht abgefangen
        if(splittedMsg.length==2){
            anzahlSensorDaten = Integer.parseInt(splittedMsg[1]);
        }

        //Nur GET Anfragen bearbeiten
        if(!(tmp[0].equals("GET"))) return;

        if(splittedMsg[0].equals("/")) sensorTyp = "All";
        else{
            splittedMsg[0] = splittedMsg[0].substring(1);
            splittedMsg[0] = splittedMsg[0].substring(0,1).toUpperCase() + splittedMsg[0].substring(1).toLowerCase();

            if(sensorDaten.containsKey(splittedMsg[0])){
                sensorTyp = splittedMsg[0];
            }
            else sensorTyp = "none";
        }

        //Bestimmten Datensatz ausgeben
        if(sensorDaten.containsKey(sensorTyp)){
            headline = headline + sensorTyp;
            angefragteDaten = sensorDaten.get(sensorTyp);
        }
        //Alle Datensätze ausgeben
        else if(sensorTyp.equals("All")){
            headline += "All";

            for(Map.Entry<String, List<String>> entry : sensorDaten.entrySet()) {
                String key = entry.getKey();
                for (String value : entry.getValue()) {
                    angefragteDaten.add(value);
                }
            }
        }
        //Keine Datensätze gefunden
        else{
            headline += "UNBEKANNT";
        }

        headline = headline + " " + weatherstation.getWeather();
    }

    /**
     * Schreibt angefragte Daten in eine HTML Nachricht
     *
     */
    public String prepareMessage(){
        String datensatz="";
        int iterator=0;

        if(angefragteDaten.isEmpty()){
            datensatz = "<br> Keine Eintraege vorhanden";
        }
        else{
            if(anzahlSensorDaten>=0){
                iterator = angefragteDaten.size() - anzahlSensorDaten;
                if(iterator<0) iterator =0;
            }

            for(;iterator<angefragteDaten.size();iterator++){
                String[] ary = angefragteDaten.get(iterator).split(" ");
                //datensatz = datensatz + "<br>" + angefragteDaten.get(iterator);
                datensatz = datensatz + "<tr>" +
                        "<td style=\" width:30%;border-width:2px; text-align:center; border-style:ridge;border-color: black;\">"+ary[0]+"</td>\n" +
                        "<td style=\" width:30%;border-width:2px; text-align:center; border-style:ridge;border-color: black;\">"+ary[1]+"</td>\n" +
                        "<td style=\" width:30%;border-width:2px; text-align:center; border-style:ridge;border-color: black;\">"+ary[2]+" "+ary[3]+"</td></tr>";
            }
        }

        String tableInit = "<table style=\"width:102%;background-color:grey;position: fixed;margin: 0em; font-size: 1.2em;font-weight: bold; margin:-1.4em;\"><tr>" +
                "<th style=\" width:20%;border-width:2px; border-style:ridge;border-color: black;\">Sensortype</th>"
                +"<th style=\" width:20%;border-width:2px; border-style:ridge;border-color: black;\">Value</th>"
                +"<th style=\" width:20%;border-width:2px; border-style:ridge;border-color: black;\">Date</th> </tr> </table>"
                +"<table style=\"width:103%; margin: -1.4em; border-width:1px; border-style:solid; border-color: black; background-color:#d0d0d0;font-weight: bold;\">";

        //Leere Zeile nach HTTP Header
        return "HTTP/1.1 200 OK\ncontent-Type:text/html\r\n\r\n"
                + "<html><head><title>Verteilte Systeme</title></head><body><p>"
                + headline
                + "<br> ______________________________________________________________________________ <br><br>"
                + tableInit
                + datensatz
                + "</table></p></body></html>";
    }

    /**
     * Sendet Nachricht an den TCP Clienten
     * @param httpResponse
     */
    public void sendMsg(String httpResponse){
        try {
            connectionSocket.getOutputStream().write(httpResponse.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(HTTP_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setReqMsgs(String[] reqMsgs){
        this.reqMsgs = reqMsgs;
    }

    public String[] getReqMsg(){
        return reqMsgs;
    }

    public void setConnectionSocket(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
    }

    public void setTestDaten(List<String> reqData){
        angefragteDaten = reqData;
    }

    public void setAnzahlDaten(int anzahl){
        anzahlSensorDaten = anzahl;
    }


    @Override
    public void run(){
        System.out.println("HTTP Received");
        try{
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            getReq(inFromClient);
            editReq();

            //System.out.println(prepareMessage());
            sendMsg(prepareMessage());
        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            try {
                connectionSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(HTTP_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
