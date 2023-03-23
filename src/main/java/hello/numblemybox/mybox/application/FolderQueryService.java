package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import hello.numblemybox.mybox.dto.FileResponse;
import hello.numblemybox.mybox.dto.FolderResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FolderQueryService {

	private static final String ADMIN = "rjsckdd12@gmail.com";
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;

	public Mono<FolderResponse> findFolder(String folderId) {
		return folderMyBoxRepository.findById(folderId)
			.flatMap(this::getFolderResponse);
	}

	public Mono<FolderResponse> findRootFolder(UserInfo userInfo) {
		return getRootFolder(userInfo.id())
			.flatMap(this::getFolderResponse);
	}

	public Flux<FolderResponse> findFoldersInParent(String folderId) {
		return folderMyBoxRepository.findByParentId(folderId).flatMap(this::getFolderResponse);
	}

	public Flux<FolderResponse> findFoldersInRoot() {
		return folderMyBoxRepository.findByTypeAndUserId(ObjectType.ROOT, ADMIN)
			.flatMapMany(root -> folderMyBoxRepository.findByParentId(root.getId())
				.flatMap(this::getFolderResponse));
	}

	public Flux<FileResponse> findFilesInParent(String folderId) {
		return fileMyBoxRepository.findByParentId(folderId).flatMap(this::getFileResponse);
	}

	public Flux<FileResponse> findFilesInRoot() {
		return folderMyBoxRepository.findByTypeAndUserId(ObjectType.ROOT, ADMIN)
			.flatMapMany(root -> fileMyBoxRepository.findByParentId(root.getId()))
			.flatMap(this::getFileResponse);
	}

	private Mono<FileResponse> getFileResponse(MyFile myFile) {
		return Mono.just(new FileResponse(myFile.getId(), myFile.getName(), myFile.getType(),
			myFile.getExtension(), myFile.getSize(), myFile.getPath()));
	}

	private Mono<FolderResponse> getFolderResponse(MyFolder myFolder) {
		return Mono.just(new FolderResponse(myFolder.getId(), myFolder.getName(),
			myFolder.getType()));
	}

	private Mono<MyFolder> getRootFolder(String userId) {
		return folderMyBoxRepository.findByTypeAndUserId(ObjectType.ROOT, userId);
	}
}
