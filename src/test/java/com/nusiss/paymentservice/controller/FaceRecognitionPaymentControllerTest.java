package com.nusiss.paymentservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nusiss.commonservice.config.ApiResponse;
import com.nusiss.commonservice.entity.User;
import com.nusiss.commonservice.feign.UserFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.web.client.RestTemplate;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaceRecognitionPaymentControllerTest {

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private MultipartFile mockImage;



    @InjectMocks
    private FaceRecognitionPaymentController controller;

    private ObjectMapper objectMapper;
    private User mockUser;
    private ApiResponse<User> mockApiResponse;


    @MockitoSettings(strictness = Strictness.LENIENT)

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(controller, "objectMapper", objectMapper);

        // Setup mock user
        mockUser = new User();
        mockUser.setUserId(123);
        mockUser.setUsername("testuser");

        // Setup mock API response
        mockApiResponse = new ApiResponse<>();
        mockApiResponse.setData(mockUser);
    }

    @Test
    void testRegisterFace_Success() throws IOException {
        // Arrange
        String authHeader = "Bearer token123";
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
//        when(mockImage.getBytes()).thenReturn("test image data".getBytes());
        when(userFeignClient.getCurrentUserInfo(authHeader))
                .thenReturn(ResponseEntity.ok(mockApiResponse));

        String expectedResponse = "{\"status\":\"success\",\"message\":\"Face registered successfully\"}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.ok(expectedResponse);

        try (MockedStatic<File> mockedFile = mockStatic(File.class);
             MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
                     (mock, context) -> {
                         when(mock.exchange(
                                 anyString(),
                                 eq(HttpMethod.POST),
                                 any(HttpEntity.class),
                                 eq(String.class)
                         )).thenReturn(mockPythonResponse);
                     })) {

            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act
            ResponseEntity<String> result = controller.registerFace(authHeader, mockImage);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(expectedResponse, result.getBody());
            verify(userFeignClient).getCurrentUserInfo(authHeader);
            verify(mockImage).transferTo(mockTempFile);

        }
    }

    @Test
    void testRegisterFace_UserFeignClientException() throws IOException {
        // Arrange
        String authHeader = "Bearer token123";
        when(userFeignClient.getCurrentUserInfo(authHeader))
                .thenThrow(new RuntimeException("Feign client error"));

        // Act
        ResponseEntity<String> result = controller.registerFace(authHeader, mockImage);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error registering face.", result.getBody());
    }

    @Test
    void testRegisterFace_IOException() throws IOException {
        // Arrange
        String authHeader = "Bearer token123";
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
        when(userFeignClient.getCurrentUserInfo(authHeader))
                .thenReturn(ResponseEntity.ok(mockApiResponse));

        try (MockedStatic<File> mockedFile = mockStatic(File.class)) {
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenThrow(new IOException("File creation failed"));

            // Act
            ResponseEntity<String> result = controller.registerFace(authHeader, mockImage);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
//            assertEquals("Error registering face.", result.getBody());
        }
    }

    @Test
    void testVerifyFace_Success() throws IOException {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
//        when(mockImage.getBytes()).thenReturn("test image data".getBytes());

        String pythonResponse = "{\"userId\":\"123\",\"confidence\":0.95}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.ok(pythonResponse);

        try (MockedStatic<File> mockedFile = mockStatic(File.class);
             MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
                     (mock, context) -> {
                         when(mock.exchange(
                                 anyString(),
                                 eq(HttpMethod.POST),
                                 any(HttpEntity.class),
                                 eq(String.class)
                         )).thenReturn(mockPythonResponse);
                     })) {

            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act
            ResponseEntity<String> result = controller.verifyFace(mockImage);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());

            // Parse response to verify content
            JsonNode responseNode = objectMapper.readTree(result.getBody());
            assertEquals("successfully detect user.", responseNode.get("message").asText());
            assertEquals(200, responseNode.get("status").asInt());
            assertEquals("123", responseNode.get("userId").asText());

            verify(mockImage).transferTo(mockTempFile);

        }
    }

    @Test
    void testVerifyFace_PythonServiceError() throws IOException {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
//        when(mockImage.getBytes()).thenReturn("test image data".getBytes());

        String errorResponse = "{\"message\":\"No face detected\"}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        try (MockedStatic<File> mockedFile = mockStatic(File.class);
             MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
                     (mock, context) -> {
                         when(mock.exchange(
                                 anyString(),
                                 eq(HttpMethod.POST),
                                 any(HttpEntity.class),
                                 eq(String.class)
                         )).thenReturn(mockPythonResponse);
                     })) {

            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act
            ResponseEntity<String> result = controller.verifyFace(mockImage);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());

            // Parse response to verify error handling
            JsonNode responseNode = objectMapper.readTree(result.getBody());
            assertEquals("No face detected", responseNode.get("message").asText());
