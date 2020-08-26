package com.deeptha.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class View_Loader {
    private Pane view;

    public Pane getPage(String fileName){
        URL fileURL = Admin_Controller.class.getResource("/com/deeptha/admin/" + fileName + ".fxml");
        try {
            view = new FXMLLoader().load(fileURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

}