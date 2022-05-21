package gwoart;

public class TestCase {
    private String name;
    private double value;

    TestCase() {

    }

    TestCase(String _name, double _value) {
        this.name = _name;
        this.value = _value;
    }

    public String getName() { return this.name; }
    public double getValue() { return this.value; }

}