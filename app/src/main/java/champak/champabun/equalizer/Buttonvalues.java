
package champak.champabun.equalizer;


public class Buttonvalues
{

    int _B1;
    int _B2;
    int _B3;
    int _bass;
    int _ids;
    int _virtualizer;
    int _vol;

    public Buttonvalues()
    {
    }

    public Buttonvalues(int i, int j, int k, int l, int i1, int j1)
    {
    	this._bass = i;
    	this._vol = j;
    	this._virtualizer = k;
    	this._B1 = l;
    	this._B2 = i1;
    	this._B3 = j1;
    }

    public Buttonvalues(int i, int j, int k, int l, int i1, int j1, int k1)
    {
    	this._ids = i;
    	this._bass = j;
    	this. _vol = k;
    	this._virtualizer = l;
    	this._B1 = i1;
    	this._B2 = j1;
    	this._B3 = k1;
    }

    public int getB1()
    {
        return this._B1;
    }

    public int getB2()
    {
        return this._B2;
    }

    public int getB3()
    {
        return this._B3;
    }

    public int getBass()
    {
        return this._bass;
    }

    public int getIDS()
    {
        return this._ids;
    }

    public int getVirtualizer()
    {
        return this._virtualizer;
    }

    public int getVol()
    {
        return this._vol;
    }

    public void setB1(int i)
    {
    	this._B1 = i;
    }

    public void setB2(int i)
    {
    	this._B2 = i;
    }

    public void setB3(int i)
    {
    	this._B3 = i;
    }

    public void setBass(int i)
    {
    	this._bass = i;
    }

    public void setIDS(int i)
    {
    	this._ids = i;
    }

    public void setVirtualizer(int i)
    {
    	this._virtualizer = i;
    }

    public void setVol(int i)
    {
    	this._vol = i;
    }
}
