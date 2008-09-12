package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * abstract presentation of class file archive's location.
 *
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public interface ClassFileArchive extends Iterable<ClassFileEntry>{
    public URL getLocation();

    public InputStream getInputStream(ClassFileEntry entry) throws IOException;

    public Iterator<ClassFileEntry> iterator();

    public boolean hasEntry(String className);

    public ClassFileEntry getEntry(String className) throws ClassNotFoundException;

    public String getName();
}
