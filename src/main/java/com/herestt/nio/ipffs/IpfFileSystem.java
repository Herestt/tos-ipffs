package com.herestt.nio.ipffs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.herestt.common.io.FileContent;

public class IpfFileSystem extends FileSystem {

	private final IpfFileSystemProvider provider;
	private final Path fileSystemPath;
	private final Map<String, ?> env;
	private final Set<FileStore> fileStores;
	
	protected IpfFileSystem(IpfFileSystemProvider provider,
			Path fileSystemPath, Map<String, ?> env) {
		this.provider = provider;
		this.fileSystemPath = fileSystemPath;
		this.env = env;
		fileStores = createFileStoresSet();
	}

	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSeparator() {
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return fileStores;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getPath(String first, String... more) {
		String path;
		if(more.length == 0)
			path = first;
		else {
			StringBuilder sb = new StringBuilder();
			sb.append(first);
			for(String segment : more) {
				if(segment.length() > 0) {
					if(sb.length() > 0)
						sb.append("/");
					sb.append(segment);
				}
			}
			path = sb.toString();
		}
		return new IpfPath(this, path);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WatchService newWatchService() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*** IPF File System Specific Methods ***/
	
	public Path getFileSystemPath() {
		return fileSystemPath;
	}
	
	private static FileSystemProvider provider(IpfPath path) {
		return path.getFileSystem().provider();
	}
	
	private Set<FileStore> createFileStoresSet() {
		Set<FileStore> set = new HashSet<>();
		try {
			long headerOffset = Files.size(fileSystemPath) - 24;
			try(SeekableByteChannel sbc = FileContent.access(fileSystemPath, headerOffset)) {
				FileContent.order(ByteOrder.LITTLE_ENDIAN);
				IpfFileStore ipffstore = new IpfFileStore(
						fileSystemPath.getName(fileSystemPath.getNameCount() - 1).toString(),
						FileContent.read().asUnsignedShort(),
						FileContent.read().asUnsignedInt(),
						FileContent.skip(4).read().asBytes(4)
						);
				set.add(ipffstore);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}
	
	/**
	 * Gets the file attributes of a stored file.
	 * 
	 * @param path The file path.
	 * @param type The type of attributes.
	 * @return The desired file attributes.
	 * 
	 * @throws IOException - if an I/O error occurs.
	 */
	@SuppressWarnings("unchecked")
	protected static <A extends BasicFileAttributes> A getFileAttributes(IpfPath path,
			Class<A> type) throws IOException {
		if(type != IpfFileAttributes.class)
			throw new UnsupportedOperationException("Only IpfFileAttributes class is allowed.");
		try(IpfDirectoryStream<IpfFileAttributes> stream = new IpfDirectoryStream<>(path, IpfFileAttributesIterator.class, null)) {
			Iterator<IpfFileAttributes> it = stream.iterator();
			while(it.hasNext()) {
				IpfFileAttributes attrs = it.next();
				if(path.toString().equals("/" + attrs.getPath()))
					return (A) attrs;
			}
		} 
		throw new FileNotFoundException();
	}
	
	/**
	 * Inflates a PKZip file content.
	 * 
	 * IPF stored files are compressed by using the PKZip algorithm.
	 * 
	 * @param input The compressed content.
	 * @param output The inflated content.
	 * 
	 * @throws IllegalArgumentException - if a buffer is direct.
	 */
	private static void inflate(ByteBuffer input, ByteBuffer output) {
		// TODO - Herestt.
	}
	
	/**
	 * Dumps a file against another file system's one.
	 * 
	 * @param file The file to dump.
	 * @param target The target to copy the content to.
	 * 
	 * @throws IOException - if an I/O error occurs.
	 */
	protected static void dump(IpfPath file, Path target) throws IOException {
		// TODO - Herestt.
	}
	
	/**
	 * Accesses the content of a file through a {@link SeekableByteChannel}.
	 * 
	 * The file is dumped against a temporary file that is deleted when the 
	 * {@link SeekableByteChannel} is closed. So mind using a try-with-resource
	 * statement when using the function.
	 * 
	 * @param file The file to access.
	 * @return A {@link SeekableByteChannel} connected the file.
	 * 
	 * @throws IOException - if an I/O error occurs.
	 */
	protected SeekableByteChannel access(IpfPath file) throws IOException {
		// TODO - Herestt.
		return null;
	}
}
