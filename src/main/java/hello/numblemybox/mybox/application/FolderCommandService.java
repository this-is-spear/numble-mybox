package hello.numblemybox.mybox.application;

import java.util.Objects;

import org.springframework.stereotype.Service;

import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class FolderCommandService {
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;

	/**
	 * 1. 저장하려는 폴더 정보를 가져온다.
	 * 2. 저장된 폴더에서 파일 이름과 같은 이름의 파일이 있는지 확인한다.
	 * 3. 파일 메타데이터를 저장한다.
	 * 4. 폴더에 연관관계 매핑 후 업데이트한다.
	 *
	 * @param folderId 저장하려는 폴더 식별자
	 * @param file     파일 메타데이터 정보 스트림
	 * @return void
	 */
	public Mono<MyFile> addFileInFolder(String folderId, MyFile file) {
		return fileMyBoxRepository.findByParentIdAndName(folderId, file.getName())
			.map(myFile -> ensureFilename(file, myFile))
			.switchIfEmpty(saveFile(file, folderId));
	}

	/**
	 * 1. 상위 폴더를 조회환다.
	 * 2. 만드려는 폴더의 이름이 있는지 확인한다.
	 * 3. 폴더를 생성해 데아터베이스에 저장한다.
	 * 4. 상위 폴더에 연관관계 매핑 후 상위 폴더를 업데이트한다.
	 *
	 * @param userInfo   사용하는 사용자
	 * @param parentId   상위 폴더 ID
	 * @param foldername 생성하려는 폴더 이름
	 * @return void
	 */
	public Mono<Void> createFolder(UserInfo userInfo, String parentId, String foldername) {
		return folderMyBoxRepository.findByParentIdAndName(parentId, foldername)
			.map(myFolder -> {
				throw new IllegalArgumentException("같은 이름의 폴더가 있습니다.");
			})
			.switchIfEmpty(folderMyBoxRepository.save(MyFolder.createFolder(null, foldername, userInfo.id(), parentId)))
			.then();
	}

	/**
	 * 폴더 이름을 수정한다.
	 *
	 * @param userInfo   현재 접속한 사용자의 정보
	 * @param folderId   수정하려는 폴더의 식별자
	 * @param foldername 수정할 폴더 이름
	 * @return 반환값 없음
	 */
	public Mono<Void> updateFolder(UserInfo userInfo, String folderId, String foldername) {
		return folderMyBoxRepository.findById(folderId)
			.map(myFolder -> ensureMember(userInfo, myFolder))
			.map(myFolder -> myFolder.updateName(foldername))
			.publishOn(Schedulers.boundedElastic())
			.map(myFolder -> folderMyBoxRepository.save(myFolder).subscribe())
			.then();
	}

	private MyFolder ensureMember(UserInfo userInfo, MyFolder myFolder) {
		if (!Objects.equals(myFolder.getUserId(), userInfo.id())) {
			throw InvalidMemberException.invalidUser();
		}
		return myFolder;
	}

	private MyFile ensureFilename(MyFile file, MyFile myFile) {
		if (myFile != null) {
			throw InvalidFilenameException.alreadyFilename();
		}
		return file;
	}

	private Mono<MyFile> saveFile(MyFile file, String folderId) {
		file.addParent(folderId);
		return fileMyBoxRepository.save(file);
	}
}
