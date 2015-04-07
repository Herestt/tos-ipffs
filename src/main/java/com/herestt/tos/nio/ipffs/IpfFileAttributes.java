package com.herestt.tos.nio.ipffs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Describes the data that represents a file stored inside the IPF file system.
 * 
 * <p>Files attributes are stored at the end of the file system, right before
 * the header.</p>
 * 
 * <p>This description gives information about :
 * 		<ul>
 * 			<li>the size of the path string (2 bytes);</li>
 * 			<li>the checksum of the content (4 bytes);</li>
 * 			<li>the size of the compressed content (4 bytes);</li>
 * 			<li>the size of the content (4 bytes);</li>
 * 			<li>the offset of the content (4 bytes);</li>
 * 			<li>the size of the file system name (2 bytes);</li>
 * 			<li>the file system's name. (fsNameSize bytes);</li>
 * 			<li>the file's path. (pathSize bytes).</li>
 * 		</ul>
 * </p> 
 * 
 * @author Herestt
 *
 */
public class IpfFileAttributes implements BasicFileAttributes {

	/**	The size of the file path string. */
	private int pathSize;
	
	/**	The checksum of the file content. */
	private long crc;
	
	/**	The size of the compressed content. */
	private long compressedSize;
	
	/**	The size of the file content. */
	private long size;
	
	/**	The offset of the file content. */
	private long offset;
	
	/**	The size of the IPF file name. */
	private int fsNameSize;
	
	/**	The name of the file system. */
	private String fsName;
	
	/**	The path of the file. */
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
		return true;
	}

	public boolean isDirectory() {
		return false;
	}

	public boolean isSymbolicLink() {
		return false;
	}

	public boolean isOther() {
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
	
	/**
	 * Gets the path size.
	 * 
	 * The size of the absolute path minus one due to the fact that
	 * paths are represented without the '/' root character.
	 * 
	 * @return the path size.
	 */
	public int getPathSize() {
		return pathSize;
	}

	/**
	 * Sets the path size.
	 * 
	 * @param pathSize The new path size.
	 */
	public void setPathSize(int pathSize) {
		this.pathSize = pathSize;
	}

	/**
	 * Gets the checksum.
	 * 
	 * @return the checksum.
	 */
	public long getCrc() {
		return crc;
	}

	/**
	 * Sets the checksum.
	 * 
	 * @param crc The new checksum.
	 */
	public void setCrc(long crc) {
		this.crc = crc;
	}

	/**
	 * Gets the compressed size.
	 * 
	 * @return The compressed size.
	 */
	public long getCompressedSize() {
		return compressedSize;
	}

	/**
	 * Sets the compressed size.
	 * 
	 * @param compressedSize The new compressed size.
	 */
	public void setCompressedSize(long compressedSize) {
		this.compressedSize = compressedSize;
	}

	/**
	 * Sets the file size.
	 * 
	 * @param size The new file size.
	 */
	public void size(long size) {
		this.size = size;
	}

	/**
	 * Gets the offset of the file content.
	 * 
	 * @return the offset.
	 */
	public long getOffset() {
		return offset;
	}

	/**
	 * Sets the offset of the content.
	 * 
	 * @param offset The new offset of the content.
	 */
	public void setOffset(long offset) {
		this.offset = offset;
	}

	/**
	 * Gets the file system's name size.
	 * 
	 * @return the file system's name size.
	 */
	public int getFsNameSize() {
		return fsNameSize;
	}

	/**
	 * Sets the file system's name size.
	 * 
	 * @param fsNameSize The new size of the file system's name.
	 */
	public void setFsNameSize(int fsNameSize) {
		this.fsNameSize = fsNameSize;
	}

	/**
	 * Gets the file system's name.
	 * 
	 * @return the file system's name.
	 */
	public String getFsName() {
		return fsName;
	}

	/**
	 * Sets the file system's name.
	 * 
	 * @param fsName The new file system's name.
	 */
	public void setFsName(String fsName) {
		this.fsName = fsName;
	}

	/**
	 * Gets the file path.
	 * 
	 * Although all the paths are absolute, they don't start with the '/' root character.
	 * 
	 * @return the file path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the file path.
	 * 
	 * @param path The new file path.
	 */
	public void setPath(String path) {
		this.path = path;
	}
}
