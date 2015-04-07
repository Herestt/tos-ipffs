package com.herestt.tos.nio.ipffs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * A {@link DirectoryStream} implementation for the IPF file system.
 * 
 * This stream connects a channel to an IPF file system and allows an iterator to be 
 * built over its content description part.
 * 
 * @author Herestt
 *
 * @param <E> The data type to iterate through.
 */
public class IpfDirectoryStream<E> implements DirectoryStream<E> {
	
	/**	The directory of which the stream is associate with. */
	private IpfPath dir;
	
	/** The iterator type.*/
	private Class<? extends IpfIterator<E>> iterator;
	
	/** The filter to apply to the iterator. */
	private Filter<? super E> filter;
	
	/** The channel that accesses the file system. */
	private SeekableByteChannel sbc;
	
	/**
	 * IPF Directory Stream constructor.
	 * 
	 * @param dir The directory of which the stream is associate with.
	 * @param iterator The iterator type.
	 * @param filter The filter to apply to the iterator.
	 */
	protected IpfDirectoryStream(IpfPath dir, Class<? extends IpfIterator<E>> iterator, Filter<? super E> filter) {
		this.dir = dir;
		this.iterator = iterator;
		this.filter = filter;
		ensureDirectory(dir);
	}
	
	/**
	 * Checks whether a path is relative to this file system.
	 * 
	 * @param dir The directory to test.
	 */
	private void ensureDirectory(IpfPath dir) {
		if(dir == null)
			throw new NullPointerException();
		if(fsPath(dir).getFileSystem() != FileSystems.getDefault())
			throw new IllegalArgumentException();
	}
	
	/**
	 * Easy access to the {@link Path} of the file system that holds the target 
	 * directory. 
	 * 
	 * @param dir The inner directory.
	 * 
	 * @return the file system path.
	 */
	private Path fsPath(IpfPath dir) {
		return dir.getFileSystem().getFileSystemPath();
	}
	
	public void close() throws IOException {
		if(sbc != null && sbc.isOpen())
			sbc.close();
	}

	public Iterator<E> iterator() {
		try {
			sbc = Files.newByteChannel(fsPath(dir));
			IpfIterator<E> it = iterator.newInstance();
			it.setDirectory(dir);
			it.setChannel(sbc);
			it.setFilter(filter);
			return it;
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Unable to create an iterator for this path.");
	}
}