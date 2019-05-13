package weatherstation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
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
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     *
     * Verarbeitet den Request und bereitet angepassten Datensatz für prepareMessage vor
     */
    public boolean editReq(){

        String[] tmp = reqMsgs[0].split(" ");
        String[] splittedMsg = tmp[1].split("&");
        anzahlSensorDaten=-1;
        Map<String, List<String>> sensorDaten = weatherstation.getSensorDatenMap();
        angefragteDaten = new ArrayList<>();
        String sensorTyp="";
        headline="ANGEFORDERTER DATENSATZ: ";

        //Parameter aus Adresse bekommen // Fehler werden noch nicht abgefangen
        if(splittedMsg.length==2){
            anzahlSensorDaten = Integer.parseInt(splittedMsg[1]);
        }

        //Nur GET Anfragen bearbeiten
        if(!(tmp[0].equals("GET"))) return false;

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

            sendMsg(prepHerstellerData(getHerstellerData()));

            return false;

        }
        //Keine Datensätze gefunden
        else{
            headline += "UNBEKANNT";

        }

        headline = headline + " " + weatherstation.getWeather();
        return true;
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
                datensatz = datensatz + "<br>" + angefragteDaten.get(iterator);
            }
        }

        //Leere Zeile nach HTTP Header
        return "HTTP/1.1 200 OK\ncontent-Type:text/html\n\n"
                + "<html><head><title>Verteilte Systeme</title></head><body><p>"
                + headline + "<br> ______________________________________________________________________________ <br><br>"
                + datensatz
                + "</p></body></html>";
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

    public String prepHerstellerData(String data){
        String[] splitData = data.split("#");
        headline = headline + " " + convertWeather(splitData[0].split("&"));
        data = "";

        for(int i=1;i<splitData.length;i++){
            data = data + "<br>" + splitData[i];
        }

        return "HTTP/1.1 200 OK\ncontent-Type:text/html\n\n"
                + "<html><head><title>Verteilte Systeme</title></head><body><p>"
                + headline + "<br> ______________________________________________________________________________ <br><br>"
                + data
                + "</p></body></html>";
    }

    public String convertWeather(String[] weather){
        if(weather.length != 4){
            return null;
        }

        return " / Aktuelles Wetter fuer " + weather[0]
                + " / Temperatur: " + weather[1]
                + " C / min: " + weather[2]
                + " C / max: " + weather[3]
                + " C";
    }

    public String getHerstellerData(){
        try {
            StringBuffer buf = new StringBuffer();
            String data;
            Socket clientSocket = new Socket(HERSTELLER_ADRESSE, HERSTELLER_PORT);
            //Socket clientSocket = new Socket("localhost", 5551);
            clientSocket.getOutputStream().write((weatherstation.getStationid()+ "\n").getBytes());

            BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            data = bf.readLine();
            bf.close();
            return data;

        } catch (IOException ex) {
            Logger.getLogger(HTTP_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            if(editReq())
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
