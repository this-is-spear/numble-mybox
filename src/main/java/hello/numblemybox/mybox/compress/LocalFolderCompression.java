package hello.numblemybox.mybox.compress;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.FileSource;
import org.zeroturnaround.zip.ZipEntrySource;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFolder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class LocalFolderCompression extends FolderCompressionTemplate {
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;

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
			.subscribe(myFile -> list.add(new FileSource(resolvePath(path, myFile.getName()),
				new File(resolvePath(myFile.getPath(), myFile.getId())))));
	}

	private String resolvePath(String path, String filename) {
		return String.format("%s/%s", path, filename);
	}

}
