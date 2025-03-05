// communitySort.js

// 페이지 로드시 hidden 필드에서 초기값을 읽어옵니다.
let sortState = {
    primarySortField: document.getElementById('primarySortField').value || '',
    primarySortOrder: document.getElementById('primarySortOrder').value || '',
    secondarySortField: document.getElementById('secondarySortField').value || '',
    secondarySortOrder: document.getElementById('secondarySortOrder').value || ''
};

// 정렬 버튼 클릭 핸들러: 클릭한 버튼의 field를 인자로 전달
function updateSort(field) {
    // 첫 번째 정렬이 아직 없으면
    if (!sortState.primarySortField) {
        sortState.primarySortField = field;
        sortState.primarySortOrder = 'desc'; // 기본 내림차순
    } else if (sortState.primarySortField === field) {
        // 이미 primary로 지정된 필드라면 토글
        sortState.primarySortOrder = sortState.primarySortOrder === 'desc' ? 'asc' : 'desc';
    } else {
        // primary와 다른 필드라면 secondary로 처리
        if (sortState.secondarySortField === field) {
            sortState.secondarySortOrder = sortState.secondarySortOrder === 'desc' ? 'asc' : 'desc';
        } else {
            sortState.secondarySortField = field;
            sortState.secondarySortOrder = 'desc';
        }
    }
    
    // 검색 조건도 함께 전달 (검색 폼의 select, input 값을 가져옴)
    const searchItem = document.getElementById('searchItem').value || 'title';
    const searchWord = document.querySelector('input[name="searchWord"]').value || '';

    // 새 URL 구성 (Controller가 primarySortField, primarySortOrder, secondarySortField, secondarySortOrder를 받음)
    const url = `/community/communityBoardList?` +
                `searchItem=${encodeURIComponent(searchItem)}` +
                `&searchWord=${encodeURIComponent(searchWord)}` +
                `&primarySortField=${encodeURIComponent(sortState.primarySortField)}` +
                `&primarySortOrder=${encodeURIComponent(sortState.primarySortOrder)}` +
                `&secondarySortField=${encodeURIComponent(sortState.secondarySortField)}` +
                `&secondarySortOrder=${encodeURIComponent(sortState.secondarySortOrder)}`;
    
    window.location.href = url;
}
