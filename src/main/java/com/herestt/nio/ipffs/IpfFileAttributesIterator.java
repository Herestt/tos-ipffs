package com.herestt.nio.ipffs;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream.Filter;

public class IpfFileAttributesIterator extends IpfFileListIterator<IpfFileAttributes> {

	protected IpfFileAttributesIterator(IpfPath dir, SeekableByteChannel sbc,
			Filter<IpfFileAttributes> filter) {
		super(dir, sbc, filter);
	}

	@Override
	public void init(IpfPath dir, SeekableByteChannel sbc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IpfFileAttributes process() {
		// TODO Auto-generated method stub
		return null;
	}

}
