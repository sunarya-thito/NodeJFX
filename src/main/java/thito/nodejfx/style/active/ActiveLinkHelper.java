package thito.nodejfx.style.active;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.*;
import thito.nodejfx.*;

import java.util.*;

public class ActiveLinkHelper {
    private Shape shape;
    private Trail[] trails;
    private ParallelTransition parallelTransition;
    private Color from = Color.WHITE, to = Color.WHITE;
    private NodeLinkContainer container;
    private NodeLinkShape.NodeLinkShapeHandler handler;
    public ActiveLinkHelper(Shape shape, NodeLinkShape.NodeLinkShapeHandler handler) {
        this.handler = handler;
        this.shape = shape;
        init();
        shape.layoutBoundsProperty().addListener(obs -> {
            boolean wasPlaying = playing;
            stop();
            init();
            if (wasPlaying) {
                play();
            }
        });
    }

    public void setSourceColor(Color from) {
        this.from = from;
    }

    public void setTargetColor(Color to) {
        this.to = to;
    }

    private void init() {
        int amount = (int) (Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) / 50);
        init(amount);
    }

    private boolean playing;

    private void init(int amount) {
        setVisible(false);
        amount = Math.abs(amount);
        if (amount <= 0) amount = 1;
        trails = new Trail[amount];
        for (int i = 0; i < amount; i++) {
            trails[i] = new Trail(i);
        }
        parallelTransition = new ParallelTransition(Arrays.stream(trails).map(Trail::getComposite).toArray(Animation[]::new));
    }

    public void play() {
        playing = true;
        setVisible(true);
        parallelTransition.playFrom(Duration.millis(Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) * 10));
    }

    public void stop() {
        playing = false;
        setVisible(false);
        parallelTransition.stop();
    }

    public void setContainer(NodeLinkContainer container) {
        boolean wasPlaying = playing;
        stop();
        this.container = container;
        if (wasPlaying) {
            play();
        }
    }

    private void setVisible(boolean visible) {
        if (trails == null) return;
        if (visible) {
            for (Trail trail : trails) trail.add();
        } else {
            for (Trail trail : trails) trail.remove();
        }
    }

    public class Trail {
        private PathTransition transition;
        private FillTransition fill;
        private ParallelTransition composite;
        private Shape node;

        public Trail(int index) {
            node = (Shape) handler.cloneDummyShape().getComponent();
//            node = new Polygon();
//            node.getPoints().addAll(
//                    0d, -6d, // top left
//                    0d, 6d, // bottom left
//                    12d, 0d); // center right
//            node.setFill(Color.WHITE);
            transition = new PathTransition(Duration.millis(Math.sqrt(Math.pow(shape.getLayoutBounds().getWidth(), 2) + Math.pow(shape.getLayoutBounds().getHeight(), 2)) * 10), shape, node);
            transition.setInterpolator(Interpolator.LINEAR);
            transition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            double target = transition.getDuration().toMillis();
            fill = new FillTransition(transition.getDuration(), node, to, from);
            composite = new ParallelTransition(transition, fill);
            composite.setCycleCount(-1);
            composite.setDelay(Duration.millis(target / trails.length * index));
        }

        public Node getNode() {
            return node;
        }

        public ParallelTransition getComposite() {
            return composite;
        }

        public PathTransition getTransition() {
            return transition;
        }

        public void add() {
            if (container == null) return;
            container.getChildren().remove(node);
            container.getChildren().add(node);
        }

        public void remove() {
            if (container == null) return;
            container.getChildren().remove(node);
        }
    }
}
