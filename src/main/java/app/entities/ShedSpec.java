package app.entities;

public class ShedSpec {

    private int shedId;
    private int length;
    private int width;
    private int carportId;

    public ShedSpec(int shedId, int length, int width) {
        this.shedId = shedId;
        this.length = length;
        this.width = width;
    }

    public ShedSpec(int shedId, int length, int width, int carportId) {
        this.shedId = shedId;
        this.length = length;
        this.width = width;
        this.carportId = carportId;
    }

    public int getShedId() {
        return shedId;
    }

    public void setShedId(int shedId) {
        this.shedId = shedId;
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

    public int getCarportId() {
        return carportId;
    }

    public void setCarportId(int carportId) {
        this.carportId = carportId;
    }
}

