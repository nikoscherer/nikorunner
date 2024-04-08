package GUI.OpenCVNodes;

public enum NodeType {
    NEWINTEGER("New Integer", new NodeIO[] {NodeIO.TEXT}, new NodeIO[] {NodeIO.INTEGER}),
    NEWFLOAT("New Float", new NodeIO[] {NodeIO.TEXT}, new NodeIO[] {NodeIO.FLOAT}),
    NEWDOUBLE("New Double", new NodeIO[] {NodeIO.TEXT}, new NodeIO[] {NodeIO.DOUBLE}),
    ADD("Add", new NodeIO[] {NodeIO.NUMBER, NodeIO.NUMBER}, new NodeIO[] {NodeIO.INTEGER}),

    OUTPUT("Output", new NodeIO[] {NodeIO.MAT}, null);

    String name;
    private NodeIO[] inputs;
    private NodeIO[] outputs;

    NodeType(String name, NodeIO[] inputs, NodeIO[] outputs) {
        if(name == null) {
            try {
                throw new Exception("name for NodeType cannot be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(inputs == null) {
            inputs = new NodeIO[]{};
        }
        if(outputs == null) {
            outputs = new NodeIO[]{};
        }
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getName() {
        return name;
    }


    public NodeIO[] getInputs() {
        return inputs;
    }

    public NodeIO getInputAtIndex(int index) {
        return inputs[index];
    }

    public NodeIO[] getOutputs() {
        return outputs;
    }
    public NodeIO getOutputAtIndex(int index) {
        return outputs[index];
    }
}