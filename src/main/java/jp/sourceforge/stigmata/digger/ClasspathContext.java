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
 * @author Haruaki TAMADA
 * @version $Revision$ 
 */
public class ClasspathContext implements Iterable<URL>{
    private static ClasspathContext DEFAULT_CONTEXT = new ClasspathContext(false);

    private ClasspathContext parent;
    private List<URL> classpath = new ArrayList<URL>();
    private ClassLoader loader = null;

    /**
     * private constructor for root context.
     */
    private ClasspathContext(boolean flag){
    }

    public ClasspathContext(){
        this(DEFAULT_CONTEXT);
    }

    /**
     * constructor with parent classpath context.
     */
    public ClasspathContext(ClasspathContext parent){
        this.parent = parent;
    }

    /**
     * returns parent classpath context.
     */
    public ClasspathContext getParent(){
        return parent;
    }

    /**
     * returns default classpath context.
     */
    public static final ClasspathContext getDefaultContext(){
        return DEFAULT_CONTEXT;
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
    public int getClasspathSize(){
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
    public void clear(){
        classpath.clear();
    }

    /**
     * clears all of classpathes of this context and parent context.
     */
    public void clearAll(){
        clear();
        if(parent != null){
            parent.clearAll();
        }
    }

    public Iterator<URL> iterator(){
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

    public synchronized ClassLoader createClassLoader(){
        if(loader == null){
            List<URL> list = new ArrayList<URL>();
            for(URL url: this){
                list.add(url);
            }

            loader = new WarClassLoader(list.toArray(new URL[list.size()]), getClass().getClassLoader());
        }
        return loader;
    }

    public ClassFileEntry find(String className) throws ClassNotFoundException{
        ClassLoader loader = createClassLoader();

        URL resource = loader.getResource(className.replace('.', '/') + ".class");
        if(resource != null){
            return new ClassFileEntry(className, resource);
        }
        return null;
    }

    public Class<?> findClass(String className) throws ClassNotFoundException{
        try{
            ClassLoader loader = createClassLoader();

            return loader.loadClass(className);
        } catch(NoClassDefFoundError e){
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }
}
