package odyssey.ru.linglibrary.subtitle.converter;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.FormatASS;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.FormatSCC;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.FormatSRT;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.FormatSTL;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.FormatTTML;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.TimedTextFileFormat;
import odyssey.ru.linglibrary.subtitle.converter.subtitleFile.TimedTextObject;


public class SubtitleConvert {

	private static final String TAG = "SubtitleConvert";

	private SubtitleConvert() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		TimedTextObject tto;
		TimedTextFileFormat ttff;
		OutputStream output;
		Log.d(TAG, "Usage: java Convert input-file input-format output-format output-file");
		//this is in case anyone may want to use this as stand alone java executable
		if (args != null && args.length == 4){



				String inputFile = args[0];
				String inputFormat = args[1];
				String outputFormat = args[2];
				String outputFile = args[3];

				if ("SRT".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSRT();
				} else if ("STL".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSTL();
				} else if ("SCC".equalsIgnoreCase(inputFormat)){
					ttff = new FormatSCC();
				} else if ("XML".equalsIgnoreCase(inputFormat)){
					ttff = new FormatTTML();
				} else if ("ASS".equalsIgnoreCase(inputFormat)){
					ttff = new FormatASS();
				} else {
					throw new Exception("Unrecognized input format: "+inputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
				}

                File file = new File(inputFile);
				InputStream is = new FileInputStream(file);
				tto = ttff.parseFile(file.getName(), is);

				if ("SRT".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toSRT());
				} else if ("STL".equalsIgnoreCase(outputFormat)){
					output = new BufferedOutputStream(new FileOutputStream(outputFile));
					output.write(tto.toSTL());
                    output.close();
				} else if ("SCC".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toSCC());
				} else if ("XML".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toTTML());
				} else if ("ASS".equalsIgnoreCase(outputFormat)){
					IOClass.writeFileTxt(outputFile, tto.toASS());
				} else {
					throw new Exception("Unrecognized input format: "+outputFormat+" only [SRT,STL,SCC,XML,ASS] are possible");
				}


			// normal test use
		} else {
               Log.d(TAG, "Usage: java Convert input-file input-format output-format output-file");
		}

	}
}
