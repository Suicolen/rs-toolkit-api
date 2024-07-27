package api.cache;

import com.displee.cache.CacheLibrary;
import com.displee.cache.index.Index;
import com.displee.cache.index.archive.Archive;

/**
 * Core has a default implementation which is what's currently used
 */
public interface Cache {

    // perhaps get rid of it and just create methods like CacheLibrary#put in here with many overloads
    CacheLibrary getLibrary();

    Index getIndex(int index);
    Archive getArchive(int index, int archive);

    boolean isOSRS();

    boolean is317();
    void update();
    void close();


}
