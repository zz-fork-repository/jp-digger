package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * 
 * 
 *
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public class DefaultClassFileArchive implements ClassFileArchive{
    private File file;
    private String className;
    
    public DefaultClassFileArchive(String file){
        this(new File(file));
    }
    
    public DefaultClassFileArchive(File file){
        this.file = file;
        parseClassName();
    }
    
    public DefaultClassFileArchive(String file, String className){
        this(new File(file), className);
    }
    
    public DefaultClassFileArchive(File file, String className){
        this.file = file;
        this.className = className;
    }

    public URL getLocation(){
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException ex) {
        }
        return null;
    }

    public InputStream getInputStream(ClassFileEntry entry) throws IOException{
        return new FileInputStream(file);
    }

    public Iterator<ClassFileEntry> iterator(){
        List<ClassFileEntry> list = new ArrayList<ClassFileEntry>();
        list.add(new ClassFileEntry(className, getLocation()));

        return list.iterator();
    }

    public boolean hasEntry(String className){
        return this.className.equals(className);
    }

    public ClassFileEntry getEntry(String className) throws ClassNotFoundException{
        return new ClassFileEntry(className, getLocation());
    }

    public String getName(){
        return className;
    }

    private void parseClassName(){
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            ClassReader reader = new ClassReader(in);
            ClassNameExtractVisitor visitor = new ClassNameExtractVisitor();
            reader.accept(visitor, true);

            this.className = visitor.getClassName();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(in != null){
                try{
                    in.close();
                } catch(IOException e){
                }
            }
        }
    }

    private static class ClassNameExtractVisitor extends EmptyVisitor{
        private String className;

        public String getClassName(){
            return className;
        }

        @Override
        public void visit(int version, int access, String name, String signature, 
                String superClassName, String[] interfaces){
            className = name;
        }
    }
}
