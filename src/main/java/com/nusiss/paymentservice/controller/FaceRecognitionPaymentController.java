package com.nusiss.paymentservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nusiss.commonservice.config.ApiResponse;
import com.nusiss.commonservice.entity.User;
import com.nusiss.commonservice.feign.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/face-recognition")
public class FaceRecognitionPaymentController {

    @Autowired
    private UserFeignClient userFeignClient;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(FaceRecognitionPaymentController.class);

    // Path to store the face image
    private static final String IMAGE_PATH = "C:/faces/"; // Adjust for Windows path

    // Endpoint for face registration
    @PostMapping("/register")
    public ResponseEntity<String> registerFace(@RequestHeader("Authorization") String var1, @RequestParam("image") MultipartFile image) throws IOException {
        try {
            // Fetch user details using Feign client
            ResponseEntity<ApiResponse<User>> userResponseEntity = userFeignClient.getCurrentUserInfo(var1);
            User user = userResponseEntity.getBody().getData();

            /*// Create a filename based on the user ID
            StringBuffer filename = new StringBuffer();
            filename.append(user.getUserId());
            filename.append("-");
            filename.append(UUID.randomUUID());
            filename.append(".jpg");
            Path path = Paths.get(IMAGE_PATH + filename);
            log.info(String.valueOf(image.getSize()));
            // Create directories and write the image
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());*/

            // Call Python service to register the face along with user info
            ResponseEntity<String> responseEntity = callPythonFaceRecognitionService(image, "register", user.getUserId());
            return responseEntity;
            /*if (responseEntity.getStatusCode() == HttpStatus.OK) {
                try {
                    // Parse JSON string into map
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> resultMap = objectMapper.readValue(responseEntity.getBody(), Map.class);

                    // Return structured response
                    return ResponseEntity.ok(resultMap);

                } catch (Exception e) {
                    e.printStackTrace();

                    Map<String, Object> errorBody = new HashMap<>();
                    errorBody.put("message", "Failed to parse face recognition result");
                    errorBody.put("error", e.getMessage());

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
                }
            } else {
                Map<String, Object> errorBody = new HashMap<>();
                errorBody.put("message", "Face recognition service failed");
                errorBody.put("status", responseEntity.getStatusCodeValue());
                errorBody.put("error", responseEntity.getBody());

                return ResponseEntity.status(responseEntity.getStatusCode()).body(errorBody);
            }*/

        } catch (Exception e) {
            log.error("Error with registering face", e);

            return ResponseEntity.status(500).body("Error registering face.");
        }

    }

    // Endpoint for face verification
    @PostMapping("/payment")
    public ResponseEntity<String> verifyFace(@RequestParam("image") MultipartFile image) throws IOException {
        try{
            /*// Save the uploaded image temporarily
            String filename = "temp_face.jpg";
            Path tempPath = Paths.get(IMAGE_PATH + filename);
            Files.write(tempPath, image.getBytes());*/

            // Call Python backend for face recognition comparison (we assume it's running on localhost:5000)
            ResponseEntity<String> responseEntity = callPythonFaceRecognitionService(image, "verify", null);

            return responseEntity;
        } catch (Exception e){
            log.error("Error with verifying face", e);

            return ResponseEntity.status(500).body("Error verifying face.");
        }

    }

    // Helper method to call Python service for face registration and verification
    private ResponseEntity<String> callPythonFaceRecognitionService(MultipartFile image, String action, Integer userId) {
        String pythonServiceUrl = "http://localhost:5000/" + action; // register or verify

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        try {
            // Convert MultipartFile to a temporary file
            File tempFile = File.createTempFile("upload-", image.getOriginalFilename());
            image.transferTo(tempFile);
            FileSystemResource fileResource = new FileSystemResource(tempFile);

            // Form the request body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", fileResource);
            if (userId != null) {
                body.add("userId", userId);
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                    pythonServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Clean up temp file
            tempFile.delete();

            return response;
        } catch (Exception e) {
            log.error("Error calling Python face recognition service", e);
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());
            errorBody.put("status", 500);
            errorBody.put("error", "Error calling Python face recognition service");
            try {
                return ResponseEntity.status(500).body(objectMapper.writeValueAsString(errorBody));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
