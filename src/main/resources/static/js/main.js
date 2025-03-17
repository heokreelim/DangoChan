document.addEventListener("DOMContentLoaded", function () {
    if (typeof lucide !== "undefined") {
        lucide.createIcons();
    } else {
        console.error("❌ Lucide가 로드되지 않았습니다. 로드 순서를 확인하세요.");
    }
});

$(document).ready(function () {

    /***************** 덱 리스트 토글 *****************/
    $('.category-right a').on('click', function () {
        const chevron = $(this).find('i');
        const category = $(this).closest('.category');
        const folderIcon = category.find('.category-left i.fa-folder-open');
        const categoryId = category.find('.categoryId').val();
        const deckList = category.next('.deckList');

        if (chevron.hasClass('fa-chevron-up')) {
            chevron.removeClass('fa-chevron-up').addClass('fa-chevron-down');

            category.css({
                'background-color': '#FFF7CC',
                'box-shadow': '4px 4px 12px #f2eacb, -4px -4px 12px #ffffff'
            });

            folderIcon.css({
                'color': '#FFBB33'
            });

            localStorage.setItem('openCategory-' + categoryId, 'true');
        } else {
            chevron.removeClass('fa-chevron-down').addClass('fa-chevron-up');

            category.css({
                'background-color': '#f5f5fa',
                'box-shadow': '4px 4px 8px #d1d9e6, -4px -4px 8px #ffffff'
            });

            folderIcon.css({
                'color': '#a0a0a0'
            });

            localStorage.setItem('openCategory-' + categoryId, 'false');
        }

        deckList.slideToggle();
    });

    /***************** 페이지 로드 시 상태 복원 *****************/
    $('.category').each(function () {
        const category = $(this);
        const categoryId = category.find('.categoryId').val();
        const chevron = category.find('.category-right a i');
        const folderIcon = category.find('.category-left i.fa-folder-open');
        const deckList = category.next('.deckList');

        const isOpen = localStorage.getItem('openCategory-' + categoryId) === 'true';

        if (isOpen) {
            chevron.removeClass('fa-chevron-up').addClass('fa-chevron-down');

            category.css({
                'background-color': '#FFF7CC',
                'box-shadow': '4px 4px 12px #f2eacb, -4px -4px 12px #ffffff'
            });

            folderIcon.css({
                'color': '#FFBB33'
            });

            deckList.show();
        } else {
            chevron.removeClass('fa-chevron-down').addClass('fa-chevron-up');

            category.css({
                'background-color': '#f5f5fa',
                'box-shadow': '4px 4px 8px #d1d9e6, -4px -4px 8px #ffffff'
            });

            folderIcon.css({
                'color': '#a0a0a0'
            });

            deckList.hide();
        }
    });

    /***************** 드롭다운 메뉴 *****************/
    $(document).on('click', '.menu-icon', function (e) {
        e.stopPropagation(); // 이벤트 버블링 방지
        $(this).siblings('.dropdown-menu').toggleClass('show');
    });

    $(document).on('click', function (e) {
        if (!$(e.target).closest('.dropdown').length) {
            $('.dropdown-menu').removeClass('show');
        }
    });

    /***************** 덱 진행도 업데이트 (덱별 계산) *****************/
    $('.deck-bottom').each(function () {
        var $this = $(this);
        // 각 icon-box에서 숫자값 읽기 (괄호 제거)
        var okText = $this.find('.icon-box:eq(0) span').text().trim().replace(/[()]/g, '');  // 외운 카드 (3점)
        var yetText = $this.find('.icon-box:eq(1) span').text().trim().replace(/[()]/g, '');  // 복습 필요한 카드 (2점)
        var noText = $this.find('.icon-box:eq(2) span').text().trim().replace(/[()]/g, '');  // 외우지 못한 카드 (1점)
        var newText = $this.find('.icon-box:eq(3) span').text().trim().replace(/[()]/g, '');  // 아직 공부하지 않은 카드 (0점)

        var ok = parseInt(okText, 10) || 0;
        var yet = parseInt(yetText, 10) || 0;
        var no = parseInt(noText, 10) || 0;
        var newCard = parseInt(newText, 10) || 0;

        // 총 카드 수: 4종류의 카드 개수 합산
        var totalCards = ok + yet + no + newCard;

        // 실제 점수 계산: (3점 * ok) + (2점 * yet) + (1점 * no) + (0점 * newCard)
        var actualScore = (ok * 3) + (yet * 2) + (no * 1) + (newCard * 0);

        // 최대 점수: 모든 카드가 3점일 경우
        var maxScore = totalCards * 3;

        var percent = 0;
        if (totalCards > 0) {
            percent = (actualScore / maxScore) * 100;
        }
        percent = Math.round(percent);

        // 계산된 진행률을 텍스트로 업데이트
        $this.find('.percentage-box .percentage-text').text(percent + "%");

        // 5단계로 색상 결정 (20% 단위)
        var newColor;
        if (percent < 20) {
            newColor = "#ff0000"; // 0~20%: 빨간색
        } else if (percent < 40) {
            newColor = "#ffa500"; // 20~40%: 주황색
        } else if (percent < 60) {
            newColor = "#ffff00"; // 40~60%: 노란색
        } else if (percent < 80) {
            newColor = "#90ee90"; // 60~80%: 연두색
        } else {
            newColor = "#008000"; // 80~100%: 녹색
        }

        // 원형 진행도: 원의 반지름 r=60일 때, 둘레는 약 376.8
        var circumference = 376.8;
        var dashoffset = circumference * (1 - (percent / 100));

        // SVG 원에 색상과 dashoffset 적용
        $this.find('.percentage-box .progress').css({
            'stroke': newColor,
            'stroke-dashoffset': dashoffset
        });

        // 덱 컨테이너(.deck-wrap)에 총 카드 수 저장 (나중에 a 태그 클릭 시 사용)
        $this.closest('.deck-wrap').attr('data-total', totalCards);
    });

    /***************** 덱의 카드가 없을 경우, 링크 불가 *****************/
    $('.flashcard-link').on('click', function (e) {
        e.preventDefault(); // 기본 이동 막음 (검사 후 이동)

        const $this = $(this);

        // 가장 가까운 .deck-wrap 안에 있는 deckId 값을 가져온다
        const deckId = $this.closest('.deck-wrap').find('input.dekcId').val();

        // 새로운 카드가 몇 개인지 가져온다 (DOM에서 표시된 값 읽기)
        const newCardCount = parseInt(
            $this.closest('.deck-wrap')
                .find('.icon-box i.fa-question') // "?" 아이콘이 있는 부모 찾기
                .next('span').text().replace(/[^\d]/g, '') || '0', 10
        );

        console.log('선택한 덱 ID:', deckId);
        console.log('새로운 카드 개수:', newCardCount);

        // 덱에 카드가 없는 경우 (카드 갯수가 0일 때)
        // if (newCardCount === 0) {
        //     alert("이 덱에 학습 가능한 새로운 카드가 없습니다. 먼저 카드를 추가하거나, 내일 다시 시도해 주세요.");
        //     return; // 이동하지 않음
        // }

        // 서버에서 studyLevel=0 카드 존재 여부를 확인
        $.ajax({
            url: '/flashcard/check',
            type: 'GET',
            data: { deckId: deckId },
            success: function (hasNoLevelZero) {
                console.log('서버 응답 (studyLevel=0 없음?):', hasNoLevelZero);

                if (hasNoLevelZero) {
                    alert("모든 카드를 학습 완료했습니다! 복습은 내일 다시 시도해 주세요.");
                    return; // 이동하지 않음
                }

                // 검증 통과 후 링크 이동
                window.location.href = $this.attr('href');
            },
            error: function (xhr, status, error) {
                console.error('서버 요청 실패:', error);
                alert('서버와의 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
            }
        });
    });
    // 현재 페이지가 home.html일 경우
    if (window.location.pathname === '/home') {
        // nav 안에 있는 logo 링크 클릭 이벤트 차단
        $('.nav-bg .logo a').on('click', function (event) {
            event.preventDefault(); // 링크 이동 방지
        });
    }


});

