function redirectToLineLogin() {
    window.location.href = "/oauth2/authorization/line";
}

function redirectToGoogleLogin() {
    window.location.href = "/oauth2/authorization/google";
}

async function guestLogin() {
    const guestLoginKey = localStorage.getItem('guestLoginKey');
    const response = await fetch('/user/guestlogin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: guestLoginKey ? JSON.stringify(guestLoginKey) : null
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem('guestLoginKey', data);
        
        console.log('Guest login successful key:' + localStorage.getItem('guestLoginKey'));
        
        window.location.href = "/home";
    } else {
        console.error('Guest login failed');
    }
}

function deleteGuestLoginKey() {
    localStorage.removeItem('guestLoginKey');
    console.log('guestLoginKey deleted from localStorage');
}