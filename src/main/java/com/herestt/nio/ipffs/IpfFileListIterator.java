package com.herestt.nio.ipffs;

import java.nio.channels.SeekableByteChannel;
import java.util.Iterator;

public abstract class IpfFileListIterator<E> implements Iterator<E> {
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E next() {
		// TODO Auto-generated method stub
		return null;
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
	 * @return The generated object.
	 */
	public abstract E process();

}