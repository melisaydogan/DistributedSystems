/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensor;

import java.util.Random;

/**
 *
 * @author melisa
 */
public class HumiditySensor extends Sensor {

    private Random random = new Random();
    private double humidity;

    public HumiditySensor(String address, int port) {

        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Humidity";
        humidity = random.nextInt((80 - 40) + 1) + 40;  //integer zwischen 40 und 80
    }

    @Override
    public void simulate() {

        double tmp = random.nextInt(9); // integer zwischen 0 und 9
        tmp /= 10;

        if (humidity >= 80) {
            humidity -= 25;
        } else {
            humidity += tmp;
        }

        humidity = Math.round(humidity * 100) / 100.0;
        sensorValue = String.valueOf(humidity) + "%";
    }

}
