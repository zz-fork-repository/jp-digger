package jp.sourceforge.stigmata.digger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Haruaki TAMADA
 */
public class JarClassFileArchive implements ClassFileArchive{
    private File file;
    private JarFile jarfile;
    private URL jarfileLocation;

    public JarClassFileArchive(String jarfile) throws IOException{
        this.file = new File(jarfile);
        this.jarfile = new JarFile(jarfile);
        this.jarfileLocation = file.toURI().toURL();
    }

    public URL getLocation(){
        return jarfileLocation;
    }

    public String getName(){
        return file.getName();
    }

    public InputStream getInputStream(ClassFileEntry entry) throws IOException{
        if(hasEntry(entry.getClassName())){
            return jarfile.getInputStream(jarfile.getEntry(entry.getClassName().replace('.', '/') + ".class"));
        }
        return null;
    }

    public Iterator<ClassFileEntry> iterator(){
        List<ClassFileEntry> list = new ArrayList<ClassFileEntry>();
        
        for(Enumeration<JarEntry> e = jarfile.entries(); e.hasMoreElements(); ){
            JarEntry entry = e.nextElement();
            if(entry.getName().endsWith(".class")){
                URL location = null;
                try {
                    location = new URL("jar:" + getLocation() + "!/" + entry.getName());
                    String className = entry.getName();
                    className = className.substring(0, className.length() - ".class".length());
                    className = className.replace('/', '.');
                    
                    list.add(new ClassFileEntry(className, location));
                } catch (MalformedURLException ex) {
                }
            }
        }
        return list.iterator();
    }

    public boolean hasEntry(String className){
        return jarfile.getEntry(className.replace('.', '/') + ".class") != null;
    }

    public ClassFileEntry getEntry(String className) throws ClassNotFoundException{
        if(hasEntry(className)){
            String entryName = className.replace('.', '/') + ".class";
            try{
                URL location = new URL("jar:" + jarfile.getName() + "!/" + entryName);
                
                return new ClassFileEntry(className, location);
            } catch(MalformedURLException e){
            }
        }
        return null;
    }

    Enumeration<JarEntry> jarentries(){
        return jarfile.entries();
    }

    boolean hasJarEntry(String entry){
        return jarfile.getEntry(entry) != null;
    }
}
