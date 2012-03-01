package jp.sourceforge.stigmata.digger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * 
 * 
 *
 * @author Haruaki TAMADA
 */
public class WarClassFileArchive extends JarClassFileArchive{
    public WarClassFileArchive(String jarfile) throws IOException{
        super(jarfile);
    }

    public InputStream getInputStream(ClassFileEntry entry) throws IOException{
        if(hasEntry(entry.getClassName())){
            return entry.getLocation().openStream();
        }
        return null;
    }

    public Iterator<ClassFileEntry> iterator(){
        List<ClassFileEntry> list = new ArrayList<ClassFileEntry>();

        for(Enumeration<JarEntry> e = jarEntries(); e.hasMoreElements(); ){
            JarEntry entry = e.nextElement();
            if(entry.getName().endsWith(".class")){
                URL location = null;
                try {
                    location = new URL("jar:" + getLocation() + "!/" + entry.getName());
                    String className = entry.getName();
                    className = className.substring("WEB-INF/classes/".length(), className.length() - ".class".length());
                    className = className.replace('/', '.');

                    list.add(new ClassFileEntry(className, location));
                } catch (MalformedURLException ex) {
                    throw new InternalError(ex.getMessage());
                }
            }
        }
        return list.iterator();
    }

    public boolean hasEntry(String className){
        return hasJarEntry("WEB-INF/classes/" + className.replace('.', '/') + ".class");
    }

    public ClassFileEntry getEntry(String className) throws ClassNotFoundException{
        if(hasEntry(className)){
            String entryName = className.replace('.', '/') + ".class";
            try{
                URL location = new URL("jar:" + getLocation() + "!/WEB-INF/classes/" + entryName);

                return new ClassFileEntry(className, location);
            } catch(MalformedURLException e){
                throw new InternalError(e.getMessage());
            }
        }
        return null;
    }
}
