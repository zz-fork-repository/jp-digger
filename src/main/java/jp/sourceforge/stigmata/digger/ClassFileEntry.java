package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class manages class name and its location.
 *
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public class ClassFileEntry{
    private URL location;
    private String className;

    public ClassFileEntry(String className, URL location){
        this.className = className;
        setLocation(location);
    }

    public String getClassName(){
        return className;
    }

    public void setLocation(URL location){
        this.location = location;
    }

    public URL getLocation(){
        return location;
    }

    public InputStream openStream() throws IOException{
        return getLocation().openStream();
    }
}
