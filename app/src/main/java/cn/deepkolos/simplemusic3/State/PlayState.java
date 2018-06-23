package cn.deepkolos.simplemusic3.State;

public class PlayState extends BaseState implements State<Boolean> {
    private static Boolean latestState = false;

    @Override
    public Boolean get() {
        return latestState;
    }

    @Override
    public void set(Boolean val) {
        latestState = val;
        notifyToAll();
    }
}
