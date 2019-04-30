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
public class RainSensor extends Sensor {

    private Random random = new Random();
    private double rain;

    public RainSensor(String address, int port) {

        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Rainfall";
        rain = random.nextInt((12 - 1) + 1) + 1;    // integer zwischen 1 und 12
    }

    @Override
    public void simulate() {

        double tmp = random.nextInt(6);
        tmp /= 10;

        rain += tmp;

        rain = Math.round(rain * 100) / 100.0;
        sensorValue = String.valueOf(rain) + "mm";

    }

}
