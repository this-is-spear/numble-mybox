package hello.numblemybox.mybox.application;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.FileSource;
import org.zeroturnaround.zip.ZipEntrySource;
import org.zeroturnaround.zip.ZipUtil;

import hello.numblemybox.member.dto.UserInfo;
import hello.numblemybox.member.exception.InvalidMemberException;
import hello.numblemybox.mybox.domain.FileMyBoxRepository;
import hello.numblemybox.mybox.domain.FolderMyBoxRepository;
import hello.numblemybox.mybox.domain.MyFile;
import hello.numblemybox.mybox.domain.MyFolder;
import hello.numblemybox.mybox.dto.LoadedFileResponse;
import hello.numblemybox.mybox.exception.InvalidFilenameException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class FolderCommandService {
	private final FolderMyBoxRepository folderMyBoxRepository;
	private final FileMyBoxRepository fileMyBoxRepository;
	private final MyBoxStorage myBoxStorage;

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

	/**
	 * 1. 선택한 폴더 식별자 내부 폴더와 파일 리스트를 조회한다.
	 * 2. src/resource/tmp/ 위치에 압축 파일을 생성한다. 압축 파일의 이름은 최상위 폴더의 식별자(fodlerId)이다.
	 * 3. 리프 노드에 존재하는 파일들의 이름을 파일 이름(fileanme)으로 변경 한 후, 경로 또한 최상위 폴더에서 지나온 폴더 경로(relative path)로 해서 압축할 후보 배열(new ZipEntrySource[])에 추가한다.
	 * 4. 3 번 과정을 반복해 상위 폴더를 이용해 압축 파일에 추가(ZipUtil.addEntries)한다.
	 * 5. 압축 파일을 반환한다. 반환할 때, 압축 파일의 이름을 최상위 폴더 이름으로 반환(foldername)한다.
	 * 6. 반환한 후, 압축 파일을 제거한다.
	 *
	 * @param userInfo 사용자 정보
	 * @param folderId 다운로드 받으려는 폴더의 식별자
	 * @return
	 */
	public Mono<LoadedFileResponse> downloadFolder(UserInfo userInfo, String folderId) {
		Path path = myBoxStorage.getZipPath();
		var ensureFolder = folderMyBoxRepository.findById(folderId)
			.map(myFile -> ensureMember(userInfo, myFile));
		createZipFile(folderId, path, ensureFolder);
		var getInputStream = myBoxStorage.downloadFile(path.resolve(folderId + ".zip"));
		return Mono.zip(ensureFolder, getInputStream).map(this::getLoadedFileResponse);
	}

	private void createZipFile(String folderId, Path path, Mono<MyFolder> ensureFolder) {
		File zip = new File(path.resolve(folderId) + ".zip");
		ZipUtil.createEmpty(zip);

		ensureFolder
			.map(myFolder -> findFilesRecursive("", myFolder))
			.map(list -> {
				var arr = list.toArray(new ZipEntrySource[] {});
				ZipUtil.addEntries(zip, arr);
				return arr;
			}).subscribe();
	}

	private List<ZipEntrySource> findFilesRecursive(String path, MyFolder myFolder) {
		List<ZipEntrySource> list = new ArrayList<>();
		fileMyBoxRepository.findByParentId(myFolder.getId())
			.subscribe(myFile -> list.add(new FileSource(resolvePath(path, myFile.getName()),
				new File(resolvePath(myFile.getPath(), myFile.getId())))));

		folderMyBoxRepository.findByParentId(myFolder.getId())
			.subscribe(
				nextFolder -> list.addAll(findFilesRecursive(resolvePath(path, myFolder.getName()), nextFolder)));
		return list;
	}

	private String resolvePath(String path, String filename) {
		return String.format("%s/%s", path, filename);
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

	private LoadedFileResponse getLoadedFileResponse(Tuple2<MyFolder, InputStream> objects) {
		return new LoadedFileResponse(
			objects.getT1().getName(),
			objects.getT2(),
			"application/zip");
	}
}
