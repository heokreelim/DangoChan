$(document).ready(function(){
    $('#searchBtn').on('click', function(){
        $('#requestPage').val(1);      // 검색 시 페이지는 1로 초기화
        $('#formMode').val('search');  // 검색 모드 설정
        $('#searchForm').submit();
    });

    $('#searchForm').on('submit', function(){
        let mode = $('#formMode').val();
        let searchInput = $.trim($("input[name='searchWord']").val());

        if(mode === 'search' && searchInput === ''){
            alert('검색어를 입력해주세요.');
            return false;
        }

        // 문제 없으면 폼 전송
    });
});

function pageFormSubmit(page) {
    $('#requestPage').val(page);
    $('#formMode').val('page');  // 페이지 이동 모드 설정
    $('#searchForm').submit();
}