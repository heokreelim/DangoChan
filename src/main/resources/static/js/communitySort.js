document.addEventListener('DOMContentLoaded', function() {
    // hidden input 요소가 모두 로드된 후 초기값 설정
    window.sortState = {
        primarySortField: document.getElementById('primarySortField').value || '',
        primarySortOrder: document.getElementById('primarySortOrder').value || '',
        secondarySortField: document.getElementById('secondarySortField').value || '',
        secondarySortOrder: document.getElementById('secondarySortOrder').value || ''
    };

    // updateSort 함수를 전역 함수로 등록
    window.updateSort = function(field) {
        // 1. 만약 현재 primary가 기본("createDate")이거나 비어있다면
        if (sortState.primarySortField === "createDate" || sortState.primarySortField === "") {
            // 기본 정렬을 secondary로 복사 (즉, 기본 정렬 해제)
            sortState.secondarySortField = sortState.primarySortField;
            sortState.secondarySortOrder = sortState.primarySortOrder;
            // 새 필드를 primary로 설정
            sortState.primarySortField = field;
            sortState.primarySortOrder = 'desc'; // 기본 내림차순
        } 
        // 2. 만약 클릭한 필드가 primary와 같다면 토글 처리
        else if (sortState.primarySortField === field) {
            sortState.primarySortOrder = sortState.primarySortOrder === 'desc' ? 'asc' : 'desc';
        } 
        // 3. primary가 이미 다른 필드로 설정되어 있다면 -> secondary 처리
        else {
            if (sortState.secondarySortField === field) {
                sortState.secondarySortOrder = sortState.secondarySortOrder === 'desc' ? 'asc' : 'desc';
            } else {
                sortState.secondarySortField = field;
                sortState.secondarySortOrder = 'desc';
            }
        }
        
        // 검색 조건 가져오기
        const searchItem = document.getElementById('searchItem').value || 'title';
        const searchWord = document.querySelector('input[name="searchWord"]').value || '';
        
        // URL 구성 (Controller는 primarySortField, primarySortOrder, secondarySortField, secondarySortOrder를 받음)
        const url = `/community/communityBoardList?` +
                    `searchItem=${encodeURIComponent(searchItem)}` +
                    `&searchWord=${encodeURIComponent(searchWord)}` +
                    `&primarySortField=${encodeURIComponent(sortState.primarySortField)}` +
                    `&primarySortOrder=${encodeURIComponent(sortState.primarySortOrder)}` +
                    `&secondarySortField=${encodeURIComponent(sortState.secondarySortField)}` +
                    `&secondarySortOrder=${encodeURIComponent(sortState.secondarySortOrder)}`;
        
        window.location.href = url;
    };
});
