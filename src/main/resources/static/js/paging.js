/**
 * 페이징 호출 코드
 * 
 */

function pageFormSubmit(page) {
    $('#requestPage').val(page);
    $('#searchForm').submit(); // 서버로 전송하기

    // alert(page);
//    location.href = `/board/boardList?page=${page}` \
//    //왜? 검색하고 페이징을 하면 페이징이 풀림~

}