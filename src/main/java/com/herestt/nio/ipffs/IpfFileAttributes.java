package com.herestt.nio.ipffs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class IpfFileAttributes implements BasicFileAttributes {

	private int nameSize;
	private long crc;
	private long compressedSize;
	private long size;
	private long offset;
	private int parentNameSize;
	private String parentName;
	private String name;
	
	
	
	public IpfFileAttributes(int nameSize, long crc, long compressedSize,
			long size, long offset, int parentNameSize, String parentName,
			String name) {
		super();
		this.nameSize = nameSize;
		this.crc = crc;
		this.compressedSize = compressedSize;
		this.size = size;
		this.offset = offset;
		this.parentNameSize = parentNameSize;
		this.parentName = parentName;
		this.name = name;
	}

	public FileTime lastModifiedTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public FileTime lastAccessTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public FileTime creationTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isRegularFile() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSymbolicLink() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOther() {
		// TODO Auto-generated method stub
		return false;
	}

	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object fileKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
