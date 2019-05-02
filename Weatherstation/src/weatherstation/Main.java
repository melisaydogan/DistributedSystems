/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weatherstation;

/**
 *
 * @author melisa
 */
public class Main {

    public static void main(String[] args) {
        Weatherstation weatherstation = new Weatherstation();
        String herstelleradresse = "localhost";
        int herstellerport = 5551;

        Http_Controller http_controller = new Http_Controller(weatherstation, herstelleradresse, herstellerport);

        weatherstation.start();
        http_controller.start();
    }
}
