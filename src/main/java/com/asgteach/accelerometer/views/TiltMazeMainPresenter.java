package com.asgteach.accelerometer.views;

import com.asgteach.accelerometer.TiltMaze;
import com.asgteach.accelerometer.Config;
import static com.asgteach.accelerometer.Config.BALL_START_X;
import static com.asgteach.accelerometer.Config.BALL_START_Y;
import static com.asgteach.accelerometer.Config.TOUCH_SLOP;
import static com.asgteach.accelerometer.Config.ScaleFactor;
import static com.asgteach.accelerometer.Config.TranslateYFactor;
import com.asgteach.accelerometer.model.CircleHole;
import com.asgteach.accelerometer.model.Maze;
import com.gluonhq.attach.accelerometer.Acceleration;
import com.gluonhq.attach.accelerometer.AccelerometerService;
import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javax.inject.Inject;

public class TiltMazeMainPresenter extends GluonPresenter<TiltMaze> {

    @FXML
    private View tiltmazeView;

    @FXML
    private Pane mazePane;

    @FXML
    private Slider speedSlider;

    @FXML
    private Label timerLabel;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label accelout;

    @FXML
    private Button accel;

    @FXML
    private Rectangle border;

    @FXML
    private Circle ball;

    @Inject
    private Maze myMaze;

    private final ImageView imageView = new ImageView();
    private final HBox hbox = new HBox();
    private final Text text = new Text("Tilt Maze");

    // Implement a timer with a progress bar
    private static final int TIMEFACTOR = 25;
    private final DoubleProperty startTime
            = new SimpleDoubleProperty(TIMEFACTOR);

    private final DoubleProperty timeSeconds
            = new SimpleDoubleProperty(startTime.get());
    private GameTimer gameTimer;
    private AccelerometerService service;
    private Acceleration acceleration;
    private boolean winner = false;
    private double previousX = 0.0;
    private double previousY = 0.0;
    private double currentX = 0.0;
    private double currentY = 0.0;
    private boolean startGame = false;

    private final double screenWidth = Config.SCREEN_BOUNDS.getWidth();
    private final double screenHeight = Config.SCREEN_BOUNDS.getHeight();
    private Path path;
    private double platformAdjust = 1.0;

