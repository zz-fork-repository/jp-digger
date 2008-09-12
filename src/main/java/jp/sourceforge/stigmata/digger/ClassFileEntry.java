package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.net.URL;

/**
 * 
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
}
