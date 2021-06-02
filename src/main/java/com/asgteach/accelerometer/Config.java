/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asgteach.accelerometer;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 *
 * @author gail
 */
public class Config {

    public static final double WIDTH = 310.5;
    public static final double HEIGHT = 672;
    public static final double BALL_RADIUS = 12;
    public static final double HOLE_RADIUS = 15;
    public static final double BALL_START_X = 225;
    public static final double BALL_START_Y = 27;
    public static final double TOUCH_SLOP = 2.0;
    public static final boolean IS_TOUCH = Platform.isSupported(ConditionalFeature.INPUT_TOUCH);
    
    public static final Rectangle2D SCREEN_BOUNDS = IS_TOUCH ? 
            Screen.getPrimary().getVisualBounds() : new Rectangle2D(0, 0, WIDTH, HEIGHT) ;

    public static double ScaleFactor = 1.0;
    public static double TranslateYFactor = 0.0;
}
