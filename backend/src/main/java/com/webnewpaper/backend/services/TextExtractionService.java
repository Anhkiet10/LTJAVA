package com.webnewpaper.backend.services;

import com.webnewpaper.backend.config.OcrProperties;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class TextExtractionService {

    private static final int MIN_TEXT_LENGTH = 200; // dưới ngưỡng này coi như PDF không có text layer thật
    private static final int MAX_OCR_PAGES = 20; // giới hạn số trang OCR để tránh quá lâu

    private final PdfDownloadService pdfDownloadService;
    private final OcrProperties ocrProperties;

    public TextExtractionService(PdfDownloadService pdfDownloadService, OcrProperties ocrProperties) {
        this.pdfDownloadService = pdfDownloadService;
        this.ocrProperties = ocrProperties;
    }

    public record ExtractionResult(String text, String method) {}

    public ExtractionResult extract(String url) throws Exception {
        byte[] pdfBytes = pdfDownloadService.downloadPdf(url);

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);

            if (text != null && text.trim().length() >= MIN_TEXT_LENGTH) {
                return new ExtractionResult(text, "PDFBOX");
            }

            // Fallback: PDF không có text layer (dạng scan) → OCR từng trang
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(ocrProperties.getTessdataPath());
            tesseract.setLanguage("eng");

            PDFRenderer renderer = new PDFRenderer(document);
            StringBuilder ocrText = new StringBuilder();
            int pageCount = Math.min(document.getNumberOfPages(), MAX_OCR_PAGES);

            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 200);
                ocrText.append(tesseract.doOCR(image)).append("\n");
            }

            return new ExtractionResult(ocrText.toString(), "OCR");
        }
    }
}