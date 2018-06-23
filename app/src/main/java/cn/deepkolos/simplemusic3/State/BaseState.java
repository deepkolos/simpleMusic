package cn.deepkolos.simplemusic3.State;

import android.content.Context;
import android.util.ArrayMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class BaseState {
    protected static WeakReference<Context> contextRef;
    private static ArrayMap<Class, ArrayList<BaseState>> map = new ArrayMap<>();
    protected OnUpdateFunc onUpdate;

    public static void setContext(Context context) {
        contextRef = new WeakReference<>(context);
    }
    private static void subscribe (Class type, BaseState obj) {
        ArrayList<BaseState> list = map.get(type);

        if (list == null) {
            list = new ArrayList<>();
            map.put(type, list);
        }

        list.add(obj);
    }

    private static void unSubscribe (Class type, BaseState obj) {
        ArrayList<BaseState> list = map.get(type);

        if (list == null) return;

        list.remove(obj);
    }

    private static void broadcast (Class type) {
        ArrayList<BaseState> list = map.get(type);

        if (list == null) return;

        Iterator<BaseState> iterator = list.iterator();
        while (iterator.hasNext()) {
            BaseState state = iterator.next();
            state.notifyDataChanged(false);
        }
    }

    public void setOnUpdate(OnUpdateFunc onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void subscribe () {
        subscribe(this.getClass(), this);
    }

    public void unSubscribe () {
        unSubscribe(this.getClass(), this);
    }

    public void notifyToAll () {
        broadcast(this.getClass());
    }

    public void notifyDataChanged(boolean force) {
        if (this.onUpdate != null)
            this.onUpdate.call();
    }

    public interface OnUpdateFunc {
        void call ();
    }
}
