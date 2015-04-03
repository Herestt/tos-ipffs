package com.herestt.nio.ipffs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.herestt.common.io.FileContent;

public class IpfFileSystem extends FileSystem {

	private final IpfFileSystemProvider provider;
	private final Path fileSystemPath;
	private final Map<String, ?> env;
	private final Set<FileStore> fileStores;
	private boolean open = false;
	private Set<SeekableByteChannel> channels;
	
	protected IpfFileSystem(IpfFileSystemProvider provider,
			Path fileSystemPath, Map<String, ?> env) {
		this.provider = provider;
		this.fileSystemPath = fileSystemPath;
		this.env = env;
		fileStores = createFileStoresSet();
		channels = Collections.synchronizedSet(new HashSet<>());
	}

	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
		if(!isOpen())
			return;
		if(channels.size() > 0) {
			for(SeekableByteChannel sbc : channels)
				if(sbc.isOpen())
					sbc.close();
			channels.clear();
		}
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public String getSeparator() {
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		return fileStores;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

	/*** IPF File System Specific Methods ***/
	
	public Path getFileSystemPath() {
		return fileSystemPath;
	}
	
	/**
	 * Creates a set containing the file store instance.
	 * 
	 * Due to the provider specification, the file system must return 
	 * a set of file stores. However the IPF file system allows only
	 * one file store object representing the '.ipf' file.
	 *  
	 * @return the file store wrapped into a set.
	 */
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
		if(!(type == BasicFileAttributes.class || type == IpfFileAttributes.class))
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
	 * @throws DataFormatException - if input data is not PKZip.
	 */
	private static void inflate(ByteBuffer input, ByteBuffer output) throws DataFormatException {
		if(input.isDirect() || output.isDirect())
			throw new IllegalArgumentException();
		Inflater decompresser = new Inflater(true);
		decompresser.setInput(input.array());
		decompresser.inflate(output.array());
		decompresser.end();
	}
	
	/**
	 * Dumps a file against another file system's one.
	 * 
	 * @param src The file to dump.
	 * @param dest The target to copy the content to.
	 * 
	 * @throws IOException - if an I/O error occurs.
	 * @throws DataFormatException 
	 */
	protected static void dump(IpfPath src, Path dest) throws IOException, DataFormatException {
		Path fs = src.getFileSystem().getFileSystemPath();
		if(src == null || fs == null || dest == null
				|| fs.getFileSystem() != FileSystems.getDefault()
				|| dest.getFileSystem() != FileSystems.getDefault())
			throw new IllegalArgumentException("The file ");
		IpfFileAttributes attrs = getFileAttributes(src, IpfFileAttributes.class);
		try(RandomAccessFile srcRaf = new RandomAccessFile(fs.toFile(), "r");
				SeekableByteChannel srcSbc = srcRaf.getChannel();
				RandomAccessFile raf = new RandomAccessFile(dest.toFile(), "rw");
				FileChannel destChannel = raf.getChannel()) {
			
			FileContent.access(srcSbc, attrs.getOffset());
			FileContent.order(ByteOrder.LITTLE_ENDIAN);
			ByteBuffer srcBuffer = ByteBuffer.wrap(FileContent.read().asBytes((int) attrs.getCompressedSize()));
			ByteBuffer destBuffer = ByteBuffer.allocate((int) attrs.size());
						
			inflate(srcBuffer, destBuffer);
			
			destChannel.write(destBuffer);
		}
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
	protected static SeekableByteChannel access(IpfPath file, Set<? extends OpenOption> options,
			FileAttribute<?>... attrs) throws IOException {
		if(options.contains(StandardOpenOption.DELETE_ON_CLOSE)) {
			String suffix = file.toString().replaceAll("/", "_");
			Path tmp = Files.createTempFile("ipf", suffix, attrs);
			try {
				dump(file, tmp);
				SeekableByteChannel sbc = new IpfSeekableByteChannelImpl(tmp, options);
				file.getFileSystem().channels.add(sbc);
				return sbc;
			} catch (DataFormatException e) {
				Files.delete(file);
				throw new IOException(); 
			}
		}
		Files.delete(file);
		throw new IllegalArgumentException();
	}
}