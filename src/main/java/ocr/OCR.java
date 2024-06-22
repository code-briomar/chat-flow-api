package ocr;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO : Improve Regex filters for more realistic outputs.
// TODO : Optimize OCR
public class OCR {
    private String imagePath = "";
    public OCR(String imagePath){
        this.imagePath = imagePath;
    }

    public String extractTextFromImage(File directoryToTessData){
        Tesseract tesseract = new Tesseract();
//        String imagePath = "C:/Users/Briane/Pictures/Screenshots/t3.png";

        // Regex filter to filter out unwanted characters
        String regex = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(regex);

        try{
            tesseract.setDatapath(directoryToTessData.getAbsolutePath());
            // Set OCR Language
            tesseract.setLanguage("eng");

            String text = tesseract.doOCR(new File(this.imagePath));

            // Apply regex and replace unwanted characters with an empty string
            Matcher matcher = pattern.matcher(text);
            String filteredText = matcher.replaceAll(" ");

            return filteredText;
        } catch (TesseractException exception){
            exception.printStackTrace();
        }
        return null;
    }
}
