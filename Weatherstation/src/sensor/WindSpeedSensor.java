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
public class WindSpeedSensor extends Sensor {

    private Random random = new Random();
    private double speed;

    public WindSpeedSensor(String address, int port) {

        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Wind Speed";
        speed = random.nextInt(49); // Integer zwischen 0 und 49 (49= starker Wind)
    }

    @Override
    public void simulate() {

        double tmp = random.nextInt(9);
        tmp /= 10;

        speed += tmp;

        speed = Math.round(speed * 100) / 100.0;
        sensorValue = String.valueOf(speed) + "km/h";
    }

}
