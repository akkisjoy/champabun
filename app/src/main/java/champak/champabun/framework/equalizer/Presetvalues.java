

package champak.champabun.framework.equalizer;


public class Presetvalues {

    int _PY1;
    int _PY2;
    int _PY3;
    int _PY4;
    int _PY5;
    String _Presetname;

    public Presetvalues() {
    }

    public Presetvalues(String s, int i, int j, int k, int l, int i1) {
        this._Presetname = s;
        this._PY1 = i;
        this._PY2 = j;
        this._PY3 = k;
        this._PY4 = l;
        this._PY5 = i1;
    }

    public String getPRESETNAME() {
        return this._Presetname;
    }

    public void setPRESETNAME(String s) {
        this._Presetname = s;
    }

    public int getPY1() {
        return this._PY1;
    }

    public void setPY1(int i) {
        this._PY1 = i;
    }

    public int getPY2() {
        return this._PY2;
    }

    public void setPY2(int i) {
        this._PY2 = i;
    }

    public int getPY3() {
        return this._PY3;
    }

    public void setPY3(int i) {
        this._PY3 = i;
    }

    public int getPY4() {
        return this._PY4;
    }

    public void setPY4(int i) {
        this._PY4 = i;
    }

    public int getPY5() {
        return this._PY5;
    }

    public void setPY5(int i) {
        this._PY5 = i;
    }
}
