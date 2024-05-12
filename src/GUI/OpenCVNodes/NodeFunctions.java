package GUI.OpenCVNodes;

public class NodeFunctions {

    @SuppressWarnings("unchecked")
    public static <T extends Number> T add(T a[]) {
        if(a[0] instanceof Integer) {
            return (T) Integer.valueOf(a[0].intValue() + a[1].intValue());
        } else if (a[0] instanceof Float) {
            return (T) Float.valueOf(a[0].floatValue() + a[1].floatValue());
        }
        
        else {
            throw new IllegalArgumentException("Unsupported Number Type");
        }
    }

    // public static <T extends Number> T subtract(T a, T b) {

    // }
}