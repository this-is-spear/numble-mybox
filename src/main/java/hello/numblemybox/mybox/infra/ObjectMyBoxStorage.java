package hello.numblemybox.mybox.infra;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.codec.multipart.FilePart;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import hello.numblemybox.mybox.application.MyBoxStorage;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ObjectMyBoxStorage implements MyBoxStorage {
	private static final Path LOCAL_PATH = Paths.get("./src/main/resources/upload");
	private final static String ENDPOINT = "https://kr.object.ncloudstorage.com";
	private final static String REGION = "kr-standard";
	private final static String ACCESS = "8jOB0VungyBgHzL4n9Xe";
	private final static String SECRET = "z8ehTkH3RJJLPSWFuA1Jom3idK2DVTtE7SDeC4TO";
	private final static String BUCKET = "this-is-spear-box";

	private static final AmazonS3 S3 = AmazonS3ClientBuilder.standard()
		.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, REGION))
		.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS, SECRET)))
		.build();

	@Override
	public Mono<File> getFile(String filename) {
		return null;
	}

	@Override
	public Mono<Void> uploadFile(Mono<FilePart> file, String fileId) {
		Path path = LOCAL_PATH.resolve(fileId);
		file.flatMap(filePart -> filePart.transferTo(path))
			.publishOn(Schedulers.boundedElastic())
			.subscribe(v -> {
				try {
					S3.putObject(BUCKET, fileId, path.toFile());
					Files.deleteIfExists(path);
				} catch (SdkClientException | IOException e) {
					e.printStackTrace();
				}
			});
		return Mono.empty();
	}

	@Override
	public Mono<InputStream> downloadFile(Mono<String> filename) {
		return filename.map(
			objectName -> {
				S3Object s3Object = S3.getObject(BUCKET, objectName);
				try (S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent()) {
					return s3ObjectInputStream;
				} catch (SdkClientException | IOException e) {
					throw new RuntimeException(e);
				}
			}
		);
	}
}
