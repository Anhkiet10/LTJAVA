package com.webnewpaper.backend.services;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PdfDownloadService {

    private final RestClient genericRestClient;

    public PdfDownloadService(RestClient genericRestClient) {
        this.genericRestClient = genericRestClient;
    }

    public byte[] downloadPdf(String url) {
        byte[] bytes = genericRestClient.get()
                .uri(url)
                .header(HttpHeaders.USER_AGENT,
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header(HttpHeaders.ACCEPT, "application/pdf,*/*")
                .retrieve()
                .body(byte[].class);

        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException("Không tải được PDF từ URL (server trả về rỗng): " + url);
        }

        return bytes;
    }
}