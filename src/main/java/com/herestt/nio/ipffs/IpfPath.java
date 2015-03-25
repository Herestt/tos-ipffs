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
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAbsolute() {
		// TODO Auto-generated method stub
		return false;
	}

	public Path getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Path getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNameCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Path getName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public Path subpath(int beginIndex, int endIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean startsWith(Path other) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	public WatchKey register(WatchService watcher, Kind<?>[] events,
			Modifier... modifiers) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public WatchKey register(WatchService watcher, Kind<?>... events)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Path> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public int compareTo(Path other) {
		// TODO Auto-generated method stub
		return 0;
	}

}
