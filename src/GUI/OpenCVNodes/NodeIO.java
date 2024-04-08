package GUI.OpenCVNodes;

import javafx.scene.paint.Color;

public enum NodeIO {
    NUMBER("", Color.web("#4c4c4c")),
    INTEGER("integer", Color.web("#3af9d9")),
    FLOAT("float", Color.web("#3af979")),
    DOUBLE("double", Color.web("#5af93a")),

    TEXT("", null),

    MAT("mat", Color.web("#bb21c6"));

    private String name;
    private Color color;

    NodeIO(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColorCode() {
        return color;
    }
}