package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class FolderCommandService {
	private final String ADMIN = "rjsckdd12@gmail.com";
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;

	/**
	 * 1. 저장하려는 폴더 정보를 가져온다.
	 * 2. 저장된 폴더에서 파일 이름과 같은 이름의 파일이 있는지 확인한다.
	 * 3. 파일 메타데이터를 저장한다.
	 * 4. 폴더에 연관관계 매핑 후 업데이트한다.
	 *
	 * @param folderId   저장하려는 폴더 식별자
	 * @param file 파일 메타데이터 정보 스트림
	 * @return void
	 */
	public Mono<Void> addFileInFolder(String folderId, Mono<MyFile> file) {
		return file.flatMap(
			myFile -> {
				var ensureFilename = fileMyBoxRepository.findByParentId(folderId).flatMap(myFolder -> {
					if (myFolder.getName().equals(myFile.getFilename())) {
						return Mono.error(new IllegalArgumentException());
					}
					return Mono.empty();
				}).then();
				myFile.addParent(folderId);
				var insertFile = fileMyBoxRepository.save(myFile);
				return Mono.when(ensureFilename, insertFile);
			}).then();
	}

	/**
	 * 1. 상위 폴더를 조회환다.
	 * 2. 만드려는 폴더의 이름이 있는지 확인한다.
	 * 3. 폴더를 생성해 데아터베이스에 저장한다.
	 * 4. 상위 폴더에 연관관계 매핑 후 상위 폴더를 업데이트한다.
	 *
	 * @param parentId   상위 폴더 ID
	 * @param foldername 생성하려는 폴더 이름
	 * @return void
	 */
	public Mono<Void> createFolder(String parentId, String foldername) {
		return folderMyBoxRepository.findById(parentId)
			.publishOn(Schedulers.boundedElastic())
			.flatMap(parent -> {
				var ensureFoldername = folderMyBoxRepository.findByParentId(parentId).flatMap(
					myFolder -> {
						if (myFolder.getName().equals(foldername)) {
							return Mono.error(new IllegalArgumentException("이름이 같을 수 없습니다."));
						}
						return Mono.empty();
					}
				).then();
				var insertFolder = folderMyBoxRepository.save(MyFolder.createFolder(null, foldername, ADMIN, parentId));
				return Mono.when(ensureFoldername, insertFolder);
			}).then();
	}

	public Mono<Void> updateFolder(String folderId, String foldername) {
		return null;
	}
}
