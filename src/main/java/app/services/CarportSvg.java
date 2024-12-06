package app.services;

import app.entities.Material;
import app.entities.MaterialSpec;

import java.util.List;

public class CarportSvg {
    private double width;
    private double length;
    private Svg carportSvg;

    public CarportSvg(double width, double length) {
        this.width = width;
        this.length = length;
        carportSvg = new Svg(0, 0, "0 0 855 690", "75%");
        carportSvg.addRectangle(0, 0, length, width, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        addBeams();
        addRafters();
    }

    private void addBeams() {
        carportSvg.addRectangle(0, 35, 4.5, width, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        carportSvg.addRectangle(0, length - 35, 4.5, width, "stroke-width:1px; stroke:#000000; fill: #ffffff");
    }

    private void addRafters() {
        for (double i = 0; i < width; i += 55.714) {
            carportSvg.addRectangle(i, 0.0, length, 4.5, "stroke:#000000; fill: #ffffff");
        }
    }

    // Dynamically add materials based on MaterialSpec and Material
    public void addMaterials(List<MaterialSpec> materialSpecs, List<Material> materials) {
        double currentX = 10; // Starting x-coordinate (with some margin)
        double currentY = 10; // Starting y-coordinate (with some margin)
        double padding = 5; // Space between materials

        for (MaterialSpec spec : materialSpecs) {
            // Find corresponding material for this MaterialSpec
            Material material = materials.stream()
                    .filter(m -> m.getMaterialId() == spec.getMaterialId())
                    .findFirst()
                    .orElse(null);

            if (material != null) {
                double materialWidth = material.getLength(); // Material width
                double materialHeight = 20; // Example fixed height for materials (adjust as needed)

                // Add as rectangle or rafter, depending on material type
                if (material.getFunction().equalsIgnoreCase("beam")) {
                    // Add as a rectangle for beams
                    if (currentX + materialWidth > width - 10) {
                        currentX = 10; // Reset X and move to next row
                        currentY += materialHeight + padding;
                    }
                    carportSvg.addRectangle(currentX, currentY, materialHeight, materialWidth,
                            "stroke:#000000; fill:#aaaaaa;"); // Grey for beams
                    currentX += materialWidth + padding; // Update position
                } else if (material.getFunction().equalsIgnoreCase("rafter")) {
                    // Add rafters as lines (example: horizontal across the carport)
                    double rafterY = currentY; // Example: align rafter at the current Y position
                    carportSvg.addLine(10, rafterY, width - 10, rafterY); // Horizontal line for rafter
                    currentY += materialHeight + padding; // Update position for next material
                } else {
                    // Other material types can be handled similarly
                    System.out.println("Unknown material function, skipping: " + material.getDescription());
                }
            }

        }

    }

    @Override
    public String toString() {
        return carportSvg.toString();
    }
}
