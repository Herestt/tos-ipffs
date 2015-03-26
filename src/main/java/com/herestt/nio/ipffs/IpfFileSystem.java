package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashSet;
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
	
	public Path getIpfFileSystemPath() {
		return fileSystemPath;
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
	
	private Set<IpfFileAttributes> getFilesAttributes() {
		Set<IpfFileAttributes> attrs = new HashSet<>();
		try {
			long headerOffset = Files.size(fileSystemPath) - 24;
			try(SeekableByteChannel sbc = FileContent.access(fileSystemPath, headerOffset)) {
				FileContent.order(ByteOrder.LITTLE_ENDIAN);
				int fileCount = FileContent.read().asUnsignedShort();
				long listOffset = FileContent.read().asUnsignedInt();
				int nameSize, parentNameSize;
				FileContent.position(listOffset);
				for(int i = 0; i < fileCount; i++) {
					IpfFileAttributes a = new IpfFileAttributes(
							nameSize = FileContent.read().asUnsignedShort(),
							FileContent.read().asUnsignedInt(),
							FileContent.read().asUnsignedInt(),
							FileContent.read().asUnsignedInt(),
							FileContent.read().asUnsignedInt(),
							parentNameSize = FileContent.read().asUnsignedShort(),
							FileContent.read().asString(parentNameSize),
							FileContent.read().asString(nameSize)
							);
					attrs.add(a);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return attrs;
	}
}
