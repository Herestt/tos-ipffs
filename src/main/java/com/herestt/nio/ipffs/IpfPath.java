package com.herestt.nio.ipffs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class IpfPath implements Path {
	
	private final IpfFileSystem ipffs;
	private final String path;
	
	protected IpfPath(IpfFileSystem ipffs, String path) {
		this.ipffs = ipffs;
		ensurePath(path);
		this.path = path;
	}
	
	private void ensurePath(String path) {
		if(!(path.matches("^(/?[a-zA-Z0-9_\\-]+)+(\\.[a-zA-Z]+)*$|/")))
			throw new IllegalArgumentException();
	}
	
	public FileSystem getFileSystem() {
		return ipffs;
	}

	public boolean isAbsolute() {
		if(path.startsWith("/"))
			return true;
		return false;
	}

	public Path getRoot() {
		if(isAbsolute())
			return new IpfPath(ipffs, "/");
		return null;
	}

	public Path getFileName() {
		if(path.length() == 0)
			return null;
		if(path.length() == 1 && path != "/")
			return this;
		String[] elements = path.split("/");
		return new IpfPath(ipffs, elements[elements.length - 1]);
	}

	public Path getParent() {
		if(path.length() == 0)
			return null;
		int nameCount = getNameCount();
		if(nameCount == 1 && isAbsolute())
			return getRoot();
		if(nameCount > 0)
			return subpath(0, nameCount- 1);
		return null;
	}

	public int getNameCount() {
		if(path.equals(getRoot().toString()))
			return 0;
		if(isAbsolute())
			return path.split("/").length - 1;
		else
			return path.split("/").length;
	}

	public Path getName(int index) {
		if(isAbsolute())
			index ++;
		return new IpfPath(ipffs, path.split("/")[index]);
	}

	public Path subpath(int beginIndex, int endIndex) {
		if(beginIndex < 0 ||
				beginIndex >= path.length() ||
				endIndex > path.length() ||
				beginIndex >= endIndex)
				throw new IllegalArgumentException();
			
			String[] elements = path.split("/");
			String first = "";
			String[] more;
			
			if(isAbsolute() && beginIndex == 0)
				first = "/";
			
			if(elements.length > 0) {
				first += elements[1];
				int length = endIndex - beginIndex - 1;
				more = new String[length];
				System.arraycopy(elements, beginIndex + 2, more, 0, length);
			}
			else
				more = new String[0];
			
			return ipffs.getPath(first, more);
	}

	public boolean startsWith(Path other) {
		if(!(other instanceof IpfPath))
			return false;
		if(getNameCount() < other.getNameCount())
			return false;		
		if(!isAbsolute() || !other.isAbsolute())
			return false;
		int nameCount = other.getNameCount();
		for(int i = 0; i < nameCount; i++) {
			String name = getName(i).toString();
			String otherName = other.getName(i).toString();
			if(!name.equals(otherName))
				return false;
		}
		return true;
	}

	public boolean startsWith(String other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean endsWith(Path other) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean endsWith(String other) {
		// TODO Auto-generated method stub
		return false;
	}

	public Path normalize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path resolve(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	public Path resolve(String other) {
		// TODO Auto-generated method stub
		return null;
	}

	public Path resolveSibling(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	public Path resolveSibling(String other) {
		// TODO Auto-generated method stub
		return null;
	}

	public Path relativize(Path other) {
		// TODO Auto-generated method stub
		return null;
	}

	public URI toUri() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path toAbsolutePath() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path toRealPath(LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public File toFile() {
		throw new UnsupportedOperationException();
	}

	public WatchKey register(WatchService watcher, Kind<?>[] events,
			Modifier... modifiers) throws IOException {
		throw new UnsupportedOperationException();
	}

	public WatchKey register(WatchService watcher, Kind<?>... events)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	public Iterator<Path> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(Path other) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return path;
	}
}
