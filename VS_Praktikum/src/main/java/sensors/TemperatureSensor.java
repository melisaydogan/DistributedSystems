/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensors;

import java.util.Random;

/**
 *
 * @author capcup
 */
public class TemperatureSensor extends Sensor {

    private Random random = new Random();
    private double temperature;

    public TemperatureSensor(String address, int port) {
        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Temperature";
        temperature = random.nextInt((22 - 16) + 1) + 16;
    }

    @Override
    public void simulate() {

        double tmp = random.nextInt(4);
        tmp /= 10;

        if (temperature >= 23) {
            temperature -= 7;
        } else {
            temperature += tmp;
        }

        temperature = Math.round(temperature * 100) / 100.0;
        sensorValue = String.valueOf(temperature) + "C";
    }

}
