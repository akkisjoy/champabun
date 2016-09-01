
package champak.champabun.framework.equalizer;


public class Pathvalues {

    int _Y1;
    int _Y2;
    int _Y3;
    int _Y4;
    int _Y5;
    int _id;

    public Pathvalues() {
    }

    public Pathvalues(int i, int j, int k, int l, int i1) {
        this._Y1 = i;
        this._Y2 = j;
        this._Y3 = k;
        this._Y4 = l;
        this._Y5 = i1;
    }

    public Pathvalues(int i, int j, int k, int l, int i1, int j1) {
        this._id = i;
        this._Y1 = j;
        this._Y2 = k;
        this._Y3 = l;
        this._Y4 = i1;
        this._Y5 = j1;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int i) {
        this._id = i;
    }

    public int getY1() {
        return this._Y1;
    }

    public void setY1(int i) {
        this._Y1 = i;
    }

    public int getY2() {
        return this._Y2;
    }

    public void setY2(int i) {
        this._Y2 = i;
    }

    public int getY3() {
        return this._Y3;
    }

    public void setY3(int i) {
        this._Y3 = i;
    }

    public int getY4() {
        return this._Y4;
    }

    public void setY4(int i) {
        this._Y4 = i;
    }

    public int getY5() {
        return this._Y5;
    }

    public void setY5(int i) {
        this._Y5 = i;
    }
}
