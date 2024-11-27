package app.entities;

public class CarportSpec {

    private int carportId;
    private int length;
    private int width;
    private boolean roofType;
    private int roofTilt;

    public CarportSpec(int carportId, int length, int width, boolean roofType, int roofTilt) {
        this.carportId = carportId;
        this.length = length;
        this.width = width;
        this.roofType = roofType;
        this.roofTilt = roofTilt;
    }

    public int getCarportId() {
        return carportId;
    }

    public void setCarportId(int carportId) {
        this.carportId = carportId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isRoofType() {
        return roofType;
    }

    public void setRoofType(boolean roofType) {
        this.roofType = roofType;
    }

    public int getRoofTilt() {
        return roofTilt;
    }

    public void setRoofTilt(int roofTilt) {
        this.roofTilt = roofTilt;
    }
}

