

package champak.champabun.equalizer;


public class Presetvalues
{

    int _PY1;
    int _PY2;
    int _PY3;
    int _PY4;
    int _PY5;
    int _Pid;
    String _Presetname;

    public Presetvalues()
    {
    }

    public Presetvalues(int i, int j, int k, int l, int i1)
    {
    	this._PY1 = i;
    	this._PY2 = j;
    	this._PY3 = k;
    	this._PY4 = l;
    	this._PY5 = i1;
    }

    public Presetvalues(int i, String s, int j, int k, int l, int i1, int j1)
    {
    	this._Pid = i;
    	this._Presetname = s;
    	this._PY1 = j;
    	this._PY2 = k;
    	this._PY3 = l;
    	this._PY4 = i1;
    	this._PY5 = j1;
    }

    public Presetvalues(String s, int i, int j, int k, int l, int i1)
    {
    	this._Presetname = s;
    	this._PY1 = i;
    	this._PY2 = j;
    	this._PY3 = k;
    	this._PY4 = l;
    	this._PY5 = i1;
    }

    public int getPID()
    {
        return this._Pid;
    }

    public String getPRESETNAME()
    {
        return this._Presetname;
    }

    public int getPY1()
    {
        return this._PY1;
    }

    public int getPY2()
    {
        return this._PY2;
    }

    public int getPY3()
    {
        return this._PY3;
    }

    public int getPY4()
    {
        return this._PY4;
    }

    public int getPY5()
    {
        return this._PY5;
    }

    public void setPID(int i)
    {
    	this._Pid = i;
    }

    public void setPRESETNAME(String s)
    {
    	this._Presetname = s;
    }

    public void setPY1(int i)
    {
    	this._PY1 = i;
    }

    public void setPY2(int i)
    {
    	this._PY2 = i;
    }

    public void setPY3(int i)
    {
    	this._PY3 = i;
    }

    public void setPY4(int i)
    {
    	this._PY4 = i;
    }

    public void setPY5(int i)
    {
    	this._PY5 = i;
    }
}
