package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream.Filter;
import java.util.Iterator;

public abstract class IpfFileListIterator<E> implements Iterator<E> {
	
	private final static int HEADER_SIZE = 24;
	
	private IpfPath dir;
	private SeekableByteChannel sbc;
	private Filter<E> filter;
	private boolean initialized = false;
	private E next;
	
	protected IpfFileListIterator(IpfPath dir, SeekableByteChannel sbc, Filter<E> filter) {
		this.dir = dir;
		this.sbc = sbc;
		this.filter = filter;
	}
	
	@Override
	public boolean hasNext() {
		if(!initialized)
			init(dir, sbc);
		E entry = process();
		if(entry == null)
			return false;
		try {
			if(filter.accept(entry)) {
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
	 * This method allows the file list iterator implementation to be initialized before
	 * the processing occurs. The method is called once when hasNext is called for the 
	 * first time
	 * 
	 * @param dir The targeted directory.
	 * @param sbc The {@link SeekableByteChannel} related to IPF file.
	 */
	public abstract void init(IpfPath dir, SeekableByteChannel sbc);
	
	/**
	 * The processing implementation. The method is called for each hasNext occurrence
	 * and is in charge of creating the object that will be returned by hasNext.
	 * 
	 * @return The generated object, or null if there is isn't any element to process..
	 */
	public abstract E process();

}
