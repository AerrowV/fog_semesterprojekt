package app.entities;

public class Material {

    private int materialId;
    private String description;
    private int length;
    private int amount;
    private String unit;
    private String function;
    private int price;

    public Material(int materialId, String description, int length, int amount, String unit, String function, int price) {
        this.materialId = materialId;
        this.description = description;
        this.length = length;
        this.amount = amount;
        this.unit = unit;
        this.function = function;
        this.price = price;
    }

    public Material(String description, int length, int amount, String unit, String function, int price) {
        this.description = description;
        this.length = length;
        this.amount = amount;
        this.unit = unit;
        this.function = function;
        this.price = price;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
