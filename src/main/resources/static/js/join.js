const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
idCheck = false;

$(function(){
    $('#idDuplCheckBtn').on('click', confirmId);
    $('#registerBtn').on('click', join);
});

function confirmId() {
    let email = $('#userId').val();

    if (!emailRegex.test(email))
    {
        alert('정확한 Email 형식을 입력하세요.');
        return;
    }

    $.ajax({
        url: '/user/idCheck'
        , method: 'POST'
        , data: {'email' : email}
        , success: function(resp) {
            // resp가 true이면 회원가입 가능
            if (resp) {
                //$('#confirmId').css('color', 'blue');
                alert('가입 가능한 Email입니다.');
                idCheck = true;
            } else {
                //$('#confirmId').css('color', 'red');
                alert('가입 불가능한 Email입니다. 다시 입력 후 중복확인을 해주세요');
                idCheck = false;
            }
        }
    })
}

function join() {
    // ID 중복체크 확인
    if (!idCheck)
    {   
        alert('Email 중복체크를 완료해주세요');
        return false;
    }
        
    let userPwd = $('#password').val();

    // 비밀번호 길이 체크
    if (userPwd.trim().length < 3 || userPwd.trim().length > 12)
    {
        //$('#confirmPwd').css('color', 'red');
        alert('비밀번호는 4~12자 이내로 입력하세요');
        return false;
    }
    
    let userPwdCheck = $('#passwordCheck').val();

    // 비밀번호 길이 체크
    if (userPwd.trim() != userPwdCheck.trim())
    {
        //$('#confirmPwd').css('color', 'red')
        alert('비밀번호 확인에 같은 값을 입력해주세요');
        return false;
    }

    let userName = $('#userName').val();
    if (userName.trim().length < 1)
    {
        //$('#confirmUserName').css('color', 'red')
        alert('닉네임을 입력하세요');
        
        return false;
    }

    return true;
}