package jp.sourceforge.stigmata.digger;

/*
 * $Id$
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.sourceforge.stigmata.digger.util.WarClassLoader;

/**
 * Context object of Classpath.
 * 
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public class ClasspathContext implements Iterable<URL>{
    private ClasspathContext parent;
    private List<URL> classpath = new ArrayList<URL>();
    private ClassLoader loader = null;
    private boolean includeSystemClass = true;

    public ClasspathContext(){
    }

    /**
     * constructor with parent classpath context.
     */
    public ClasspathContext(ClasspathContext parent){
        this.parent = parent;
        includeSystemClass = getParent().isIncludeSystemClasses();
    }

    /**
     * returns parent classpath context.
     */
    public ClasspathContext getParent(){
        return parent;
    }

    /**
     * If this method returns true, this object searches byte code 
     * by original ClassLoader and current ClassLoader.
     * If this method returns false, this object searches byte code
     * by original ClassLoader only. Not search from current ClassLoader. 
     */
    public boolean isIncludeSystemClasses(){
        return includeSystemClass;
    }

    /**
     * Set searching byte code by current ClassLoader.
     * @see isIncludeSystemClasses
     */
    public synchronized void setIncludeSystemClasses(boolean flag){
        if(includeSystemClass != flag){
            loader = null;
        }
        this.includeSystemClass = flag;
        if(parent != null){
            parent.setIncludeSystemClasses(flag);
        }
    }

    /**
     * adds given url to this context.  If this context already has given url or
     * parent context has given url, this method do nothing.
     */
    public synchronized void addClasspath(URL url){
        if(!contains(url)){
            classpath.add(url);
            loader = null;
        }
    }

    /**
     * returns that this context or parent context have given url.
     */
    public synchronized boolean contains(URL url){
        return (parent != null && parent.contains(url)) || classpath.contains(url); 
    }

    /**
     * returns a size of classpath list, which this context and parent context have.
     */
    public synchronized int getClasspathSize(){
        int count = classpath.size();
        if(parent != null){
            count += parent.getClasspathSize();
        }
        return count;
    }

    /**
     * returns an array of all of classpathes include parent context.
     */
    public synchronized URL[] getClasspathList(){
        List<URL> list = new ArrayList<URL>();
        for(URL url: this){
            list.add(url);
        }
        return list.toArray(new URL[list.size()]);
    }

    /**
     * clears all of classpathes of this context. not clear parent context.
     * If you want to clear this context and parent context, use {@link #clearAll <code>clearAll</code>} method.
     * @see clearAll
     */
    public synchronized void clear(){
        classpath.clear();
    }

    /**
     * clears all of classpathes of this context and parent context.
     */
    public synchronized void clearAll(){
        clear();
        if(parent != null){
            parent.clearAll();
        }
    }

    /**
     * returns an iterator of classpath list.
     */
    public synchronized Iterator<URL> iterator(){
        if(parent == null){
            return classpath.iterator();
        }
        else{
            final Iterator<URL> parentIterator = parent.iterator();
            final Iterator<URL> thisIterator = classpath.iterator();
            return new Iterator<URL>(){
                public boolean hasNext(){
                    boolean next = parentIterator.hasNext();
                    if(!next){
                        next = thisIterator.hasNext();
                    }
                    return next;
                }
                public URL next(){
                    URL nextObject = null;
                    if(parentIterator.hasNext()){
                        nextObject = parentIterator.next();
                    }
                    else{
                        nextObject = thisIterator.next();
                    }
                    return nextObject;
                }
                public void remove(){
                }
            };
        }
    }


    /**
     * construct and returns a ClassLoader object which loads from classpath list.
     */
    public synchronized ClassLoader createClassLoader(){
        if(loader == null){
            List<URL> list = new ArrayList<URL>();
            for(URL url: this){
                list.add(url);
            }

            ClassLoader parentClassLoader = null;
            if(parent != null){
                parentClassLoader = parent.createClassLoader();
            }
            else{
                if(isIncludeSystemClasses()){
                     parentClassLoader = getClass().getClassLoader();
                }
                else{
                    parentClassLoader = null;
                }
            }
            loader = new WarClassLoader(list.toArray(new URL[list.size()]), parentClassLoader);
        }
        return loader;
    }

    /**
     * returns a {@link ClassFileEntry <code>ClassFileEntry</code>} object
     * which is named given className.
     * 
     * @return ClassFileEntry object, if not found given class, then returns null.
     */
    public synchronized ClassFileEntry findEntry(String className){
        ClassLoader loader = createClassLoader();

        URL resource = loader.getResource(className.replace('.', '/') + ".class");

        if(resource != null){
            return new ClassFileEntry(className, resource);
        }
        return null;
    }

    /**
     * returns this context has given class entry or not.
     */
    public synchronized boolean hasEntry(String className){
        ClassLoader loader = createClassLoader();
        return loader.getResource(className.replace('.', '/') + ".class") != null;
    }

    /**
     * finds and returns a {@link Class <code>Class</code>} object
     * which is named given className.
     */
    public synchronized Class<?> findClass(String className) throws ClassNotFoundException{
        try{
            ClassLoader loader = createClassLoader();

            Class<?> clazz = loader.loadClass(className);

            return clazz;
        } catch(NoClassDefFoundError e){
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }
}
