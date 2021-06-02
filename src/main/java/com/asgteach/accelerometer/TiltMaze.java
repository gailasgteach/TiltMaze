
package com.asgteach.accelerometer;

import com.gluonhq.attach.util.Constants;
import com.asgteach.accelerometer.views.AppViewManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;

public class TiltMaze extends MobileApplication {

    @Override
    public void init() {
        System.setProperty(Constants.ATTACH_DEBUG, "true");
        AppViewManager.registerViews(this);
    }

    @Override
    public void postInit(Scene scene) {
//        AppViewManager.registerDrawer(this);
        Swatch.YELLOW.assignTo(scene);

        scene.getStylesheets().add(TiltMaze.class.getResource("style.css").toExternalForm());
        //((Stage) scene.getWindow()).getIcons().add(new Image(TiltMaze.class.getResourceAsStream("/tiltmaze-logo.png")));
        System.out.println("height=" + scene.getHeight() + ", width=" + scene.getWidth());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
