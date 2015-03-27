package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;
import java.util.Iterator;

import com.herestt.common.io.FileContent;

public class IpfDirectoryIterator implements Iterator<Path>{

	private final static int HEADER_SIZE = 24;
	
	private IpfFileSystem ipffs;
	private Filter<? super Path> filter;
	private int fileCount;
	private int currentCount = 0;
	private long listOffset;
	private Path next;
	
	public IpfDirectoryIterator(IpfFileSystem ipffs, SeekableByteChannel sbc, Filter<? super Path> filter) {
		this.ipffs = ipffs;
		this.filter = filter;
		try {
			long headerOffset = sbc.size() - HEADER_SIZE;
			FileContent.access(sbc, headerOffset);
			FileContent.order(ByteOrder.LITTLE_ENDIAN);
			this.fileCount = FileContent.read().asUnsignedShort();
			this.listOffset = FileContent.read().asUnsignedInt();
			FileContent.position(listOffset);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Path readNextPath() throws IOException {
		int pathSize = FileContent.read().asUnsignedShort();
		FileContent.skip(16);
		int fsNameSize = FileContent.read().asUnsignedShort();
		FileContent.skip(fsNameSize);
		String strPath = FileContent.read().asString(pathSize);
		return ipffs.getPath("/" + strPath);
	}
	
	@Override
	public boolean hasNext() {
		if(currentCount >= fileCount)
			return false;
		try {
			Path p = readNextPath();
			currentCount++;
			if(filter.accept(p)) {
				next = p;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Path next() {
		return next;
	}
}
