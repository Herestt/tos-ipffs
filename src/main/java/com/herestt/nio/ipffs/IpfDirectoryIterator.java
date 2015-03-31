package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import com.herestt.common.io.FileContent;

public class IpfDirectoryIterator extends IpfIterator<Path> {
	
	private final static int HEADER_SIZE = 24;
	
	private int fileCount;
	private int currentCount = 0;
	private long listOffset;
	
	@Override
	public void init() {
		Path ipf = path.getFileSystem().getFileSystemPath();
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
	public Path process() {
		if(currentCount >= fileCount)
			return null;
		Path p = null;
		try {
			int pathSize = FileContent.read().asUnsignedShort();
			FileContent.skip(16);
			int fsNameSize = FileContent.read().asUnsignedShort();
			FileContent.skip(fsNameSize);
			String strPath = FileContent.read().asString(pathSize);
			p = path.getFileSystem().getPath("/" + strPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentCount++;
		return p;
	}
}