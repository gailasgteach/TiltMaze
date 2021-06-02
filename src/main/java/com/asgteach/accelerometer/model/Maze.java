/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asgteach.accelerometer.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author gail
 */
public class Maze {
    private final List<Rectangle> barriers = new ArrayList<>();
    private final List<CircleHole> holes = new ArrayList<>();

    public Maze() {
        buildBarriers();
        buildHoles();
    }

    public List<Rectangle> getBarriers() {
        return barriers;
    }

    public List<CircleHole> getHoles() {
        return holes;
    }
    
    // Rectangle(double x, double y, double width, double height)
    private void buildBarriers() {
        barriers.add(new Rectangle(245, 5, 5, 100));
        barriers.add(new Rectangle(75, 50, 175, 5));
        barriers.add(new Rectangle(25, 155, 165, 5));
        barriers.add(new Rectangle(190, 155, 5, 150));
        barriers.add(new Rectangle(95, 305, 185, 5));
        barriers.add(new Rectangle(300, 175, 100, 5));
        barriers.add(new Rectangle(60, 210, 5, 60));
        barriers.add(new Rectangle(10, 240, 50, 5));
        barriers.add(new Rectangle(245, 365, 5, 30));
        
        barriers.forEach((r) -> {
            r.setFill(Color.BLACK);
        });
        
    }
    
    // CircleHole(double centerX, double centerY, boolean winner, int hole_number)
    private void buildHoles() {
        int hole_number = 1;
        holes.add(new CircleHole(40, 135, false, hole_number++));
        holes.add(new CircleHole(130, 115, false, hole_number++));
        holes.add(new CircleHole(225, 75, false, hole_number++));
        holes.add(new CircleHole(275, 100, false, hole_number++));
        holes.add(new CircleHole(215, 200, false, hole_number++));
        holes.add(new CircleHole(325, 155, false, hole_number++));
        holes.add(new CircleHole(325, 75, false, hole_number++));
        holes.add(new CircleHole(375, 25, false, hole_number++));
        holes.add(new CircleHole(377, 200, false, hole_number++));
        holes.add(new CircleHole(377, 270, false, hole_number++));
        holes.add(new CircleHole(300, 305, false, hole_number++));
        holes.add(new CircleHole(32, 335, false, hole_number++));
        holes.add(new CircleHole(115, 375, false, hole_number++));
        holes.add(new CircleHole(200, 330, false, hole_number++));
        holes.add(new CircleHole(280, 375, false, hole_number++));
        holes.add(new CircleHole(360, 365, false, hole_number++));
        holes.add(new CircleHole(300, 240, false, hole_number++));
        holes.add(new CircleHole(170, 283, false, hole_number++));
        holes.add(new CircleHole(120, 180, false, hole_number++));
        holes.add(new CircleHole(82, 245, false, hole_number++));
        holes.add(new CircleHole(275, 25, true, hole_number++));
        holes.add(new CircleHole(40, 220, true, hole_number++));  
    }
    
    
    
}
