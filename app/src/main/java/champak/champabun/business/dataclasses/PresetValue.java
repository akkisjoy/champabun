package champak.champabun.business.dataclasses;

import champak.champabun.framework.equalizer.EqualizerValue;

public class PresetValue {
    private int value;
    private String name;
    private EqualizerValue[] equalizerValue;

    public PresetValue(int value, String name, EqualizerValue[] equalizerValue) {
        this.value = value;
        this.name = name;
        this.equalizerValue = equalizerValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EqualizerValue[] getEqualizerValue() {
        return equalizerValue;
    }

}
