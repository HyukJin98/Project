package edu.du.samplep.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileUploadController {

    // application.properties에서 upload-dir 값을 읽어옵니다.
    @Value("${file.upload-dir}")
    private String uploadDir;

    // 애플리케이션 시작 시 업로드 디렉토리가 존재하는지 확인하고, 없으면 생성합니다.
    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();  // 디렉터리가 없으면 생성
        }
    }

    // 파일 업로드 처리
    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "파일을 선택해주세요.";
        }

        // 파일을 지정된 디렉토리에 저장
        Path path = Paths.get(uploadDir, file.getOriginalFilename());
        file.transferTo(path);

        return "파일 업로드 성공: " + file.getOriginalFilename();
    }

    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(uploadDir, fileName);
        byte[] fileContent = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    // 업로드된 파일 목록 조회 (선택 사항)
    @GetMapping("/files")
    @ResponseBody
    public String listFiles() {
        File dir = new File(uploadDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return "업로드된 파일이 없습니다.";
        }

        StringBuilder fileList = new StringBuilder("업로드된 파일 목록:<br>");
        for (File file : files) {
            fileList.append("<a href='/uploads/").append(file.getName()).append("'>")
                    .append(file.getName()).append("</a><br>");
        }
        return fileList.toString();
    }
}
