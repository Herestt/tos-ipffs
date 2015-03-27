package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class IpfDirectoryStream implements DirectoryStream<Path> {
	
	private IpfPath dir;
	Filter<? super Path> filter;
	private SeekableByteChannel sbc = null;
	
	public IpfDirectoryStream(IpfPath dir, Filter<? super Path> filter) {
		this.dir = dir;
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

	public Iterator<Path> iterator() {
		try {
			sbc = Files.newByteChannel(fsPath(dir), StandardOpenOption.READ);
			return new IpfDirectoryIterator(dir.getFileSystem(), sbc, filter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		throw new IllegalArgumentException("Cannot create an iterator for the '" +
				fsPath(dir).toString() + "' file.");
	}
}