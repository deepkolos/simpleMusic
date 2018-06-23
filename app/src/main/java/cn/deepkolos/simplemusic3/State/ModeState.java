package cn.deepkolos.simplemusic3.State;

import android.content.Context;

import java.lang.reflect.Method;

import cn.deepkolos.simplemusic3.Model.Song;

public class ModeState extends BaseState implements State<Integer> {
    private int mode;

    @Override
    public Integer get() {
        return mode;
    }

    @Override
    public void set(Integer val) {
        mode = val;
    }

    public Song getNext () {
        Class<?> nextClass = ModeState.CLASSES[mode];

        try {
            Method next = nextClass.getMethod("next", Context.class);
            return (Song) next.invoke(nextClass.newInstance(), contextRef.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final int SEQUENCE = 0;
    public static final int LOOP = 1;
    public static final int RANDOM = 2;
    public static final int SINGLE = 3;
    public static final int[] LIST = {SEQUENCE, LOOP, RANDOM, SINGLE};

    public static final Class[] CLASSES = {Sequence.class, Loop.class, Random.class, Single.class};

    public interface I {
        // 假装支持static
        //static void next (ArrayList<Song> playlist);
    }

    public static class Sequence {
        public static Song next (Context ctx) {
            PlayListState playlistState = new PlayListState();
            PlayingSongState playingSongState = new PlayingSongState();

            if (playlistState.get() == null)
                return null;

            int index = playlistState.get().indexOf(playingSongState.get());
            if (index == -1) {
                // 可能是playingSong 为null, 或者播放的不是该列表的song, 那么就选取该列表的第一首
                return playlistState.get().get(0);

            } else if (index + 1 < playlistState.get().size()) {
                // 然后是next的逻辑
                return playlistState.get().get(index + 1);
            }

            return null;
        }
    }

    public static class Loop {
        public static Song next (Context ctx) {
            PlayListState playlistState = new PlayListState();
            PlayingSongState playingSongState = new PlayingSongState();

            if (playlistState.get() == null)
                return null;

            int index = playlistState.get().indexOf(playingSongState.get());

            if (index == -1) {
                // 可能是playingSong 为null, 或者播放的不是该列表的song, 那么就选取该列表的第一首
                return playlistState.get().get(0);

            } else if (index + 1 < playlistState.get().size()) {
                // 然后是next的逻辑
                return playlistState.get().get(index + 1);
            } else {
                return playlistState.get().get(0);
            }
        }
    }

    public static class Random {
        public static Song next (Context ctx) {
            PlayListState playlistState = new PlayListState();
            PlayingSongState playingSongState = new PlayingSongState();

            if (playlistState.get() == null)
                return null;

            int currIndex = playlistState.get().indexOf(playingSongState.get());
            int len = playlistState.get().size();
            int nextIndex;
            java.util.Random rand = new java.util.Random();

            do {
                nextIndex = rand.nextInt(len);
            } while (nextIndex == currIndex);

            if (len == 0)
                return null;
            return playlistState.get().get(nextIndex);
        }
    }

    public static class Single {
        public static Song next (Context ctx) {
            PlayingSongState playingSongState = new PlayingSongState();
            return playingSongState.get();
        }
    }
}
