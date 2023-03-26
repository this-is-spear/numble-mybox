package hello.numblemybox.mybox.infra;

import static hello.numblemybox.ObjectStorageProperties.*;

import java.io.File;
import java.io.InputStream;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

import hello.numblemybox.mybox.application.MyBoxStorage;
import reactor.core.publisher.Mono;

public class ObjectMyBoxStorage implements MyBoxStorage {

	private static final AmazonS3 S3 = AmazonS3ClientBuilder.standard()
		.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
		.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS, SECRET)))
		.build();

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
		return file.flatMapMany(Part::content).flatMap(dataBuffer -> {
			S3.putObject(BUCKET, fileId, dataBuffer.asInputStream(), new ObjectMetadata());
			return Mono.empty();
		}).then();
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> filename) {
		return filename.map(objectName -> S3.getObject(BUCKET, objectName).getObjectContent());
	}
}
