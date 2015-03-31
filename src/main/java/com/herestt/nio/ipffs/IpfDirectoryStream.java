package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class IpfDirectoryStream<E> implements DirectoryStream<E> {
	
	private IpfPath dir;
	private Class<? extends IpfIterator<E>> iterator;
	private Filter<? super E> filter;
	private SeekableByteChannel sbc;
	
	protected IpfDirectoryStream(IpfPath dir, Class<? extends IpfIterator<E>> iterator, Filter<? super E> filter) {
		this.dir = dir;
		this.iterator = iterator;
		this.filter = filter;
		ensureDirectory(dir);
	}
	
	private void ensureDirectory(IpfPath dir) {
		if(dir == null)
			throw new NullPointerException();
		if(fsPath(dir).getFileSystem() != FileSystems.getDefault())
			throw new IllegalArgumentException();
	}
	
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