package app.services;
import app.entities.Material;
import app.entities.MaterialSpec;
import java.util.List;

public class CarportSvg {
    private double width;
    private double length;
    private Svg carportSvg;

    public CarportSvg(double width, double length) {
        this.width = length;
        this.length = width;
        carportSvg = new Svg(0, 0, "0 0 855 690", "75%");
        carportSvg.addRectangle(0, 0, width, length, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addBeams();
        addRafters();
    }

    private void addBeams() {
        carportSvg.addRectangle(0, 35, 4.5, width, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0, length - 35, 4.5, width, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        double maxRafterSpacing = 55;
        int numberOfRafters = (int) Math.ceil(width / maxRafterSpacing);
        double rafterSpacing = width / (numberOfRafters - 1);

        for (int i = 0; i < numberOfRafters; i++) {
            double x = i * rafterSpacing;
            carportSvg.addRectangle(x, 0.0, length, 4.5, "stroke:#000000; fill: #ffffff");
        }
    }

    public void addMaterials(List<MaterialSpec> materialSpecs, List<Material> materials) {
        double currentX = 10;
        double currentY = 10;
        double padding = 5;

        for (MaterialSpec spec : materialSpecs) {

            Material material = materials.stream().filter(m -> m.getMaterialId() == spec.getMaterialId()).findFirst().orElse(null);

            if (material != null) {
                double materialWidth = material.getLength();
                double materialHeight = 20;

                if (material.getFunction().equalsIgnoreCase("beam")) {

                    if (currentX + materialWidth > width - 10) {
                        currentX = 10;
                        currentY += materialHeight + padding;
                    }
                    carportSvg.addRectangle(currentX, currentY, materialHeight, materialWidth, "stroke:#000000; fill:#aaaaaa;"); // Grey for beams
                    currentX += materialWidth + padding; // Update position
                } else if (material.getFunction().equalsIgnoreCase("rafter")) {
                    double rafterY = currentY;
                    carportSvg.addLine(10, rafterY, width - 10, rafterY);
                    currentY += materialHeight + padding;
                }
            }
        }
    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}