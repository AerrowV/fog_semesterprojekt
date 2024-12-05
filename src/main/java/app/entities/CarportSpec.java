package app.entities;

public class CarportSpec {

    private double carportId;
    private double length;
    private double width;
    private boolean roofType;


    public CarportSpec(double carportId, double length, double width, boolean roofType) {
        this.carportId = carportId;
        this.length = length;
        this.width = width;
        this.roofType = roofType;
    }

    public double getCarportId() {
        return carportId;
    }

    public void setCarportId(double carportId) {
        this.carportId = carportId;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public boolean isRoofType() {
        return roofType;
    }

    public void setRoofType(boolean roofType) {
        this.roofType = roofType;
    }
}

