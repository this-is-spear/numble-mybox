package hello.numblemybox.mybox.compress;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.zeroturnaround.zip.ZipEntrySource;
import org.zeroturnaround.zip.ZipUtil;

import hello.numblemybox.mybox.domain.MyFolder;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public abstract class FolderCompressionTemplate {
	protected static final String EMPTY_PATH = "";
	protected static final String ZIP_EXTENSION = ".zip";

	/**
	 * 폴더를 압축합니다. 압축된 파일을 로컬에서 저장되고, 로컬에서 저장되는 파일을 호출한 메서드에서 삭제해야 합니다.
	 *
	 * @return zip 파일
	 */
	public Mono<File> compressFolderInLocal(MyFolder myFolder, Path path) {
		var zip = createZip(myFolder.getId(), path);
		var list = getNodes(myFolder);
		ZipUtil.createEmpty(zip);
		ZipUtil.pack(list.toArray(new ZipEntrySource[] {}), zip);
		return Mono.just(zip);
	}

	/**
	 * 로컬에서 압축된 파일 데이터를 가져옵니다.
	 *
	 * @param path 압축된 파일이 있는 위치
	 * @return 파일 데이터
	 */
	public Mono<InputStream> downloadFileInLocal(Mono<Path> path) {
		return path.publishOn(Schedulers.boundedElastic()).map(this::getInputStream);
	}

	protected abstract File createZip(String folderId, Path path);

	protected abstract List<ZipEntrySource> getNodes(MyFolder ensureFolder);

	private InputStream getInputStream(Path filePath) {
		try {
			return Files.newInputStream(filePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			deleteZip(filePath);
		}
	}

	@SneakyThrows
	private void deleteZip(Path filePath) {
		Files.deleteIfExists(filePath);
	}
}
