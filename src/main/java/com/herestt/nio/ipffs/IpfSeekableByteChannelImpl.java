package com.herestt.nio.ipffs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class IpfSeekableByteChannelImpl implements SeekableByteChannel {
	
	private File file;
	private RandomAccessFile raf;
	private FileChannel channel;
	private boolean writable = false;
	
	public IpfSeekableByteChannelImpl(IpfPath path, Set<? extends OpenOption> options,
			IpfFileAttributes attrs) throws IOException {
		
		checkPath(path);
		String mode = applyOptions(options);
		file = dumpFile(path, attrs);
		raf = new RandomAccessFile(file, mode);
		channel = raf.getChannel();
	}
	
	private void checkPath(IpfPath path) throws FileNotFoundException {
		Path fsPath = path.getFileSystem().getFileSystemPath();
		if(fsPath.getFileSystem() != FileSystems.getDefault())
			throw new IllegalArgumentException();
		if(!Files.isRegularFile(fsPath))
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
				&& options.size() == 1
				|| options.size() == 0)
			mode = "r";
		if(mode == null)
			throw new IllegalArgumentException();
		return mode;
	}
	
	private File dumpFile(IpfPath path, IpfFileAttributes attrs) throws FileNotFoundException, IOException {
		
		String suffix = path.toString().replaceAll("/", "_");
		File tmpFile = Files.createTempFile("ipf", suffix).toFile();
		File fsFile = path.getFileSystem().getFileSystemPath().toFile();
		
		try(RandomAccessFile fsraf = new RandomAccessFile(fsFile, "r");
				FileOutputStream tmpfos = new FileOutputStream(tmpFile);
				FileChannel fsfc = fsraf.getChannel();
				WritableByteChannel tmpfc = tmpfos.getChannel()) {
			
			ByteBuffer raw = ByteBuffer.allocate((int) attrs.getCompressedSize());
			ByteBuffer result = ByteBuffer.allocate((int) attrs.size());
			
			fsfc.read(raw, attrs.getOffset());
			decompress(raw, result);
			tmpfc.write(result);
			
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tmpFile;
	}
	
	private void decompress(ByteBuffer input, ByteBuffer output)
			throws DataFormatException {
		Inflater decompresser = new Inflater(true);
		decompresser.setInput(input.array());
		decompresser.inflate(output.array());
		decompresser.end();
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
