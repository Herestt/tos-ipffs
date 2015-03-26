package com.herestt.nio.ipffs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public class IpfFileAttributes implements BasicFileAttributes {

	private int pathSize;
	private long crc;
	private long compressedSize;
	private long size;
	private long offset;
	private int fsNameSize;
	private String fsName;
	private String path;
	
	public IpfFileAttributes() {}

	public IpfFileAttributes(int pathSize, long crc, long compressedSize,
			long size, long offset, int fsNameSize, String fsName, String path) {
		super();
		this.pathSize = pathSize;
		this.crc = crc;
		this.compressedSize = compressedSize;
		this.size = size;
		this.offset = offset;
		this.fsNameSize = fsNameSize;
		this.fsName = fsName;
		this.path = path;
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
		return size;
	}

	public Object fileKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/*** IPF File Attributes Specific Methods ***/
	
	public int getPathSize() {
		return pathSize;
	}

	public void setPathSize(int pathSize) {
		this.pathSize = pathSize;
	}

	public long getCrc() {
		return crc;
	}

	public void setCrc(long crc) {
		this.crc = crc;
	}

	public long getCompressedSize() {
		return compressedSize;
	}

	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	public void size(long size) {
		this.size = size;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getFsNameSize() {
		return fsNameSize;
	}

	public void setFsNameSize(int fsNameSize) {
		this.fsNameSize = fsNameSize;
	}

	public String getFsName() {
		return fsName;
	}

	public void setFsName(String fsName) {
		this.fsName = fsName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
