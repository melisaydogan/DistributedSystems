/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author capcup
 */
public class Main {
    public static void main(String[] args) {
        String address = "localhost";
        int serverport = 9997;
        
        // define and add sensors to array ..
        Sensor[] sensors = {new TemperatureSensor(address, serverport)};
        
        while (true) {
            for (int i = 0; i < sensors.length; i++) {
                sensors[i].simulate();
                sensors[i].generateMessage();
                sensors[i].send();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
    }
}
