package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * This interface is for the class manages the location of an archive,
 * and the set of a class file included in the archive.
 *
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public interface ClassFileArchive extends Iterable<ClassFileEntry>{
    /**
     * returns the location of this archive.
     */
    public URL getLocation();

    /**
     * returns the InputStream object from given entry.
     */
    public InputStream getInputStream(ClassFileEntry entry) throws IOException;

    /**
     * returns an entries of this archive.
     */
    public Iterator<ClassFileEntry> iterator();

    /**
     * returns this archive has given class entry or not.
     */
    public boolean hasEntry(String className);

    /**
     * returns an entry of given class name.
     */
    public ClassFileEntry getEntry(String className) throws ClassNotFoundException;

    /**
     * returns the name of this archive.
     */
    public String getName();
}
