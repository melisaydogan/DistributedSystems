package sensors;

import java.util.Random;

public class WindSpeedSensor extends Sensor{
    private Random random = new Random();
    private double speed;

    public WindSpeedSensor(String address, int port){
        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Wind Speed";
        speed = random.nextInt((2 - 1) + 1) + 1;
    }
    @Override
    public void simulate(){
        double tmp = random.nextInt(2);
        tmp /= 10;

        if(speed >=3) speed -= 1.5;
        else speed += tmp;

        speed = Math.round(speed *100)/100.0;
        sensorValue = String.valueOf(speed) + "U/sec";
    }
}
