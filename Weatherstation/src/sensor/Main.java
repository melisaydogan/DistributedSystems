/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author melisa
 */
public class Main {

    public static void main(String[] args) {
        String address = "localhost";
        int serverport = 9991;

        // define and add sensors to array ..
        Sensor[] sensors = {new TemperatureSensor(address, serverport), new HumiditySensor(address, serverport), new RainSensor(address, serverport), new WindSpeedSensor(address, serverport)};
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
