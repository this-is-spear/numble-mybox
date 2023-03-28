package hello.numblemybox.mybox.compress;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ByteSource;
import org.zeroturnaround.zip.ZipEntrySource;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.infra.ObjectMyBoxStorage;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public final class ObjectStorageCompression extends FolderCompressionTemplate {
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;
	private final ObjectMyBoxStorage objectMyBoxStorage;

	@Override
	protected File createZip(String folderId, Path path) {
		return new File(path.resolve(folderId) + ZIP_EXTENSION);
	}

	@Override
	protected List<ZipEntrySource> getNodes(MyFolder ensureFolder) {
		return findFilesRecursive(EMPTY_PATH, ensureFolder);
	}

	private List<ZipEntrySource> findFilesRecursive(String path, MyFolder myFolder) {
		List<ZipEntrySource> list = new ArrayList<>();
		addFiles(path, myFolder, list);
		findFolders(path, myFolder, list);
		return list;
	}

	private void findFolders(String path, MyFolder myFolder, List<ZipEntrySource> list) {
		folderMyBoxRepository.findByParentId(myFolder.getId())
			.subscribe(
				nextFolder -> list.addAll(findFilesRecursive(resolvePath(path, myFolder.getName()), nextFolder)));
	}

	private void addFiles(String path, MyFolder myFolder, List<ZipEntrySource> list) {
		fileMyBoxRepository.findByParentId(myFolder.getId())
			.subscribe(myFile -> objectMyBoxStorage.downloadFile(myFile.getId())
				.subscribe(inputStream -> {
					try {
						list.add(new ByteSource(resolvePath(path, myFile.getName()), inputStream.readAllBytes()));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				})
			);
	}

	private String resolvePath(String path, String filename) {
		return String.format("%s/%s", path, filename);
	}
}
