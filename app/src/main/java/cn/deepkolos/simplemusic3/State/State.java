package cn.deepkolos.simplemusic3.State;

public interface State<T> {
    T get ();
    void set (T val);
}
