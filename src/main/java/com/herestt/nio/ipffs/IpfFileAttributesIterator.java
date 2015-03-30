package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

import com.herestt.common.io.FileContent;

public class IpfFileAttributesIterator extends IpfFileListIterator<IpfFileAttributes> {

	private final static int HEADER_SIZE = 24;
	
	private int fileCount;
	private int currentCount = 0;
	private long listOffset;
	
	protected IpfFileAttributesIterator(IpfPath dir, SeekableByteChannel sbc,
			Filter<IpfFileAttributes> filter) {
		super(dir, sbc, filter);
	}

	@Override
	public void init(IpfPath dir, SeekableByteChannel sbc) {
		Path ipf = dir.getFileSystem().getFileSystemPath();
		try {
			long headerOffset = Files.size(ipf) - HEADER_SIZE;
			FileContent.access(sbc, headerOffset);
			FileContent.order(ByteOrder.LITTLE_ENDIAN);
			fileCount = FileContent.read().asUnsignedShort();
			listOffset = FileContent.read().asUnsignedInt();
			FileContent.position(listOffset);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IpfFileAttributes process() {
		// TODO Auto-generated method stub
		return null;
	}

}
