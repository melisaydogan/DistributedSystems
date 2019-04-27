package sensors;

import java.util.Random;

public class RainSensor extends Sensor{
    Random random = new Random();

    public RainSensor(String address, int port){
        SERVERADDRESS = address;
        SERVERPORT = port;
        sensorType = "Rain";
    }
    @Override
    public void simulate(){
        double value = random.nextInt(10);

        if((value % 2) == 0) sensorValue = "no rain";
        else sensorValue = "rain";
    }
}
