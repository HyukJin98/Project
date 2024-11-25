package edu.du.samplep.service;

import edu.du.samplep.entity.FileUpload;
import edu.du.samplep.entity.Post;
import edu.du.samplep.repository.FileUploadRepository;
import edu.du.samplep.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final PostRepository postRepository;

    // application.properties에서 업로드 디렉토리 경로를 읽어옴
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public FileUploadService(FileUploadRepository fileUploadRepository, PostRepository postRepository) {
        this.fileUploadRepository = fileUploadRepository;
        this.postRepository = postRepository;
    }

    // 업로드 디렉토리 생성 확인
    @PostConstruct
    public void init() {
        // uploadDir 값이 null인지 확인
        if (uploadDir == null || uploadDir.isEmpty()) {
            throw new IllegalArgumentException("uploadDir is not defined in application.properties");
        }

        System.out.println("Upload directory: " + uploadDir);  // 디버깅 출력

        // 업로드 디렉토리가 존재하지 않으면 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
    }

    // 파일 업로드 처리
    public FileUpload uploadFile(MultipartFile file, Long postId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        // 파일 이름과 경로 설정
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

        // 파일 저장
        file.transferTo(filePath.toFile());

        // 게시글 가져오기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        // 파일 정보 저장
        FileUpload fileUpload = FileUpload.builder()
                .fileName(fileName)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .post(post)
                .build();
        return fileUploadRepository.save(fileUpload);
    }

    // 게시글에 첨부된 모든 파일 목록 가져오기
    public List<FileUpload> getFilesForPost(Long postId) {
        return fileUploadRepository.findByPostId(postId);
    }

    // 파일 삭제 (선택적 구현)
    public void deleteFile(Long fileId) throws IOException {
        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid file ID"));

        Path filePath = Paths.get(fileUpload.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);  // 파일 시스템에서 삭제
        }

        fileUploadRepository.delete(fileUpload);  // 데이터베이스에서 파일 정보 삭제
    }
}
