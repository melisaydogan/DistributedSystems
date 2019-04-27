package sensors;

import java.util.Random;

public class HumiditySensor extends Sensor{
    private Random random = new Random();
    private double humidity;

    public HumiditySensor(String address, int port){
        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Humidity";
        humidity = random.nextInt((80 - 40) + 1) + 40;
    }

    @Override
    public void simulate(){
        double tmp = random.nextInt(9);
        tmp /= 10;

        if(humidity >=80) humidity -= 25;
        else humidity += tmp;

        humidity = Math.round(humidity *100)/100.0;
        sensorValue = String.valueOf(humidity) + "%";
    }
}
