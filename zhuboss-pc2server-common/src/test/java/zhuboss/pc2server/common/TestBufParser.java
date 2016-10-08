package zhuboss.pc2server.common;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.UUID;

import zhuboss.pc2server.common.bufparser.TransPackage;
import zhuboss.pc2server.common.bufparser.TransPackageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import junit.framework.TestCase;

public class TestBufParser extends TestCase {
	public void testParser() throws Exception{
			PooledByteBufAllocator allocator = new PooledByteBufAllocator();
			
			TransPackageDecoder transPackageDecoder = new TransPackageDecoder(false);
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			for(int i=0;i<10000;i++){
				os.write(String.valueOf(i).getBytes());
			}
			System.out.println("data size:" + os.toByteArray().length);
			TransPackage transPackage = new TransPackage(JavaUtil.uuid2Bytes(UUID.randomUUID()), Constants.OPERATE_TYPE_DATA,Constants.ZIP_FLAG_NO, os.toByteArray());
			
			byte[] bytes = transPackage.getFullBytes();
			
			ByteBuf[] bufs = new ByteBuf[]{
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes, 0, 3)),
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes, 3, 100)),
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes, 100, bytes.length))
					};
			System.out.println("1.....");
			for(int i=0;i<bufs.length;i++){
//				transPackageDecoder.decode..
//				bufParser.parse(bufs[i]);
			}
			
			
			bufs = new ByteBuf[]{
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes, 0, 100)),
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes, 100, bytes.length))
					};
			System.out.println("2.....");
			for(int i=0;i<bufs.length;i++){
//				transPackageDecoder.decode..
			}
			
			bufs = new ByteBuf[]{
					allocator.buffer().writeBytes(Arrays.copyOfRange(bytes,0,bytes.length))
					};
			System.out.println("3.....");
			for(int i=0;i<bufs.length;i++){
//				transPackageDecoder.decode..
			}
			
	}
}
