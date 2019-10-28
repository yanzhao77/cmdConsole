package sample;

import entity.ConsoleTextAera;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    ConsoleTextAera testArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testArea.init(null);
    }
}
