import static spark.Spark.post;

import ocr.OCR;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
public class ChatFlowAPI {
    public static void main(String[] args) {
        // Set a directory for file uploads
        File uploadDir = new File("upload");
        uploadDir.mkdir(); // Create the directory if it doesn't exist
        Spark.staticFiles.externalLocation("upload");

        post("/post-chatflow-data", (req, res) -> handleFileUpload(req, res));
    }

    private static String handleFileUpload(Request req, Response res) {
        if (!ServletFileUpload.isMultipartContent(req.raw())) {
            res.status(400);
            return "Only multipart content is supported";
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(req.raw());
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    // Check if the file is an image
                    String contentType = item.getContentType();
                    if (contentType != null && contentType.startsWith("image/")) {
                        // Process the uploaded image
                        File uploadFile = new File("upload/" + item.getName());
                        item.write(uploadFile);

                        // Pass the image the OCR class and extract the text
                        OCR extractTextOCR = new OCR(uploadFile.getAbsolutePath());
                        File directoryToTessData = new File("assets/tessdata");
                        String processedText = extractTextOCR.extractTextFromImage(directoryToTessData);
                        if(processedText == null){
                            return "Error processing image";
                        }

                        // Return a success message with the image name
                        return processedText;
                    } else {
                        res.status(400);
                        return "Only image files are supported";
                    }
                }
            }
        } catch (Exception e) {
            res.status(500);
            return "File upload failed: " + e.getMessage();
        }

        res.status(400);
        return "No file uploaded";
    }

}
