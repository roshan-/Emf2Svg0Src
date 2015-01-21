package org.freehep.graphicsio.emf;

import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.bmp.ColorEntry;
import net.sf.image4j.io.LittleEndianInputStream;

import org.freehep.graphicsio.emf.gdi.BitmapInfoHeader;
import org.freehep.graphicsio.emf.gdi.BlendFunction;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * this class creates a BufferedImage from EMF imaga data stored in a byte[].
 *
 * @author Steffen Greiffenberg
 * @version $Id:
 *          freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/
 *          EMFImageLoader.java 10ec7516e3ce 2007/02/06 18:42:34 duns $
 */
public class EMFImageLoader {

	/**
	 * creates a BufferedImage from an EMFInputStream using BitmapInfoHeader
	 * data
	 *
	 * @param bmi
	 *            BitmapInfoHeader storing Bitmap informations
	 * @param width
	 *            expected image width
	 * @param height
	 *            expected image height
	 * @param emf
	 *            EMF stream
	 * @param len
	 *            length of image data
	 * @param blendFunction
	 *            contains values for transparency
	 * @return BufferedImage or null
	 * @throws java.io.IOException
	 *             thrown by EMFInputStream
	 */
	public static BufferedImage readImage(
			BitmapInfoHeader bmi,
			int width,
			int height,
			EMFInputStream emf,
			int len,
			BlendFunction blendFunction) throws IOException {

		// 0    Windows 98/Me, Windows 2000/XP: The number of bits-per-pixel
		// is specified or is implied by the JPEG or PNG format.

		if (bmi.getBitCount() == 1) {
			// 1 	The bitmap is monochrome, and the bmiColors
			// member of BITMAPINFO contains two entries. Each
			// bit in the bitmap array represents a pixel. If
			// the bit is clear, the pixel is displayed with
			// the color of the first entry in the bmiColors
			// table; if the bit is set, the pixel has the color
			// of the second entry in the table.
			
			int colorsUsed = bmi.getClrUsed();
			//System.out.println("Colors Used "+colorsUsed);
			//System.out.println("Len "+len);
			if(colorsUsed == 0){
				colorsUsed = 1 << bmi.getBitCount();
			}
			int[] colors = emf.readUnsignedByte(colorsUsed * 4);

			// convert it to a color table
			ColorEntry[] colorTable = new ColorEntry[2];
			// iterator for color data
			int color = 0;
			for (int i = 0; i < colorsUsed; i++, color = i * 4) {
				colorTable[i] = new ColorEntry(
						colors[color + 2],
						colors[color + 1],
						colors[color],
						255);
			}
			
			byte[] imageData = emf.readByte((int) emf.getLength());
			//System.out.println("image data length "+imageData.length);
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image =  BMPDecoder.read1(bmi.toInfoHeader(), 
					new LittleEndianInputStream(imageDataStream), colorTable);
			imageDataStream.close();
			return image;

			/* for debugging: shows every loaded image
            javax.swing.JFrame f = new javax.swing.JFrame("test");
            f.getContentPane().setBackground(Color.green);
            f.getContentPane().setLayout(
                new java.awt.BorderLayout(0, 0));
            f.getContentPane().add(
                java.awt.BorderLayout.CENTER,
                new javax.swing.JLabel(
                    new javax.swing.ImageIcon(result)));
            f.setSize(new java.awt.Dimension(width + 20, height + 20));
            f.setVisible(true);*/




		} else if ((bmi.getBitCount() == 4) &&
				(bmi.getCompression() == EMFConstants.BI_RGB)) {
			int colorsUsed = bmi.getClrUsed();

			int[] colors = emf.readUnsignedByte(colorsUsed * 4);

			// convert it to a color table
			ColorEntry[] colorTable = new ColorEntry[16];
			// iterator for color data
			int color = 0;
			for (int i = 0; i < colorsUsed; i++, color = i * 4) {
				colorTable[i] = new ColorEntry(
						colors[color + 2],
						colors[color + 1],
						colors[color],
						255);
			}

			// fill with black to avoid ArrayIndexOutOfBoundExceptions;
			// some images seem to use more colors than stored in ClrUsed
			if (colorsUsed < colorTable.length) {
				Arrays.fill(colorTable, colorsUsed, colorTable.length, new ColorEntry(0,0,0,0));
			}
			
			byte[] imageData = emf.readByte((int) emf.getLength());
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image =  BMPDecoder.read4(bmi.toInfoHeader(), 
					new LittleEndianInputStream(imageDataStream), colorTable);
			imageDataStream.close();
			return image;
			
			
		}
		else if ((bmi.getBitCount() == 8) &&
				(bmi.getCompression() == EMFConstants.BI_RGB)) {
			// 8 	The bitmap has a maximum of 256 colors, and the bmiColors member
			// of BITMAPINFO contains up to 256 entries. In this case, each byte in
			// the array represents a single pixel.

			// TODO has to be done in BitMapInfoHeader?
			// read the color table
			int colorsUsed = bmi.getClrUsed();

			int[] colors = emf.readUnsignedByte(colorsUsed * 4);

			// convert it to a color table
			ColorEntry[] colorTable = new ColorEntry[256];
			// iterator for color data
			int color = 0;
			for (int i = 0; i < colorsUsed; i++, color = i * 4) {
				colorTable[i] = new ColorEntry(
						colors[color + 2],
						colors[color + 1],
						colors[color],
						255);
			}

			// fill with black to avoid ArrayIndexOutOfBoundExceptions;
			// some images seem to use more colors than stored in ClrUsed
			if (colorsUsed < colorTable.length) {
				Arrays.fill(colorTable, colorsUsed, colorTable.length, new ColorEntry(0,0,0,0));
			}
			
			byte[] imageData = emf.readByte((int) emf.getLength());
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image =  BMPDecoder.read8(bmi.toInfoHeader(), 
					new LittleEndianInputStream(imageDataStream), colorTable);
			imageDataStream.close();
			return image;
			

		}

		// The bitmap has a maximum of 2^16 colors. If the biCompression member
		// of the BITMAPINFOHEADER is BI_RGB, the bmiColors member of BITMAPINFO is
		// NULL.
		else if ((bmi.getBitCount() == 16) &&
				(bmi.getCompression() == EMFConstants.BI_RGB)) {

			// Each WORD in the bitmap array represents a single pixel. The
			// relative intensities of red, green, and blue are represented with
			// five bits for each color component. The value for blue is in the least
			// significant five bits, followed by five bits each for green and red.
			// The most significant bit is not used. The bmiColors color table is used
			// for optimizing colors used on palette-based devices, and must contain
			// the number of entries specified by the biClrUsed member of the
			// BITMAPINFOHEADER.
			
			byte[] imageData = emf.readByte(len);
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image =  BMPDecoder.read16(bmi.toInfoHeader(), new LittleEndianInputStream(imageDataStream));
			imageDataStream.close();
			return image;

		}
		
		else if (bmi.getBitCount() == 24) {

			byte[] imageData = emf.readByte(len);
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image =  BMPDecoder.read24(bmi.toInfoHeader(), new LittleEndianInputStream(imageDataStream));
			imageDataStream.close();
			return image;

		}
//		
			// The bitmap has a maximum of 2^32 colors. If the biCompression member of the
			// BITMAPINFOHEADER is BI_RGB, the bmiColors member of BITMAPINFO is NULL.
		else if ((bmi.getBitCount() == 32)
				&& (bmi.getCompression() == EMFConstants.BI_RGB)) {

			byte[] imageData = emf.readByte(len);
			InputStream imageDataStream = new ByteArrayInputStream(imageData);
			BufferedImage image = BMPDecoder.read32(bmi.toInfoHeader(),
					new LittleEndianInputStream(imageDataStream));
			imageDataStream.close();

			return image;

		}
			// If the biCompression member of the BITMAPINFOHEADER is BI_BITFIELDS,
			// the bmiColors member contains three DWORD color masks that specify the
			// red, green, and blue components, respectively, of each pixel. Each DWORD
			// in the bitmap array represents a single pixel.

			// Windows NT/ 2000: When the biCompression member is BI_BITFIELDS, bits set in
			// each DWORD mask must be contiguous and should not overlap the bits of
			// another mask. All the bits in the pixel do not need to be used.

			// Windows 95/98/Me: When the biCompression member is BI_BITFIELDS, the system
			// supports only the following 32-bpp color mask: The blue mask is 0x000000FF,
			// the green mask is 0x0000FF00, and the red mask is 0x00FF0000.
			else if ((bmi.getBitCount() == 32) &&
					(bmi.getCompression() == EMFConstants.BI_BITFIELDS)) {
				/* byte[] bytes =*/ emf.readByte(len);
				return null;
			} else {
				/* byte[] bytes =*/ emf.readByte(len);
				return null;
			}
		}
}