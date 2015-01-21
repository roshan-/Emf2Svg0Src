package com.optigra.soft.tagsresolver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.freehep.graphicsio.emf.EMFHeader;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.util.io.Tag;

public class EmfTagPrinter {
	
	private static String sampleLocation = "/home/rdyyak/development/workspace/emf2svgimproved/pictures/emfs/";
	private static String outputDirectory = "/home/rdyyak/development/workspace/emf2svgimproved/pictures/svg/";

	public static void main(String[] args) throws Exception {		
		generateAll();		
	}
	
	public static void generateAll() throws Exception{
		Map<String, String> picMap = new HashMap<String, String>();
//		picMap.put("542321_image3.emf", "542321_image3-transcript.txt");
//		picMap.put("ConvertedFromAI_Poor_InfoG_green_report.emf", "ConvertedFromAI_Poor_InfoG_green_report-transcript.txt");
//		picMap.put("ConvertedFromAI_Valley.emf", "ConvertedFromAI_Valley-transcript.txt");
//		picMap.put("ConvertedFromAI_GreenCartoonLandscapesVectorBackground.emf", "ConvertedFromAI_GreenCartoonLandscapesVectorBackground-transcript.txt");
//		picMap.put("ConvertedFromAI_colorful_text2_.emf", "ConvertedFromAI_colorful_text2_-transcript.txt");
//		picMap.put("ConvertedFromAI_colorful_text.emf", "ConvertedFromAI_colorful_text-transcript.txt");
//		picMap.put("ConvertedFromAI_Hillscape.emf", "ConvertedFromAI_Hillscape-transcript.txt");
//		picMap.put("10008800_image3.emf", "10008800_image3-transcript.txt");
//		picMap.put("10008800_image7.emf", "10008800_image7-transcript.txt");
//		picMap.put("542321_image102.emf", "542321_image102-transcript.txt");
//		picMap.put("542321_image120.emf", "542321_image120-transcript.txt");
		picMap.put("sample_libemf_file.emf", "sample_libemf_file.txt");
//		picMap.put("542321_image41.emf", "542321_image41-transcript.txt");
		
		EMFInputStream eistream = null;
		PrintStream ostream = null;
		try{
		for(Entry<String, String> pic: picMap.entrySet() ){
			
			eistream = new EMFInputStream(new FileInputStream(sampleLocation + pic.getKey()));			
			ostream = new PrintStream(new FileOutputStream(outputDirectory + pic.getValue()));
			
			EMFHeader header = eistream.readHeader();
			ostream.println(header.toString());

	        // read all tags
	        Tag tag;
	        while ((tag = eistream.readTag()) != null) {
	        	ostream.println(tag.toString());
	        }
	        
	        eistream.close();
	        ostream.close();
			
		}
		}finally{
			if(eistream != null) eistream.close();
			if(ostream != null) ostream.close();
		}
	}

}
