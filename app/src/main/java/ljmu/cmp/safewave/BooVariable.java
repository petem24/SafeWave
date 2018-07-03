package ljmu.cmp.safewave;

/**
 * Created by Pete on 20/03/2018.
 */
//THIS CLASS IS FOR HAVING LISTENERS ACROSS CLASSES
public class BooVariable {
    private boolean boo = false;
    private ChangeListener listener;

    public boolean isBoo() {
        return boo;
    }

    public void setBoo(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
