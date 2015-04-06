package com.herestt.nio.ipffs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

/**
 * Describes the information data about an <code>.ipf</code> file.
 * 
 * @author Herestt
 *
 */
public class IpfFileStore extends FileStore {

	/** The <code>.ipf</code> file.*/
	private String name;
	
	/** The count of files stored in the file system. */
	private long fileCount;
	
	/** Beginning offset of the file list. */
	private long listOffset;
	
	/** The magic number that identifies <code>.ipf</code> files*/
	private byte[] magicNumber;

	protected IpfFileStore(String name, long fileCount, long listOffset,
			byte[] magicNumber) {
		this.name = name;
		this.fileCount = fileCount;
		this.listOffset = listOffset;
		this.magicNumber = magicNumber;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String type() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getTotalSpace() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean supportsFileAttributeView(
			Class<? extends FileAttributeView> type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(
			Class<V> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*** IPF File Store Specific Methods ***/

	/**
	 * Gets the count of files held by this store.
	 * 
	 * @return the file count.
	 */
	public long getFileCount() {
		return fileCount;
	}

	/**
	 * Gets the offset where begins the file description list. 
	 * 
	 * @return the file list offset.
	 */
	public long getListOffset() {
		return listOffset;
	}

	/**
	 * Gets the magic number for IPF file systems.
	 * 
	 * @return the magic number.
	 */
	public byte[] getMagicNumber() {
		return magicNumber;
	}
}
