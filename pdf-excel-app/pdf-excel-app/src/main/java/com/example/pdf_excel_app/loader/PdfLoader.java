package com.example.pdf_excel_app.loader;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PdfLoader {
  public Document loadPdf(MultipartFile file) throws IOException {

      //validate file was uploaded
      if(file==null || file.isEmpty()){
          throw new IllegalArgumentException("file is empty");
      }

      //check the original file extension
     String filename =  file.getOriginalFilename();

      //validate the file pdf only
      if(filename == null || !filename.toLowerCase().endsWith(".pdf")){
          throw new IllegalArgumentException("\"Only PDF files are allowed");
      }

      //// Load the PDF from uploaded bytes.
     try(PDDocument pdfDocument = Loader.loadPDF(file.getBytes()))
     {
       // Create PDF text extractor.
         PDFTextStripper  pdfTextStripper = new PDFTextStripper();

         // Extract all text from the PDF.
         String text = pdfTextStripper.getText(pdfDocument);
         // Reject PDFs that contain no extractable text.
         if(text == null || text.isBlank())
         {
             throw new IllegalArgumentException("No readable text found in PDF");
         }
         // Create metadata for document identification.
         Map<String, Object> metadata = new HashMap<String, Object>();
         metadata.put("source", filename);
         metadata.put("fileType", "PDF");
         metadata.put("totalPages", pdfDocument.getNumberOfPages());
         return  new Document(text,metadata );

     }

  }
}
