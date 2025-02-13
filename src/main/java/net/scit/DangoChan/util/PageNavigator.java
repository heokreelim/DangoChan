package net.scit.DangoChan.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageNavigator {
	private final int pagePerGroup = 10;	// 그룹당 페이지 수
	private int pageLimit;					// 한 페이지당 글개수
	private int page;						// 사용자가 요청한 페이지
	private int totalPages;					// 총 페이지 개수 (직접 계산해보겠다!)
	private int totalGroupCount;			// 총 그룹수(1 2 3 4 5 6 7 8 9 10 / 11 12 13 14 15 16 이렇다면 총 2개의 그룹이겠지?)
	
	private int startPageGroup;				// 현재 그룹의 첫 페이지
	private int endPageGroup;				// 현재 그룹의 마지막 페이지
	
	private int currentGroup;				// 사용자가 요청한 페이지가 어느 그룹에 위치해 있나요?
	
	public PageNavigator(int pageLimit, int page, int totalPages) {
		// 멤버 초기화
		this.pageLimit = pageLimit;
		this.page = page;
		this.totalPages = totalPages;
		
		// 총 그룹수 계산
		// 총 글 개수가 152개면 16페이지, 2그룹이 나와야 한다는 걸 생각해봅시다
		totalGroupCount = totalPages / pagePerGroup; // 16 /10 => 1
		totalGroupCount += (totalPages % pagePerGroup == 0) ? 0 : 1;
		
		System.out.println("=== totalGroupCount(총 그룹수): " + totalGroupCount);
		
		// 사용자가 요청한 페이지의 첫 번째, 마지막
		// page: 15, pageLimit: 10 : 1 (자료형이 int)
		// 자료형이 double일 경우 15.0 / 10 => 1.5 => Math.ceil(1.5) => 2 => 2 - 1 = 1 * 10 ==> 10
		
		//  1 ~ 10 사이의 페이지 요청하면 startPageGroup => 1
		// 11 ~ 20 사이의 페이지 요청하면 startPageGroup => 11
		startPageGroup = ((int)(Math.ceil((double) page / pageLimit)) - 1) * pageLimit + 1;
		
		System.out.println("=== 요청한 페이지그룹의 첫 페이지(총 그룹수): " + startPageGroup);
		
		//  1 ~ 10 사이의 페이지 요청하면 endPageGroup => 10
		// 11 ~ 20 사이의 페이지 요청하면 endPageGroup => 16
		// 예를 들어 3페이지를 요청했으면, sPG = 1, ePG = 10;
		// 16페이지를 요청했으면, sPG = 11, ePG = 20;
		endPageGroup = 
				(startPageGroup + pagePerGroup - 1) >= totalPages
				? totalPages : (startPageGroup + pagePerGroup - 1); 
		System.out.println("=== 요청한 페이지그룹의 마지막 페이지(총 그룹수): " + endPageGroup);
		
		if(endPageGroup == 0) endPageGroup = 1;
		
		// 16p를 요청했다면 현재 그룹? 2
		// (16 - 1) / 10 = 1 (자료형이 int) -> +1 => 2		
		currentGroup = (page - 1) / pagePerGroup + 1;
		System.out.println("=== 현재 그룹: " + currentGroup);
	}

}
