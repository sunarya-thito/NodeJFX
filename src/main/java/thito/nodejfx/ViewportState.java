package thito.nodejfx;

import java.io.Serializable;

public class ViewportState implements Serializable {
    private double panX;
    private double panY;
    private int zoom;

    public void setPanX(double panX) {
        this.panX = panX;
    }

    public void setPanY(double panY) {
        this.panY = panY;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public double getPanX() {
        return panX;
    }

    public double getPanY() {
        return panY;
    }

    public int getZoom() {
        return zoom;
    }
}
