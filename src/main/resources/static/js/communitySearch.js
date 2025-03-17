$(document).ready(function(){
    $('#searchForm').on('submit', function(){
        let searchInput = $.trim($("input[name='searchWord']").val());
        if(searchInput === ''){
            alert('검색어를 입력해주세요.');
            return false;  // 이벤트 전파 및 기본 동작 중지
        }
    });
});
