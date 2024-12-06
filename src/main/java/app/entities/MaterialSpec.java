package app.entities;

public class MaterialSpec {

    private int materialSpecId;
    private int carportId;
    private int materialId;
    private int materialOrderAmount;
    private int orderId;

    public MaterialSpec(int materialSpecId, int carportId, int materialId, int materialOrderAmount) {
        this.materialSpecId = materialSpecId;
        this.carportId = carportId;
        this.materialId = materialId;
        this.materialOrderAmount = materialOrderAmount;
    }

    public MaterialSpec(int materialSpecId, int materialOrderAmount, int orderId) {
        this.materialSpecId = materialSpecId;
        this.materialOrderAmount = materialOrderAmount;
        this.orderId = orderId;
    }

    public int getMaterialSpecId() {
        return materialSpecId;
    }

    public void setMaterialSpecId(int materialSpecId) {
        this.materialSpecId = materialSpecId;
    }

    public int getCarportId() {
        return carportId;
    }

    public void setCarportId(int carportId) {
        this.carportId = carportId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getMaterialOrderAmount() {
        return materialOrderAmount;
    }

    public void setMaterialOrderAmount(int materialOrderAmount) {
        this.materialOrderAmount = materialOrderAmount;
    }
}
