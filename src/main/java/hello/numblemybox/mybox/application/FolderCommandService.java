package hello.numblemybox.mybox.application;

import org.springframework.stereotype.Service;

import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.domain.ObjectType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
	 * @param myFileFlux 파일 메타데이터 정보 스트림
	 * @return void
	 */
	public Mono<Void> addFileInFolder(String folderId, Flux<MyFile> myFileFlux) {
		return myFileFlux.flatMap(
			myFile -> {
				var getParent = folderMyBoxRepository.findById(folderId);
				var ensureDuplicate = getParent.flatMap(parent -> {
					var names = parent.getFiles().stream().map(MyFile::getFilename).toList();
					if (names.contains(myFile.getFilename())) {
						return Mono.error(IllegalArgumentException::new);
					}
					return Mono.empty();
				}).then();

				var insertFile = getParent.flatMap(parent -> fileMyBoxRepository.insert(myFile)
					.flatMap(file -> {
						parent.addMyObject(file);
						return Mono.empty();
					})).then();

				return Mono.when(getParent, ensureDuplicate, insertFile);
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
			.flatMap(parent -> {
				var ensureFolder = Mono.justOrEmpty(
					parent.getChildren()
						.stream()
						.filter(myObject -> myObject.getType()
							.equals(ObjectType.FOLDER))
						.filter(myObject -> myObject.getName().equals(foldername))
						.findFirst()).flatMap(foundFolder -> {
					if (foundFolder != null) {
						return Mono.error(new IllegalArgumentException("같은 이름의 폴더가 존재합니다."));
					}
					return Mono.empty();
				});

				var folder = MyFolder.createFolder(null, foldername, ADMIN);
				var insert = folderMyBoxRepository.insert(folder);
				var mono = insert.flatMap(myFolder -> {
					parent.addMyObject(myFolder);
					return Mono.empty();
				});
				var folderMono = folderMyBoxRepository.insert(parent);
				return Mono.when(ensureFolder, insert, mono, folderMono);
			});
	}
}
