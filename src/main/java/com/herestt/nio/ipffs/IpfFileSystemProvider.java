package com.herestt.nio.ipffs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

/**
 * A IPF file system provider implementation.
 * 
 * <p>This provider allows {@link URI}s respecting the <code>ipf</code> schemes. Actually, due to the
 * fact that <code>.ipf</code> files are stored in the default file system, the {@link URI} has first
 * to represent the default scheme by starting by <code>file:///</code>, and a IPF file system by 
 * finishing by the <code>.ipf</code> extension.
 * The <code>ipf:</code> scheme takes place to wrap the targeted file held by the IPF file system. 
 * So, to reach a file, a {@link URI} has first to be relative to the file system, then to the targeted file :
 * <pre>
 * {@code
 * URI uri = new URI("ipf:file:///C:/Program%20Files/TreeOfSavior/data/ies.ipf/ability.ies");
 * Path path = Paths.get(uri);
 * }
 * </pre></p>
 * 
 * @see IpfFileSystem
 * @see IpfPath
 * 
 * @author Herestt
 *
 */
public class IpfFileSystemProvider extends FileSystemProvider {

	private Map<Path, IpfFileSystem> fileSystems = new HashMap<>();
	
	@Override
	public String getScheme() {
		return "ipf";
	}

	private Path toFileSystemPath(URI uri) {
		String scheme = uri.getScheme();
		if(scheme == null || !scheme.equals(getScheme()))
			throw new IllegalArgumentException("URI must use the '"+ getScheme()
					+"' scheme so as to be handled by the IPF file system.");
		
		String spec = uri.getSchemeSpecificPart();
		int index = spec.indexOf(".ipf");
		if(index == -1)
			throw new IllegalArgumentException("The URI doesn't target a ." + getScheme() + " file.");
		
		spec = spec.substring(0, index + 4).replace(" ", "%20");
		Path path = Paths.get(URI.create(spec));
		path.normalize();
		
		return path;
	}
	
	private IpfPath toIpfPath(Path path) {
		if(path == null)
			throw new NullPointerException();
		if(!(path instanceof IpfPath))
			throw new IllegalArgumentException();
		return (IpfPath) path;
	}
	
	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env)
			throws IOException {
		return newFileSystem(toFileSystemPath(uri), env);
	}

	@Override
	public FileSystem newFileSystem(Path path, Map<String, ?> env)
			throws IOException {
		if(path.getFileSystem() != FileSystems.getDefault())
			throw new UnsupportedOperationException("The path must target an '" + getScheme()
					+ "' file hosted into the default file system.");
		synchronized (fileSystems) {
			if(fileSystems.containsKey(path))
				throw new FileSystemAlreadyExistsException();
			IpfFileSystem ipffs = new IpfFileSystem(this, path, env);
			fileSystems.put(path, ipffs);
			return  ipffs;
		}
	}
	
	@Override
	public FileSystem getFileSystem(URI uri) {
		synchronized(fileSystems) {
			Path fsPath = toFileSystemPath(uri);
			if(!fileSystems.containsKey(fsPath))
				throw new FileSystemNotFoundException();
			return fileSystems.get(fsPath);
		}
	}

	private String getFileSystemSpecificPart(URI uri) {
		String spec = uri.getSchemeSpecificPart();
		String ext = ".ipf";
		int index = spec.indexOf(ext);
		if(index == -1)
			throw new IllegalArgumentException();
		return spec.substring(index + ext.length());
	}
	
	@Override
	public Path getPath(URI uri) {
		FileSystem fs = null;
		try {
			fs = getFileSystem(uri);
		} catch(FileSystemNotFoundException e) {
			try {
				fs = newFileSystem(uri, new HashMap<String, String>());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return fs.getPath(getFileSystemSpecificPart(uri));
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path,
			Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		return IpfFileSystem.access(toIpfPath(path), options, attrs);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir,
			Filter<? super Path> filter) throws IOException {
		return new IpfDirectoryStream<>(toIpfPath(dir), IpfDirectoryIterator.class, filter);
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Path path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options)
			throws IOException {
		try {
			IpfFileSystem.dump(toIpfPath(source), target);
		} catch (DataFormatException e) {
			throw new IOException();
		}
	}

	@Override
	public void move(Path source, Path target, CopyOption... options)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		Path fsPath = toIpfPath(path).getFileSystem().getFileSystemPath();
		if(fsPath.getFileSystem() != FileSystems.getDefault())
			throw new UnsupportedOperationException("The path must be relative to the default file system.");
		synchronized (fileSystems) {
			if(!fileSystems.containsKey(fsPath))
				throw new FileSystemNotFoundException();
			Iterator<FileStore> it = fileSystems.get(fsPath).getFileStores().iterator();
			FileStore fstore = it.next();	// A path is only related to one file store.
			if(fstore == null)
				throw new IllegalArgumentException("This path doesn't have a file store.");
			return fstore;
		}
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path,
			Class<V> type, LinkOption... options) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path,
			Class<A> type, LinkOption... options) throws IOException {
		return IpfFileSystem.getFileAttributes(toIpfPath(path), type);
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes,
			LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value,
			LinkOption... options) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