//            assertEquals("400 BAD_REQUEST", responseNode.get("status").asText());
            assertEquals("", responseNode.get("userId").asText());
        }
    }

    @Test
    void testVerifyFace_Exception() throws IOException {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");

        try (MockedStatic<File> mockedFile = mockStatic(File.class)) {
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenThrow(new IOException("File creation failed"));

            // Act
            ResponseEntity<String> result = controller.verifyFace(mockImage);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
//            assertEquals("Error verifying face.", result.getBody());
        }
    }

    @Test
    void testCallPythonFaceRecognitionService_RegisterAction() throws Exception {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
        String expectedResponse = "{\"status\":\"success\"}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.ok(expectedResponse);

        try (MockedStatic<File> mockedFile = mockStatic(File.class);
             MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
                     (mock, context) -> {
                         when(mock.exchange(
                                 eq("http://167.71.204.186:5000/register"),
                                 eq(HttpMethod.POST),
                                 any(HttpEntity.class),
                                 eq(String.class)
                         )).thenReturn(mockPythonResponse);
                     })) {

            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act - Use reflection to call private method
            ResponseEntity<String> result = (ResponseEntity<String>)
                    ReflectionTestUtils.invokeMethod(controller, "callPythonFaceRecognitionService",
                            mockImage, "register", 123);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals(expectedResponse, result.getBody());
        }
    }

    @Test
    void testCallPythonFaceRecognitionService_VerifyAction() throws Exception {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
        String pythonResponse = "{\"userId\":\"456\"}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.ok(pythonResponse);

        try (MockedStatic<File> mockedFile = mockStatic(File.class);
             MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
                     (mock, context) -> {
                         when(mock.exchange(
                                 eq("http://167.71.204.186:5000/verify"),
                                 eq(HttpMethod.POST),
                                 any(HttpEntity.class),
                                 eq(String.class)
                         )).thenReturn(mockPythonResponse);
                     })) {

            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act - Use reflection to call private method
            ResponseEntity<String> result = (ResponseEntity<String>)
                    ReflectionTestUtils.invokeMethod(controller, "callPythonFaceRecognitionService",
                            mockImage, "verify", null);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());

            // Parse and verify response
            JsonNode responseNode = objectMapper.readTree(result.getBody());
            assertEquals("successfully detect user.", responseNode.get("message").asText());
            assertEquals(200, responseNode.get("status").asInt());
            assertEquals("456", responseNode.get("userId").asText());
        }
    }

    @Test
    void testCallPythonFaceRecognitionService_FileTransferException() throws Exception {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
        doThrow(new IOException("Transfer failed")).when(mockImage).transferTo(any(File.class));

        try (MockedStatic<File> mockedFile = mockStatic(File.class)) {
            File mockTempFile = mock(File.class);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act - Use reflection to call private method
            ResponseEntity<String> result = (ResponseEntity<String>)
                    ReflectionTestUtils.invokeMethod(controller, "callPythonFaceRecognitionService",
                            mockImage, "register", 123);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

            // Parse and verify error response
            JsonNode responseNode = objectMapper.readTree(result.getBody());
            assertEquals("Transfer failed", responseNode.get("message").asText());
            assertEquals(500, responseNode.get("status").asInt());
            assertEquals("Error calling Python face recognition service", responseNode.get("error").asText());
        }
    }

    @Test
    void testVerifyFace_EmptyUserId() throws IOException {
        // Arrange
        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
//        when(mockImage.getBytes()).thenReturn("test image data".getBytes());
//        doNothing().when(mockImage).transferTo(any(File.class));

        String pythonResponse = "{\"userId\":\"\"}";
        ResponseEntity<String> mockPythonResponse = ResponseEntity.ok(pythonResponse);

        try (
                MockedStatic<File> mockedFile = mockStatic(File.class);
                MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class, (mock, context) -> {
                    // 正确位置：构造时就设置 exchange 返回
                    when(mock.exchange(
                            anyString(),
                            eq(HttpMethod.POST),
                            any(HttpEntity.class),
                            eq(String.class)
                    )).thenReturn(mockPythonResponse);
                })
        ) {
            File mockTempFile = mock(File.class);
            when(mockTempFile.delete()).thenReturn(true);
            mockedFile.when(() -> File.createTempFile(anyString(), anyString()))
                    .thenReturn(mockTempFile);

            // Act
            ResponseEntity<String> result = controller.verifyFace(mockImage);

            // Assert
            assertEquals(HttpStatus.OK, result.getStatusCode());

            JsonNode responseNode = objectMapper.readTree(result.getBody());
            assertEquals("successfully detect user.", responseNode.get("message").asText());
            assertEquals(200, responseNode.get("status").asInt());
            assertEquals("", responseNode.get("userId").asText());
        }
    }


}