package com.herestt.tos.nio.ipffs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream.Filter;
import java.util.Iterator;

/**
 * An abstract IPF file system iterator.  
 * 
 * <p>When using an implementation of this iterator, at first call of the {@link #hasNext()} 
 * method, the {@link #init()} method is called. This method is in charge of setting up the 
 * processing phase. To do so, this abstract class supplies :
 * <ul>
 * 	<li>the {@link IpfPath} ({@link #path}) of the targeted directory;</li>
 * 	<li>a {@link SeekableByteChannel} ({@link #sbc}) to access its file system.</li>
 * </ul></p>
 * 
 * <p>Once the initialization is done, the {@link #process()} method will be called at each iteration so as
 * to parse and return the current element. If there is no more element, this function must return <code>null</code>.</p>
 * 
 * <p>In conclusion, an iterator has to implements both the {@link #init()} and the {@link #process()} 
 * methods.</p>
 * 
 * @author Herestt
 *
 * @param <E> The data type of the elements that represents file attributes.
 * 
 * @see IpfDirectoryIterator
 * @see IpfFileAttributesIterator
 */
public abstract class IpfIterator<E> implements Iterator<E> {
	
	/** The inner file system path. */
	protected IpfPath path;
	
	/** The {@link SeekableByteChannel} that access the current IPF file. */
	protected SeekableByteChannel sbc;
	
	/** The filter that determines whether an element must be return by the iterator. */
	protected Filter<? super E> filter;
	
	/**	Determines whether the iterator must be initialized by calling the 'init' method. */
	private boolean initialized = false;
	
	/** The next element to return. */
	private E next;
	
	/**
	 * This method allows the file list iterator implementation to be initialized before
	 * the processing occurs. The method is called once when hasNext is called for the 
	 * first time
	 */
	public abstract void init();
	
	/**
	 * The processing implementation. The method is called for each hasNext occurrence
	 * and is in charge of creating the object that will be returned by hasNext.
	 * 
	 * @return The generated object, or null if there is isn't any element to process..
	 */
	public abstract E process();
	
	@Override
	public boolean hasNext() {
		if(!initialized) {
			init();
			initialized = true;
		}
		E entry = process();
		if(entry == null)
			return false;
		try {
			if((filter == null) || (filter.accept(entry))) {
				next = entry;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public E next() {
		return next;
	}
	
	/**
	 * Sets the working directory.
	 * 
	 * @param dir The working directory.
	 */
	public void setDirectory(IpfPath dir) {
		this.path = dir;
	}
	
	/**
	 * Sets a {@link SeekableByteChannel} that accesses the IPF file.
	 *  
	 * @param channel The {@link SeekableByteChannel}.
	 */
	public void setChannel(SeekableByteChannel channel) {
		this.sbc = channel;
	}
	
	/**
	 * Sets the filter used for evaluating whether the iterator has to
	 * return the current element.
	 *  
	 * @param filter The filter.
	 */
	public void setFilter(Filter<? super E> filter) {
		this.filter = filter;
	}
}