    public void initialize() {
        tiltmazeView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10);
                text.setFont(Font.font(24));
                hbox.getChildren().addAll(imageView, text);
                appBar.setTitle(hbox);
                imageView.setImage(new Image(getClass().getResource("tiltmaze-logo.png").toExternalForm()));
            }
        });

        System.out.println("Screen width = " + screenWidth
                + " x Screen height = " + screenHeight);
        setScaleFactor();

        DisplayService.create().ifPresent(s -> {
            Dimension2D resolution = s.getScreenResolution();
            System.out.printf("Screen resolution: %.0fx%.0f\n", resolution.getWidth(), resolution.getHeight());
            if (s.isDesktop()) {
                System.out.println("Desktop!");
            } else if (s.isPhone()) {
                System.out.println("Phone!");
            } else if (s.isTablet()) {
                System.out.println("Tablet!");
            }
        });

        if (Platform.isAndroid()) {
            platformAdjust = -1.0;
        }
        gameTimer = new GameTimer();
        accel.textProperty().bind(new When(gameTimer.runningProperty())
                .then("Stop Game")
                .otherwise("Start Game"));

        ball.setTranslateX(BALL_START_X);
        ball.setTranslateY(BALL_START_Y);
        previousX = ball.getTranslateX();
        previousY = ball.getTranslateY();
        ball.setRadius(Config.BALL_RADIUS);
        ball.setFill(new RadialGradient(
                0, 0, .2, .2, .5,
                true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0, Color.WHITESMOKE),
                    new Stop(1, Color.GREY)
                }));
        DropShadow d = new DropShadow();
        d.setOffsetX(3.0);
        d.setOffsetY(3.0);
        ball.setEffect(d);

        // set up ball path
        path = new Path();
        path.setStrokeWidth(4);
        path.setStroke(Color.DODGERBLUE);

        myMaze.getBarriers().forEach(t -> {
            mazePane.getChildren().add(t);
        });

        myMaze.getHoles().forEach(t -> {
            mazePane.getChildren().add(t.getHole());
            Text txt = new Text(t.getHole_number() + "");
            if (t.isWinner()) {
                txt.setText("*");
            }
            mazePane.getChildren().add(txt);
            // center text over hole
            txt.setTranslateX(t.getHole().getCenterX() - txt.getLayoutBounds().getWidth() / 2 - 2);
            txt.setTranslateY(t.getHole().getCenterY() + txt.getLayoutBounds().getHeight() / 2 - 2);
            txt.setFill(Color.WHITE);
        });
        
        mazePane.getChildren().add(path);

        // scale    
        mazePane.setScaleX(ScaleFactor);
        mazePane.setScaleY(ScaleFactor);
        mazePane.setTranslateX(-((border.getWidth() - screenWidth) / 2) * ScaleFactor);
        mazePane.setTranslateY(TranslateYFactor);
        ball.toFront();
        // only allow changing speedSlider if game is not running
        speedSlider.disableProperty().bind(gameTimer.running);        
        myMaze.getBarriers().forEach(t -> {
            System.out.println("Barrier: (" + t.getX() + "," + t.getY()
                    + "), width=" + t.getWidth() + ", height=" + t.getHeight());
        });
        myMaze.getHoles().forEach(t -> {
            System.out.println("Hole: (" + t.getHole().getCenterX() + ","
                    + t.getHole().getCenterY() + ")");
        });

        timeSeconds.set(startTime.get());
        // Bind the timerLabel text property to the timeSeconds property
        timerLabel.textProperty().bind(
                Bindings.format("%4.1f", timeSeconds));
        timerLabel.setPrefWidth(40);
        // Configure the progress
        // Bind the progress progressProperty
        // to the timeSeconds property
        progress.setRadius(25);
        progress.setStyle("-fx-color: blue;");
        progress.progressProperty().bind(timeSeconds.divide(startTime));

        timerLabel.toFront();
        speedSlider.valueProperty().addListener((o) -> {
            startTime.set(TIMEFACTOR / speedSlider.getValue());
            timeSeconds.set(startTime.get());
        });

        speedSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                if (n <= 0.5) {
                    return "Novice";
                }
                if (n <= 1.0) {
                    return "Intermediate";
                }
                if (n <= 1.5) {
                    return "Advanced";
                }

                return "Expert";
            }

            @Override
            public Double fromString(String s) {
                switch (s) {
                    case "Novice":
                        return 0.5d;
                    case "Intermediate":
                        return 1d;
                    case "Advanced":
                        return 1.5d;
                    case "Expert":
                        return 2d;

                    default:
                        return 2d;
                }
            }
        });

        accel.setOnAction((t) -> {
            if (gameTimer.isRunning()) {
                gameTimer.stop();
                if (service != null) {
                    service.stop();
                }
            } else {
                setupGame();
                accelout.setText("Tilt Maze");
                if (service != null) {
                    service.start();
                }
                gameTimer.start();
            }
        });

        try {
            service = AccelerometerService.create().get();
            System.out.println("Got accelerometer service.");
        } catch (Exception e) {
            // No accelerometer, using mouse events.
            System.out.println("No accelerometer service.");
            mazePane.setOnMouseMoved(evt -> {
                currentX = evt.getX();
                currentY = evt.getY();
            });
            ball.setOnMousePressed(evt -> {
                startGame = true;
            });
        }
    }

    private void setScaleFactor() {
        // set the scale factor based on screen size
        double scaleFactorX = (screenWidth - 10.0) / border.getWidth();
        double scaleFactorY = (screenHeight - 300.0) / border.getHeight();
        ScaleFactor = Math.min(scaleFactorX, scaleFactorY);
        ScaleFactor = Math.min(ScaleFactor, 1.5);
        System.out.println("ScaleFactor = " + ScaleFactor);
        if (ScaleFactor <= .8) {
            TranslateYFactor = 0.0;
        } else if (ScaleFactor <= 1.2) {
            TranslateYFactor = 40;
        } else if (ScaleFactor <= 1.5) {
            TranslateYFactor = 85;
        }
        System.out.println("TranslateYFactor = " + TranslateYFactor);
    }

    private void setupGame() {
        timeSeconds.set(startTime.get());
        System.out.println("path elements = " + path.getElements().size());
        path.getElements().clear();
        mazePane.getChildren().remove(path);
        path = null;
        System.gc();
        path = new Path();
        path.setStrokeWidth(4);
        path.setStroke(Color.DODGERBLUE);
        mazePane.getChildren().add(path);
        ball.toFront();
        ball.setTranslateX(BALL_START_X);
        ball.setTranslateY(BALL_START_Y);
        ball.setOpacity(1.0);
        previousX = ball.getTranslateX();
        previousY = ball.getTranslateY();
        path.setOpacity(1.0);
        path.getElements()
            .add(new MoveTo(ball.getTranslateX(), ball.getTranslateY()));
    }

    private boolean collisionX(double vx) {
        return ball.getTranslateX() + ball.getRadius() + vx
                > (border.getWidth() + border.getX()
                + border.getLayoutX() - border.getStrokeWidth())
                || ball.getTranslateX() - ball.getRadius() + vx
                < (border.getX() + border.getStrokeWidth()
                + border.getLayoutX());
    }

    private boolean collisionY(double vy) {
        return ball.getTranslateY() - ball.getRadius() + vy
                < (border.getY() + border.getStrokeWidth()
                + border.getLayoutY())
                || ball.getTranslateY() + ball.getRadius() + vy
                > (border.getHeight() + border.getY()
                + border.getLayoutY() - border.getStrokeWidth());
    }

    private double fdistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private boolean fellInHole() {
        for (CircleHole hole : myMaze.getHoles()) {
            Circle c = hole.getHole();
            var d = fdistance(ball.getTranslateX(),
                    ball.getTranslateY(),
                    c.getCenterX(),
                    c.getCenterY());
            if (d <= c.getRadius() + ball.getRadius() - TOUCH_SLOP) {
                gameTimer.stop();
                startGame = false;
                winner = hole.isWinner();
                doFinish(c.getCenterX(), c.getCenterY());
                return true;
            }
        }
        return false;
    }

    // We timed out!
    private void doTimeout() {
        gameTimer.stop();
        startGame = false;
        accelout.setText("You Timed Out!");
        setupGame();
    }

    // Animate the ball "into" the hole
    private void doFinish(double xEnd, double yEnd) {
        Timeline finishTimeLine = new Timeline();
        finishTimeLine.getKeyFrames().clear();

        finishTimeLine.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(ball.translateXProperty(), xEnd - ball.getCenterX()),
                        new KeyValue(ball.translateYProperty(), yEnd - ball.getCenterY())),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(ball.opacityProperty(), 1.0),
                        new KeyValue(path.opacityProperty(), 1.0)),
                
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(ball.opacityProperty(), 0),
                        new KeyValue(path.opacityProperty(), 0))
        );

        finishTimeLine.setOnFinished(e -> {
            if (winner) {
                accelout.setText(String.format("You Won! %4.1f secs", startTime.get() - timeSeconds.get()));
            } else {
                accelout.setText("You Lost!");
            }
            setupGame();

        });
        finishTimeLine.play();
    }

    private void barrierDetection(double xv, double yv) {
        // first check the rectangular barriers
        boolean collisionFoundX = false;
        boolean collisionFoundY = false;
        for (Rectangle barrier : myMaze.getBarriers()) {

            //If I keep moving in my current X direction, will I collide with the barrier?
            if (ball.getTranslateX() + ball.getRadius() + xv > barrier.getX()
                    && ball.getTranslateX() + xv - ball.getRadius() < barrier.getX() + barrier.getWidth()
                    && ball.getTranslateY() + ball.getRadius() > barrier.getY()
                    && ball.getTranslateY() - ball.getRadius() < barrier.getY() + barrier.getHeight()) {
                collisionFoundX = true;
            }
            //If I keep moving in my current Y direction, will I collide with the barrier?
            if (ball.getTranslateX() + ball.getRadius() > barrier.getX()
                    && ball.getTranslateX() - ball.getRadius() < barrier.getX() + barrier.getWidth()
                    && ball.getTranslateY() + ball.getRadius() + yv > barrier.getY()
                    && ball.getTranslateY() - ball.getRadius() + yv < barrier.getY() + barrier.getHeight()) {
                collisionFoundY = true;
            }
        }

        boolean xnew = false;
        boolean ynew = false;
        // next check the outer walls
        if (!collisionFoundX && !collisionX(xv)) {
            ball.setTranslateX(ball.getTranslateX() + xv);
            xnew = true;
        }
        if (!collisionFoundY && !collisionY(yv)) {
            ball.setTranslateY(ball.getTranslateY() + yv);
            ynew = true;
        }
        if (xnew || ynew) {
            path.getElements()
            .add(new LineTo(ball.getTranslateX(), ball.getTranslateY()));
        }
    }

    private void update() {
        double xfactor;
        double yfactor;
        int fr = gameTimer.getFrameRate() <= 0 ? 60 : gameTimer.getFrameRate();
        timeSeconds.set(timeSeconds.get() - (1.0 / fr));
        if (timeSeconds.get() <= 0.0) {
            doTimeout();
        } else {
            try {
                acceleration = service.getCurrentAcceleration();
                xfactor = platformAdjust * acceleration.getX() * speedSlider.getValue();
                yfactor = platformAdjust * acceleration.getY() * speedSlider.getValue() * -1;
            } catch (Exception e) {
                // no accelerometer service; use mouse input
                if (startGame) {
                    xfactor = (currentX - previousX);
                    previousX = currentX;
                    yfactor = (currentY - previousY);
                    previousY = currentY;
                } else {
                    xfactor = 0.0;
                    yfactor = 0.0;
                }
            }
            // Did we fall into a hole? If so, we won't return
            if (!fellInHole()) {
                barrierDetection(xfactor, yfactor);
            }
        }
    }

    private class GameTimer extends AnimationTimer {

        private final BooleanProperty running = new SimpleBooleanProperty(false);
        long lastFrameTime = -1L;
        long deltaTimeNano;
        IntegerProperty frameRate = new SimpleIntegerProperty(60);

        public boolean isRunning() {
            return running.get();
        }

        public ReadOnlyBooleanProperty runningProperty() {
            return running;
        }

        public int getFrameRate() {
            return frameRate.get();
        }

        public IntegerProperty frameRateProperty() {
            return frameRate;
        }

        @Override
        public void handle(long currentTimeNano) {
            updateFrameTime(currentTimeNano);
            updateFrameRate();
            update();
        }

        protected void updateFrameTime(long currentTimeNano) {
            deltaTimeNano = currentTimeNano - lastFrameTime;
            lastFrameTime = currentTimeNano;
        }

        protected void updateFrameRate() {
            frameRate.set((int) Math.round(getFrameRateHertz()));
        }

        public long getDeltaTimeNano() {
            return deltaTimeNano;
        }

        public double getFrameRateHertz() {
            double fr = 1d / deltaTimeNano;
            return fr * 1e9;
        }

        @Override
        public void start() {
            super.start();
            running.set(true);
        }

        @Override
        public void stop() {
            super.stop();
            running.set(false);
        }

    }
}
