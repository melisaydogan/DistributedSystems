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
public class TemperatureSensor extends Sensor {

    private Random random = new Random();
    private double temperature;

    public TemperatureSensor(String address, int port) {

        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Temperature";
        temperature = random.nextInt(35); //Integer zwischen 0 und 35
    }

    @Override
    public void simulate() {

            double tmp = random.nextInt(9);
            tmp /= 10;

            temperature += tmp;

            temperature = Math.round(temperature * 100) / 100.0;
            sensorValue = String.valueOf(temperature) + "C";

    }

}
