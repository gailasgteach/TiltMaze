/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asgteach.accelerometer.model;

import com.asgteach.accelerometer.Config;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 *
 * @author gail
 */
public class CircleHole {
    private final Circle hole;
    private final boolean winner;
    private final int hole_number;

    public CircleHole(double centerX, double centerY, boolean winner, int hole_number) {
        this.winner = winner; 
        this.hole_number = hole_number;
        hole = new Circle(centerX, centerY, Config.HOLE_RADIUS);
        if (winner) {
            hole.setFill(new RadialGradient(
                0, 0, .5, .5, .5,
                true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0, Color.LIGHTGREEN),
                    new Stop(1, Color.GREEN)
                }));
        } else {
            hole.setFill(new RadialGradient(
                0, 0, .5, .5, .5,
                true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0, Color.DODGERBLUE),
                    new Stop(1, Color.DARKBLUE)
                }));
        }
               
    }

    public Circle getHole() {
        return hole;
    }

    public boolean isWinner() {
        return winner;
    }

    public int getHole_number() {
        return hole_number;
    }
    
    
    
    
}
