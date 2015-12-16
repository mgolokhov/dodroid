package doit.study.droid;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class InterActivityDataHolder {
    Map<String, WeakReference<Object>> data = new HashMap<>();

    public void save(String key, Object object) {
        data.put(key, new WeakReference<Object>(object));
    }

    public Object retrieve(String key) {
        WeakReference<Object> ref = data.remove(key);
        if (ref == null) return null;
        return ref.get();
    }
}
