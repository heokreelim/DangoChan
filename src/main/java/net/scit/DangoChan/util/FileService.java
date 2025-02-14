package net.scit.DangoChan.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {
	// 파일 저장 메서드 : 저장 파일명을 반환
		public static String saveFile(MultipartFile uploadFile, String uploadPath) {

			// 저장 디렉토리 생성
			// 파일이 비어있지 않다면
			if (!uploadFile.isEmpty()) {
				File path = new File(uploadPath);

				// 파일 경로가 존재하지 않다면 새로 생성
				if (!path.isDirectory()) {
					path.mkdirs(); // make directories
				}
			}

			// 만약 원본 전체 파일명이 happy.jpg 라면
			String originalFileName = uploadFile.getOriginalFilename(); // 원본 전체 파일명 (happy.jpg)
			String savedFileName = null; 								// 저장할 때 사용할 전체 파일명
			String filename = null; 									// 파일명 (happy)
			String ext = null; 											// 확장자명 (jpg)
			String uuid = UUID.randomUUID().toString();					// 난수 발생 

			// .의 위치 찾기
			int position = originalFileName.lastIndexOf("."); // 12 0 ~ 11

			// 만약 확장자가 없어서 position값이 -1 인 경우를 거르기
			if (position == -1) {
				// 확장자가 없는 파일이라면
				ext = "";
				filename = originalFileName;
			} else {
				// 확장자가 있는 파일이라면 
				ext = originalFileName.substring(position + 1);
				filename = originalFileName.substring(0, position);
			}
			
			savedFileName = filename + "_" + uuid + "." + ext;
			
			// 디렉토리에 저장하기 (총 경로 주소) 
			String fullPath =  uploadPath + "/" + savedFileName;	// 문자열
			
			File serverFile = null;
			serverFile = new File(fullPath);
			
			try {
				uploadFile.transferTo(serverFile);
			} catch (IOException e) {
				// 저장이 안 된 경우이므로, DB도 저장하면 안된다.
				savedFileName = null;
				e.printStackTrace();
			}

			return savedFileName;
		};
		
		// 파일 삭제 메서드 
		public static boolean deleteFile(String fullPath) {
			boolean result = false;		// 삭제 여부 반환
			
			File file = new File(fullPath);
			if (file.isFile()) {
				result = file.delete();
			}
			
			return result;
		}
	}
