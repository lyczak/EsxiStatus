package ga.gaba.EsxiStatus.models;

/**
 * Created by glyczak on 12/9/18.
 */
public class PlotablePerfCounter {
    private int id;
    private String name;
    private String unit;
    private double multiplier;

    public PlotablePerfCounter(int id, String name, String unit, double multiplier) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.multiplier = multiplier;
    }

    public PlotablePerfCounter(int id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        multiplier = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public String toString() {
        return String.format("%s (%s)", name, unit);
    }
}
