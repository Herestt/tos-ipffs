package com.herestt.nio.ipffs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

/**
 * A {@link SeekableByteChannel} implementation for the IPF file system.
 * 
 * <p>This {@link SeekableByteChannel} handles reading and writing operations on IPF stored files. 
 * Therefore only {@link IpfPath} are allowed.
 * The configuration is made through {@link StandardOpenOption}. Only {@link StandardOpenOption#READ},
 * {@link StandardOpenOption#WRITE} and {@link StandardOpenOption#APPEND} are allowed.</p>
 * 
 * <p><b>Notice:</b> By default this channel is opened with the {@link StandardOpenOption#READ} option.</p>
 * 
 * @author Herestt
 *
 */
public class IpfSeekableByteChannelImpl implements SeekableByteChannel {
	
	private File file;
	private RandomAccessFile raf;
	private FileChannel channel;
	private boolean writable = false;
	
	public IpfSeekableByteChannelImpl(Path path, Set<? extends OpenOption> options)
			throws IOException {
		checkPath(path);
		String mode = applyOptions(options);
		file = path.toFile();
		raf = new RandomAccessFile(file, mode);
		channel = raf.getChannel();
	}
	
	private void checkPath(Path path) throws FileNotFoundException {
		if(path.getFileSystem() != FileSystems.getDefault())
			throw new IllegalArgumentException();
		if(!Files.isRegularFile(path))
			throw new FileNotFoundException();
	}
	
	private String applyOptions(Set<? extends OpenOption> options) {
		String mode = null;
		if(options.contains(StandardOpenOption.WRITE)
				|| options.contains(StandardOpenOption.APPEND)) {
			mode = "rwd";
			writable = true;
		}
		else if(options.contains(StandardOpenOption.READ)
				|| options.size() == 0)
			mode = "r";
		if(mode == null)
			throw new IllegalArgumentException();
		return mode;
	}
	
	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		if(channel != null)
			channel.close();
		if(raf != null)
			raf.close();
		if(file != null)
			file.delete();
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		return channel.read(dst);
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if(writable)
			return channel.write(src);
		throw new UnsupportedOperationException();
	}

	@Override
	public long position() throws IOException {
		return channel.position();
	}

	@Override
	public SeekableByteChannel position(long newPosition) throws IOException {
		return channel.position(newPosition);
	}

	@Override
	public long size() throws IOException {
		return channel.size();
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		return channel.truncate(size);
	}
}