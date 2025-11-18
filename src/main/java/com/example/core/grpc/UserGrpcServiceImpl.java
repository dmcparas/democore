package com.example.core.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

// These classes are generated from your .proto file
import com.example.core.grpc.UserRequest;
import com.example.core.grpc.UserResponse;
import com.example.core.grpc.UserServiceGrpc;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.FileOutputStream;

@GrpcService
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    @Value("${spring.kafka.topic.name}")
    private String topicName;

    public UserGrpcServiceImpl(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println("✅ gRPC UserService is up and running!");
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        String userId = request.getId();

        // Simulated user data
        UserResponse response = UserResponse.newBuilder()
                .setId(userId)
                .setName("John Doe")
                .setEmail("john@example.com")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FileUploadRequest> uploadFile(StreamObserver<FileUploadResponse> responseObserver) {
        return new StreamObserver<>() {
            FileOutputStream outputStream;
            String filename;

            @Override
            public void onNext(FileUploadRequest request) {
                try {
                    if (outputStream == null) {
                        filename = request.getFilename();
                        outputStream = new FileOutputStream("uploads/" + filename);
                    }
                    outputStream.write(request.getContent().toByteArray());
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                try {
                    if (outputStream != null) outputStream.close();
                    responseObserver.onNext(FileUploadResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("File uploaded successfully: " + filename)
                            .build());
                    responseObserver.onCompleted();
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }
        };
    }

    @Override
    public StreamObserver<FileUploadRequest> uploadFileToKafka(StreamObserver<FileUploadResponse> responseObserver) {

        return new StreamObserver<>() {
            String filename;

            @Override
            public void onNext(FileUploadRequest request) {
                try {
                    if (filename == null) {
                        filename = request.getFilename();
                    }

                    byte[] chunk = request.getContent().toByteArray();

                    // Send chunk to Kafka
                    kafkaTemplate.send(topicName, filename, chunk)
                            .whenComplete((result, ex) -> {
                                if (ex != null) {
                                    responseObserver.onError(ex);
                                }
                            });


                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                try {
                    // Ensure messages are flushed
                    kafkaTemplate.flush();

                    responseObserver.onNext(
                            FileUploadResponse.newBuilder()
                                    .setSuccess(true)
                                    .setMessage("File uploaded to Kafka successfully: " + filename)
                                    .build()
                    );
                    responseObserver.onCompleted();

                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }
        };
    }

}
