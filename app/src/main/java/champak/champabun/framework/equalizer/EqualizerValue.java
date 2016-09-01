package champak.champabun.framework.equalizer;

public class EqualizerValue
{
	private int id;
	// private int min;
	// private int max;
	private int value;

	// private String unit;

	public EqualizerValue(int id, int value)
	{
		// this(id, 0, 0, value, null);
		this.id = id;
		this.value = value;
	}

	// public EqualizerValue(int id, int min, int max, int value, String unit)
	// {
	// this.id = id;
	// this.min = min;
	// this.max = max;
	// this.value = value;
	// this.unit = unit;
	// }

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	// public int getMin()
	// {
	// return min;
	// }
	//
	// public void setMin(int min)
	// {
	// this.min = min;
	// }
	//
	// public int getMax()
	// {
	// return max;
	// }
	//
	// public void setMax(int max)
	// {
	// this.max = max;
	// }

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	// public String getUnit()
	// {
	// return unit;
	// }
	//
	// public void setUnit(String unit)
	// {
	// this.unit = unit;
	// }
}
