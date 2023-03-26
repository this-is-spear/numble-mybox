package hello.numblemybox.mybox.infra;

import static hello.numblemybox.ObjectStorageProperties.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import hello.numblemybox.mybox.application.MyBoxStorage;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

@Service
public class ObjectMyBoxStorage implements MyBoxStorage {

	private static final AmazonS3 S3 = AmazonS3ClientBuilder.standard()
		.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
		.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS, SECRET)))
		.build();

	@Override
	public String getPath() {
		return String.format("%s/%s", ENDPOINT, BUCKET);
	}

	@Override
	public Mono<File> getFile(String filename) {
		return null;
	}

	public Mono<Void> deleteFile(String fileId) {
		S3.deleteObject(BUCKET, fileId);
		return Mono.empty();
	}

	@Override
	public Mono<Void> uploadFile(Mono<FilePart> file, String fileId) {
		return file.flatMapMany(Part::content)
			.flatMap(dataBuffer -> Mono.just(S3.putObject(getPutObjectRequest(fileId, dataBuffer))))
			.then();
	}

	@SneakyThrows(IOException.class)
	private PutObjectRequest getPutObjectRequest(String fileId, DataBuffer dataBuffer) {
		var bytes = IOUtils.toByteArray(dataBuffer.asInputStream());
		var objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(bytes.length);
		var byteArrayInputStream = new ByteArrayInputStream(bytes);
		return new PutObjectRequest(BUCKET, fileId, byteArrayInputStream, objectMetadata);
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> filename) {
		return filename.map(objectName -> S3.getObject(BUCKET, objectName).getObjectContent());
	}
}
